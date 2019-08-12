package ai.sparklabinc.d1.datasource;

import ai.sparklabinc.d1.dao.DbBasicConfigDao;
import ai.sparklabinc.d1.dao.DbSecurityConfigDao;
import ai.sparklabinc.d1.datasource.impl.ConnectionServiceImpl;
import ai.sparklabinc.d1.entity.DbBasicConfigDO;
import ai.sparklabinc.d1.entity.DbSecurityConfigDO;
import ai.sparklabinc.d1.exception.custom.IllegalParameterException;
import ai.sparklabinc.d1.util.DataSourcePoolUtils;
import ai.sparklabinc.d1.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/2 20:08
 * @description:
 */
@Component
public class DataSourceFactory {
    public volatile ConcurrentHashMap<Long, Session> sshSessionMap = new ConcurrentHashMap<>();
    public volatile ConcurrentHashMap<Long, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    @Resource(name = "DbBasicConfigDao")
    private DbBasicConfigDao dbBasicConfigDao;

    @Resource(name = "DbSecurityConfigDao")
    private DbSecurityConfigDao dbSecurityConfigDao;

    @Autowired
    private ConnectionService connectionService;

    public DataSource builder(String dbType, Long dsId) throws Exception {
        /***************************************************************
         *step1 先判断是否创建了DataSource，没有则获取是sqlite中的数据源配置信息
         ***************************************************************
         */
        //获取本地开放的端口
        boolean useSshTunnel = false;
        int localPort = findRandomOpenPort();
        String sshUser = "";
        String sshPassword = "";
        String sshHost = "";
        int sshPort = 22;
        String sshAuthType = Constants.SshAuthType.PASSWORD.toString();
        String sshKeyFile = "";
        String sshKeyContent = "";
        String sshPassPhrase = "";

        String dbHost = "";
        int dbPort = 3306;
        String dbUserName = "";
        String dbPassword = "";
        String url = "";
        if (dataSourceMap.get(dsId) == null) {
            DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dsId);
            DbSecurityConfigDO dbSecurityConfigDO = dbSecurityConfigDao.findById(dsId);

            if (dbSecurityConfigDO != null) {
                useSshTunnel = dbSecurityConfigDO.getUseSshTunnel();
                if (dbSecurityConfigDO.getSshLocalPort() != null && dbSecurityConfigDO.getSshLocalPort() > 0) {
                    localPort = dbSecurityConfigDO.getSshLocalPort();
                }
                sshUser = dbSecurityConfigDO.getSshProxyUser();
                sshPassword = dbSecurityConfigDO.getSshProxyPassword();
                sshHost = dbSecurityConfigDO.getSshProxyHost();
                sshPort = dbSecurityConfigDO.getSshProxyPort();
                if (StringUtils.isNotNullNorEmpty(dbSecurityConfigDO.getSshAuthType())) {
                    sshAuthType = dbSecurityConfigDO.getSshAuthType();
                }
                sshKeyFile = dbSecurityConfigDO.getSshKeyFile();
                sshKeyContent = dbSecurityConfigDO.getSshKeyContent();
                sshPassPhrase = dbSecurityConfigDO.getSshPassPhrase();

            }

            if (dbBasicConfigDO == null) {
                return null;
            }

            dbHost = dbBasicConfigDO.getDbHost();
            dbPort = dbBasicConfigDO.getDbPort();
            dbUserName = dbBasicConfigDO.getDbUser();
            dbPassword = dbBasicConfigDO.getDbPassword();

        }


        /***************************************************************
         *step2 先判断是否配置了ssh的连接和是否存在已建立的ssh连接，否则去查数据源的ssh配置信息，建立ssh
         * 并放到内存中
         ***************************************************************
         */
        if (useSshTunnel && sshSessionMap.get(dsId) == null) {
            if (createSshSession(dsId, localPort, sshUser,
                    sshPassword, sshHost, sshPort,
                    dbHost, dbPort, sshKeyFile, sshKeyContent,
                    sshAuthType, sshPassPhrase)) {
                //创建失败
                return null;
            }

        }

