package ai.sparklabinc.generator.impl;

import ai.sparklabinc.dto.QueryParameterGroupDTO;
import ai.sparklabinc.dto.SQLGenerResultDTO;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.generator.SQLGenerator;

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
    public SQLGenerResultDTO buildSQL(String database, String schema, String table, Map<String, String[]> params, QueryParameterGroupDTO queryParameterGroup, List<DsFormTableSettingDO> dsFormTableSettingDOS) {
        return null;
    }
}
