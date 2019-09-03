package io.g740.d1.dict.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 9:59
 * @description :
 */
public class FormDictConfigurationDO {

    public static final String TABLE_NAME = "db_form_dict_configuration";

    private String fieldId;

    private String fieldFormDfKey;

    private String fieldFormFieldKey;

    private String fieldDomain;

    private String fieldItem;

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

    public String getFieldDomain() {
        return fieldDomain;
    }

    public void setFieldDomain(String fieldDomain) {
        this.fieldDomain = fieldDomain;
    }

    public String getFieldItem() {
        return fieldItem;
    }

    public void setFieldItem(String fieldItem) {
        this.fieldItem = fieldItem;
    }
}
