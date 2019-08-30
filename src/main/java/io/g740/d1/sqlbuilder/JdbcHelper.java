package io.g740.d1.sqlbuilder;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/30 14:50
 * @description :
 */
public class JdbcHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcHelper.class);

    public static  <T> List<T> query(DataSource dataSource, String sql, List<?> sqlParamList, Class<T> entityClazz) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (sqlParamList != null) {
                for (int i = 0; i < sqlParamList.size(); i++) {
                    preparedStatement.setObject(i + 1, sqlParamList.get(i));
                }
            }
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<T> rowList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> rowMap = new HashMap<>();
                for (int i = 0; i < columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i + 1);
                    Object object = resultSet.getObject(columnLabel);
                    rowMap.put(columnLabel, object);
                }
                String jsonString = JSON.toJSONString(rowMap);
                T o = JSON.parseObject(jsonString, entityClazz);
                rowList.add(o);
            }
            return rowList;
        } catch (Exception e) {
            LOGGER.error("", e);
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    public static long count(DataSource dataSource, String sql, List<?> sqlParamList) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (sqlParamList != null) {
                for (int i = 0; i < sqlParamList.size(); i++) {
                    preparedStatement.setObject(i + 1, sqlParamList.get(i));
                }
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return 0;
    }

    public static void insert(DataSource dataSource, String sql, List<?> sqlParamList, boolean autoCommit) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            if (!autoCommit) {
                connection.setAutoCommit(false);
            }
            preparedStatement = connection.prepareStatement(sql);
            if (sqlParamList != null && !sqlParamList.isEmpty()) {
                for (int i = 0; i < sqlParamList.size(); i++) {
                    preparedStatement.setObject(i + 1, sqlParamList.get(i));
                }
            }
            preparedStatement.execute();
            // get generate keys
//            try {
//                resultSet = preparedStatement.getGeneratedKeys();
//                if (resultSet != null && resultSet.next()) {
//                    Object id = resultSet.getObject(1, );
//                }
//            } catch (SQLException e) {
//                LOGGER.error("", e);
//            }
            if (!autoCommit) {
                connection.commit();
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
            if (!autoCommit && connection != null) {
                connection.rollback();
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static <T> String buildQueryField(Class<T> clazz) {
        List<BeanParser.ColumnNode> columnNodeList = BeanParser.buildNode(clazz);
        if (columnNodeList == null || columnNodeList.isEmpty()) {
            return null;
        }
        StringBuilder sqlBuilder = new StringBuilder();
        for (int i = 0; i < columnNodeList.size(); i++) {
            BeanParser.ColumnNode columnNode = columnNodeList.get(i);
            String columnName = columnNode.getColumnName();
            String fieldName = columnNode.getFieldName();
            if (i == 0) {
                sqlBuilder.append(" ").append(columnName).append(" as ").append(fieldName);
            } else {
                sqlBuilder.append(", ").append(columnName).append(" as ").append(fieldName);
            }
        }
        return sqlBuilder.toString();
    }


}
