package ai.sparklabinc.datasource;

import org.apache.tomcat.jdbc.pool.DataSource;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/2 19:43
 * @description:
 */
public interface ConnectionPoolService {

    DataSource createDatasource(Properties properties) throws SQLException;
}
