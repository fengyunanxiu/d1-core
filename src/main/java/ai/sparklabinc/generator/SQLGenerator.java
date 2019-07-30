package ai.sparklabinc.generator;

import ai.sparklabinc.dto.QueryParameterGroupDTO;
import ai.sparklabinc.dto.SQLGenerResultDTO;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import java.util.List;
import java.util.Map;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/29 15:58
 * @description:
 * @version: V1.0
 */
public interface SQLGenerator {
    SQLGenerResultDTO buildSQL(String database, String schema, String table,
                               Map<String, String[]> requestParams,
                               QueryParameterGroupDTO queryParameterGroup, List<DsFormTableSettingDO> dsFormTableSettingDOS) throws Exception;
}
