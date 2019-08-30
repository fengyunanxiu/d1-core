package io.g740.d1.sqlbuilder;

import io.g740.d1.util.DateUtils;
import io.g740.d1.util.UUIDUtils;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 11:00
 * @description :
 */
public class Insert<T, ID extends Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Insert.class);

    private Class<T> tClass;

    private Class<ID> idClass;

    public Insert() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if(genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            tClass = (Class<T>) actualTypeArguments[0];
            idClass = (Class<ID>) actualTypeArguments[1];
        }
    }

    public void insert(Class<T> clazz, T t, DataSource dataSource, boolean autoCommit) throws Exception {
        List<BeanParser.ColumnNode> columnNodeList = BeanParser.buildNode(clazz, t);
        String tableName = BeanParser.parseTableName(clazz);
        SQL sql = single(columnNodeList, tableName);
        if (sql == null) {
            LOGGER.error("SQL is null");
            return;
        }
        List<Object> fieldValueList = sql.getFieldValueList();
        LOGGER.info("sql :{}", sql.getSql());
        JdbcHelper.insert(dataSource, sql.getSql(), fieldValueList, autoCommit);
    }

    public void insert(Class<T> clazz, List<T> list, DataSource dataSource) throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }
        String tableName = BeanParser.parseTableName(clazz);
        StringBuilder sqlBuilder = new StringBuilder(" insert into " + tableName);
        List<Object> sqlParams = new ArrayList<>();
        List<String> valuesPlaceholderList = new ArrayList<>();
        List<String> fieldNameList = new ArrayList<>();
        StringBuilder valuePlaceholderBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            List<BeanParser.ColumnNode> columnNodeList = BeanParser.buildNode(clazz, t);
            List<String> valuePlaceholderList = new ArrayList<>();
            for (int j = 0; j < columnNodeList.size(); j++) {
                BeanParser.ColumnNode columnNode = columnNodeList.get(i);
                String columnName = columnNode.getColumnName();
                Object columnValue = columnNode.getColumnValue();
                sqlParams.add(columnValue);
                if (i == 0) {
                    // 获取field name
                    fieldNameList.add(columnName);
                }
                valuePlaceholderList.add("?");
            }
            valuesPlaceholderList.add("(" + String.join(",", valuePlaceholderList) + ")");
        }
        sqlBuilder.append("(").append(String.join(",", fieldNameList)).append(")")
                .append(" values ")
                .append(String.join(",", valuesPlaceholderList));
        LOGGER.info("save(List<T> list): {}", sqlBuilder.toString());

        JdbcHelper.insert(dataSource, sqlBuilder.toString(), sqlParams, true);
    }

    private SQL single(List<BeanParser.ColumnNode> columnNodeList, String tableName) {
        if (columnNodeList == null || columnNodeList.isEmpty()) {
            return null;
        }
        List<String> fieldNameList = new ArrayList<>();
        List<Object> fieldValueList = new ArrayList<>();
        List<String> fieldValuePlaceholderList = new ArrayList<>();
        for (int i = 0; i < columnNodeList.size(); i++) {
            BeanParser.ColumnNode columnNode = columnNodeList.get(i);
            boolean id = columnNode.isId();
            if (id) {
                GenerateType generateType = columnNode.getGenerateType();
                switch (generateType) {
                    case AUTO:
                        // 自增主键，insert 排除掉该字段
                        continue;
                    case UUID:
                        // 主键设置为uuid
                        columnNode.setColumnValue(UUIDUtils.compress());
                        break;
                    default:
                        break;
                }
            }
            fieldNameList.add(columnNode.getColumnName());
            Object fieldValue = columnNode.getColumnValue();
            fieldValueList.add(fieldValue);
            fieldValuePlaceholderList.add("?");
        }
        StringBuilder sql = new StringBuilder(" insert into ")
                .append(tableName)
                .append(" ( ")
                .append(String.join(",", fieldNameList))
                .append(" ) ")
                .append(" values ( ")
                .append(String.join(",", fieldValuePlaceholderList))
                .append(" ) ");

        return new SQL(sql.toString(), fieldNameList, fieldValueList);
    }




}
