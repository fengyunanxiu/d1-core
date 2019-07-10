package ai.sparklabinc.dao;

import ai.sparklabinc.entity.DataExportTaskDO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/9 16:26
 * @description:
 * @version: V1.0
 */
public interface DataExportTaskDao {
    DataExportTaskDO addDataExportTask(DataExportTaskDO dataExportTaskDO) throws Exception;

    DataExportTaskDO findById(Long id) throws IOException, SQLException;

    DataExportTaskDO updateDataExportTask(DataExportTaskDO dataExportTaskDO) throws IOException, SQLException;
}
