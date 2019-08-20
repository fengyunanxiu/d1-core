package io.g740.d1.generator;

import io.g740.d1.dto.QueryParameterGroupDTO;
import io.g740.d1.dto.SQLGenerResultDTO;
import io.g740.d1.entity.DfFormTableSettingDO;

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
                               QueryParameterGroupDTO queryParameterGroup, List<DfFormTableSettingDO> dfFormTableSettingDOS) throws Exception;
}
