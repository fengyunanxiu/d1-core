package ai.sparklabinc.dao;

import ai.sparklabinc.entity.DsFormTableSettingDO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author : Kingzer
 * @date : 2019-07-03 07:45
 * @description :
 */
public interface DsFormTableSettingDao {


    List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKey(String dataSourceKey) throws SQLException, IOException;

    Integer add(DsFormTableSettingDO dsFormTableSettingDO) throws IOException, SQLException;

    List<Map<String,Object>> selectAllDsFormTableSettingByDsKey(String dataSourceKey) throws SQLException, IOException;

    Integer updateDataSourceKey(String dataSourceKey, String newDataSourceKey) throws SQLException, IOException;

    Integer deleteDataSourceKey(String dsKey) throws SQLException, IOException;

    List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKeyForExport(String dataSourceKey) throws SQLException, IOException;


    Integer updateDsFormTableSetting(DsFormTableSettingDO dsFormTableSettingDO) throws SQLException, IOException;
}
