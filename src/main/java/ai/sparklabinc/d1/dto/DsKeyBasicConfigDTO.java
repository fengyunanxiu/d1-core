package ai.sparklabinc.d1.dto;

public class DsKeyBasicConfigDTO {

    private String dsKey;

    private Long fkDbId;

    private String schema;

    private String tableName;

    private String description;


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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
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

}
