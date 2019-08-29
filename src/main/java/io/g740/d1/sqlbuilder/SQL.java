package io.g740.d1.sqlbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 11:17
 * @description :
 */
public class SQL {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQL.class);

    private String sql;

    private List<String> fieldNameList;

    private List<Object> fieldValueList;

    public SQL(String sql, List<String> fieldNameList, List<Object> fieldValueList) {
        this.sql = sql;
        this.fieldNameList = fieldNameList;
        this.fieldValueList = fieldValueList;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getFieldNameList() {
        return fieldNameList;
    }

    public void setFieldNameList(List<String> fieldNameList) {
        this.fieldNameList = fieldNameList;
    }

    public List<Object> getFieldValueList() {
        return fieldValueList;
    }

    public void setFieldValueList(List<Object> fieldValueList) {
        this.fieldValueList = fieldValueList;
    }
}
