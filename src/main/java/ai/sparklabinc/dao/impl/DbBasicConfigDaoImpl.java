package ai.sparklabinc.dao.impl;

import ai.sparklabinc.dao.DbBasicConfigDao;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.dto.DbInforamtionDTO;
import ai.sparklabinc.entity.DbBasicConfigDO;
import ai.sparklabinc.util.DateUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/3 14:00
 * @description:
 */
@Repository
public class DbBasicConfigDaoImpl implements DbBasicConfigDao {
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public DbBasicConfigDO findById(Long id) throws SQLException, IOException {

        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null));

        String querySql = "select * from db_basic_config where id = ? ";


        DbBasicConfigDO dbBasicConfigDO = queryRunner.query(querySql, new ResultSetHandler<DbBasicConfigDO>() {
            @Override
            public DbBasicConfigDO handle(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    DbBasicConfigDO dbBasicConfigDO = new DbBasicConfigDO();
                    Long id = resultSet.getLong("id");
                    String gmtCreateStr = resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");
                    String type = resultSet.getString("type");
                    String name = resultSet.getString("name");
                    String host = resultSet.getString("host");
                    Integer port = resultSet.getInt("port");
                    String user = resultSet.getString("user");
                    String password = resultSet.getString("password");
                    String url = resultSet.getString("url");
                    //封装数据
                    dbBasicConfigDO.setId(id);
                    dbBasicConfigDO.setGmtCreate(gmtCreateStr);
                    dbBasicConfigDO.setGmtModified(gmtModifiedStr);
                    dbBasicConfigDO.setType(type);
                    dbBasicConfigDO.setName(name);
                    dbBasicConfigDO.setHost(host);
                    dbBasicConfigDO.setPort(port);
                    dbBasicConfigDO.setUser(user);
                    dbBasicConfigDO.setPassword(password);
                    dbBasicConfigDO.setUrl(url);
                    return dbBasicConfigDO;
                }
                return null;

            }
        }, id);
        return dbBasicConfigDO;
    }


    @Override
    public Long add(DbBasicConfigDO dbBasicConfigDO) throws SQLException, IOException {

        String sql = "insert into db_basic_config( gmt_create, gmt_modified, type, name, host, port, user, password,url)" +
                "values ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DataSource dataSource = dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null);
        Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        String now = DateUtils.ofLongStr(new java.util.Date());
        //绑定参数
        bindParameters(preparedStatement, now, now,
                dbBasicConfigDO.getType(),
                dbBasicConfigDO.getName(),
                dbBasicConfigDO.getHost(),
                dbBasicConfigDO.getPort(),
                dbBasicConfigDO.getUser(),
                dbBasicConfigDO.getPassword(),
                dbBasicConfigDO.getUrl());
        preparedStatement.execute();
        ResultSet rs = preparedStatement.getGeneratedKeys();
        //返回
        Long id = 0L;
        while (rs.next()) {
            id = rs.getLong(1);
        }
        if (conn != null) {
            conn.close();
        }
        return id;
    }

    @Override
    public Integer delete(Long dsId) throws SQLException, IOException {
        String sql = "delete from db_basic_config where id = ?";
        DataSource dataSource = dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null);
        Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        //绑定参数
        bindParameters(preparedStatement,dsId);
        int update = preparedStatement.executeUpdate();
        if (conn != null) {
            conn.close();
        }
        return update;
    }

    @Override
    public List<DbInforamtionDTO> selectDataSources(Long dsId) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null));
        String querySql = "select id,name as label,1 as level,'' as type from db_basic_config where 1=1 ";
        List<DbInforamtionDTO> dbInforamtionDTOList=null;
        if(dsId!=null){
            querySql+=" and id = ?";
            dbInforamtionDTOList=queryRunner.query(querySql, new BeanListHandler<>(DbInforamtionDTO.class),dsId);
        }else {
            dbInforamtionDTOList=queryRunner.query(querySql, new BeanListHandler<>(DbInforamtionDTO.class));
        }
        return  dbInforamtionDTOList;
    }

    @Override
    public List<Map<String, Object>> selectDataSourceProperty(Long dsId) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null));
        String querySql = "select * from ds_full_config_view where id = ?";
        List<Map<String, Object>> result = queryRunner.query(querySql, new MapListHandler(),dsId);
        return  result;
    }

    @Override
    public Integer editDataSourceProperty(DbBasicConfigDO dbBasicConfigDO) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null));
        String sql="update db_basic_config set  gmt_modified = ?," +
                "   type = ?," +
                "   name = ?," +
                "   host = ?," +
                "   port = ?," +
                "   user = ?," +
                "   password = ?," +
                "   url = ?" +
                "where  id = ?";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectsParams={now,
                dbBasicConfigDO.getType(),
                dbBasicConfigDO.getName(),
                dbBasicConfigDO.getHost(),
                dbBasicConfigDO.getPort(),
                dbBasicConfigDO.getUser(),
                dbBasicConfigDO.getPassword(),
                dbBasicConfigDO.getUrl(),
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
