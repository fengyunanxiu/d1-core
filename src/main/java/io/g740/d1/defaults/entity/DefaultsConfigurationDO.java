package io.g740.d1.defaults.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 14:54
 * @description :
 */
public class DefaultsConfigurationDO {

    public static final String TABLE_NAME = "db_defaults_configuration";

    private String fieldId;

    private String fieldFormDfKey;

    private String fieldFormFieldKey;

    private DefaultConfigurationType fieldType;

    private String fieldPluginConf;

    private String fieldManualConf;

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldFormDfKey() {
        return fieldFormDfKey;
    }

    public void setFieldFormDfKey(String fieldFormDfKey) {
        this.fieldFormDfKey = fieldFormDfKey;
    }

    public String getFieldFormFieldKey() {
        return fieldFormFieldKey;
    }

    public void setFieldFormFieldKey(String fieldFormFieldKey) {
        this.fieldFormFieldKey = fieldFormFieldKey;
    }

    public DefaultConfigurationType getFieldType() {
        return fieldType;
    }

    public void setFieldType(DefaultConfigurationType fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldPluginConf() {
        return fieldPluginConf;
    }

    public void setFieldPluginConf(String fieldPluginConf) {
        this.fieldPluginConf = fieldPluginConf;
    }

    public String getFieldManualConf() {
        return fieldManualConf;
    }

    public void setFieldManualConf(String fieldManualConf) {
        this.fieldManualConf = fieldManualConf;
    }
}
