package io.g740.d1.dict.vo;

import io.g740.d1.dict.entity.DictDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 10:07
 * @description :
 */
public class FormDictConfigurationVO {

    private String fieldId;

    private String fieldFormDfKey;

    private String fieldFormFieldKey;

    private String fieldDomain;

    private String fieldItem;

    private List<DictDO> dictList;

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

    public List<DictDO> getDictList() {
        return dictList;
    }

    public void setDictList(List<DictDO> dictList) {
        this.dictList = dictList;
    }
}
