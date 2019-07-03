package ai.sparklabinc;

import ai.sparklabinc.datasource.DataSourceFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class D1CoreApplicationTests {
    @Autowired
    private DataSourceFactory dataSourceFactory;


    @Test
    public void contextLoads() {
        DataSource sqlite = dataSourceFactory.builder("SQLITE");
        try {
            Connection connection = sqlite.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from  user_info");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                System.out.println(resultSet.getString(2));
            }
            if(connection!=null){
                System.out.println("连接成功");
            }else{
                System.out.println("连接失败");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
