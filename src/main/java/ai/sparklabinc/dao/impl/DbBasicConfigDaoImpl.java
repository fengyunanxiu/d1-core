package ai.sparklabinc.dao.impl;

import ai.sparklabinc.dao.DbBasicConfigDao;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.entity.DbBasicConfigDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    private QueryRunner queryRunner;

    @PostConstruct
    public void initQueryRunner() throws IOException, SQLException {
        queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,0L));
    }

    @Override
    public DbBasicConfigDO findById(Long id) throws SQLException {
        try {
            queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,0L));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String querySql = "select * from db_basic_config where id = ? ";

        DbBasicConfigDO dbBasicConfigDO = this.queryRunner.query(querySql, new ResultSetHandler<DbBasicConfigDO>() {
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
        },id);
        return  dbBasicConfigDO;
    }
}
