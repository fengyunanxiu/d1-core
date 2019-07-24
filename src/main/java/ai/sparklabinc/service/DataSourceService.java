package ai.sparklabinc.service;

import ai.sparklabinc.dto.DbBasicConfigDTO;
import ai.sparklabinc.dto.DbInforamtionDTO;
import ai.sparklabinc.dto.DbSecurityConfigDTO;
import ai.sparklabinc.dto.DsKeyBasicConfigDTO;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.entity.DsKeyBasicConfigDO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Function:
 * @Author: DAM
 * @Date: 2019/7/1 20:32
 * @Description:
 * @Version V1.0
 */
public interface DataSourceService {
    boolean Connection2DataSource(Long dsId) throws SQLException, IOException;

    DbInforamtionDTO addDataSources(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO)  throws IOException, SQLException;

    boolean deleteDataSources(Long dsId) throws IOException, SQLException;

    List<DbInforamtionDTO> selectDataSources(Long dsId, Integer dsKeyFilter) throws IOException, SQLException;

    List<Map<String, Object>>  selectDataSourceProperty(Long dsId) throws IOException, SQLException;

    boolean editDataSourceProperty(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws IOException, SQLException;

    DbInforamtionDTO addDataSourceKey(DsKeyBasicConfigDTO dsKeyBasicConfigDTO) throws Exception;

    List<Map<String,Object>> selectAllDsFormTableSettingByDsKey(String dsKey) throws Exception;

    boolean updateDataSourceKey(String dsKey, String newDsKey, String description) throws IOException, SQLException;

    boolean deleteDataSourceKey(String dsKey) throws IOException, SQLException;

    boolean dataSourceTestConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception;

    Boolean saveDsFormTableSetting(List<DsFormTableSettingDO> dsFormTableSettingDOSForUpdate,List<DsFormTableSettingDO> dsFormTableSettingDOSForAdd) throws Exception;

    List<Map<String,Object>> refreshDsFormTableSetting(String dsKey) throws Exception;

    DsKeyBasicConfigDO getDsKeyBasicInfo(String dsKey) throws Exception;
}
