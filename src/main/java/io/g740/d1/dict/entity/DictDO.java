package io.g740.d1.dict.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 15:38
 * @description :
 */
public class DictDO {

    public static final String TABLE_NAME = "db_dict";

    public static final String F_ID = "field_id";
    public static final String F_GMT_CREATE = "field_gmt_create";
    public static final String F_GMT_MODIFIED = "field_gmt_modified";
    public static final String F_DOMAIN = "field_domain";
    public static final String F_ITEM = "field_item";
    public static final String F_VALUE = "field_value";
    public static final String F_LABEL = "field_label";
    public static final String F_SEQUENCE = "field_sequence";
    public static final String F_PARENT_ID = "field_parent_id";
    public static final String F_DOMAIN_ITEM_GMT_CREATE = "domain_item_gmt_create";

    private String fieldId;

    private Date gmtCreate;

    private Date gmtModified;

    private String fieldDomain;

    private String fieldItem;

    private String fieldValue;

    private String fieldLabel;

    private String fieldSequence;

    private String fieldParentId;

    private Date domainItemGmtCreate;

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
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

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldSequence() {
        return fieldSequence;
    }

    public void setFieldSequence(String fieldSequence) {
        this.fieldSequence = fieldSequence;
    }

    public String getFieldParentId() {
        return fieldParentId;
    }

    public void setFieldParentId(String fieldParentId) {
        this.fieldParentId = fieldParentId;
    }


    public Date getDomainItemGmtCreate() {
        return domainItemGmtCreate;
    }

    public void setDomainItemGmtCreate(Date domainItemGmtCreate) {
        this.domainItemGmtCreate = domainItemGmtCreate;
    }
}
