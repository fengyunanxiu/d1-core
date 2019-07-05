package ai.sparklabinc.dao;

import ai.sparklabinc.dto.DbInforamtionDTO;
import ai.sparklabinc.entity.DsKeyBasicConfigDO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 20:49
 * @description :
 */
public interface DsKeyBasicConfigDao {
    DsKeyBasicConfigDO getDsKeyBasicConfigByDsKey(String dataSourceKey) throws SQLException, IOException;

    List<DbInforamtionDTO> getDataSourceKey(Long dsId, String schema, String tableName) throws IOException, SQLException;

    Integer addDataSourceKey(DsKeyBasicConfigDO dsKeyBasicConfigDO) throws IOException, SQLException;

    Integer updateDataSourceKey(String dsKey, String newDsKey, String description) throws IOException, SQLException;
}
