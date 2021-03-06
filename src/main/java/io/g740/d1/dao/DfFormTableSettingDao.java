package io.g740.d1.dao;

import io.g740.d1.entity.DfFormTableSettingDO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author : Kingzer
 * @date : 2019-07-03 07:45
 * @description :
 */
public interface DfFormTableSettingDao {

    DataDaoType getDataDaoType();

    List<DfFormTableSettingDO> getAllDfFormTableSettingByDfKey(String dataFacetKey) throws SQLException, IOException;

    Integer batchAdd(List<DfFormTableSettingDO> dfFormTableSettingDOS) throws IOException, SQLException;

    List<Map<String,Object>> selectAllDfFormTableSettingByDfKey(String dataFacetKey) throws SQLException, IOException;

    Integer updateDataFacetKey(String dataFacetKey, String newDataFacetKey) throws SQLException, IOException;

    Integer deleteDataFacetKey(String dfKey) throws SQLException, IOException;

    List<DfFormTableSettingDO> getAllDfFormTableSettingByDfKeyForExport(String dataFacetKey) throws SQLException, IOException;


    Integer updateDfFormTableSetting(DfFormTableSettingDO dfFormTableSettingDO) throws SQLException, IOException;

    void updateDefaultValueByDfKeyAndFieldName(String dfKey, String fieldName, String jsonValue) throws SQLException;

    void updateDefaultStrategyTypeByDfKeyAndFieldName(String dfKey, String fieldName, String strategyType) throws SQLException;

    void updateDomainAndItemByDfKeyAndFieldName(String dfKey, String fieldName, String domain, String item) throws SQLException;

    DfFormTableSettingDO queryByDfKeyAndFieldName(String dfKey, String fieldName) throws SQLException;
}
