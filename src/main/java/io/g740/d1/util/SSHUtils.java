package io.g740.d1.util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/12 11:29
 * @description :
 */
public class SSHUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSHUtils.class);


    /**
     * 创建SSH Tunnel
     *
     * @param sshUser
     * @param password
     * @param sshHost
     * @param sshPort
     * @param targetHost
     * @param targetPort
     * @param localPort
     * @param properties
     * @return
     * @throws Exception
     */
    public int createSSHTunnel(String sshUser, String password, String sshHost, int sshPort,
                               String targetHost, int targetPort, int localPort,
                               Properties properties) throws Exception {
        properties.put("StrictHostKeyChecking", "no");
        JSch jSch = new JSch();
        final Session session = jSch.getSession(sshUser, sshHost, sshPort);
        session.setPassword(password);
        session.setConfig(properties);
        return session.setPortForwardingL(localPort, targetHost, targetPort);
    }

    public int createSSHTunnel(String sshUser, String password, String sshHost, int sshPort,
                               String targetHost, int targetPort,
                               Properties properties) throws Exception {
        properties.put("StrictHostKeyChecking", "no");
        JSch jSch = new JSch();
        final Session session = jSch.getSession(sshUser, sshHost, sshPort);
        session.setPassword(password);
        session.setConfig(properties);
        int localPort = findRandomOpenPort();
        return session.setPortForwardingL(localPort, targetHost, targetPort);
    }

    /**
     * 创建Tunnel
     *
     * @param sshUser
     * @param sshHost
     * @param sshKeyFilePath
     * @param sshPassPhrase
     * @param sshPort
     * @param targetHost
     * @param targetPort
     * @param localPort
     * @param properties
     * @return
     * @throws JSchException
     */
    public int createSSHTunnel(String sshUser, String sshHost, String sshKeyFilePath,
                               String sshPassPhrase, int sshPort,
                               String targetHost, int targetPort, int localPort,
                               Properties properties) throws Exception {
        properties.put("StrictHostKeyChecking", "no");
        JSch jSch = new JSch();
        final Session session = jSch.getSession(sshUser, sshHost, sshPort);
        jSch.addIdentity(sshKeyFilePath, sshPassPhrase);
        session.setConfig(properties);
        return session.setPortForwardingL(localPort, targetHost, targetPort);
    }

    public int createSSHTunnel(String sshUser, String sshHost, String sshKeyFilePath,
                               String sshPassPhrase, int sshPort,
                               String targetHost, int targetPort,
                               Properties properties) throws Exception {
        properties.put("StrictHostKeyChecking", "no");
        JSch jSch = new JSch();
        final Session session = jSch.getSession(sshUser, sshHost, sshPort);
        jSch.addIdentity(sshKeyFilePath, sshPassPhrase);
        session.setConfig(properties);
        int localPort = findRandomOpenPort();
        return session.setPortForwardingL(localPort, targetHost, targetPort);
    }

    public int createSSHTunnel(String sshUser, String sshHost, InputStream sshKeyInputStream,
                               String sshPassPhrase, int sshPort,
                               String targetHost, int targetPort,
                               Properties properties) throws Exception {
        properties.put("StrictHostKeyChecking", "no");
        JSch jSch = new JSch();
        final Session session = jSch.getSession(sshUser, sshHost, sshPort);
        final List<String> lines = IOUtils.readLines(sshKeyInputStream, StandardCharsets.UTF_8.name());
        final Path path = Files.createTempFile("ssh-key" + UUID.randomUUID().toString(), "rsa");
        Files.write(path, lines);
        jSch.addIdentity(path.toAbsolutePath().toString(), sshPassPhrase);
        session.setConfig(properties);
        int localPort = findRandomOpenPort();
        return session.setPortForwardingL(localPort, targetHost, targetPort);
    }


    /**
     * 获取随机端口
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
