package io.g740.d1.service;

import io.g740.d1.dto.DbInformationDTO;
import io.g740.d1.dto.DfKeyBasicConfigDTO;
import io.g740.d1.entity.DfFormTableSettingDO;
import io.g740.d1.entity.DfKeyBasicConfigDO;

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
    DbInformationDTO addDataFacetKey(DfKeyBasicConfigDTO dfKeyBasicConfigDTO) throws Exception;

    List<Map<String, Object>> selectAllDfFormTableSettingByDfKey(String dfKey) throws Exception;

    void updateDataFacetKey(String dfKey, String newDfKey, String description) throws Exception;

    void deleteDataFacetKey(String dfKey) throws IOException, SQLException;

    void saveDfFormTableSetting(List<DfFormTableSettingDO> dfFormTableSettingDOSForUpdate, List<DfFormTableSettingDO> dfFormTableSettingDOSForAdd) throws Exception;

    List<Map<String, Object>> refreshDfFormTableSetting(String dfKey) throws Exception;

    DfKeyBasicConfigDO getDfKeyBasicInfo(String dfKey) throws Exception;

    void updateDefaultValueByDfKeyAndFieldKey(String dfKey, String fieldKey, String jsonValue) throws Exception;

    void updateDefaultValueStrategyType(String dfKey, String fieldKey, String strategyType) throws Exception;

    void updateDomainAndItemByDfKeyAndFieldName(String dfKey, String fieldName, String domain, String item) throws Exception;

    void saveDfFormTableSetting(List<DfFormTableSettingDO> dfFormTableSettingDOS) throws Exception;
}
