package io.g740.d1.dao.impl.sqlserver;

import io.g740.d1.dao.DataDaoType;
import io.g740.d1.dao.DfKeyBasicConfigDao;
import io.g740.d1.datasource.DataSourceFactory;
import io.g740.d1.dto.DbInformationDTO;
import io.g740.d1.dto.DfKeyInfoDTO;
import io.g740.d1.entity.DfKeyBasicConfigDO;
import io.g740.d1.util.DateUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 20:49
 * @description :
 */
@Repository("SqlserverDfKeyBasicConfigDaoImpl")
public class SqlserverDfKeyBasicConfigDaoImpl implements DfKeyBasicConfigDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlserverDfKeyBasicConfigDaoImpl.class);

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;

    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.SQLSERVER;
    }




    @Override
    public DfKeyBasicConfigDO getDfKeyBasicConfigByDfKey(String dataFacetKey) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String querySql = "select * from df_key_basic_config where df_key = ? ";
        LOGGER.info("querySql:{}", querySql);

        DfKeyBasicConfigDO dfKeyBasicConfigDO = queryRunner.query(querySql, new ResultSetHandler<DfKeyBasicConfigDO>() {
            @Override
            public DfKeyBasicConfigDO handle(ResultSet resultSet) throws SQLException {
                DfKeyBasicConfigDO resultDfKeyBasicConfigDO = null;
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String gmtCreateStr =  resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");
                    String dfKey = resultSet.getString("df_key");
                    Long fkDbId = resultSet.getLong("fk_db_id");
                    String schema = resultSet.getString("schema_name");
                    String tableName = resultSet.getString("table_name");
                    String description = resultSet.getString("description");

                    resultDfKeyBasicConfigDO = new DfKeyBasicConfigDO();
                    resultDfKeyBasicConfigDO.setId(id);
                    resultDfKeyBasicConfigDO.setGmtCreate(gmtCreateStr);
                    resultDfKeyBasicConfigDO.setGmtModified(gmtModifiedStr);
                    resultDfKeyBasicConfigDO.setDfKey(dfKey);
                    resultDfKeyBasicConfigDO.setFkDbId(fkDbId);
                    resultDfKeyBasicConfigDO.setSchemaName(schema);
                    resultDfKeyBasicConfigDO.setTableName(tableName);
                    resultDfKeyBasicConfigDO.setDescription(description);
                }
                return resultDfKeyBasicConfigDO;
            }
        }, dataFacetKey);

        return dfKeyBasicConfigDO;
    }

    @Override
    public List<DbInformationDTO> getDataFacetKey(Long dsId, String schema, String tableName) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql ="select  id," +
                "       df_key as label," +
                "       4 as level" +
                " from df_key_basic_config" +
                " where fk_db_id=? " +
                " and schema_name=? " +
                " and table_name=?";
        List<DbInformationDTO> result = queryRunner.query(sql, new BeanListHandler<>(DbInformationDTO.class), dsId, schema, tableName);
        return  result;
    }


    @Override
    public List<DfKeyInfoDTO> getAllDataFacetKey() throws SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql ="select  id," +
                "       fk_db_id as fkDbId," +
                "       schema_name as schemaName ," +
                "       table_name as tableName," +
                "       df_key as label," +
                "       4 as level" +
                " from df_key_basic_config" +
                " where fk_db_id is not null " +
                " and schema_name is not null " +
                " and table_name is not null";
        List<DfKeyInfoDTO> result = queryRunner.query(sql, new BeanListHandler<>(DfKeyInfoDTO.class));
        return  result;
    }

    @Override
    public Integer addDataFacetKey(DfKeyBasicConfigDO dfKeyBasicConfigDO) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql ="insert into df_key_basic_config( df_key, fk_db_id, schema_name, table_name, " +
                " description, gmt_create, gmt_modified)" +
                " values ( ?, ?, ?, ?, ?, ?, ?) ";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectParams={dfKeyBasicConfigDO.getDfKey(),
                dfKeyBasicConfigDO.getFkDbId(),
                dfKeyBasicConfigDO.getSchemaName(),
                dfKeyBasicConfigDO.getTableName(),
                dfKeyBasicConfigDO.getDescription(),
                now,now};
        int result = queryRunner.update(sql, objectParams);
        return  result;
    }


    @Override
    public Integer updateDataFacetKey(String dfKey,String newDfKey,String description) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql =" update df_key_basic_config set df_key = ?,description = ?,gmt_modified = ?" +
                " where df_key = ? ";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectParams={newDfKey,description,now,dfKey};
        int result = queryRunner.update(sql, objectParams);
        return  result;
    }

    @Override
    public Integer deleteDataFacetKey(String dfKey) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql="delete from df_key_basic_config where df_key = ? ";
        int result=queryRunner.update(sql,dfKey);
        return result;
    }


    @Override
    public Long addDataFacetKeyAndReturnId(DfKeyBasicConfigDO dfKeyBasicConfigDO) throws Exception {
        Connection conn=null;
        Long id = 0L;
        try {
            String sql = "insert into df_key_basic_config( df_key, fk_db_id, schema_name, table_name, " +
                    " description, gmt_create, gmt_modified)" +
                    " values ( ?, ?, ?, ?, ?, ?, ?) ";
            DataSource dataSource = d1BasicDataSource;
            conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            String now = DateUtils.ofLongStr(new java.util.Date());
            //绑定参数
            bindParameters(preparedStatement, dfKeyBasicConfigDO.getDfKey(),
                    dfKeyBasicConfigDO.getFkDbId(),
                    dfKeyBasicConfigDO.getSchemaName(),
                    dfKeyBasicConfigDO.getTableName(),
                    dfKeyBasicConfigDO.getDescription(),
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
