package ai.sparklabinc.d1.generator.impl;

import ai.sparklabinc.d1.dto.QueryParameterGroupDTO;
import ai.sparklabinc.d1.dto.SQLGenerResultDTO;
import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
import ai.sparklabinc.d1.generator.SQLGenerator;

import java.util.List;
import java.util.Map;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/29 16:06
 * @description:
 * @version: V1.0
 */
public class OralceGenerator implements SQLGenerator {

    @Override
    public SQLGenerResultDTO buildSQL(String database, String schema, String table, Map<String, String[]> params, QueryParameterGroupDTO queryParameterGroup, List<DfFormTableSettingDO> dfFormTableSettingDOS) {
        return null;
    }
}
