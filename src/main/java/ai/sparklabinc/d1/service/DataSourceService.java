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

    List<DbInforamtionDTO> selectDataSources() throws IOException, SQLException;

    DbInforamtionDTO refreshDataSources(Long dsId) throws IOException, SQLException;

    List<Map<String, Object>>  selectDataSourceProperty(Long dsId) throws IOException, SQLException;

    boolean editDataSourceProperty(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws IOException, SQLException;


    boolean dataSourceTestConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception;
;
}
