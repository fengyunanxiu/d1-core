package ai.sparklabinc.datasource;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.*;


public class MySqlConnOverSSH {

	/**
	 * Java Program to connect to remote database through SSH using port forwarding
	 * @author Pankaj@JournalDev
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		int lport=3307;
	    String user="ruizhi.cao";
	    String password="zj7UPo7fXkh#f#CX3";
		String host="10.126.147.5";

		String rhost="b2b-prd-ipbm-ipbm-0.mysql.database.chinacloudapi.cn";
		int rport=3306;
	    String dbuserName = "pgadmin@b2b-prd-ipbm-ipbm-0";
        String dbpassword = "dNxFDpsr3E61";
        String dbName = "ipbm_yhis?useSSL=true&requireSSL=false&useUnicode=true&characterEncoding=UTF-8";
        String url = "jdbc:mysql://localhost:"+lport+"/"+dbName;
        String driverName="com.mysql.jdbc.Driver";
        Connection conn = null;
        Session session= null;
	    try{
	    	//Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
	    	java.util.Properties config = new java.util.Properties(); 
	    	config.put("StrictHostKeyChecking", "no");
	    	JSch jsch = new JSch();
	    	session=jsch.getSession(user, host, 22);
	    	session.setPassword(password);
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
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	if(conn != null && !conn.isClosed()){
	    		System.out.println("Closing Database Connection");
	    		conn.close();
	    	}

//	    	if(session !=null && session.isConnected()){
//	    		System.out.println("Closing SSH Connection");
//	    		session.disconnect();
//	    	}
	    }
	}

}