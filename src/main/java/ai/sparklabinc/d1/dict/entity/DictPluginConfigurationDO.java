package ai.sparklabinc.d1.dict.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 11:38
 * @description :
 */
public class DictPluginConfigurationDO {

    public static String TABLE_NAME = "db_dict_plugin_configuration";
    public static String F_ID = "field_id";
    public static String F_DOMAIN = "field_domain";
    public static String F_ITEM = "field_item";
    public static String F_ENABLE = "field_enable";
    public static String F_TYPE = "field_type";
    public static String F_PARAM = "field_param";
    public static String F_CRON = "field_cron";

    private Long fieldId;

    private String fieldDomain;

    private String fieldItem;

    private Boolean fieldEnable;

    private String fieldType;

    /**
     * JSON形式
     */
    private String fieldParam;

    private String fieldCron;

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
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

    public Boolean getFieldEnable() {
        return fieldEnable;
    }

    public void setFieldEnable(Boolean fieldEnable) {
        this.fieldEnable = fieldEnable;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldParam() {
        return fieldParam;
    }

    public void setFieldParam(String fieldParam) {
        this.fieldParam = fieldParam;
    }

    public String getFieldCron() {
        return fieldCron;
    }

    public void setFieldCron(String fieldCron) {
        this.fieldCron = fieldCron;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictPluginConfigurationDO that = (DictPluginConfigurationDO) o;
        return Objects.equals(fieldId, that.fieldId) &&
                Objects.equals(fieldDomain, that.fieldDomain) &&
                Objects.equals(fieldItem, that.fieldItem) &&
                Objects.equals(fieldEnable, that.fieldEnable) &&
                Objects.equals(fieldType, that.fieldType) &&
                Objects.equals(fieldParam, that.fieldParam) &&
                Objects.equals(fieldCron, that.fieldCron);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldId, fieldDomain, fieldItem, fieldEnable, fieldType, fieldParam, fieldCron);
    }

    @Override
    public String toString() {
        return "DictPluginConfigurationDO{" +
                "fieldId=" + fieldId +
                ", fieldDomain='" + fieldDomain + '\'' +
                ", fieldItem='" + fieldItem + '\'' +
                ", fieldEnable=" + fieldEnable +
                ", fieldType='" + fieldType + '\'' +
                ", fieldParam='" + fieldParam + '\'' +
                ", fieldCron='" + fieldCron + '\'' +
                '}';
    }
}
