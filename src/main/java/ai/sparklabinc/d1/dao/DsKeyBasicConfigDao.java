package ai.sparklabinc.d1.dao;

import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DfKeyInfoDTO;
import ai.sparklabinc.d1.dto.DsKeyInfoDTO;
import ai.sparklabinc.d1.entity.DfKeyBasicConfigDO;
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

    DfKeyBasicConfigDO getDsKeyBasicConfigByDsKey(String dataSourceKey) throws SQLException, IOException;

    List<DbInforamtionDTO> getDataFacetKey(Long dsId, String schema, String tableName) throws IOException, SQLException;

    List<DfKeyInfoDTO> getAllDataFacetKey() throws IOException, SQLException;

    Integer addDataFacetKey(DfKeyBasicConfigDO dfKeyBasicConfigDO) throws IOException, SQLException;

    Integer updateDataFacetKey(String dfKey, String newDfKey, String description) throws IOException, SQLException;

    Integer deleteDataFacetKey(String dfKey)throws IOException, SQLException;

    Long addDataFacetKeyAndReturnId(DfKeyBasicConfigDO dfKeyBasicConfigDO) throws Exception;
}
