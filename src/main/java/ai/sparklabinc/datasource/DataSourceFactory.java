package ai.sparklabinc.datasource;

import ai.sparklabinc.datasource.impl.SqlitePoolServiceImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/2 20:08
 * @description:
 */
@Component
public class DataSourceFactory {
    public final ConcurrentHashMap<String,Object> sshSessionMap=new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String,Object> connectionPoolMap=new ConcurrentHashMap<>();
    @Value("${sqlite.url}")
    private String sqliteURL;

    public DataSource builder(String dbType){
        if(dbType.equalsIgnoreCase(Constants.DATABASE_TYPE_SQLITE)){
            ConnectionPoolService sqlitePoolService = new SqlitePoolServiceImpl();
            Properties properties = new Properties();
            properties.setProperty("sqliteURL", this.sqliteURL);
            return sqlitePoolService.createDatasource(properties);
        }else {
            return null;
        }

    }

    public static void main(String[] args) {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        DataSource sqlite = dataSourceFactory.builder("SQLITE");
        try {
            Connection connection = sqlite.getConnection();
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
