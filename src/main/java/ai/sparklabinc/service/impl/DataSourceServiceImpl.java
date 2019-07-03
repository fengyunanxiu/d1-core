package ai.sparklabinc.service.impl;

import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.service.DataSourceService;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Function:
 * @Author: DAM
 * @Date: 2019/7/1 20:33
 * @Description:
 * @Version V1.0
 */

@Service
public class DataSourceServiceImpl implements DataSourceService {
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public boolean dataSourcesTestConnection(Long dsId) {
        Connection connection = null;
        try {
            DataSource sqlite = dataSourceFactory.builder(Constants.DATABASE_TYPE_MYSQL, dsId);
            connection = sqlite.getConnection();
            if(connection!=null){
                return true;
            }
        } catch (IOException e) {
            System.out.println("error>>>"+e.getMessage());
        } catch (SQLException e) {
            System.out.println("error>>>"+e.getMessage());
        }finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("error>>>>"+e.getMessage());
                }
            }
        }
        return false;
    }
}
