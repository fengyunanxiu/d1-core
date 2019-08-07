package ai.sparklabinc.d1.dict.entity;

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

    private static final String TABLE_NAME = "db_form_dict_configuration";

    private String fId;

    private String fFormDfKey;

    private String fFormFieldKey;

    private String fDomain;

    private String fItem;

    public String getFId() {
        return fId;
    }

    public void setFId(String fId) {
        this.fId = fId;
    }

    public String getFFormDfKey() {
        return fFormDfKey;
    }

    public void setFFormDfKey(String fFormDfKey) {
        this.fFormDfKey = fFormDfKey;
    }

    public String getFFormFieldKey() {
        return fFormFieldKey;
    }

    public void setFFormFieldKey(String fFormFieldKey) {
        this.fFormFieldKey = fFormFieldKey;
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
}
