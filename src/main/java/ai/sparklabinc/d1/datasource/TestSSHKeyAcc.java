package ai.sparklabinc.d1.datasource;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * @function: ssh证书
 * @author: dengam
 * @date: 2019/7/11 17:31
 * @param:
 * @return:
 */
public class TestSSHKeyAcc {
    public static void main(String[] arg) {

        String keyFile = "C:\\Users\\DAM\\Desktop\\id_rsa";
        String user = "ubuntu";
        String host = "192.168.199.231";
        String passphrase = "123456";
        int lport=3307;


        String driverName="com.mysql.jdbc.Driver";
        String rhost="192.168.199.231";
        int rport=3306;
        String dbuserName = "root";
        String dbpassword = "FnJUC1XsShQp6cZb";
        String dbName = "common";
        String url = "jdbc:mysql://localhost:"+lport+"/"+dbName;
        Connection conn=null;
        Session session = null;

        int port = 22;
        try {
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            jsch.addIdentity(keyFile, passphrase);

            session = jsch.getSession(user, host, port);
            session.setConfig(config);
            session.connect();

            System.out.println("Connected");
            int assinged_port=session.setPortForwardingL(lport, rhost, rport);
            System.out.println("localhost:"+assinged_port+" -> "+rhost+":"+rport);
            System.out.println("Port Forwarded");


            //mysql database connectivity
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection (url, dbuserName, dbpassword);
            PreparedStatement preparedStatement = conn.prepareStatement("show databases");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                System.out.println(resultSet.getString(1));
            }

            System.out.println ("Database connection established");
            System.out.println("DONE");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            System.out.println(sftp.pwd());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

} 