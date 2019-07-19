package ai.sparklabinc.vo;

import ai.sparklabinc.dto.OptionDTO;

import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-02 21:55
 * @description :
 */
public class DsKeyQueryFormSettingVO {

    private String viewFieldLabel;

    private String dbFieldName;

    /**
     * 作为查询字段时，元素类型
     */
    private String formFieldQueryType;

    /**
     * 作为查询字段时，字段取值范围  id/value
     */
    private List<OptionDTO> fieldOptionalValueList;

    /**
     * 作为查询字段时，为前端模型预留空字符串字段
     */
    private Object fieldValue = "" ;


    private Integer formFieldSequence;

    public String getViewFieldLabel() {
        return viewFieldLabel;
    }

    public void setViewFieldLabel(String viewFieldLabel) {
        this.viewFieldLabel = viewFieldLabel;
    }

    public String getDbFieldName() {
        return dbFieldName;
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }

    public String getFormFieldQueryType() {
        return formFieldQueryType;
    }

    public void setFormFieldQueryType(String formFieldQueryType) {
        this.formFieldQueryType = formFieldQueryType;
    }

    public List<OptionDTO> getFieldOptionalValueList() {
        return fieldOptionalValueList;
    }

    public void setFieldOptionalValueList(List<OptionDTO> fieldOptionalValueList) {
        this.fieldOptionalValueList = fieldOptionalValueList;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Integer getFormFieldSequence() {
        return formFieldSequence;
    }

    public void setFormFieldSequence(Integer formFieldSequence) {
        this.formFieldSequence = formFieldSequence;
    }
}
