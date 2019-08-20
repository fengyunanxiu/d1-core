package io.g740.d1.datasource.impl;

import io.g740.d1.constant.DsConstants;
import io.g740.d1.dao.DbSecurityConfigDao;
import io.g740.d1.datasource.ConnectionService;
import io.g740.d1.datasource.Constants;
import io.g740.d1.dto.DbBasicConfigDTO;
import io.g740.d1.dto.DbSecurityConfigDTO;
import io.g740.d1.entity.DbSecurityConfigDO;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.exception.custom.IllegalParameterException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.ServerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.UUID;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/11 10:17
 * @description:
 * @version: V1.0
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConnectionServiceImpl.class);

    @Resource(name="DbSecurityConfigDao")
    private DbSecurityConfigDao dbSecurityConfigDao;

    @Override
    public void createConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception {
        boolean connectResult = false;
        //数据库类型
        String dbType = dbBasicConfigDTO.getDbType();
        //驱动包名称
        String driverName = "";
        if (StringUtils.isBlank(dbType)) {
            throw new IllegalParameterException("db type can not be null!");
        }

        boolean useSshTunnel = false;
        if (dbSecurityConfigDTO == null) {
            dbSecurityConfigDTO = new DbSecurityConfigDTO();
        }
        //ssh
        int localPort = findRandomOpenPort();
        String sshUser = dbSecurityConfigDTO.getSshProxyUser();
        String sshPassword = dbSecurityConfigDTO.getSshProxyPassword();
        String sshHost = dbSecurityConfigDTO.getSshProxyHost();
        Integer sshPort = dbSecurityConfigDTO.getSshProxyPort();
        String sshAuthType = StringUtils.isBlank(dbSecurityConfigDTO.getSshAuthType()) ? Constants.SshAuthType.PASSWORD.toString() : dbSecurityConfigDTO.getSshAuthType();
        String sshKeyFile = dbSecurityConfigDTO.getSshKeyFile();
        String sshPassPhrase = dbSecurityConfigDTO.getSshPassPhrase();

        //db basic
        String dbHost = dbBasicConfigDTO.getDbHost();
        Integer dbPort = dbBasicConfigDTO.getDbPort();
        String dbUserName = dbBasicConfigDTO.getDbUser();
        String dbPassword = dbBasicConfigDTO.getDbPassword();
        String url = "";
        Connection conn = null;
        Session session = null;

        if (dbSecurityConfigDTO.getUseSshTunnel()) {
            useSshTunnel = true;
        }

        try {
            if (useSshTunnel) {
                if (dbSecurityConfigDTO.getSshLocalPort() != null && dbSecurityConfigDTO.getSshLocalPort() > 0) {
                    localPort = dbSecurityConfigDTO.getSshLocalPort();
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
                    String sshKeyFilePath= generateSshKeyFile(dbBasicConfigDTO.getId(), sshKeyFile, sshKeyFile);
                    jsch.addIdentity(sshKeyFilePath, sshPassPhrase);
                } else {
                    session.setPassword(sshPassword);
                }
                session.setConfig(config);
                session.connect();
                LOGGER.info("Connected");
                int assinged_port = session.setPortForwardingL(localPort, dbHost, dbPort);
                LOGGER.info("localhost:" + assinged_port + " -> " + dbHost + ":" + dbPort);
                LOGGER.info("Port Forwarded");
            }

            String urlSuffix = DsConstants.urlSuffix;


            switch (dbType) {
                case Constants.DATABASE_TYPE_MYSQL:
                    //url
                    if (dbSecurityConfigDTO.getUseSsl() != null && dbSecurityConfigDTO.getUseSsl()) {
                        urlSuffix += "&useSSL=true";
                    } else {
                        urlSuffix += "&useSSL=false";
                    }
                    if (useSshTunnel) {
                        url = "jdbc:mysql://localhost:" + localPort + (StringUtils.isBlank(dbBasicConfigDTO.getDbUrl()) ? "" : ("/" + dbBasicConfigDTO.getDbUrl()));
                    } else {
                        url = "jdbc:mysql://" + dbHost + ":" + dbPort + (StringUtils.isBlank(dbBasicConfigDTO.getDbUrl()) ? "" : ("/" + dbBasicConfigDTO.getDbUrl()));
                    }
                    //驱动
                    driverName = "com.mysql.jdbc.Driver";
                    break;
                case Constants.DATABASE_TYPE_POSTGRESQL:
                    Map<String, String> otherParams = dbBasicConfigDTO.getOtherParams();
                    String database = otherParams.get("database");
                    if (StringUtils.isBlank(database)) {
                        throw new IllegalParameterException("database can not be null");
                    }
                    if (dbSecurityConfigDTO.getUseSsl() != null && dbSecurityConfigDTO.getUseSsl()) {
                        urlSuffix += "&sslmode=require";
                    }

                    if (useSshTunnel) {
                        url = "jdbc:postgresql://localhost:" + localPort + "/" + database + (StringUtils.isBlank(dbBasicConfigDTO.getDbUrl()) ? "" : (dbBasicConfigDTO.getDbUrl()));
                    } else {
                        url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + database + (StringUtils.isBlank(dbBasicConfigDTO.getDbUrl()) ? "" : (dbBasicConfigDTO.getDbUrl()));
                    }
                    driverName = "org.postgresql.Driver";
                    break;
                default:
                    driverName = "com.mysql.jdbc.Driver";
            }

            url += urlSuffix;
            //mysql database connectivity
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, dbUserName, dbPassword);

            LOGGER.info("Database connection established");
            LOGGER.info("DONE");
            if (conn != null) {
                connectResult = true;
            }
        } catch (Exception e) {
            LOGGER.error("error>>>" + e);
            throw new ServiceException(e.getMessage());
        } finally {
            if (conn != null && !conn.isClosed()) {
                LOGGER.info("Closing Database Connection");
                conn.close();
            }

            if (session != null && session.isConnected()) {
                LOGGER.info("Closing SSH Connection");
                session.disconnect();
            }
        }
        if(!connectResult){
            throw  new ServiceException("");
        }

    }

    @Override
    public String generateSshKeyFile(Long dsId, String sshKeyFile, String keyText) throws Exception {

        File file = new File(sshKeyFile);
        //key文件存在则返回
        if(file.exists()){
           return file.getAbsolutePath();
        }

        if(StringUtils.isBlank(keyText)){
            throw new ServerException("ssh key content can not be null");
        }
        //否则根据文件内容创建文件
        String keyFilePath="";
        String projectPath = System.getProperty("user.dir");
        String savePath=projectPath+File.separator+"UploadFile";
        FileWriter fileWriter=null;
        try{
            keyFilePath=savePath+File.separator+UUID.randomUUID()+"_rsa";
            fileWriter=new FileWriter(keyFilePath);
            fileWriter.write(keyText);
        }finally {
            if(fileWriter!=null){
                fileWriter.flush();
                fileWriter.close();
            }
        }
        //保存最新的key文件
        if(dsId!=null&&dsId>0){
            DbSecurityConfigDO dbSecurityConfigDO = dbSecurityConfigDao.findById(dsId);
            dbSecurityConfigDO.setSshKeyFile(keyFilePath);
            dbSecurityConfigDao.editDataSourceProperty(dbSecurityConfigDO);

        }
        return keyFilePath;
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
