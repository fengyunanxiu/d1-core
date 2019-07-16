package ai.sparklabinc.datasource.impl;

import ai.sparklabinc.constant.DsConstants;
import ai.sparklabinc.datasource.ConnectionService;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.dto.DbBasicConfigDTO;
import ai.sparklabinc.dto.DbSecurityConfigDTO;
import ai.sparklabinc.exception.custom.IllegalParameterException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/11 10:17
 * @description:
 * @version: V1.0
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Override
    public boolean createConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception {
        boolean connectResult = false;
        //数据库类型
        String dbType = dbBasicConfigDTO.getType();
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

        //db basic
        String dbHost = dbBasicConfigDTO.getHost();
        Integer dbPort = dbBasicConfigDTO.getPort();
        String dbUserName = dbBasicConfigDTO.getUser();
        String dbPassword = dbBasicConfigDTO.getPassword();
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
                session.setPassword(sshPassword);
                session.setConfig(config);
                session.connect();
                System.out.println("Connected");
                int assinged_port = session.setPortForwardingL(localPort, dbHost, dbPort);
                System.out.println("localhost:" + assinged_port + " -> " + dbHost + ":" + dbPort);
                System.out.println("Port Forwarded");

            }

            String urlSuffix = DsConstants.urlSuffix;
            if(useSshTunnel){
                if(dbSecurityConfigDTO.getUseSsl()!= null && dbSecurityConfigDTO.getUseSsl() ){
                    urlSuffix += "&useSSL=true";
                }else{
                    urlSuffix += "&useSSL=false";
                }
            }

            switch (dbType) {
                case Constants.DATABASE_TYPE_MYSQL:
                    //url
                    if (useSshTunnel) {
                        url = "jdbc:mysql://localhost:" + localPort + (dbBasicConfigDTO.getUrl() == null ? "" : ("/" + dbBasicConfigDTO.getUrl()));
                    } else {
                        url = "jdbc:mysql://" + dbHost + ":" + dbPort + (dbBasicConfigDTO.getUrl() == null ? "" : ("/" + dbBasicConfigDTO.getUrl()));
                    }
                    //驱动
                    driverName = "com.mysql.jdbc.Driver";
                    break;
                case Constants.DATABASE_TYPE_ORACLE:
                    if (useSshTunnel) {
                        url = "jdbc:mysql://localhost:" + localPort + (dbBasicConfigDTO.getUrl() == null ? "" : ("/" + dbBasicConfigDTO.getUrl()));
                    } else {
                        url = dbBasicConfigDTO.getUrl();
                    }
                    driverName = "oracle.jdbc.driver.OracleDriver";
                    break;
                case Constants.DATABASE_TYPE_SQLSERVER:
                    if (useSshTunnel) {
                        url = "jdbc:mysql://localhost:" + localPort + (dbBasicConfigDTO.getUrl() == null ? "" : ("/" + dbBasicConfigDTO.getUrl()));
                    } else {
                        url = dbBasicConfigDTO.getUrl();
                    }
                    driverName = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
                    break;
                case Constants.DATABASE_TYPE_SQLITE:
                    if (useSshTunnel) {
                        url = "jdbc:mysql://localhost:" + localPort + (dbBasicConfigDTO.getUrl() == null ? "" : ("/" + dbBasicConfigDTO.getUrl()));
                    } else {
                        url = dbBasicConfigDTO.getUrl();
                    }
                    driverName = "org.sqlite.JDBC";
                    break;
                case Constants.DATABASE_TYPE_POSTGRESQL:
                    if (useSshTunnel) {
                        url = "jdbc:mysql://localhost:" + localPort + (dbBasicConfigDTO.getUrl() == null ? "" : ("/" + dbBasicConfigDTO.getUrl()));
                    } else {
                        url = dbBasicConfigDTO.getUrl();
                    }
                    driverName = "org.postgresql.Driver";
                    break;
                default:
                    driverName = "com.mysql.jdbc.Driver";
            }

            url += urlSuffix;
            //mysql database connectivity
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(url, dbUserName, dbPassword);
//            PreparedStatement preparedStatement = conn.prepareStatement("show databases");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                System.out.println(resultSet.getString(1));
//            }
            System.out.println("Database connection established");
            System.out.println("DONE");
            if (conn != null) {
                connectResult = true;
            }
        } catch (Exception e) {
            System.out.println("error>>>" + e);
        } finally {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Closing Database Connection");
                conn.close();
            }

            if (session != null && session.isConnected()) {
                System.out.println("Closing SSH Connection");
                session.disconnect();
            }
        }
        return connectResult;
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
