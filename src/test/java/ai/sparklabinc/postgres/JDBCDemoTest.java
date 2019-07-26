package ai.sparklabinc.postgres;

import com.ctc.wstx.util.DataUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.Map;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/25 10:31
 * @description:
 * @version: V1.0
 */
public class JDBCDemoTest {

    public static void main(String[] args) throws Exception {
        //mysql database connectivity
        String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbName="db_test";
        Integer dbPort=1401;
        String dbuserName="sa";
        String dbpassword="Root12345678910";
        Connection connection=null;

        String url="jdbc:sqlserver://192.168.199.231:1401;DatabaseName=db_test";
        Class.forName(driverName).newInstance();
        connection = DriverManager.getConnection (url, dbuserName, dbpassword);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name from db_test.guest.tb_test where  money = ?");
        preparedStatement.setObject(1,"111.222");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            System.out.println(resultSet.getObject(1));
        }

//        public static void main(String[] args) throws Exception {
//            //mysql database connectivity
//            String driverName="oracle.jdbc.driver.OracleDriver";
//            String dbName="orcl";
//            Integer dbPort=1521;
//            String dbuserName="aa";
//            String dbpassword="aa";
//            Connection connection=null;
//
//            String url="jdbc:oracle:thin:@localhost:"+dbPort+"/"+dbName;
//            Class.forName(driverName).newInstance();
//            connection = DriverManager.getConnection (url, dbuserName, dbpassword);
//            PreparedStatement preparedStatement = connection.prepareStatement("SELECT name from db_test.guest.tb_test where  birthday = ?");
//            preparedStatement.setObject(1,java.sql.Timestamp.valueOf("2019-07-26 00:00:00"));
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()){
//                System.out.println(resultSet.getObject(1));
//            }



    }
}
