package ai.sparklabinc.d1.dao.impl.mysql;

import ai.sparklabinc.d1.dao.DsTreeMenuCacheDao;
import ai.sparklabinc.d1.entity.DsTreeMenuCacheDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @function:
 * @author: DAM
 * @date: 2019/8/11 20:53
 * @description:
 * @version: V1.0
 */
@Repository("MysqlDsTreeMenuCacheDaoImpl")
public class MysqlDsTreeMenuCacheDaoImpl implements DsTreeMenuCacheDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(MysqlDsTreeMenuCacheDaoImpl.class);

    @Resource(name = "D1BasicDataSource")
    private DataSource d1BasicDataSource;

    @Override
    public Integer addDsTreeMenuCache(DsTreeMenuCacheDO dsTreeMenuCacheDO) throws Exception {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        Object[] objectParams = new Object[]{
                dsTreeMenuCacheDO.getDsId(),
                dsTreeMenuCacheDO.getDsBasicInfo(),
                dsTreeMenuCacheDO.getDsSchemaInfo(),
                dsTreeMenuCacheDO.getDsTableViewInfo(),
                dsTreeMenuCacheDO.getDsKeyInfo(),
        };
        String sql = "insert into ds_tree_menu_cache (ds_id, ds_basic_info, ds_schema_info, ds_table_view_info, ds_key_info)" +
                " value ( ?, ?, ?, ?, ?)";
        LOGGER.info("sql string:{}",sql);
        return queryRunner.update(sql, objectParams);

    }

    @Override
    public Integer updateDsTreeMenuCache(DsTreeMenuCacheDO dsTreeMenuCacheDO) throws Exception {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        Object[] objectParams = new Object[]{
                dsTreeMenuCacheDO.getDsBasicInfo(),
                dsTreeMenuCacheDO.getDsSchemaInfo(),
                dsTreeMenuCacheDO.getDsTableViewInfo(),
                dsTreeMenuCacheDO.getDsKeyInfo(),
                dsTreeMenuCacheDO.getDsId()};
        String sql = "update ds_tree_menu_cache " +
                " set ds_basic_info      = ?," +
                "     ds_schema_info     = ?," +
                "     ds_table_view_info = ?," +
                "     ds_key_info= ?" +
                " where ds_id = ?";
        LOGGER.info("sql string:{}",sql);
        return queryRunner.update(sql, objectParams);
    }

    @Override
    public DsTreeMenuCacheDO getDsTreeMenuCache(Long dsId) throws Exception {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql = "select ds_id as dsId," +
                "   ds_basic_info as dsBasicInfo," +
                "   ds_schema_info as dsSchemaInfo ," +
                "   ds_table_view_info as dsTableViewInfo," +
                "   ds_key_info as dsKeyInfo" +
                " from ds_tree_menu_cache" +
                " where ds_id = ?";
        LOGGER.info("sql string:{}",sql);
        DsTreeMenuCacheDO dsTreeMenuCacheDO = queryRunner.query(sql, new BeanHandler<>(DsTreeMenuCacheDO.class), dsId);
        return dsTreeMenuCacheDO;
    }
}
