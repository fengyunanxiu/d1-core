package ai.sparklabinc.d1.dao.mysql;

import ai.sparklabinc.d1.dao.DataDaoType;
import ai.sparklabinc.d1.dao.DsKeyBasicConfigDao;
import ai.sparklabinc.d1.datasource.Constants;
import ai.sparklabinc.d1.datasource.DataSourceFactory;
import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DsKeyInfoDTO;
import ai.sparklabinc.d1.entity.DsKeyBasicConfigDO;
import ai.sparklabinc.d1.util.DateUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
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
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 20:49
 * @description :
 */
@Repository("MysqlDsKeyBasicConfigDaoImpl")
public class MysqlDsKeyBasicConfigDaoImpl implements DsKeyBasicConfigDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDsKeyBasicConfigDaoImpl.class);

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.MYSQL;
    }




    @Override
    public DsKeyBasicConfigDO getDsKeyBasicConfigByDsKey(String dataSourceKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,null));
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
    public List<DbInforamtionDTO> getDataSourceKey(Long dsId, String schema, String tableName) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,null));
        String sql ="select  id," +
                "       ds_key as label," +
                "       4 as level" +
                " from ds_key_basic_config" +
                " where fk_db_id=? " +
                " and schema=? " +
                " and table_name=?";
        List<DbInforamtionDTO> result = queryRunner.query(sql, new BeanListHandler<>(DbInforamtionDTO.class), dsId, schema, tableName);
        return  result;
    }


    @Override
    public List<DsKeyInfoDTO> getAllDataSourceKey() throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,null));
        String sql ="select  id," +
                "       fk_db_id as fkDbId," +
                "       schema ," +
                "       table_name as tableName," +
                "       ds_key as label," +
                "       4 as level" +
                " from ds_key_basic_config" +
                " where fk_db_id is not null " +
                " and schema is not null " +
                " and table_name is not null";
        List<DsKeyInfoDTO> result = queryRunner.query(sql, new BeanListHandler<>(DsKeyInfoDTO.class));
        return  result;
    }

    @Override
    public Integer addDataSourceKey(DsKeyBasicConfigDO dsKeyBasicConfigDO) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,null));
        String sql ="insert into ds_key_basic_config( ds_key, fk_db_id, schema, table_name, " +
                " description, gmt_create, gmt_modified)" +
                " values ( ?, ?, ?, ?, ?, ?, ?) ";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectParams={dsKeyBasicConfigDO.getDsKey(),
                dsKeyBasicConfigDO.getFkDbId(),
                dsKeyBasicConfigDO.getSchema(),
                dsKeyBasicConfigDO.getTableName(),
                dsKeyBasicConfigDO.getDescription(),
                now,now};
        int result = queryRunner.update(sql, objectParams);
        return  result;
    }


    @Override
    public Integer updateDataSourceKey(String dsKey,String newDsKey,String description) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,null));
        String sql =" update ds_key_basic_config set ds_key = ?,description = ?,gmt_modified = ?" +
                    " where ds_key = ? ";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectParams={newDsKey,description,now,dsKey};
        int result = queryRunner.update(sql, objectParams);
        return  result;
    }

    @Override
    public Integer deleteDataSourceKey(String dsKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,null));
        String sql="delete from ds_key_basic_config where ds_key = ? ";
        int result=queryRunner.update(sql,dsKey);
        return result;
    }

    @Override
    public Long addDataSourceKeyAndReturnId(DsKeyBasicConfigDO dsKeyBasicConfigDO) throws Exception {
        Connection conn=null;
        Long id = 0L;
        try {
            String sql = "insert into ds_key_basic_config( ds_key, fk_db_id, schema, table_name, " +
                    " description, gmt_create, gmt_modified)" +
                    " values ( ?, ?, ?, ?, ?, ?, ?) ";
            DataSource dataSource = dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null);
            conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            String now = DateUtils.ofLongStr(new java.util.Date());
            //绑定参数
            bindParameters(preparedStatement, dsKeyBasicConfigDO.getDsKey(),
                    dsKeyBasicConfigDO.getFkDbId(),
                    dsKeyBasicConfigDO.getSchema(),
                    dsKeyBasicConfigDO.getTableName(),
                    dsKeyBasicConfigDO.getDescription(),
                    now,now);
            preparedStatement.execute();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            //获取生成的id
            while (rs.next()) {
                id = rs.getLong(1);
            }
        }catch (Exception e){
            throw  e;
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return id;
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
