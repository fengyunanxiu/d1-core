package ai.sparklabinc.d1.dict.dao.impl.mysql;

import ai.sparklabinc.d1.dao.convert.QueryRunnerRowProcessor;
import ai.sparklabinc.d1.dict.dao.DictRepository;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.exception.ServiceException;
import ai.sparklabinc.d1.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Handler;
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
        // 排序语句
        sqlBuilder.append(" order by field_domain, field_item, field_sequence ");
        // 分页信息
        sqlBuilder.append(" limit ?, ? ");
        paramList.add(offset);
        paramList.add(pageSize);
        LOGGER.info("query sql: {}", sqlBuilder.toString());
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sqlBuilder.toString(), new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), paramList.toArray(new Object[0]));
    }


    /**
     * 不重复的Domain和Item总量
     *
     * @param params
     * @return
     * @throws SQLException
     */
    @Override
    public long countByDomainAndItem(Map<String, String> params) throws SQLException {

        /**
         * 子查询语句
         */
        StringBuilder innerSqlBuilder = new StringBuilder("select distinct " + DictDO.F_DOMAIN + "," + DictDO.F_ITEM + " from " + DictDO.TABLE_NAME + " where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                innerSqlBuilder.append(" and " + key + " like ? ");
                paramList.add("%" + value + "%");
            }
        }
        StringBuilder sqlBuilder = new StringBuilder("select count(*) as c from (").append(innerSqlBuilder.toString()).append(" )t");
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sqlBuilder.toString(), new ResultSetHandler<Long>() {
            @Override
            public Long handle(ResultSet rs) throws SQLException {
                long count = 0L;
                if (rs.next()) {
                    count = rs.getLong("c");
                }
                return count;
            }
        }, paramList.toArray(new Object[0]));
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
        // 排序
        sqlBuilder.append(" order by field_domain, field_item, field_sequence ");
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        LOGGER.info("find by field_domain and field_item sql: {}", sqlBuilder.toString());
        return qr.query(sqlBuilder.toString(), new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), sqlParamList.toArray(new Object[0]));
    }

    @Override
    public List<DictDO> findByDomainAndItem(String domain, String item) throws SQLException {
        if (StringUtils.isNullOrEmpty(domain) || StringUtils.isNullOrEmpty(item)) {
            return null;
        }
        String sql = " select * from " + DictDO.TABLE_NAME + " where field_domain = ? and field_item = ? order by field_domain, field_item, field_sequence ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), domain, item);
    }

    /**
     * 分页查询不重复的domain和item
     *
     * @param params
     * @param offset
     * @param pageSize
     * @return
     * @throws SQLException
     */
    @Override
    public List<Map<String, String>> queryDistinctDomainItemLimit(Map<String, String> params, long offset, int pageSize) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("select " + DictDO.F_DOMAIN + ", " + DictDO.F_ITEM + " from " + DictDO.TABLE_NAME + " where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sqlBuilder.append(" and " + key + " like ? ");
                paramList.add("%" + value + "%");
            }
        }
        // group by 语句
        sqlBuilder.append(" group by " + DictDO.F_DOMAIN + ", " + DictDO.F_ITEM);
        // 分页
        sqlBuilder.append(" limit ?, ? ");
        paramList.add(offset);
        paramList.add(pageSize);
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sqlBuilder.toString(), new ResultSetHandler<List<Map<String, String>>>() {
            List<Map<String, String>> tmp = new ArrayList<>();

            @Override
            public List<Map<String, String>> handle(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    Map<String, String> row = new HashMap<>();
                    tmp.add(row);
                    String domain = rs.getString(DictDO.F_DOMAIN);
                    String item = rs.getString(DictDO.F_ITEM);
                    row.put(DictDO.F_DOMAIN, domain);
                    row.put(DictDO.F_ITEM, item);
                }
                return tmp;
            }
        }, paramList.toArray(new Object[0]));
    }

    /**
     * 根据domain和item分页查询
     *
     * @param params
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public List<DictDO> queryLimitByDomainAndItem(Map<String, String> params, long offset, int pageSize) throws SQLException {
        // 先查询有哪些domain和item
        List<Map<String, String>> domainItemMapList = this.queryDistinctDomainItemLimit(params, offset, pageSize);
        // 在这些domain和item的基础上在根据条件查询
        StringBuilder sqlBuilder = new StringBuilder("select " + DictDO.F_ID + "," + DictDO.F_VALUE + "," +
                DictDO.F_SEQUENCE + "," + DictDO.F_ENABLE + "," + DictDO.F_LABEL + "," + DictDO.F_PARENT_ID + " from " + DictDO.TABLE_NAME + " where 1= 1");
        // 拼接条件
        List<Object> paramList = new ArrayList<>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sqlBuilder.append(" and " + key + " like ? ");
                paramList.add("%" + value + "%");
            }
        }
        // 补充Domain，Item条件
        StringBuilder domainItemSqlCondition = new StringBuilder(" and (");
        if (domainItemMapList != null && !domainItemMapList.isEmpty()) {
            for (int i = 0; i < domainItemMapList.size(); i++) {
                Map<String, String> domainItemMap = domainItemMapList.get(i);
                String domain = domainItemMap.get(DictDO.F_DOMAIN);
                String item = domainItemMap.get(DictDO.F_ITEM);
                paramList.add("%" + domain + "%");
                paramList.add("%" + item + "%");
                if (i == 0) {
                    domainItemSqlCondition.append("(").append(DictDO.F_DOMAIN).append(" like ").append(" ?")
                            .append(" and ").append(DictDO.F_ITEM).append(" like ").append(" ?").append(")");

                } else {
                    domainItemSqlCondition.append(" or (").append(DictDO.F_DOMAIN).append(" like ").append(" ?")
                            .append(" and ").append(DictDO.F_ITEM).append(" like ").append(" ?").append(")");
                }
            }
        }
        domainItemSqlCondition.append(") ");
        //
        sqlBuilder.append(domainItemSqlCondition.toString());
        // 排序语句
        sqlBuilder.append(" order by " + DictDO.F_DOMAIN + ", " + DictDO.F_ITEM + ",  " + DictDO.F_SEQUENCE);
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sqlBuilder.toString(), new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), paramList.toArray(new Object[0]));
    }

}
