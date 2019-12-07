package io.g740.d1.init;

import io.g740.d1.config.BasicDbConfig;
import io.g740.d1.datasource.Constants;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.exception.custom.IllegalParameterException;
import io.g740.d1.util.DataSourcePoolUtils;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;
/**
 * @function:
 * @author: DAM
 * @date: 2019/7/11 10:17
 * @description:
 * @version: V1.0
 */
@Component
public class D1BasicDataService {
    private final static Logger LOGGER = LoggerFactory.getLogger(D1BasicDataService.class);


    public DataSource createD1BasicDataSource(BasicDbConfig basicDbConfig) throws Exception {

        //数据库类型
        String dbType = basicDbConfig.getType();
        //驱动包名称
        String driverName = "";
        if (StringUtils.isBlank(dbType)) {
            throw new IllegalParameterException("db type can not be null!");
        }

        boolean useSshTunnel = false;

        //ssh
        int localPort = findRandomOpenPort();
        String sshUser = basicDbConfig.getSshProxyUser();
        String sshPassword = basicDbConfig.getSshProxyPassword();
        String sshHost = basicDbConfig.getSshProxyHost();
        Integer sshPort = basicDbConfig.getSshProxyPort();
        String sshAuthType = StringUtils.isBlank(basicDbConfig.getSshAuthType()) ? Constants.SshAuthType.PASSWORD.toString() : basicDbConfig.getSshAuthType();
        String sshKeyFile = basicDbConfig.getSshKeyFile();
        String sshPassPhrase = basicDbConfig.getSshPassPhrase();

        //db basic
        String url = basicDbConfig.getUrl();
        String dbHost = getHost(basicDbConfig);
        Integer dbPort = getPort(basicDbConfig);
        String dbUserName = basicDbConfig.getUser();
        String dbPassword = basicDbConfig.getPassword();

        Session session = null;

        if (basicDbConfig.getUseSshTunnel()) {
            useSshTunnel = true;
            basicDbConfig.setUseSsl(true);
        }
        if (useSshTunnel) {
            if (basicDbConfig.getSshLocalPort() != null && basicDbConfig.getSshLocalPort() > 0) {
                localPort = basicDbConfig.getSshLocalPort();
            }

            //Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session = jsch.getSession(sshUser, sshHost, sshPort);
            if (sshAuthType.equalsIgnoreCase(Constants.SshAuthType.KEY_PAIR.toString())) {
                if (StringUtils.isBlank(sshKeyFile)) {
                    throw new IllegalParameterException("ssh key file can not be null");
                }
                jsch.addIdentity(sshKeyFile, sshPassPhrase);
            } else {
                session.setPassword(sshPassword);
            }
            session.setConfig(config);
            session.connect();
            LOGGER.info("Connected");
            int assinged_port = session.setPortForwardingL(localPort, dbHost, dbPort);
            LOGGER.info("localhost:" + assinged_port + " -> " + dbHost + ":" + dbPort);
            LOGGER.info("Port Forwarded");
            url = getSshUrl(basicDbConfig, localPort);
        }

        switch (dbType.toUpperCase()) {
            case Constants.DATABASE_TYPE_SQLITE:
                driverName="org.sqlite.JDBC";
                break;
            case Constants.DATABASE_TYPE_MYSQL:
                //驱动
                driverName = "com.mysql.jdbc.Driver";
                break;
            case Constants.DATABASE_TYPE_POSTGRESQL:
                driverName = "org.postgresql.Driver";
                break;
            case Constants.DATABASE_TYPE_SQLSERVER:
                driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                break;
            case Constants.DATABASE_TYPE_ORACLE:
                driverName = "oracle.jdbc.driver.OracleDriver";
                break;
            default:
                driverName = "org.sqlite.JDBC";
        }

        //mysql database connectivity
        Class.forName(driverName).newInstance();

        Properties properties = new Properties();
        properties.setProperty("Url", url);
        properties.setProperty("User", StringUtils.isBlank(dbUserName)?"":dbUserName);
        properties.setProperty("Password", StringUtils.isBlank(dbPassword)?"":dbPassword);
        properties.setProperty("Driver",driverName);

        DataSource datasource = DataSourcePoolUtils.createDatasource(properties);
        LOGGER.info("D1 Core Init>>>D1 Core Basic Database connection established");
        LOGGER.info("D1 Core Init>>>DONE");
        return datasource;

    }



