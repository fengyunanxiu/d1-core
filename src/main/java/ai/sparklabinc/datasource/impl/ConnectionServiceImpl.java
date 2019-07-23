package ai.sparklabinc.datasource.impl;

import ai.sparklabinc.constant.DsConstants;
import ai.sparklabinc.datasource.ConnectionService;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.dto.DbBasicConfigDTO;
import ai.sparklabinc.dto.DbSecurityConfigDTO;
import ai.sparklabinc.exception.custom.IllegalParameterException;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

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
        String sshAuthType = StringUtils.isBlank(dbSecurityConfigDTO.getSshAuthType()) ? Constants.SshAuthType.PASSWORD.toString() : dbSecurityConfigDTO.getSshAuthType();
        String sshKeyFile = dbSecurityConfigDTO.getSshKeyFile();
        String sshPassPhrase = dbSecurityConfigDTO.getSshPassPhrase();

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
                if (sshAuthType.equalsIgnoreCase(Constants.SshAuthType.KEY_PAIR.toString())) {
                    if (org.apache.commons.lang3.StringUtils.isBlank(sshKeyFile)) {
                        throw new IllegalParameterException("ssh key file can not be null");
                    }
                    jsch.addIdentity(sshKeyFile, sshPassPhrase);
                } else {
                    session.setPassword(sshPassword);
                }
                session.setPassword(sshPassword);
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
                    if (useSshTunnel) {
                        if (dbSecurityConfigDTO.getUseSsl() != null && dbSecurityConfigDTO.getUseSsl()) {
                            urlSuffix += "&useSSL=true";
                        } else {
                            urlSuffix += "&useSSL=false";
                        }
                        url = "jdbc:mysql://localhost:" + localPort + (StringUtils.isBlank(dbBasicConfigDTO.getUrl()) ? "" : ("/" + dbBasicConfigDTO.getUrl()));
                    } else {
                        url = "jdbc:mysql://" + dbHost + ":" + dbPort + (StringUtils.isBlank(dbBasicConfigDTO.getUrl()) ? "" : ("/" + dbBasicConfigDTO.getUrl()));
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
                        url = "jdbc:postgresql://localhost:" + localPort + "/" + database + (StringUtils.isBlank(dbBasicConfigDTO.getUrl()) ? "" : (dbBasicConfigDTO.getUrl()));
                    } else {
                        url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + database + (StringUtils.isBlank(dbBasicConfigDTO.getUrl()) ? "" : (dbBasicConfigDTO.getUrl()));
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

            LOGGER.info("Database connection established");
            LOGGER.info("DONE");
            if (conn != null) {
                connectResult = true;
            }
        } catch (Exception e) {
            LOGGER.error("error>>>" + e);
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
