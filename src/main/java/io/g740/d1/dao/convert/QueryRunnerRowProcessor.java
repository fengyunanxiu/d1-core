package io.g740.d1.dao.convert;

import io.g740.d1.util.DateUtils;
import io.g740.d1.util.StringUtils;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.RowProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 17:28
 * @description :
 */
public class QueryRunnerRowProcessor implements RowProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryRunnerRowProcessor.class);

    private BasicRowProcessor basicRowProcessor = new BasicRowProcessor();

    @Override
    public Object[] toArray(ResultSet rs) throws SQLException {
        return this.basicRowProcessor.toArray(rs);
    }

    @Override
    public <T> T toBean(ResultSet rs, Class<? extends T> type) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Map<String, Object> resultValueMap = new HashMap<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String fieldName = metaData.getColumnLabel(i).toLowerCase();
            String camelFileName = StringUtils.underlineToCamel("set_"+fieldName);
            resultValueMap.put(camelFileName, rs.getObject(i));
        }
        try {
            T t = type.newInstance();
            Method[] declaredMethods = type.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                String methodName = declaredMethod.getName();
                Object resultValue = resultValueMap.get(methodName);
                if (resultValue != null) {
                    Class<?> parameterType = declaredMethod.getParameterTypes()[0];
                    resultValue = convertType(resultValue, parameterType);
                    declaredMethod.invoke(t, resultValue);
                }
            }
            return t;
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new SQLException(e);
        }
    }

    @Override
    public <T> List<T> toBeanList(ResultSet rs, Class<? extends T> type) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        List<T> result = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> resultValueMap = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String fieldName = metaData.getColumnLabel(i).toLowerCase();
                String camelFileName = StringUtils.underlineToCamel("set_"+fieldName);
                resultValueMap.put(camelFileName, rs.getObject(i));
            }
            try {
                T t = type.newInstance();
                Method[] declaredMethods = type.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    String methodName = declaredMethod.getName();
                    Object resultValue = resultValueMap.get(methodName);
                    if (resultValue != null) {
                        Class<?> parameterType = declaredMethod.getParameterTypes()[0];
                        resultValue = convertType(resultValue, parameterType);
                        declaredMethod.invoke(t, resultValue);
                    }
                }
                result.add(t);
            } catch (Exception e) {
                LOGGER.error("", e);
                throw new SQLException(e);
            }
        }
        return result;
    }

    private <T> Object convertType(Object resultValue, Class<?> parameterType) {
        if (parameterType == String.class) {
            resultValue = resultValue.toString();
        }
        if (parameterType == Long.class) {
            resultValue = Long.valueOf(resultValue.toString());
        }
        if (parameterType == Integer.class) {
            resultValue = Integer.valueOf(resultValue.toString());
        }
        if (parameterType == Float.class) {
            resultValue = Float.valueOf(resultValue.toString());
        }
        if (parameterType == Double.class) {
            resultValue = Double.valueOf(resultValue.toString());
        }
        if (parameterType == Date.class) {
            if (resultValue instanceof Date) {
            } else {
                try {
                    resultValue = DateUtils.ofLongDate(resultValue.toString());
                } catch (Exception e) {
                    resultValue = DateUtils.ofShortDate(resultValue.toString());
                }
            }
        }
        if (parameterType == BigDecimal.class) {
            resultValue = new BigDecimal(resultValue.toString());
        }
        if (parameterType.isEnum()) {
            resultValue = Enum.valueOf((Class<Enum>)parameterType, resultValue.toString());
        }
        return resultValue;
    }

    @Override
    public Map<String, Object> toMap(ResultSet rs) throws SQLException {
        return this.basicRowProcessor.toMap(rs);
    }




}
