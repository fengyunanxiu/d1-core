package ai.sparklabinc.d1.defaults.dao.impl.mysql;

import ai.sparklabinc.d1.dao.convert.QueryRunnerRowProcessor;
import ai.sparklabinc.d1.defaults.dao.DefaultsConfigurationRepository;
import ai.sparklabinc.d1.defaults.entity.DefaultConfigurationType;
import ai.sparklabinc.d1.defaults.entity.DefaultsConfigurationDO;
import ai.sparklabinc.d1.util.StringUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 15:12
 * @description :
 */
@Repository("MySQLDefaultsConfigurationRepository")
public class DefaultsConfigurationRepositoryImpl implements DefaultsConfigurationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultsConfigurationRepositoryImpl.class);

    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;

    @Override
    public List<DefaultsConfigurationDO> queryByDfKeyAndFieldKey(String dfKey, String fieldKey) throws SQLException {
        String sql = " select * from " + DefaultsConfigurationDO.TABLE_NAME + " where field_form_df_key = ? and field_form_field_key = ?";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanListHandler<>(DefaultsConfigurationDO.class, new QueryRunnerRowProcessor()), dfKey, fieldKey);
    }

    @Override
    public DefaultsConfigurationDO queryById(String id) throws SQLException {
        String sql = " select * from " + DefaultsConfigurationDO.TABLE_NAME + " where field_id = ?";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanHandler<>(DefaultsConfigurationDO.class, new QueryRunnerRowProcessor()), id);
    }

    @Override
    public List<DefaultsConfigurationDO> queryAll() throws SQLException {
        String sql = "select * from " + DefaultsConfigurationDO.TABLE_NAME;
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanListHandler<>(DefaultsConfigurationDO.class, new QueryRunnerRowProcessor()));
    }

    @Override
    public DefaultsConfigurationDO insert(DefaultsConfigurationDO defaultsConfigurationDO) throws Exception {
        String fieldFormDfKey = defaultsConfigurationDO.getFieldFormDfKey();
        String fieldFormFieldKey = defaultsConfigurationDO.getFieldFormFieldKey();
        DefaultConfigurationType fieldType = defaultsConfigurationDO.getFieldType();
        String fieldPluginConf = defaultsConfigurationDO.getFieldPluginConf();
        String fieldManualConf = defaultsConfigurationDO.getFieldManualConf();
        if (StringUtils.isNullOrEmpty(fieldFormDfKey)
                || StringUtils.isNullOrEmpty(fieldFormFieldKey)) {
            throw new Exception("field form df key and field form field key 不能为空");
        }
        String sql = " insert into " + DefaultsConfigurationDO.TABLE_NAME +
                " (field_id, field_form_df_key, field_form_field_key, field_type, field_plugin_conf, field_manual_conf) " +
                " values(?, ?, ?, ?, ?, ?)";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.insert(sql, new BeanHandler<>(DefaultsConfigurationDO.class, new QueryRunnerRowProcessor()),
                UUID.randomUUID().toString(), fieldFormDfKey, fieldFormFieldKey, fieldType.name(), fieldPluginConf, fieldManualConf);
    }

    @Override
    public void update(DefaultsConfigurationDO defaultsConfigurationDO) throws Exception {
        String fieldId = defaultsConfigurationDO.getFieldId();
        String fieldFormDfKey = defaultsConfigurationDO.getFieldFormDfKey();
        String fieldFormFieldKey = defaultsConfigurationDO.getFieldFormFieldKey();
        DefaultConfigurationType fieldType = defaultsConfigurationDO.getFieldType();
        String fieldPluginConf = defaultsConfigurationDO.getFieldPluginConf();
        String fieldManualConf = defaultsConfigurationDO.getFieldManualConf();
        if (StringUtils.isNullOrEmpty(fieldId)
                || StringUtils.isNullOrEmpty(fieldFormDfKey)
                || StringUtils.isNullOrEmpty(fieldFormFieldKey)) {
            throw new Exception("field id, field from df key, field form field key 不能为空");
        }
        String sql = " update " + DefaultsConfigurationDO.TABLE_NAME +
                " set field_type = ?, field_plugin_conf = ?, field_manual_conf = ?" +
                " where field_id = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        qr.update(sql, fieldType.name(), fieldPluginConf, fieldManualConf, fieldId);
    }

}
