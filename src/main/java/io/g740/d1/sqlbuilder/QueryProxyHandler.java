package io.g740.d1.sqlbuilder;

import com.alibaba.fastjson.JSON;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.util.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 16:45
 * @description :
 */
public class QueryProxyHandler<T> implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryProxyHandler.class);

    private DataSource d1BasicDataSource;

    private Class<T> entityClazz;

    public QueryProxyHandler(DataSource d1BasicDataSource, Class<T> entityClazz) {
        this.d1BasicDataSource = d1BasicDataSource;
        this.entityClazz = entityClazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        BeanSqlSegment beanSqlSegment = parseMethod(name, entityClazz);
        String sql = beanSqlSegment.getSql();
        List<Type.Java> paramJavaType = beanSqlSegment.getParamJavaType();
        if (paramJavaType.size() != args.length) {
            throw new Exception("params number error");
        }
        List<Object> sqlParamList = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            Object sqlParam = args[i];
            Type.Java java = paramJavaType.get(i);
            sqlParam = Type.typeTransform(java, sqlParam);
            sqlParamList.add(sqlParam);
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.d1BasicDataSource.getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < sqlParamList.size(); i++) {
                preparedStatement.setObject(i + 1, sqlParamList.get(i));
            }
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<Object> rowList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> rowMap = new HashMap<>();
                for (int i = 0; i < columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i + 1);
                    Object object = resultSet.getObject(columnLabel);
                    rowMap.put(columnLabel, object);
                }
                String jsonString = JSON.toJSONString(rowMap);
                Object o = JSON.parseObject(jsonString, entityClazz);
                rowList.add(o);
            }
            connection.commit();
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

    public  BeanSqlSegment parseMethod(String source, Class<T> clazz) throws Exception {
        PartTree partTree = new PartTree(source, clazz);
        Iterator<PartTree.OrPart> orPartIterator = partTree.iterator();
        BeanSqlSegment beanSqlSegment = parseOrPart(orPartIterator, clazz);
        Sort sort = partTree.getSort();
        String sortSql = parseSort(sort, clazz);
        String partSql = beanSqlSegment.getSql();

        Field[] declaredFields = clazz.getDeclaredFields();
        StringBuilder filedSqlBuilder = new StringBuilder();
        int i = 0;
        for (Field declaredField : declaredFields) {
            Column annotation = declaredField.getAnnotation(Column.class);
            if (annotation == null) {
                // 必须要有Column注解
                continue;
            }
            String fieldName = declaredField.getName();
            String columnName = fieldName;
            columnName = annotation.value();
            if (i == 0) {
                filedSqlBuilder.append(columnName).append(" as ").append(fieldName);
            } else {
                filedSqlBuilder.append(",").append(columnName).append(" as ").append(fieldName);
            }
            i++;
        }

        String sql = "select " + filedSqlBuilder + " from " + BeanParser.parseTableName(clazz);
        if (StringUtils.isNotNullNorEmpty(partSql)) {
            sql += " where " + partSql;
        }
        if (StringUtils.isNotNullNorEmpty(sortSql)) {
            sql += " " + sortSql;
        }
        beanSqlSegment.setSql(sql);
        return beanSqlSegment;
    }

    private BeanSqlSegment parsePart(Iterator<Part> iterator, Class<T> clazz) throws NoSuchFieldException {
        StringBuilder sqlBuilder = new StringBuilder();
        List<Type.Java> paramJavaType = new ArrayList<>();
        int i = 0;
        while (iterator.hasNext()) {
            Part nextPart = iterator.next();
            Part.Type type = nextPart.getType();
            switch (type) {
                case SIMPLE_PROPERTY:
                    PropertyPath property = nextPart.getProperty();
                    String propertyName = property.getSegment();
                    Field declaredField = clazz.getDeclaredField(propertyName);
                    declaredField.setAccessible(true);
                    Column annotation = declaredField.getAnnotation(Column.class);
                    String columnName = propertyName;
                    if (annotation != null) {
                        columnName = annotation.value();
                        Type.Java java = annotation.javaType();
                        paramJavaType.add(java);
                    }
                    if (i == 0) {
                        sqlBuilder.append(columnName).append(" = ? ");
                    } else {
                        sqlBuilder.append(" and ").append(columnName).append(" = ? ");
                    }
                    break;
                default:
                    break;
            }
            i++;
        }
        if (sqlBuilder.length() > 0) {
            sqlBuilder.insert(0, "(").insert(sqlBuilder.length() - 1, ") ");
        }
        return new BeanSqlSegment(sqlBuilder.toString(), paramJavaType);
    }

    private BeanSqlSegment parseOrPart(Iterator<PartTree.OrPart> orPartIterator, Class<T> clazz) throws NoSuchFieldException {
        StringBuilder sqlBuilder = new StringBuilder();
        int i = 0;
        List<Type.Java> paramJavaTypeList = new ArrayList<>();
        while (orPartIterator.hasNext()) {
            PartTree.OrPart orPart = orPartIterator.next();
            Iterator<Part> partIterator = orPart.iterator();
            BeanSqlSegment beanSqlSegment = parsePart(partIterator, clazz);

            String sql = beanSqlSegment.getSql();
            List<Type.Java> paramJavaType = beanSqlSegment.getParamJavaType();
            if (i == 0) {
                sqlBuilder.append(sql);
            } else {
                sqlBuilder.append(" or ").append(sql);
            }
            paramJavaTypeList.addAll(paramJavaType);
            i++;
        }
        return new BeanSqlSegment(sqlBuilder.toString(), paramJavaTypeList);
    }

    private String parseSort(Sort sort, Class<T> clazz) throws NoSuchFieldException {
        StringBuilder sqlBuilder = new StringBuilder();
        Iterator<Sort.Order> orderIterator = sort.iterator();
        int i = 0;
        while (orderIterator.hasNext()) {
            Sort.Order order = orderIterator.next();
            String direction = order.getDirection().toString();
            String property = order.getProperty();
            Field declaredField = clazz.getDeclaredField(property);
            Column annotation = declaredField.getAnnotation(Column.class);
            String columnName = property;
            if (annotation != null) {
                columnName = annotation.value();
            }
            if (i == 0) {
                sqlBuilder.append(columnName).append(" ").append(direction);
            } else {
                sqlBuilder.append(", ").append(columnName).append(" ").append(direction);
            }
            i++;
        }
        if (sqlBuilder.length() > 0) {
            sqlBuilder.insert(0, " order by ");
            sqlBuilder.insert(sqlBuilder.length(), " ");
        }
        return sqlBuilder.toString();
    }

    public static void main(String[] args) throws Exception {
//        QueryProxyHandler query = new QueryProxyHandler();
//        BeanSqlSegment beanSqlSegment = query.parseMethod("findByFieldDomainAndFieldItemOrDomainItemGmtCreateOrderByFieldIdDescFieldDomainAsc", DictDO.class);
//        System.out.println(beanSqlSegment);
    }


}
