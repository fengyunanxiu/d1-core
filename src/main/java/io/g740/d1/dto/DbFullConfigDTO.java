package io.g740.d1.dto;

/**
 * @function:
 * @author DAM
 * @date: 2019/7/10 15:12
 * @description:
 * @version: V1.0
 */
public class DbFullConfigDTO {
    private DbBasicConfigDTO dbBasicConfigDTO;
    private DbSecurityConfigDTO dbSecurityConfigDTO;

    public DbBasicConfigDTO getDbBasicConfigDTO() {
        return dbBasicConfigDTO;
    }

    public void setDbBasicConfigDTO(DbBasicConfigDTO dbBasicConfigDTO) {
        this.dbBasicConfigDTO = dbBasicConfigDTO;
    }

    public DbSecurityConfigDTO getDbSecurityConfigDTO() {
        return dbSecurityConfigDTO;
    }

    public void setDbSecurityConfigDTO(DbSecurityConfigDTO dbSecurityConfigDTO) {
        this.dbSecurityConfigDTO = dbSecurityConfigDTO;
    }
}
