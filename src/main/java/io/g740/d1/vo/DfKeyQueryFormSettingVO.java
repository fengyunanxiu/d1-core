package io.g740.d1.vo;

import java.util.List;

import io.g740.d1.dict.dto.DictOptionCascadeQueryDTO;
import io.g740.d1.dto.OptionDTO;

/**
 * @author : Kingzer
 * @date : 2019-07-02 21:55
 * @description :
 */
public class DfKeyQueryFormSettingVO {

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
     * 作为查询字段时，字段取值范围(级联结构)  id/value
     */
    private List<DictOptionCascadeQueryDTO> fieldCascadeOptionalValueList;
    
    /**
     * 作为查询字段时，字段取值范围(级联结构)  id/value
     */
    private String fieldCascadeChildFieldName;

    
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

	public List<DictOptionCascadeQueryDTO> getFieldCascadeOptionalValueList() {
		return fieldCascadeOptionalValueList;
	}

	public void setFieldCascadeOptionalValueList(List<DictOptionCascadeQueryDTO> fieldCascadeOptionalValueList) {
		this.fieldCascadeOptionalValueList = fieldCascadeOptionalValueList;
	}

	public String getFieldCascadeChildFieldName() {
		return fieldCascadeChildFieldName;
	}

	public void setFieldCascadeChildFieldName(String fieldCascadeChildFieldName) {
		this.fieldCascadeChildFieldName = fieldCascadeChildFieldName;
	}
    
}
