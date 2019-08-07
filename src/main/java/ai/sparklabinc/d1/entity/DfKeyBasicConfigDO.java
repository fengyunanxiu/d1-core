package ai.sparklabinc.d1.entity;

public class DfKeyBasicConfigDO {


    private Long id;

    private String dfKey;

    private Long fkDbId;

    private String schemaName;

    private String tableName;

    private String description;

    private String gmtCreate;

    private String gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDfKey() {
        return dfKey;
    }

    public void setDfKey(String dfKey) {
        this.dfKey = dfKey;
    }

    public Long getFkDbId() {
        return fkDbId;
    }

    public void setFkDbId(Long fkDbId) {
        this.fkDbId = fkDbId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
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

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }
}
