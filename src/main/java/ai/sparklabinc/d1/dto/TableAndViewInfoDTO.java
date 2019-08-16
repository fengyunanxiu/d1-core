package ai.sparklabinc.d1.dto;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/4 15:57
 * @description:
 * @version: V1.0
 */
public class TableAndViewInfoDTO {
    private String tableSchema;
    private String tableName;
    private Long level;
    private String type;

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
