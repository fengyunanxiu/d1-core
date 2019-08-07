package ai.sparklabinc.d1.dict.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 11:38
 * @description :
 */
public class DictPluginConfigurationDO {

    public static String TABLE_NAME = "db_dict_plugin_configuration";

    public static String F_ENABLE = "f_enable";

    private Long fId;

    private String fDomain;

    private String fItem;

    private Boolean fEnable;

    private String fType;

    /**
     * JSON形式
     */
    private String fParam;

    private String fCron;

    public Long getFId() {
        return fId;
    }

    public void setFId(Long fId) {
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

    public Boolean getFEnable() {
        return fEnable;
    }

    public void setFEnable(Boolean fEnable) {
        this.fEnable = fEnable;
    }

    public String getFType() {
        return fType;
    }

    public void setFType(String fType) {
        this.fType = fType;
    }

    public String getFParam() {
        return fParam;
    }

    public void setFParam(String fParam) {
        this.fParam = fParam;
    }

    public String getFCron() {
        return fCron;
    }

    public void setFCron(String fCron) {
        this.fCron = fCron;
    }
}
