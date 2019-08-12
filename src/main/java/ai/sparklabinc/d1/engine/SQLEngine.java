package ai.sparklabinc.d1.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/8 16:49
 * @description :
 */
@Component
public class SQLEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLEngine.class);

    public List<Map<String, String>> execute(String jdbcUrl, String username, String password, String sql) throws Exception {
        try(Connection connection = getConnection(jdbcUrl, username, password)) {
            return executeSQL(connection, sql);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    private Connection getConnection(String jdbcUrl, String username, String password) throws Exception {
        if (jdbcUrl.startsWith("jdbc:mysql")) {
            Class.forName("com.mysql.jdbc.Driver");
        }
        if (jdbcUrl.startsWith("jdbc:postgresql")) {
            Class.forName("org.postgresql.Driver");
        }
        if (jdbcUrl.startsWith("jdbc:oracle")) {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        if (jdbcUrl.startsWith("jdbc:sqlserver")) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    private List<Map<String, String>> executeSQL(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);) {
            List<Map<String, String>> result = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> rowMap = new LinkedHashMap<>();
                result.add(rowMap);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    String columnValue = resultSet.getString(i);
                    rowMap.put(columnLabel, columnValue);
                }
            }
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

}
