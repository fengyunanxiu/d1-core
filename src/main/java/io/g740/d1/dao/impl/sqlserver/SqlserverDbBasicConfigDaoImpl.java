package io.g740.d1.dao.impl.sqlserver;

import io.g740.d1.dao.DataDaoType;
import io.g740.d1.dao.DbBasicConfigDao;
import io.g740.d1.dto.DbInformationDTO;
import io.g740.d1.entity.DbBasicConfigDO;
import io.g740.d1.util.DateUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/3 14:00
 * @description:
 */
@Repository("SqlserverDbBasicConfigDaoImpl")
public class SqlserverDbBasicConfigDaoImpl implements DbBasicConfigDao {
    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;

    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.SQLSERVER;
    }

    @Override
    public DbBasicConfigDO findById(Long id) throws SQLException {

        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);

        String querySql = "select * from db_basic_config where id = ? ";

        DbBasicConfigDO dbBasicConfigDO = queryRunner.query(querySql, new ResultSetHandler<DbBasicConfigDO>() {
            @Override
            public DbBasicConfigDO handle(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    DbBasicConfigDO dbBasicConfigDO = new DbBasicConfigDO();
                    Long id = resultSet.getLong("id");
                    String gmtCreateStr = resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");
                    String type = resultSet.getString("db_type");
                    String name = resultSet.getString("db_name");
                    String host = resultSet.getString("db_host");
                    Integer port = resultSet.getInt("db_port");
                    String user = resultSet.getString("db_user");
                    String password = resultSet.getString("db_password");
                    String url = resultSet.getString("db_url");
                    String otherParams = resultSet.getString("other_params");
                    //封装数据
                    dbBasicConfigDO.setId(id);
                    dbBasicConfigDO.setGmtCreate(gmtCreateStr);
                    dbBasicConfigDO.setGmtModified(gmtModifiedStr);
                    dbBasicConfigDO.setDbType(type);
                    dbBasicConfigDO.setDbName(name);
                    dbBasicConfigDO.setDbHost(host);
                    dbBasicConfigDO.setDbPort(port);
                    dbBasicConfigDO.setDbUser(user);
                    dbBasicConfigDO.setDbPassword(password);
                    dbBasicConfigDO.setDbUrl(url);
                    dbBasicConfigDO.setOtherParams(otherParams);
                    return dbBasicConfigDO;
                }
                return null;

            }
        }, id);
        return dbBasicConfigDO;
    }


    @Override
    public Long add(DbBasicConfigDO dbBasicConfigDO) throws SQLException {
        Connection conn=null;
        Long id = 0L;
        try {

            String sql = "insert into db_basic_config( gmt_create, gmt_modified, db_type, db_name, db_host, db_port, db_user, db_password,db_url,other_params)" +
                    "values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            DataSource dataSource = d1BasicDataSource;
            conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            String now = DateUtils.ofLongStr(new java.util.Date());
            //绑定参数
            bindParameters(preparedStatement, now, now,
                    dbBasicConfigDO.getDbType(),
                    dbBasicConfigDO.getDbName(),
                    dbBasicConfigDO.getDbHost(),
                    dbBasicConfigDO.getDbPort(),
                    dbBasicConfigDO.getDbUser(),
                    dbBasicConfigDO.getDbPassword(),
                    dbBasicConfigDO.getDbUrl(),
                    dbBasicConfigDO.getOtherParams());
            preparedStatement.execute();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            //获取生成的id
            while (rs.next()) {
                id = rs.getLong(1);
            }
        }finally {
            if (conn != null) {
                conn.close();
            }
        }
        return id;
    }


    @Override
    public Integer delete(Long dsId) throws SQLException {
        Connection conn=null;
        int update=0;
        try {
            String sql = "delete from db_basic_config where id = ?";
            DataSource dataSource = d1BasicDataSource;
            conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            //绑定参数
            bindParameters(preparedStatement, dsId);
            update = preparedStatement.executeUpdate();
        }finally {
            if (conn != null) {
                conn.close();
            }
        }
        return update;
    }

    @Override
    public List<DbInformationDTO> selectDataSources(Long dsId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String querySql = "select id,db_name as label,1 as level,'' as type from db_basic_config where 1=1 ";
        List<DbInformationDTO> dbInformationDTOList =null;
        if(dsId!=null){
            querySql+=" and id = ? order by id asc";
            dbInformationDTOList =queryRunner.query(querySql, new BeanListHandler<>(DbInformationDTO.class),dsId);
        }else {
            querySql+=" order by id asc";
            dbInformationDTOList =queryRunner.query(querySql, new BeanListHandler<>(DbInformationDTO.class));
        }
        return dbInformationDTOList;
    }

    @Override
    public List<Map<String, Object>> selectDataSourceProperty(Long dsId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String querySql = "select * from ds_full_config_view where id = ?";
        List<Map<String, Object>> result = queryRunner.query(querySql, new MapListHandler(),dsId);
        return  result;
    }


    @Override
    public Integer editDataSourceProperty(DbBasicConfigDO dbBasicConfigDO) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql="update db_basic_config set  gmt_modified = ?," +
                "   db_type = ?," +
                "   db_name = ?," +
                "   db_host = ?," +
                "   db_port = ?," +
                "   db_user = ?," +
                "   db_password = ?," +
                "   db_url = ? ," +
                "   other_params = ? " +
                "where  id = ?";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectsParams={now,
                dbBasicConfigDO.getDbType(),
                dbBasicConfigDO.getDbName(),
                dbBasicConfigDO.getDbHost(),
                dbBasicConfigDO.getDbPort(),
                dbBasicConfigDO.getDbUser(),
                dbBasicConfigDO.getDbPassword(),
                dbBasicConfigDO.getDbUrl(),
                dbBasicConfigDO.getOtherParams(),
                dbBasicConfigDO.getId()
        };
        int update = queryRunner.update(sql,objectsParams);
        return update;
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
