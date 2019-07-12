package ai.sparklabinc.datasource;

import com.jcraft.jsch.*;

/**
 * @function: ssh证书
 * @author:   dengam
 * @date:    2019/7/11 17:31
 * @param:   
 * @return:   
 */
public class TestKeyAcc { 
public static void main(String[] arg) { 

   String keyFile = "./id_rsa"; 
   String user = "username"; 
   String host = "127.0.0.1"; 
   String passphrase = "111111"; 
   int port = 22; 
   try { 
    JSch jsch = new JSch(); 
    jsch.addIdentity(keyFile); 

    Session session = jsch.getSession(user, host, port); 

    // username and passphrase will be given via UserInfo interface. 
    UserInfo ui = new MyUserInfo(passphrase); 
    session.setUserInfo(ui); 
    session.connect(); 

    Channel channel = session.openChannel("sftp");
    channel.connect(); 
    ChannelSftp sftp = (ChannelSftp) channel; 
    System.out.println(sftp.pwd()); 
   } catch (Exception e) { 
    e.printStackTrace(); 
    System.out.println(e); 
   } 
} 

public static class MyUserInfo implements UserInfo { 
   private String passphrase = null; 

   public MyUserInfo(String passphrase) { 
    this.passphrase = passphrase; 
   }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean promptPassword(String s) {
        return false;
    }

    @Override
    public boolean promptPassphrase(String s) {
        return false;
    }

    @Override
    public boolean promptYesNo(String s) {
        return false;
    }

    @Override
    public void showMessage(String s) {

    }
}
} 