    private String getSshUrl(BasicDbConfig basicDbConfig, int localPort) {
        String url=basicDbConfig.getUrl();
        if(basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_ORACLE)){
            String[] split = url.split("@");
            String hostAndPort="@localhost:"+localPort+":";
            String  newUrl=split[0]+hostAndPort+split[1].split(":")[2];
            LOGGER.info("newUrl:{}",newUrl);
            return  newUrl;
        }else if(basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_SQLSERVER)){
            String[] split = url.split("//");
            String hostAndPort="//localhost:"+localPort+";";
            String  newUrl=split[0]+hostAndPort+split[1].split(";")[1];
            LOGGER.info("newUrl:{}",newUrl);
            return newUrl;
        }else{
            String[] split = url.split("//");
            String hostAndPort="//localhost:"+localPort+"/";
            String  newUrl=split[0]+hostAndPort+split[1].split("/")[1];
            LOGGER.info("newUrl:{}",newUrl);
            return newUrl;
        }

    }

    /**
     * 从url中提取端口
     * @param basicDbConfig
     * @return
     */
    private Integer getPort(BasicDbConfig basicDbConfig) throws ServiceException {
        String url = basicDbConfig.getUrl();
        String[] split = url.split(":");
        if(basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_ORACLE)){
            String portStr = split[4];
            if(StringUtils.isNotBlank(portStr)){
                return Integer.parseInt(portStr);
            }else {
                throw new ServiceException("init failed,can not find port from jdbc url");
            }
        }else if(basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_MYSQL)
                ||basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_POSTGRESQL)
                ){
            String portStr = split[3];
            portStr=portStr.split("/")[0];
            if(StringUtils.isNotBlank(portStr)){
                return Integer.parseInt(portStr);
            }else {
                throw new ServiceException("init failed,can not find port from jdbc url");
            }

        }else if(basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_SQLSERVER)){
            String portStr = split[3];
            portStr=portStr.split(";")[0];
            if(StringUtils.isNotBlank(portStr)){
                return Integer.parseInt(portStr);
            }else {
                throw new ServiceException("init failed,can not find port from jdbc url");
            }

        }
        return null;
    }

    /**
     * 从url中提取host地址
     * @param basicDbConfig
     * @return
     */
    private String getHost(BasicDbConfig basicDbConfig) throws ServiceException {

        String url = basicDbConfig.getUrl();
        String[] split = url.split(":");
        if(basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_ORACLE)){
            String hostStr = split[3];
            hostStr=hostStr.substring(1,hostStr.length());
            if(StringUtils.isNotBlank(hostStr)){
                return hostStr;
            }else {
                throw new ServiceException("init failed,can not find port from jdbc url");
            }
        }else if(basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_MYSQL)
                ||basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_POSTGRESQL)
                ||basicDbConfig.getType().equalsIgnoreCase(Constants.DATABASE_TYPE_SQLSERVER)) {
            String hostStr = split[2];
            hostStr = hostStr.substring(2, hostStr.length());
            if (StringUtils.isNotBlank(hostStr)) {
                return hostStr;
            } else {
                throw new ServiceException("init failed,can not find port from jdbc url");
            }
        }

        return null;
    }

    /**
     * 获取端口
     *
     * @return
     * @throws IOException
     */
    private Integer findRandomOpenPort() throws IOException {
        try (
                ServerSocket socket = new ServerSocket(0)
        ) {
            return socket.getLocalPort();
        }
    }



}
