package ai.sparklabinc.jdbc;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
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

    public static void main(String[] args) throws Exception{
        //mysql database connectivity
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbName = "db_test";
        Integer dbPort = 1401;
        String dbuserName = "sa";
        String dbpassword = "Root12345678910";
        Connection connection = null;

        String url = "jdbc:sqlserver://192.168.199.231:1401;DatabaseName=db_test";
        Class.forName(driverName).newInstance();
        connection = DriverManager.getConnection(url, dbuserName, dbpassword);
        String sql="SELECT name from db_test.guest.tb_test where name = ? order by id,name asc";

        String pageSql = PagerUtils.limit(sql,JdbcConstants.SQL_SERVER, 0, 2);
        System.out.println("page:"+pageSql);
        PreparedStatement preparedStatement = connection.prepareStatement(pageSql);
        preparedStatement.setObject(1,"%张%");

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getObject(1));
        }
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
