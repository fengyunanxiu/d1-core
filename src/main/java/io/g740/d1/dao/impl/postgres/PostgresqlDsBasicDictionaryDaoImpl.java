package io.g740.d1.dao.impl.postgres;

import io.g740.d1.dao.DataDaoType;
import io.g740.d1.dao.DsBasicDictionaryDao;
import io.g740.d1.datasource.DataSourceFactory;
import io.g740.d1.entity.DsBasicDictionaryDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 15:18
 * @description :
 */
@Repository("PostgresqlDsBasicDictionaryDaoImpl")
public class PostgresqlDsBasicDictionaryDaoImpl implements DsBasicDictionaryDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresqlDsBasicDictionaryDaoImpl.class);

    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;

    @Autowired
    private DataSourceFactory dataSourceFactory;
    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.POSTGRESQL;
    }


    @Override
    public List<DsBasicDictionaryDO> findListByDomainName(String domainName) throws SQLException {

        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String querySql = "select * from ds_basic_dictionary where domain_name = ?  order by gmt_modified desc ";
        LOGGER.info("querySql:{}",querySql);

        List<DsBasicDictionaryDO> dsBasicDictionaryDOList = queryRunner.query(querySql, new ResultSetHandler<List<DsBasicDictionaryDO>>() {
            @Override
            public List<DsBasicDictionaryDO> handle(ResultSet resultSet) throws SQLException {
                List<DsBasicDictionaryDO> dsBasicDictionaryDOS = new ArrayList<>();
                DsBasicDictionaryDO dsBasicDictionaryDO = null;

                while (resultSet.next()){
                    Long id = resultSet.getLong("id");
                    String domainName = resultSet.getString("domain_name");
                    String itemId = resultSet.getString("item_id");
                    String itemVal = resultSet.getString("item_val");
                    Boolean isAuto = resultSet.getBoolean("is_auto");
                    String gmtCreate = resultSet.getString("gmt_create");
                    String gmtModified = resultSet.getString("gmt_modified");

                    dsBasicDictionaryDO = new DsBasicDictionaryDO();
                    dsBasicDictionaryDO.setId(id);
                    dsBasicDictionaryDO.setDomainName(domainName);
                    dsBasicDictionaryDO.setItemId(itemId);
                    dsBasicDictionaryDO.setItemVal(itemVal);
                    dsBasicDictionaryDO.setAuto(isAuto);
                    dsBasicDictionaryDO.setGmtCreate(gmtCreate);
                    dsBasicDictionaryDO.setGmtModified(gmtModified);
                    dsBasicDictionaryDOS.add(dsBasicDictionaryDO);
                }
                return dsBasicDictionaryDOS;
            }
        },domainName);
        return dsBasicDictionaryDOList;
    }
}