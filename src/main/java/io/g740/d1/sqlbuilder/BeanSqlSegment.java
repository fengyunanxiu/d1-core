package io.g740.d1.sqlbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 17:37
 * @description :
 */
public class BeanSqlSegment {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanSqlSegment.class);


    private String sql;

    private List<Type.Java> paramJavaType;

    public BeanSqlSegment(String sql, List<Type.Java> paramJavaType) {
        this.sql = sql;
        this.paramJavaType = paramJavaType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Type.Java> getParamJavaType() {
        return paramJavaType;
    }

    public void setParamJavaType(List<Type.Java> paramJavaType) {
        this.paramJavaType = paramJavaType;
    }

    @Override
    public String toString() {
        return "BeanSqlSegment{" +
                "sql='" + sql + '\'' +
                ", paramJavaType=" + paramJavaType +
                '}';
    }
}
