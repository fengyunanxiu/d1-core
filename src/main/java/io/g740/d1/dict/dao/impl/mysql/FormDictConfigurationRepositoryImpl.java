package io.g740.d1.dict.dao.impl.mysql;

import io.g740.d1.dao.convert.QueryRunnerRowProcessor;
import io.g740.d1.dict.dao.FormDictConfigurationRepository;
import io.g740.d1.dict.entity.FormDictConfigurationDO;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.util.StringUtils;
import io.g740.d1.util.UUIDUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import static io.g740.d1.dict.entity.FormDictConfigurationDO.*;




/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 10:06
 * @description :
 */
@Repository("MySQLFormDictConfigurationRepository")
public class FormDictConfigurationRepositoryImpl implements FormDictConfigurationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormDictConfigurationRepositoryImpl.class);

    @Resource(name = "D1BasicDataSource")
    private DataSource d1BasicDataSource;

    @Override
    public List<FormDictConfigurationDO> queryByFrom(String formDfKey, String formFieldKey) throws SQLException {
        String sql = " select * from db_form_dict_configuration where field_form_df_key = ? and field_form_field_key = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanListHandler<>(FormDictConfigurationDO.class, new QueryRunnerRowProcessor()),
                formDfKey, formFieldKey);
    }

    @Override
    public FormDictConfigurationDO queryById(String id) throws SQLException {
        String sql = "select * from db_form_dict_configuration where field_id = ?";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return  qr.query(sql, new BeanHandler<>(FormDictConfigurationDO.class, new QueryRunnerRowProcessor()), id);
    }

    @Override
    public FormDictConfigurationDO add(FormDictConfigurationDO formDictConfigurationDO) throws Exception {
        String formDfKey = formDictConfigurationDO.getFieldFormDfKey();
        String formFieldKey = formDictConfigurationDO.getFieldFormFieldKey();
        String domain = formDictConfigurationDO.getFieldDomain();
        String item = formDictConfigurationDO.getFieldItem();
        if (StringUtils.isNullOrEmpty(formDfKey)
                || StringUtils.isNullOrEmpty(formFieldKey)
                || StringUtils.isNullOrEmpty(domain)
                || StringUtils.isNullOrEmpty(item)) {
            throw new ServiceException("field_form_df_key, field_form_field_key, field_domain, field_item 不能为空");
        }
        String sql = "insert into db_form_dict_configuration(field_id, field_form_df_key, field_form_field_key, field_domain, field_item) values(?, ?, ?, ?, ?)";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.insert(sql, new BeanHandler<>(FormDictConfigurationDO.class, new QueryRunnerRowProcessor()), UUIDUtils.compress(), formDfKey, formFieldKey, domain, item);
    }

    @Override
    public void update(FormDictConfigurationDO formDictConfigurationDO) throws Exception {
        String id = formDictConfigurationDO.getFieldId();
        String domain = formDictConfigurationDO.getFieldDomain();
        String item = formDictConfigurationDO.getFieldItem();
        if (StringUtils.isNullOrEmpty(id)) {
            throw new ServiceException("field_id 不能为空");
        }
        String sql = " update db_form_dict_configuration set field_domain = ?, field_item = ? where field_id = ?";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        qr.update(sql, domain, item, id);
    }

    @Override
    public void saveOrUpdateList(List<FormDictConfigurationDO> formDictConfigurationDOS) throws SQLException {
        String executeSql = "insert into db_form_dict_configuration(field_id, field_form_df_key," +
                " field_form_field_key, field_domain, field_item) values(?, ?, ?, ?, ?)  on duplicate key update field_domain = ? ,field_item =?";
        try (Connection connection = this.d1BasicDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(executeSql.toString());
             ){
            connection.setAutoCommit(false);
            for (FormDictConfigurationDO formDictConfigurationDO : formDictConfigurationDOS) {
                String formDfKey = formDictConfigurationDO.getFieldFormDfKey();
                String formFieldKey = formDictConfigurationDO.getFieldFormFieldKey();
                String domain = formDictConfigurationDO.getFieldDomain();
                String item = formDictConfigurationDO.getFieldItem();
                preparedStatement.setObject(1, UUIDUtils.compress());
                preparedStatement.setObject(2, formDfKey);
                preparedStatement.setObject(3, formFieldKey);
                preparedStatement.setObject(4, domain);
                preparedStatement.setObject(5, item);
                preparedStatement.setObject(6, domain);
                preparedStatement.setObject(7, item);
                preparedStatement.addBatch();
            }
            preparedStatement.executeLargeBatch();
            connection.commit();
        }catch (Exception e){
            LOGGER.error("jdbcTemplate.getDataSource().getConnection().commit() is Fail ");
            throw  e;
        }
    }

}
