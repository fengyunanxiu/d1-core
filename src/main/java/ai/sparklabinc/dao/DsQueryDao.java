package ai.sparklabinc.dao;

import ai.sparklabinc.dto.AssemblyResultDTO;
import ai.sparklabinc.dto.PageResultDTO;

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
    PageResultDTO excuteQuery(AssemblyResultDTO assemblyResultDTO, Long fkDbId) throws IOException, SQLException;
}
