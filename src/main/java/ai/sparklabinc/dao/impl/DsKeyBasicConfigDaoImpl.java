package ai.sparklabinc.dao.impl;

import ai.sparklabinc.dao.DsKeyBasicConfigDao;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.dto.DbInforamtionDTO;
import ai.sparklabinc.entity.DsKeyBasicConfigDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 20:49
 * @description :
 */
@Repository
public class DsKeyBasicConfigDaoImpl implements DsKeyBasicConfigDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DsKeyBasicConfigDaoImpl.class);

    @Autowired
    private DataSourceFactory dataSourceFactory;




    @Override
    public DsKeyBasicConfigDO getDsKeyBasicConfigByDsKey(String dataSourceKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,0L));
        String querySql = "select * from ds_key_basic_config where ds_key = ? ";
        LOGGER.info("querySql:{}", querySql);

        DsKeyBasicConfigDO dsKeyBasicConfigDO = queryRunner.query(querySql, new ResultSetHandler<DsKeyBasicConfigDO>() {
            @Override
            public DsKeyBasicConfigDO handle(ResultSet resultSet) throws SQLException {
                DsKeyBasicConfigDO resultDsKeyBasicConfigDO = null;
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String gmtCreateStr =  resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");
                    String dsKey = resultSet.getString("ds_key");
                    Long fkDbId = resultSet.getLong("fk_db_id");
                    String schema = resultSet.getString("schema");
                    String tableName = resultSet.getString("table_name");
                    String description = resultSet.getString("description");

                    resultDsKeyBasicConfigDO = new DsKeyBasicConfigDO();
                    resultDsKeyBasicConfigDO.setId(id);
                    resultDsKeyBasicConfigDO.setGmtCreate(gmtCreateStr);
                    resultDsKeyBasicConfigDO.setGmtModified(gmtModifiedStr);
                    resultDsKeyBasicConfigDO.setDsKey(dsKey);
                    resultDsKeyBasicConfigDO.setFkDbId(fkDbId);
                    resultDsKeyBasicConfigDO.setSchema(schema);
                    resultDsKeyBasicConfigDO.setTableName(tableName);
                    resultDsKeyBasicConfigDO.setDescription(description);
                }
                return resultDsKeyBasicConfigDO;
            }
        }, dataSourceKey);

        return dsKeyBasicConfigDO;
    }

    @Override
    public List<DbInforamtionDTO> getDataSourceKey(Long dsId,String schema, String tableName) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,dsId));
        String sql ="select  id," +
                "       ds_key as label," +
                "       4 as level" +
                " from ds_key_basic_config" +
                " where fk_db_id=? " +
                " and schemal=? " +
                " and table_name=?";
        List<DbInforamtionDTO> result = queryRunner.query(sql, new BeanListHandler<>(DbInforamtionDTO.class), dsId, schema, tableName);
        return  result;
    }



}
