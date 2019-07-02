package ai.sparklabinc.entity;

import java.util.Date;

public class DsKeyBasicConfigDO {


    private Long id;

    private String dsKey;

    private Long fkDbId;

    private String schemal;

    private String tableName;

    private String description;

    private Date gmtCreate;

    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDsKey() {
        return dsKey;
    }

    public void setDsKey(String dsKey) {
        this.dsKey = dsKey;
    }

    public Long getFkDbId() {
        return fkDbId;
    }

    public void setFkDbId(Long fkDbId) {
        this.fkDbId = fkDbId;
    }

    public String getSchemal() {
        return schemal;
    }

    public void setSchemal(String schemal) {
        this.schemal = schemal;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
