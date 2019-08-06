package ai.sparklabinc.d1.dao.convert;

import ai.sparklabinc.d1.util.StringUtils;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.RowProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.RemoteEndpoint;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (rs.next()) {
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
                        declaredMethod.invoke(t, resultValue);
                    }
                }
                return t;
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return null;
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
                        declaredMethod.invoke(t, resultValue);
                    }
                }
                result.add(t);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> toMap(ResultSet rs) throws SQLException {
        return this.basicRowProcessor.toMap(rs);
    }




}
