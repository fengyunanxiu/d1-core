package ai.sparklabinc.d1.service;

import ai.sparklabinc.d1.dto.DbBasicConfigDTO;
import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DbSecurityConfigDTO;
import ai.sparklabinc.d1.dto.DfKeyBasicConfigDTO;
import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
import ai.sparklabinc.d1.entity.DfKeyBasicConfigDO;

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

    List<DbInforamtionDTO> selectDataSources(Long dsId, Integer dfKeyFilter) throws IOException, SQLException;

    List<Map<String, Object>>  selectDataSourceProperty(Long dsId) throws IOException, SQLException;

    boolean editDataSourceProperty(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws IOException, SQLException;

    DbInforamtionDTO addDataFacetKey(DfKeyBasicConfigDTO dfKeyBasicConfigDTO) throws Exception;

    List<Map<String,Object>> selectAllDfFormTableSettingByDfKey(String dfKey) throws Exception;

    boolean updateDataFacetKey(String dfKey, String newDfKey, String description) throws IOException, SQLException;

    boolean deleteDataFacetKey(String dfKey) throws IOException, SQLException;

    boolean dataSourceTestConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception;

    Boolean saveDfFormTableSetting(List<DfFormTableSettingDO> dfFormTableSettingDOSForUpdate, List<DfFormTableSettingDO> dfFormTableSettingDOSForAdd) throws Exception;

    List<Map<String,Object>> refreshDfFormTableSetting(String dfKey) throws Exception;

    DfKeyBasicConfigDO getDfKeyBasicInfo(String dfKey) throws Exception;
}
