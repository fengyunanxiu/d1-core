package ai.sparklabinc.d1.component;//package ai.sparklabinc.component;
//
//
//import org.apache.tomcat.jdbc.pool.DataSource;
//import org.apache.tomcat.jdbc.pool.PoolProperties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//
///**
// * @author : Kingzer
// * @date : 2019-07-03 17:32
// * @description :
// */
//@Component
//public class DefaultDataSourceComponent {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataSourceComponent.class);
//
//    private DataSource dataSource;
//
//    @Value("${spring.datasource.driver-class-name}")
//    private String driverClassName;
//
//    @Value("${spring.datasource.url}")
//    private String url;
//
//    @Value("${spring.datasource.username}")
//    private String username;
//
//    @Value("${spring.datasource.password}")
//    private String password;
//
//    public DataSource getDataSource() {
//        if(dataSource != null){
//            return dataSource;
//        }else{
//            synchronized (DefaultDataSourceComponent.class){
//                dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
//
//                PoolProperties properties = new PoolProperties();
//                properties.setJmxEnabled(false);
//                properties.setTestWhileIdle(true);
//                properties.setTestOnBorrow(true);
//                properties.setTestOnReturn(true);
//                properties.setTestOnConnect(true);
//                /** 验证链接 **/
//                properties.setValidationQuery("SELECT 1 limit 0");
//                /** 验证查询的超时时间（秒）**/
//                properties.setValidationQueryTimeout(60);
//                properties.setTimeBetweenEvictionRunsMillis(5000);
//                properties.setMaxWait(10000);
//                /**丢失废弃连接**/
//                properties.setRemoveAbandoned(true);
//                /**超过指定秒数，将被认为废弃连接，并且丢掉，默认60s**/
//                properties.setRemoveAbandonedTimeout(300);
//                properties.setJdbcInterceptors(
//                        "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
//                                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
//                                + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
//
//                properties.setDriverClassName(driverClassName);
//                properties.setUrl(url);
//                properties.setUsername(username);
//                properties.setPassword(password);
//
//                dataSource.setPoolProperties(properties);
//                return dataSource;
//            }
//        }
//    }
//
//    public Connection getConnection() throws SQLException {
//        dataSource = getDataSource();
//        return dataSource.getConnection();
//    }
//
//    public void close() {
//        if (dataSource != null) {
//            dataSource.close(true);
//        }
//    }
//
//}
////package ai.sparklabinc.component;
////
////
////import org.apache.tomcat.jdbc.pool.DataSource;
////import org.apache.tomcat.jdbc.pool.PoolProperties;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.stereotype.Component;
////
////import java.sql.Connection;
////import java.sql.SQLException;
////
////@Component
////public class DefaultDataSourceComponent {
////
////    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataSourceComponent.class);
////
////    private DataSource dataSource;
////
////    @Value("${spring.datasource.driver-class-name}")
////    private String driverClassName;
////
////    @Value("${spring.datasource.url}")
////    private String url;
////
////    @Value("${spring.datasource.username}")
////    private String username;
////
////    @Value("${spring.datasource.password}")
////    private String password;
////
////    public DataSource getDataSource() {
////        if(dataSource != null){
////            return dataSource;
////        }else{
////            synchronized (DefaultDataSourceComponent.class){
////                dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
////
////                PoolProperties properties = new PoolProperties();
////                properties.setJmxEnabled(false);
////                properties.setTestWhileIdle(true);
////                properties.setTestOnBorrow(true);
////                properties.setTestOnReturn(true);
////                properties.setTestOnConnect(true);
////                /** 验证链接 **/
////                properties.setValidationQuery("SELECT 1 limit 0");
////                /** 验证查询的超时时间（秒）**/
////                properties.setValidationQueryTimeout(60);
////                properties.setTimeBetweenEvictionRunsMillis(5000);
////                properties.setMaxWait(10000);
////                /**丢失废弃连接**/
////                properties.setRemoveAbandoned(true);
////                /**超过指定秒数，将被认为废弃连接，并且丢掉，默认60s**/
////                properties.setRemoveAbandonedTimeout(300);
////                properties.setJdbcInterceptors(
////                        "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
////                                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
////                                + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
////
////                properties.setDriverClassName(driverClassName);
////                properties.setUrl(url);
////                properties.setUsername(username);
////                properties.setPassword(password);
////
////                dataSource.setPoolProperties(properties);
////                return dataSource;
////            }
////        }
////    }
////
////    public Connection getConnection() throws SQLException {
////        dataSource = getDataSource();
////        return dataSource.getConnection();
////    }
////
////    public void close() {
////        if (dataSource != null) {
////            dataSource.close(true);
////        }
////    }
////
////}
