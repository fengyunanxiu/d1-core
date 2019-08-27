package io.g740.d1.engine;

import io.g740.d1.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

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

    public List<Map<String, String>> execute(String jdbcUrl, String username, String password, String sql) {
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
        Statement statement = null;
        ResultSet resultSet = null;
        try  {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            List<Map<String, String>> result = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> rowMap = new LinkedHashMap<>();
                result.add(rowMap);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i);

//                    String columnTypeName = metaData.getColumnTypeName(i);
//                    switch (columnTypeName) {
//                        case "DATE":
//                        case "TIME":
//                        case "DATETIME":
//                        case "TIMESTAMP":
//                    }
                    Object object = resultSet.getObject(i);
                    if (object != null) {
                        if (object instanceof Date) {
                            String s = DateUtils.ofLongStr((Date) object);
                            rowMap.put(columnLabel, s);
                        } else {
                            rowMap.put(columnLabel, object.toString());
                        }

                    }
//                    String columnValue = resultSet.getString(i);
                }
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

}
