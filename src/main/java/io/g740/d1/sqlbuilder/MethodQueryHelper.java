package io.g740.d1.sqlbuilder;

import com.alibaba.fastjson.JSON;
import io.g740.d1.util.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/30 14:02
 * @description :
 */
public class MethodQueryHelper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodQueryHelper.class);

    private static final String QUERY_PATTERN = "find|read|get|query|stream";

    private DataSource dataSource;

    private Class<T> entityClazz;

    public MethodQueryHelper(DataSource dataSource, Class<T> entityClazz) {
        this.dataSource = dataSource;
        this.entityClazz = entityClazz;
    }

    public List<T> query(String methodName, Object[] args) throws Throwable {
        SQL sql = querySQL(methodName, args);
        String sqlStr = sql.getSql();
        List<Object> sqlParamList = sql.getFieldValueList();
        return JdbcHelper.query(this.dataSource, sqlStr, sqlParamList, entityClazz);
    }

    private SQL querySQL(String methodName, Object[] args) throws Exception {
        if (args == null) {
            args = new Object[0];
        }
        BeanSqlSegment beanSqlSegment = parseMethod(methodName, entityClazz);
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
        return new SQL(sql, null, sqlParamList);
    }

    private BeanSqlSegment parseMethod(String methodName, Class<T> clazz) throws Exception {
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
        PartTree partTree = new PartTree(methodName, clazz);
        Iterator<PartTree.OrPart> orPartIterator = partTree.iterator();
        BeanSqlSegment beanSqlSegment = parseOrPart(orPartIterator, clazz);
        String partSql = beanSqlSegment.getSql();
        if (StringUtils.isNotNullNorEmpty(partSql)) {
            sql += " where " + partSql;
        }
        Sort sort = partTree.getSort();
        if (sort != null) {
            String sortSql = parseSort(sort, clazz);
            if (StringUtils.isNotNullNorEmpty(sortSql)) {
                sql += " " + sortSql;
            }
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
