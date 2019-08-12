package ai.sparklabinc.d1.dict.dao.impl.mysql;

import ai.sparklabinc.d1.dao.convert.QueryRunnerRowProcessor;
import ai.sparklabinc.d1.dict.dao.DictPluginConfigurationRepository;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ai.sparklabinc.d1.dict.entity.DictPluginConfigurationDO;

import static ai.sparklabinc.d1.dict.entity.DictPluginConfigurationDO.*;


import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 15:22
 * @description :
 */
@Repository("MySQLDictPluginConfigurationRepositoryImpl")
public class DictPluginConfigurationRepositoryImpl implements DictPluginConfigurationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictPluginConfigurationRepositoryImpl.class);

    @Resource(name = "D1BasicDataSource")
    private DataSource d1BasicDataSource;

    /**
     * 获取所有的可执行插件
     *
     * @return
     */
    @Override
    public List<DictPluginConfigurationDO> findAllEnable() throws SQLException {
        String sql = " select * from " + TABLE_NAME + " where " + F_ENABLE + " = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanListHandler<>(DictPluginConfigurationDO.class, new QueryRunnerRowProcessor()), true);
    }

    @Override
    public List<DictPluginConfigurationDO> findAllEnableWithLockTransaction(Connection connection) throws SQLException {
        String sql = " select * from " + TABLE_NAME + " where " + F_ENABLE + " = ?";
        QueryRunner qr = new QueryRunner();
        return qr.query(connection, sql, new BeanListHandler<>(DictPluginConfigurationDO.class, new QueryRunnerRowProcessor()), true);
    }

    /**
     *
     * @param domain
     * @param item
     * @return
     * @throws SQLException
     */
    @Override
    public DictPluginConfigurationDO findByDomainAndItem(String domain, String item) throws SQLException {
        String sql = " select * from " + TABLE_NAME + " where " + F_DOMAIN + " = ? and " + F_ITEM + " = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanHandler<>(DictPluginConfigurationDO.class, new QueryRunnerRowProcessor()), domain, item);
    }


}
