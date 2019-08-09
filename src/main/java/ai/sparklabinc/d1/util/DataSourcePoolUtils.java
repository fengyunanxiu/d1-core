package ai.sparklabinc.d1.util;

import ai.sparklabinc.d1.exception.ServiceException;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import java.sql.Connection;
import java.util.Properties;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/2 19:45
 * @description:
 */
public class DataSourcePoolUtils {

    public static DataSource createDatasource(Properties properties) throws Exception {
        DataSource ds = null;
        PoolProperties p = new PoolProperties();
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(3);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        p.setDriverClassName(properties.getProperty("Driver"));
        p.setUsername(properties.getProperty("User"));
        p.setPassword(properties.getProperty("Password"));
        p.setUrl((String) properties.get("Url"));
        // 初始化时获取100条连接
        p.setInitialSize(3);
        // 每60秒检查所有连接池中的空闲连接
        p.setValidationInterval(60);
        // 最大空闲时间,3600秒内未使用则连接被丢弃。若为0则永不丢弃
        p.setMaxIdle(3);
        p.setRollbackOnReturn(true);
        ds = new DataSource();
        ds.setPoolProperties(p);
        //校验datasource连接是够有效
        validDatasourceConnect(ds);
        return ds;
    }

    private static void validDatasourceConnect(DataSource ds) throws Exception {
        Connection connection=null;
        try {
            connection = ds.getConnection();
            if (connection == null) {
                //注销连接池
                ds.postDeregister();
                throw new ServiceException("data source create failed，because data source can't connect");
            }
        }finally {
            if(connection!=null){
                connection.close();
            }
        }
    }



}