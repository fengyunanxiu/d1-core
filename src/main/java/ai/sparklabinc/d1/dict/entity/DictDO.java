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

    public static final String TABLE_NAME = "dict";

    public static final String F_DOMAIN = "domain";
    public static final String F_ITEM = "item";
    public static final String F_VALUE = "value";
    public static final String F_LABEL = "label";
    public static final String F_SEQUENCE = "sequence";
    public static final String F_ENABLE = "enable";
    public static final String F_PARENT_ID = "parent_id";

    private String id;

    private String domain;

    private String item;

    private String value;

    private String label;

    private String sequence;

    private String enable;

    @Column("parent_id")
    private String parentId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
