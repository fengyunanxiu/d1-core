package ai.sparklabinc.d1.service;

import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DfKeyBasicConfigDTO;
import ai.sparklabinc.d1.entity.DsFormTableSettingDO;
import ai.sparklabinc.d1.entity.DsKeyBasicConfigDO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @function:
 * @author: DAM
 * @date: 2019/8/8 9:44
 * @description:
 * @version: V1.0
 */
public interface DataFacetKeyService {
    DbInforamtionDTO addDataSourceKey(DfKeyBasicConfigDTO dfKeyBasicConfigDTO) throws Exception;

    List<Map<String, Object>> selectAllDsFormTableSettingByDsKey(String dfKey) throws Exception;

    boolean updateDataSourceKey(String dfKey, String newDfKey, String description) throws IOException, SQLException;

    boolean deleteDataSourceKey(String dfKey) throws IOException, SQLException;

    Boolean saveDsFormTableSetting(List<DsFormTableSettingDO> dsFormTableSettingDOSForUpdate, List<DsFormTableSettingDO> dsFormTableSettingDOSForAdd) throws Exception;

    List<Map<String, Object>> refreshDsFormTableSetting(String dfKey) throws Exception;

    DsKeyBasicConfigDO getDfKeyBasicInfo(String dfKey) throws Exception;
}
