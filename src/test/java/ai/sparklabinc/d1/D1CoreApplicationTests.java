//package ai.sparklabinc.d1;
//
//import ai.sparklabinc.d1.datasource.DataSourceFactory;
//import org.apache.tomcat.jdbc.pool.DataSource;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class D1CoreApplicationTests {
//    @Autowired
//    private DataSourceFactory dataSourceFactory;
//
//
//    @Test
//    public void contextLoads() {
//
//        try {
//            DataSource sqlite = dataSourceFactory.builder("SQLITE1",1L);
//            Connection connection = sqlite.getConnection();
//            PreparedStatement preparedStatement = connection.prepareStatement("show databases");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()){
//                System.out.println(resultSet.getString(1));
//            }
//            if(connection!=null){
//                System.out.println("连接成功");
//            }else{
//                System.out.println("连接失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//}
