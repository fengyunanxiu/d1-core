package ai.sparklabinc.datasource.impl;

import ai.sparklabinc.datasource.ConnectionPoolService;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/2 19:45
 * @description:
 */

public class MysqlPoolServiceImpl implements ConnectionPoolService {

    @Override
    public DataSource createDatasource(Properties properties) throws SQLException {
        DataSource ds = null;
        PoolProperties p = new PoolProperties();
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername(properties.getProperty("User"));
        p.setPassword(properties.getProperty("Password"));
        p.setUrl((String) properties.get("Url"));
        // 初始化时获取100条连接
        p.setInitialSize(3);
        // 每60秒检查所有连接池中的空闲连接
        p.setValidationInterval(60);
        // 最大空闲时间,3600秒内未使用则连接被丢弃。若为0则永不丢弃
        p.setMaxIdle(100);
        p.setRollbackOnReturn(true);
        ds = new DataSource();
        ds.setPoolProperties(p);
        //校验datasource连接是够有效
        validDatasourceConnect(ds);

        return ds;
    }

    private void validDatasourceConnect(DataSource ds) throws SQLException {
        Connection connection=null;
        try {
            connection = ds.getConnection();
            if (connection == null) {
                throw new SQLException("data source create failed，because data source can't connect");
            }
        }finally {
            if(connection!=null){
                connection.close();
            }
        }
    }



}
