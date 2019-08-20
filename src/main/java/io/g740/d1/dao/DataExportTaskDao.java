package io.g740.d1.dao;

import io.g740.d1.entity.DataExportTaskDO;

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

    DataDaoType getDataDaoType();

    DataExportTaskDO addDataExportTask(DataExportTaskDO dataExportTaskDO) throws Exception;

    DataExportTaskDO findById(Long id) throws IOException, SQLException;

    DataExportTaskDO updateDataExportTask(DataExportTaskDO dataExportTaskDO) throws IOException, SQLException;
}
