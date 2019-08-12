package ai.sparklabinc.d1.service;

import ai.sparklabinc.d1.dto.DbBasicConfigDTO;
import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DbSecurityConfigDTO;

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
    boolean Connection2DataSource(Long dsId) throws Exception;


    DbInforamtionDTO addDataSources(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO)  throws Exception;

    boolean deleteDataSources(Long dsId) throws Exception;

    List<DbInforamtionDTO> selectDataSources() throws Exception;

    DbInforamtionDTO refreshDataSources(Long dsId) throws Exception;

    List<Map<String, Object>>  selectDataSourceProperty(Long dsId) throws Exception;

    boolean editDataSourceProperty(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception;


    boolean dataSourceTestConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception;
;
}
