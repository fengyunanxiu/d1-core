package ai.sparklabinc.d1.dao.impl;

import ai.sparklabinc.d1.dao.DfFormTableSettingDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public abstract DataSource d1BasicDataSource ();

    @Override
    public void updateDefaultValueByDfKeyAndFieldName(String dfKey, String fieldName, String jsonValue) throws SQLException {
        String sql = " update df_form_table_setting set form_field_default_val = ? where df_key = ? and db_field_name = ? ";
        QueryRunner qr = new QueryRunner(d1BasicDataSource());
        qr.update(sql, jsonValue, dfKey, fieldName);
    }

}
