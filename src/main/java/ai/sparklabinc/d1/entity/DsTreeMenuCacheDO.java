package ai.sparklabinc.d1.entity;

/**
 * @function:
 * @author: DAM
 * @date: 2019/8/11 20:56
 * @description:
 * @version: V1.0
 */
public class DsTreeMenuCacheDO {
    private Long dsId;
    private String dsBasicInfo;
    private String dsSchemaInfo;
    private String dsTableViewInfo;
    private String dsKeyInfo;

    public Long getDsId() {
        return dsId;
    }

    public void setDsId(Long dsId) {
        this.dsId = dsId;
    }

    public String getDsBasicInfo() {
        return dsBasicInfo;
    }

    public void setDsBasicInfo(String dsBasicInfo) {
        this.dsBasicInfo = dsBasicInfo;
    }

    public String getDsSchemaInfo() {
        return dsSchemaInfo;
    }

    public void setDsSchemaInfo(String dsSchemaInfo) {
        this.dsSchemaInfo = dsSchemaInfo;
    }

    public String getDsTableViewInfo() {
        return dsTableViewInfo;
    }

    public void setDsTableViewInfo(String dsTableViewInfo) {
        this.dsTableViewInfo = dsTableViewInfo;
    }

    public String getDsKeyInfo() {
        return dsKeyInfo;
    }

    public void setDsKeyInfo(String dsKeyInfo) {
        this.dsKeyInfo = dsKeyInfo;
    }
}
