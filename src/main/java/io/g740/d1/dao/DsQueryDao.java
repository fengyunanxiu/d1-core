package io.g740.d1.dao;

import io.g740.d1.dto.PageResultDTO;
import io.g740.d1.dto.SQLGenerResultDTO;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/8 15:24
 * @description:
 * @version: V1.0
 */
public interface DsQueryDao {
    DataDaoType getDataDaoType();

    PageResultDTO excuteQuery(SQLGenerResultDTO assemblyResultDTO, Long fkDbId) throws Exception;
}
