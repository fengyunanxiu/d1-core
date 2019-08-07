package ai.sparklabinc.d1.dict.dao.impl.mysql;

import ai.sparklabinc.d1.dao.convert.QueryRunnerRowProcessor;
import ai.sparklabinc.d1.dict.dao.DictRepository;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.exception.ServiceException;
import ai.sparklabinc.d1.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 15:41
 * @description :
 */
@Repository("MySQLDictRepository")
public class DictRepositoryImpl implements DictRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictRepositoryImpl.class);

    @Resource(name = "D1BasicDataSource")
    private DataSource d1BasicDataSource;

    /**
     * @param params
     * @param offset
     * @param pageSize
     * @return
     * @throws SQLException
     */
    @Override
    public List<DictDO> query(Map<String, String> params, long offset, int pageSize) throws SQLException {

        StringBuilder sqlBuilder = new StringBuilder("select * from " + DictDO.TABLE_NAME + " where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sqlBuilder.append(" and " + key + " like ? ");
                paramList.add("%" + value + "%");
            }
        }
        // 分页信息
        sqlBuilder.append(" limit ?, ? ");
        paramList.add(offset);
        paramList.add(pageSize);
        LOGGER.info("query sql: {}", sqlBuilder.toString());
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sqlBuilder.toString(), new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), paramList.toArray(new Object[0]));
    }

    /**
     * @param dictDOList
     * @return
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public List<DictDO> batchInsert(List<DictDO> dictDOList) throws ServiceException, SQLException {
        if (dictDOList == null || dictDOList.isEmpty()) {
            throw new ServiceException("dict list is null");
        }
        List<Object[]> paramList = new ArrayList<>();
        String sql = String.format(" insert into %s (%s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?)",
                DictDO.TABLE_NAME, DictDO.F_ID, DictDO.F_DOMAIN, DictDO.F_ITEM, DictDO.F_VALUE, DictDO.F_LABEL, DictDO.F_SEQUENCE, DictDO.F_ENABLE, DictDO.F_PARENT_ID);
        for (DictDO dictDO : dictDOList) {
            if (dictDO == null
                    || StringUtils.isNullOrEmpty(dictDO.getFieldDomain())
                    || StringUtils.isNullOrEmpty(dictDO.getFieldItem())
                    || StringUtils.isNullOrEmpty(dictDO.getFieldValue())
                    || StringUtils.isNullOrEmpty(dictDO.getFieldSequence())
                    || StringUtils.isNullOrEmpty(dictDO.getFieldEnable())) {
                throw new ServiceException("domain, item, value, sequence, enable不能为空");
            }
            Object[] param = new Object[]{
                    UUID.randomUUID().toString(),
                    dictDO.getFieldDomain(),
                    dictDO.getFieldItem(),
                    dictDO.getFieldValue(),
                    dictDO.getFieldLabel(),
                    dictDO.getFieldSequence(),
                    dictDO.getFieldEnable(),
                    dictDO.getFieldParentId()
            };
            paramList.add(param);
        }
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        LOGGER.info("batch insert sql: {}", sql);
        return qr.insertBatch(sql, new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), paramList.toArray(new Object[0][0]));
    }


    /**
     * @param dictDOList
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public void batchUpdate(List<DictDO> dictDOList) throws ServiceException, SQLException {
        if (dictDOList == null || dictDOList.isEmpty()) {
            throw new ServiceException("dict list is null");
        }
        String sql = "update " + DictDO.TABLE_NAME + " set field_domain = ?, field_item = ?, field_value = ?, field_label = ?, field_sequence = ?, field_enable = ?, field_parent_id = ? where field_id = ?";
        List<Object[]> paramList = new ArrayList<>();
        for (DictDO dictDO : dictDOList) {
            if (dictDO == null
                    || StringUtils.isNullOrEmpty(dictDO.getFieldId())
                    || StringUtils.isNullOrEmpty(dictDO.getFieldDomain())
                    || StringUtils.isNullOrEmpty(dictDO.getFieldItem())
                    || StringUtils.isNullOrEmpty(dictDO.getFieldValue())
                    || StringUtils.isNullOrEmpty(dictDO.getFieldSequence())
                    || StringUtils.isNullOrEmpty(dictDO.getFieldEnable())) {
                throw new ServiceException("field_id, field_domain, field_item, field_value, field_sequence, field_enable不能为空");
            }
            Object[] param = new Object[]{
                    dictDO.getFieldDomain(),
                    dictDO.getFieldItem(),
                    dictDO.getFieldValue(),
                    dictDO.getFieldLabel(),
                    dictDO.getFieldSequence(),
                    dictDO.getFieldEnable(),
                    dictDO.getFieldParentId(),
                    dictDO.getFieldId()
            };
            paramList.add(param);
        }
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        LOGGER.info("bath update sql{}", sql);
        qr.batch(sql, paramList.toArray(new Object[0][0]));
    }

    /**
     * @param idList
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public void batchDelete(List<String> idList) throws ServiceException, SQLException {
        if (idList == null || idList.isEmpty()) {
            throw new ServiceException("id is null");
        }
        StringBuilder sqlBuilder = new StringBuilder("delete from " + DictDO.TABLE_NAME + " where field_id in (");
        for (String id : idList) {
            sqlBuilder.append("?,");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        LOGGER.info("batch delete sql: {}", sqlBuilder.toString());
        qr.update(sqlBuilder.toString(), idList.toArray(new Object[0]));
    }

    /**
     * @param dictDOList
     * @return
     * @throws SQLException
     */
    @Override
    public List<DictDO> findByDomainAndItemAndValue(List<DictDO> dictDOList) throws SQLException {
        if (dictDOList == null || dictDOList.isEmpty()) {
            return null;
        }
        List<String[]> domainItemList = dictDOList.stream().map((item) -> new String[]{item.getFieldDomain(), item.getFieldItem()}).collect(Collectors.toList());
        StringBuilder sqlBuilder = new StringBuilder("select * from " + DictDO.TABLE_NAME);
        List<String> sqlParamList = new ArrayList<>();
        for (int i = 0; i < dictDOList.size(); i++) {
            DictDO dictDO = dictDOList.get(i);
            sqlParamList.add(dictDO.getFieldDomain());
            sqlParamList.add(dictDO.getFieldItem());
            sqlParamList.add(dictDO.getFieldValue());
            if (i == 0) {
                sqlBuilder.append(" where (field_domain = ? and field_item = ? and field_value = ?) ");
            } else {
                sqlBuilder.append(" or (field_domain = ? and field_item = ? and field_value = ?) ");
            }
        }
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        LOGGER.info("find by field_domain and field_item sql: {}", sqlBuilder.toString());
        return qr.query(sqlBuilder.toString(), new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), sqlParamList.toArray(new Object[0]));
    }

    @Override
    public List<DictDO> findByDomainAndItem(String domain, String item) throws SQLException {
        if (StringUtils.isNullOrEmpty(domain) || StringUtils.isNullOrEmpty(item)) {
            return null;
        }
        String sql = " select * from " + DictDO.TABLE_NAME + " where field_domain = ? and field_item = ?";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), domain, item);
    }

}
