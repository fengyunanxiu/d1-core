package io.g740.d1.dao.impl;

import io.g740.d1.dao.DfFormTableSettingDao;
import io.g740.d1.dao.convert.QueryRunnerRowProcessor;
import io.g740.d1.entity.DfFormTableSettingDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.g740.d1.entity.DfFormTableSettingDO.*;

import javax.xml.crypto.Data;
import java.sql.SQLException;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/8 10:09
 * @description :
 */
public abstract class AbstractDfFormTableSettingDao implements DfFormTableSettingDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDfFormTableSettingDao.class);

    public abstract DataSource d1BasicDataSource();

    @Override
    public void updateDefaultValueByDfKeyAndFieldName(String dfKey, String fieldName, String jsonValue) throws SQLException {
        String sql = " update " + TABLE_NAME + " set " + F_FORM_FIELD_DEFAULT_VAL + " = ? where " + F_DF_KEY + " = ? and " + F_DB_FIELD_NAME + " = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource());
        qr.update(sql, jsonValue, dfKey, fieldName);
    }

    @Override
    public void updateDefaultStrategyTypeByDfKeyAndFieldName(String dfKey, String fieldName, String strategyType) throws SQLException {
        String sql = " update " + TABLE_NAME + " set " + F_FORM_FIELD_DEF_VAL_STRATEGY + " = ? where " + F_DF_KEY + " = ? and " + F_DB_FIELD_NAME + " = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource());
        qr.update(sql, strategyType, dfKey, fieldName);
    }

    @Override
    public void updateDomainAndItemByDfKeyAndFieldName(String dfKey, String fieldName, String domain, String item) throws SQLException {
        String sql = "update " + TABLE_NAME + " set " + F_FORM_FIELD_DICT_DOMAIN_NAME + " = ?," + F_FORM_FIELD_DICT_ITEM + " = ? where "
                + F_DF_KEY + " = ? and " + F_DB_FIELD_NAME + " = ?";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource());
        qr.update(sql, domain, item, dfKey, fieldName);
    }

    @Override
    public DfFormTableSettingDO queryByDfKeyAndFieldName(String dfKey, String fieldName) throws SQLException {
        String sql = "select * from " + TABLE_NAME + " where " + F_DF_KEY + " = ? and " + F_DB_FIELD_NAME + " = ?" ;
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource());
        return qr.query(sql, new BeanHandler<>(DfFormTableSettingDO.class, new QueryRunnerRowProcessor()), dfKey, fieldName);
    }

}