        /***************************************************************
         *step3 先判断是否创建了DataSource，有就拿到现有的数据源，没有就建立
         * 并放到内存中
         ***************************************************************
         */
        if (dataSourceMap.get(dsId) == null) {
            return createDataSource(dsId, useSshTunnel, localPort, dbHost, dbPort, dbUserName, dbPassword, url);
        } else {
            return dataSourceMap.get(dsId);
        }

    }


    private boolean createSshSession(Long dsId, int localPort, String sshUser,
                                     String sshPassword, String sshHost, int sshPort,
                                     String dbHost, int dbPort, String sshKeyFile, String sshKeyContent,
                                     String sshAuthType, String sshPassPhrase) throws IOException {
        Session session;
        try {
            //Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session = jsch.getSession(sshUser, sshHost, sshPort);
            if (sshAuthType.equalsIgnoreCase(Constants.SshAuthType.KEY_PAIR.toString())) {
                if (org.apache.commons.lang3.StringUtils.isBlank(sshKeyFile)) {
                    throw new IllegalParameterException("ssh key file can not be null");
                }
                String sshKeyFilePath = connectionService.generateSshKeyFile(dsId, sshKeyFile, sshKeyContent);
                jsch.addIdentity(sshKeyFilePath, sshPassPhrase);
            } else {
                session.setPassword(sshPassword);
            }
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            int assinged_port = session.setPortForwardingL(localPort, dbHost, dbPort);
            System.out.println("localhost:" + assinged_port + " -> " + dbHost + ":" + dbPort);
            System.out.println("Port Forwarded");
            //session不等于null并且已经连接上
            if (session != null && session.isConnected()) {
                sshSessionMap.put(dsId, session);
            }
        } catch (Exception e) {
            System.out.println("error>>>>" + e.getMessage());
            throw new IOException(e.getMessage());
        }
        return false;
    }

    /**
     * 获取端口
     *
     * @return
     * @throws IOException
     */
    private Integer findRandomOpenPort() throws IOException {
        try (
                ServerSocket socket = new ServerSocket(0);
        ) {
            return socket.getLocalPort();

        }
    }

    /**
     * 创建数据源
     *
     * @param dsId
     * @param useSshTunnel
     * @param localPort
     * @param dbHost
     * @param dbPort
     * @param dbUserName
     * @param dbPassword
     * @param url
     * @return
     * @throws SQLException
     * @throws IOException
     */
    private DataSource createDataSource(Long dsId, boolean useSshTunnel, int localPort, String dbHost, int dbPort, String dbUserName, String dbPassword, String url) throws Exception {
        DataSource datasource = null;
        String driverName = "";
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dsId);
        DbSecurityConfigDO dbSecurityConfigDTO = dbSecurityConfigDao.findById(dsId);
        if (dbBasicConfigDO == null || StringUtils.isNullOrEmpty(dbBasicConfigDO.getDbType())) {
            throw new SQLException("database type can not be null");
        }
        switch (dbBasicConfigDO.getDbType()) {
            case Constants.DATABASE_TYPE_MYSQL:
                //url
                datasource = CreateMysqlDataSource(dsId, useSshTunnel, localPort, dbHost, dbPort, dbUserName, dbPassword, dbBasicConfigDO, dbSecurityConfigDTO);
                break;
            case Constants.DATABASE_TYPE_POSTGRESQL:
                datasource = createPostGresqlDataSource(dsId, useSshTunnel, localPort, dbHost, dbPort, dbUserName, dbPassword, dbBasicConfigDO, dbSecurityConfigDTO);
                break;
            default:
                return null;
        }
        return datasource;
    }

    private DataSource CreateMysqlDataSource(Long dsId, boolean useSshTunnel, int localPort, String dbHost, int dbPort, String dbUserName, String dbPassword, DbBasicConfigDO dbBasicConfigDO, DbSecurityConfigDO dbSecurityConfigDTO) throws Exception {
        String url;
        String driverName;
        DataSource datasource;
        if (useSshTunnel) {
            url = "jdbc:mysql://localhost:" + localPort + (org.apache.commons.lang3.StringUtils.isBlank(dbBasicConfigDO.getDbUrl()) ? "?" : (dbBasicConfigDO.getDbUrl()));
        } else {
            url = "jdbc:mysql://" + dbHost + ":" + dbPort + (org.apache.commons.lang3.StringUtils.isBlank(dbBasicConfigDO.getDbUrl()) ? "" : (dbBasicConfigDO.getDbUrl()));
        }
        if (dbSecurityConfigDTO.getUseSsl() != null && dbSecurityConfigDTO.getUseSsl()) {
            url += "&useSSL=true";
        } else {
            url += "&useSSL=false";
        }
        //驱动
        driverName = "com.mysql.jdbc.Driver";
        Properties mysqlProperties = new Properties();
        mysqlProperties.setProperty("Url", url);
        mysqlProperties.setProperty("User", dbUserName);
        mysqlProperties.setProperty("Password", dbPassword);
        datasource = DataSourcePoolUtils.createDatasource(mysqlProperties);
        dataSourceMap.put(dsId, datasource);
        return datasource;
    }


    private DataSource createPostGresqlDataSource(Long dsId, boolean useSshTunnel, int localPort, String dbHost, int dbPort, String dbUserName, String dbPassword, DbBasicConfigDO dbBasicConfigDO, DbSecurityConfigDO dbSecurityConfigDTO) throws Exception {
        String url;
        String driverName;
        DataSource datasource;
        String otherParams = dbBasicConfigDO.getOtherParams();
        if (StringUtils.isNullOrEmpty(otherParams)) {
            throw new SQLException("database can not be null");
        }
        Map otherParamsMap = JSON.parseObject(otherParams, Map.class);

        String database = otherParamsMap.get("database") + "";
        if (org.apache.commons.lang3.StringUtils.isBlank(database)) {
            throw new SQLException("database can not be null");
        }
        if (useSshTunnel) {
            url = "jdbc:postgresql://localhost:" + localPort + "/" + database + (org.apache.commons.lang3.StringUtils.isBlank(dbBasicConfigDO.getDbUrl()) ? "" : (dbBasicConfigDO.getDbUrl()));
        } else {
            url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + database + (org.apache.commons.lang3.StringUtils.isBlank(dbBasicConfigDO.getDbUrl()) ? "" : (dbBasicConfigDO.getDbUrl()));
        }

        if (dbSecurityConfigDTO.getUseSsl() != null && dbSecurityConfigDTO.getUseSsl()) {
            url += "&sslmode=require";
        }
        //驱动
        driverName = "org.postgresql.Driver";
        Properties postGresqlProperties = new Properties();
        postGresqlProperties.setProperty("Url", url);
        postGresqlProperties.setProperty("User", dbUserName);
        postGresqlProperties.setProperty("Password", dbPassword);
        datasource = DataSourcePoolUtils.createDatasource(postGresqlProperties);
        dataSourceMap.put(dsId, datasource);
        return datasource;
    }


}
