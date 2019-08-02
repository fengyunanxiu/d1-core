package ai.sparklabinc.d1.dao;

import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DsKeyInfoDTO;
import ai.sparklabinc.d1.entity.DsKeyBasicConfigDO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 20:49
 * @description :
 */
public interface DsKeyBasicConfigDao {
    DataDaoType getDataDaoType();

    DsKeyBasicConfigDO getDsKeyBasicConfigByDsKey(String dataSourceKey) throws SQLException, IOException;

    List<DbInforamtionDTO> getDataSourceKey(Long dsId, String schema, String tableName) throws IOException, SQLException;

    List<DsKeyInfoDTO> getAllDataSourceKey() throws IOException, SQLException;

    Integer addDataSourceKey(DsKeyBasicConfigDO dsKeyBasicConfigDO) throws IOException, SQLException;

    Integer updateDataSourceKey(String dsKey, String newDsKey, String description) throws IOException, SQLException;

    Integer deleteDataSourceKey(String dsKey)throws IOException, SQLException;

    Long addDataSourceKeyAndReturnId(DsKeyBasicConfigDO dsKeyBasicConfigDO) throws Exception;
}