package ai.sparklabinc.d1.dao;

import ai.sparklabinc.d1.dto.AssemblyResultDTO;
import ai.sparklabinc.d1.dto.PageResultDTO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/8 15:24
 * @description:
 * @version: V1.0
 */
public interface DsQueryDao {
    DataDaoType getDataDaoType();

    PageResultDTO excuteQuery(AssemblyResultDTO assemblyResultDTO, Long fkDbId) throws IOException, SQLException, Exception;
}
