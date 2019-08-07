package ai.sparklabinc.d1.dict.entity;

import ai.sparklabinc.d1.dao.convert.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 15:38
 * @description :
 */
public class DictDO {

    public static final String TABLE_NAME = "db_dict";

    public static final String F_ID = "f_id";
    public static final String F_DOMAIN = "f_domain";
    public static final String F_ITEM = "f_item";
    public static final String F_VALUE = "f_value";
    public static final String F_LABEL = "f_label";
    public static final String F_SEQUENCE = "f_sequence";
    public static final String F_ENABLE = "f_enable";
    public static final String F_PARENT_ID = "f_parent_id";

    private String fId;

    private String fDomain;

    private String fItem;

    private String fValue;

    private String fLabel;

    private String fSequence;

    private String fEnable;

    @Column("parent_id")
    private String fParentId;

    public String getFId() {
        return fId;
    }

    public void setFId(String fId) {
        this.fId = fId;
    }

    public String getFDomain() {
        return fDomain;
    }

    public void setFDomain(String fDomain) {
        this.fDomain = fDomain;
    }

    public String getFItem() {
        return fItem;
    }

    public void setFItem(String fItem) {
        this.fItem = fItem;
    }

    public String getFValue() {
        return fValue;
    }

    public void setFValue(String fValue) {
        this.fValue = fValue;
    }

    public String getFLabel() {
        return fLabel;
    }

    public void setFLabel(String fLabel) {
        this.fLabel = fLabel;
    }

    public String getFSequence() {
        return fSequence;
    }

    public void setFSequence(String fSequence) {
        this.fSequence = fSequence;
    }

    public String getFEnable() {
        return fEnable;
    }

    public void setFEnable(String fEnable) {
        this.fEnable = fEnable;
    }

    public String getFParentId() {
        return fParentId;
    }

    public void setFParentId(String fParentId) {
        this.fParentId = fParentId;
    }
}
