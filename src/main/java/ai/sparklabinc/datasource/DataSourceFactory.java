package ai.sparklabinc.datasource;

import ai.sparklabinc.dao.DbBasicConfigDao;
import ai.sparklabinc.dao.DbSecurityConfigDao;
import ai.sparklabinc.datasource.impl.MysqlPoolServiceImpl;
import ai.sparklabinc.datasource.impl.SqlitePoolServiceImpl;
import ai.sparklabinc.entity.DbBasicConfigDO;
import ai.sparklabinc.entity.DbSecurityConfigDO;
import ai.sparklabinc.exception.custom.IllegalParameterException;
import ai.sparklabinc.util.StringUtils;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
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
    @Value("${sqlite.url}")
    private String sqliteURL;

    @Autowired
    private DbBasicConfigDao dbBasicConfigDao;

    @Autowired
    private DbSecurityConfigDao dbSecurityConfigDao;

    public DataSource builder(String dbType, Long dsId) throws IOException, SQLException {
        if (dbType.equalsIgnoreCase(Constants.DATABASE_TYPE_SQLITE)) {
            ConnectionPoolService sqlitePoolService = new SqlitePoolServiceImpl();
            Properties properties = new Properties();
            properties.setProperty("sqliteURL", this.sqliteURL);
            return sqlitePoolService.createDatasource(properties);
        } else {
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
            String sshAuthType=Constants.SshAuthType.PASSWORD.toString();
            String sshKeyFile="";
            String sshPassPhrase="";

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
                    sshAuthType = dbSecurityConfigDO.getSshAuthType();
                    sshKeyFile = dbSecurityConfigDO.getSshKeyFile();
                    sshPassPhrase=dbSecurityConfigDO.getSshPassPhrase();

                }

                if (dbBasicConfigDO == null) {
                    return null;
                }

                dbHost = dbBasicConfigDO.getHost();
                dbPort = dbBasicConfigDO.getPort();
                dbUserName = dbBasicConfigDO.getUser();
                dbPassword = dbBasicConfigDO.getPassword();

                if (useSshTunnel) {
                    url = "jdbc:mysql://localhost:" + localPort + "/" + dbBasicConfigDO.getUrl();
                } else {
                    url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbBasicConfigDO.getUrl();
                }
            }


            /***************************************************************
             *step2 先判断是否配置了ssh的连接和是否存在已建立的ssh连接，否则去查数据源的ssh配置信息，建立ssh
             * 并放到内存中
             ***************************************************************
             */
            if (useSshTunnel && sshSessionMap.get(dsId) == null) {
                if (createSshSession(dsId, localPort, sshUser,
                        sshPassword, sshHost, sshPort,
                        dbHost, dbPort,sshKeyFile,
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
                ConnectionPoolService mysqlPoolService = new MysqlPoolServiceImpl();
                Properties properties = new Properties();
                properties.setProperty("Url", url);
                properties.setProperty("User", dbUserName);
                properties.setProperty("Password", dbPassword);
                DataSource datasource = mysqlPoolService.createDatasource(properties);
                dataSourceMap.put(dsId, datasource);
                return datasource;
            } else {
                return dataSourceMap.get(dsId);
            }
        }

    }

    private boolean createSshSession(Long dsId, int localPort, String sshUser,
                                     String sshPassword, String sshHost, int sshPort,
                                     String dbHost, int dbPort, String sshKeyFile,
                                     String sshAuthType,String sshPassPhrase) {
        Session session;
        try {
            //Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session = jsch.getSession(sshUser, sshHost, sshPort);
            if(sshAuthType.equalsIgnoreCase(Constants.SshAuthType.KEY_PAIR.toString())){
                if(org.apache.commons.lang3.StringUtils.isBlank(sshKeyFile)){
                    throw new IllegalParameterException("ssh key file can not be null");
                }
                jsch.addIdentity(sshKeyFile,sshPassPhrase);
            }else {
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
            return true;
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


}
