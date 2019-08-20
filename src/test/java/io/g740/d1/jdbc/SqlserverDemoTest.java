package io.g740.d1.jdbc;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.sql.*;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/29 11:19
 * @description:
 * @version: V1.0
 */
public class SqlserverDemoTest {

//
//        public static void main(String[] args) throws Exception {
//        //mysql database connectivity
//        String driverName = "oracle.jdbc.driver.OracleDriver";
//        String dbName = "orcl";
//        Integer dbPort = 1521;
//        String dbuserName = "scott";
//        String dbpassword = "tiger";
//        Connection connection = null;
//
//        String url = "jdbc:oracle:thin:@localhost:" + dbPort + "/" + dbName;
//        Class.forName(driverName).newInstance();
//        connection = DriverManager.getConnection(url, dbuserName, dbpassword);
//
//        String sql = "CREATE or replace VIEW ds_full_config_view as\n" +
//                "select t1.id,\n" +
//                "       t1.gmt_create,\n" +
//                "       t1.gmt_modified,\n" +
//                "       t1.db_type,\n" +
//                "       t1.db_name,\n" +
//                "       t1.db_host,\n" +
//                "       t1.db_port,\n" +
//                "       t1.db_user,\n" +
//                "       t1.db_password,\n" +
//                "       t1.db_url,\n" +
//                "       t1.other_params,\n" +
//                "       t2.gmt_create   as security_gmt_create,\n" +
//                "       t2.gmt_modified as security_gmt_modified,\n" +
//                "       t2.use_ssl,\n" +
//                "       t2.use_ssh_tunnel,\n" +
//                "       t2.ssl_ca_file,\n" +
//                "       t2.ssl_client_certificate_file,\n" +
//                "       t2.ssl_client_key_file,\n" +
//                "       t2.ssh_proxy_host,\n" +
//                "       t2.ssh_proxy_port,\n" +
//                "       t2.ssh_proxy_user,\n" +
//                "       t2.ssh_local_port,\n" +
//                "       t2.ssh_auth_type,\n" +
//                "       t2.ssh_proxy_password,\n" +
//                "       t2.ssh_key_file,\n" +
//                "       t2.ssh_pass_phrase\n" +
//                "from db_basic_config t1\n" +
//                "         left outer join db_security_config t2\n" +
//                "                         on t1.id = t2.id\n";
//        PreparedStatement preparedStatement = connection.prepareStatement(sql);
//        preparedStatement.execute();
//    }


//    public static void main(String[] args) throws Exception {
//        //mysql database connectivity
//        String driverName = "oracle.jdbc.driver.OracleDriver";
//        String dbName = "orcl";
//        Integer dbPort = 1521;
//        String dbuserName = "aa";
//        String dbpassword = "aa";
//        Connection connection = null;
//
//        String url = "jdbc:oracle:thin:@localhost:" + dbPort + "/" + dbName;
//        Class.forName(driverName).newInstance();
//        connection = DriverManager.getConnection(url, dbuserName, dbpassword);
//
//        String sql = "SELECT name from scott.tb_test where  birthday = ?";
//        String pageSql = PagerUtils.limit(sql, JdbcConstants.ORACLE, 1, 2);
//        System.out.println(pageSql);
//        PreparedStatement preparedStatement = connection.prepareStatement(pageSql);
//        preparedStatement.setObject(1, java.sql.Timestamp.valueOf("2019-07-26 00:00:00"));
//        ResultSet resultSet = preparedStatement.executeQuery();
//        while (resultSet.next()) {
//            System.out.println(resultSet.getObject(1));
//        }
//    }

//    public static void main(String[] args) throws Exception{
//        //mysql database connectivity
//        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//        String dbName = "db_test";
//        Integer dbPort = 1401;
//        String dbuserName = "sa";
//        String dbpassword = "Root12345678910";
//        Connection connection = null;
//
//        String url = "jdbc:sqlserver://192.168.199.231:1401;DatabaseName=db_test";
//        Class.forName(driverName).newInstance();
//        connection = DriverManager.getConnection(url, dbuserName, dbpassword);
//        String sql="SELECT name from db_test.guest.tb_test where name like ? order by id,name asc";
//
//        String pageSql = PagerUtils.limit(sql,JdbcConstants.SQL_SERVER, 0, 2);
//        System.out.println("page:"+pageSql);
//        PreparedStatement preparedStatement = connection.prepareStatement(pageSql);
//        preparedStatement.setObject(1,"%修%");
//
//        ResultSet resultSet = preparedStatement.executeQuery();
//        while (resultSet.next()) {
//            System.out.println(resultSet.getObject(1));
//        }
//    }


    public static void main(String[] args) throws Exception{
        //mysql database connectivity
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbName = "db_test";
        Integer dbPort = 1401;
        String dbuserName = "sa";
        String dbpassword = "Root12345678910";
        Connection connection = null;

        String url = "jdbc:sqlserver://192.168.199.231:1401;DatabaseName=d1_core";
        Class.forName(driverName).newInstance();
        connection = DriverManager.getConnection(url, dbuserName, dbpassword);
        String sql="IF  EXISTS (select * from dbo.sysobjects where xtype='V' and Name = 'ds_full_config_view')\n" +
                " drop view ds_full_config_view go\n" +
                "CREATE VIEW ds_full_config_view as\n" +
                "select t1.id,\n" +
                "       t1.gmt_create,\n" +
                "       t1.gmt_modified,\n" +
                "       t1.db_type,\n" +
                "       t1.db_name,\n" +
                "       t1.db_host,\n" +
                "       t1.db_port,\n" +
                "       t1.db_user,\n" +
                "       t1.db_password,\n" +
                "       t1.db_url,\n" +
                "       t1.other_params,\n" +
                "       t2.gmt_create   as security_gmt_create,\n" +
                "       t2.gmt_modified as security_gmt_modified,\n" +
                "       t2.use_ssl,\n" +
                "       t2.use_ssh_tunnel,\n" +
                "       t2.ssl_ca_file,\n" +
                "       t2.ssl_client_certificate_file,\n" +
                "       t2.ssl_client_key_file,\n" +
                "       t2.ssh_proxy_host,\n" +
                "       t2.ssh_proxy_port,\n" +
                "       t2.ssh_proxy_user,\n" +
                "       t2.ssh_local_port,\n" +
                "       t2.ssh_auth_type,\n" +
                "       t2.ssh_proxy_password,\n" +
                "       t2.ssh_key_file,\n" +
                "       t2.ssh_pass_phrase\n" +
                "from db_basic_config t1\n" +
                "         left outer join db_security_config t2\n" +
                "                         on t1.id = t2.id \n" +
                "                         ";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.execute();
    }

//    public static void main(String[] args) throws Exception{
//
//        String driverName="org.postgresql.Driver";
//        String dbName = "postgres";
//        Integer dbPort = 5432;
//        String dbuserName = "postgres";
//        String dbpassword = "123456";
//        Connection connection = null;
//
//
//        String url = "jdbc:postgresql://192.168.199.231:"+dbPort+"/"+dbName;
//        Class.forName(driverName).newInstance();
//        connection = DriverManager.getConnection(url, dbuserName, dbpassword);
//        String sql="SELECT name from tb_test";
//        String pageSql = PagerUtils.limit(sql, JdbcConstants.POSTGRESQL, 1, 2);
//        System.out.println("page:"+pageSql);
//
//        PreparedStatement preparedStatement = connection.prepareStatement(pageSql);
//        ResultSet resultSet = preparedStatement.executeQuery();
//        while (resultSet.next()) {
//            System.out.println(resultSet.getObject(1));
//        }
//    }


}
