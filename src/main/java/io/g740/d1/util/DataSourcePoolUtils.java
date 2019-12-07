package io.g740.d1.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import io.g740.d1.exception.ServiceException;

import javax.sql.DataSource;
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


        Properties wholeProperties = new Properties();
        wholeProperties.put("username",properties.getProperty("User"));
        wholeProperties.put("password",properties.getProperty("Password"));
        wholeProperties.put("url",properties.getProperty("Url"));
        wholeProperties.put("driverClassName",properties.getProperty("Driver"));


        wholeProperties.put("testWhileIdle","false");
        wholeProperties.put("testOnBorrow","true");

        wholeProperties.put("testOnReturn","false");
        wholeProperties.put("timeBetweenEvictionRunsMillis","30000");
        wholeProperties.put("maxActive","3");
        wholeProperties.put("maxWait","10000");
        wholeProperties.put("initialSize","3");
        wholeProperties.put("maxIdle","10");
        wholeProperties.put("validationQuery","SELECT 1");
        wholeProperties.put("removeAbandonedTimeout","60");
        wholeProperties.put("removeAbandoned","true");
        wholeProperties.put("logAbandoned","true");
        wholeProperties.put("filters","stat");


        DataSource dataSource = DruidDataSourceFactory.createDataSource(wholeProperties);

        //p.setJmxEnabled(true);
       // p.setRemoveAbandonedTimeout(60);
       // p.setLogAbandoned(true);
        //p.setRemoveAbandoned(true);
//        p.setJdbcInterceptors(
//                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
//                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        // 每60秒检查所有连接池中的空闲连接
       // p.setValidationInterval(60);
//        p.setRollbackOnReturn(true);



        //校验datasource连接是够有效
        validDatasourceConnect(dataSource);
        return dataSource;
    }

    private static void validDatasourceConnect(DataSource ds) throws Exception {
        Connection connection=null;
        try {
            connection = ds.getConnection();
            if (connection == null) {
                //注销连接池
                ((DruidDataSource)ds).postDeregister();
                throw new ServiceException("data source create failed，because data source can't connect");
            }
        }finally {
            if(connection!=null){
                connection.close();
            }
        }
    }



}
