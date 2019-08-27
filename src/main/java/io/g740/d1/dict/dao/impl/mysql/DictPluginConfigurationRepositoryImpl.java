package io.g740.d1.dict.dao.impl.mysql;

import io.g740.d1.dao.convert.QueryRunnerRowProcessor;
import io.g740.d1.dict.dao.DictPluginConfigurationRepository;
import io.g740.d1.util.UUIDUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import io.g740.d1.dict.entity.DictPluginConfigurationDO;

import static io.g740.d1.dict.entity.DictPluginConfigurationDO.*;


import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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

    /**
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

    @Override
    public DictPluginConfigurationDO create(DictPluginConfigurationDO dictPluginConfigurationDO) throws SQLException {
        String sql = "insert into " + TABLE_NAME + " (" + F_ID + "," + F_DOMAIN + "," + F_ITEM + "," + F_ENABLE + "," + F_TYPE + "," + F_PARAM + "," + F_CRON + ") values (?, ?, ?, ?, ?, ?, ?) ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.insert(sql, new BeanHandler<>(DictPluginConfigurationDO.class, new QueryRunnerRowProcessor()),
                UUIDUtils.compress(),
                dictPluginConfigurationDO.getFieldDomain(),
                dictPluginConfigurationDO.getFieldItem(),
                dictPluginConfigurationDO.getFieldEnable(),
                dictPluginConfigurationDO.getFieldType(),
                dictPluginConfigurationDO.getFieldParam(),
                dictPluginConfigurationDO.getFieldCron());
    }

    @Override
    public void update(DictPluginConfigurationDO dictPluginConfigurationDO) throws SQLException {
        String sql = "update " + TABLE_NAME + " set " + F_DOMAIN + " = ?, " + F_ITEM + " = ?," + F_ENABLE + " = ?, " + F_TYPE + " = ?, " + F_PARAM + " = ?, " + F_CRON + " = ? where " + F_ID + " = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        qr.update(sql, dictPluginConfigurationDO.getFieldDomain(),
                dictPluginConfigurationDO.getFieldItem(),
                dictPluginConfigurationDO.getFieldEnable(),
                dictPluginConfigurationDO.getFieldType(),
                dictPluginConfigurationDO.getFieldParam(),
                dictPluginConfigurationDO.getFieldCron(),
                dictPluginConfigurationDO.getFieldId());
    }


}
