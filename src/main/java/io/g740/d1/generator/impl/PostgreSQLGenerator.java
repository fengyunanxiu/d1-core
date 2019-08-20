package io.g740.d1.generator.impl;

import io.g740.d1.dto.QueryParameterGroupDTO;
import io.g740.d1.dto.SQLGenerResultDTO;
import io.g740.d1.entity.DfFormTableSettingDO;
import io.g740.d1.generator.SQLGenerator;

import java.util.List;
import java.util.Map;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/29 16:01
 * @description:
 * @version: V1.0
 */
public class PostgreSQLGenerator implements SQLGenerator {

    @Override
    public SQLGenerResultDTO buildSQL(String database, String schema, String table, Map<String, String[]> params, QueryParameterGroupDTO queryParameterGroup, List<DfFormTableSettingDO> dfFormTableSettingDOS) {
        return null;
    }
}
