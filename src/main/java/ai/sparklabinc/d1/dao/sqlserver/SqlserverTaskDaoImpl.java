package ai.sparklabinc.d1.dao.sqlserver;

import ai.sparklabinc.d1.dao.DataDaoType;
import ai.sparklabinc.d1.dao.DataExportTaskDao;
import ai.sparklabinc.d1.datasource.Constants;
import ai.sparklabinc.d1.datasource.DataSourceFactory;
import ai.sparklabinc.d1.entity.DataExportTaskDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/9 16:27
 * @description:
 * @version: V1.0
 */
@Repository("SqlserverTaskDaoImpl")
public class SqlserverTaskDaoImpl implements DataExportTaskDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(SqlserverTaskDaoImpl.class);
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.SQLSERVER;
    }

    @Override
    public DataExportTaskDO addDataExportTask(DataExportTaskDO dataExportTaskDO) throws Exception {
        DataSource dataSource = dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null);
        Connection connection = null;
        Long id = 0L;
        try {
            connection = dataSource.getConnection();
            String sql = "insert into data_export_task(start_at, end_at, failed_at, details, file_name, file_path)" +
                    " values (?, ?, ?, ?, ?, ?);";
            LOGGER.info("sql:{}", sql);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            //绑定参数
            bindParameters(preparedStatement,
                    dataExportTaskDO.getStartAt(),
                    dataExportTaskDO.getEndAt(),
                    dataExportTaskDO.getFailedAt(),
                    dataExportTaskDO.getDetails(),
                    dataExportTaskDO.getFileName(),
                    dataExportTaskDO.getFilePath());
            int update = preparedStatement.executeUpdate();
            if (update > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                while (generatedKeys.next()) {
                    id = generatedKeys.getLong(1);
                }
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        if (id > 0) {
            dataExportTaskDO.setId(id);
        } else {
            throw new Exception("Add data export task is failed!");
        }
        return dataExportTaskDO;
    }

    @Override
    public DataExportTaskDO findById(Long id) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null));
        String sql = "select id," +
                "   start_at as startAt," +
                "   end_at as endAt," +
                "   failed_at as failedAt," +
                "   details," +
                "   file_name as fileName," +
                "   file_path as filePath" +
                " from data_export_task" +
                " where id = ?";
        LOGGER.info("sql:{}", sql);
        DataExportTaskDO dataExportTaskDO = queryRunner.query(sql, new BeanHandler<>(DataExportTaskDO.class), id);
        return dataExportTaskDO;
    }

    @Override
    public DataExportTaskDO updateDataExportTask(DataExportTaskDO dataExportTaskDO) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null));
        String sql = "update data_export_task set start_at=?," +
                "  end_at=?," +
                "  failed_at=?, " +
                "  details=?," +
                "  file_name=?, " +
                "  file_path=?" +
                " where id=?";
        LOGGER.info("sql:{}", sql);
        Object[] objectParams = {dataExportTaskDO.getStartAt(),
                dataExportTaskDO.getEndAt(),
                dataExportTaskDO.getFailedAt(),
                dataExportTaskDO.getDetails(),
                dataExportTaskDO.getFileName(),
                dataExportTaskDO.getFilePath(),
                dataExportTaskDO.getId()
        };
        int update = queryRunner.update(sql, objectParams);
        if(update>0){
            return dataExportTaskDO;
        }else {
            throw new SQLException("update data export task is failed!");
        }
    }

    /**
     * 绑定参数方法
     *
     * @param stmt
     * @param params
     * @throws SQLException
     */
    private void bindParameters(PreparedStatement stmt, Object... params) throws SQLException {
        //绑定参数
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
