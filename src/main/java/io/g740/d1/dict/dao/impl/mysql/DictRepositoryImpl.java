package io.g740.d1.dict.dao.impl.mysql;

import io.g740.d1.dao.convert.QueryRunnerRowProcessor;
import io.g740.d1.dict.dao.DictRepository;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.util.StringUtils;
import ch.qos.logback.classic.db.names.TableName;
import io.g740.d1.util.UUIDUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Handler;
import java.util.stream.Collectors;

import static io.g740.d1.dict.entity.DictDO.*;

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
        String sql = "insert into " + TABLE_NAME + " (" + F_ID + ", " + F_GMT_CREATE + ", " + F_DOMAIN + ", " + F_ITEM + ", " + F_VALUE + ", " + F_LABEL + ", " + F_SEQUENCE + ", " + F_ENABLE + ", " + F_PARENT_ID + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                    UUIDUtils.compress(),
                    new Date(),
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
     * @param idList
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public void batchDelete(List<String> idList) throws ServiceException, SQLException {
        if (idList == null || idList.isEmpty()) {
            throw new ServiceException("id is null");
        }
        StringBuilder sqlBuilder = new StringBuilder("delete from " + DictDO.TABLE_NAME + " where " + F_ID + " in (");
        for (String id : idList) {
            sqlBuilder.append("?,");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        LOGGER.info("batch delete sql: {}", sqlBuilder.toString());
        qr.update(sqlBuilder.toString(), idList.toArray(new Object[0]));
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
//        StringBuilder sqlBuilder = new StringBuilder("select " + DictDO.F_ID + "," + DictDO.F_VALUE + "," +
//                DictDO.F_SEQUENCE + "," + DictDO.F_ENABLE + "," + DictDO.F_LABEL + "," + DictDO.F_PARENT_ID + " from " + DictDO.TABLE_NAME + " where 1= 1");

        StringBuilder sqlBuilder = new StringBuilder("select * from " + DictDO.TABLE_NAME + " where 1= 1");
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

    @Override
    public void updateValueByDomainAndItem(List<DictDO> dictDOList) throws SQLException {
        String sql = " insert into " + TABLE_NAME + " (" + F_ID + ", " + F_GMT_CREATE + ", " + F_DOMAIN + ", " + F_ITEM + ", " + F_VALUE + " , " + F_LABEL + " , " + F_SEQUENCE + " , " + F_ENABLE + " , " + F_PARENT_ID + " ) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?) on duplicate key update " + F_GMT_MODIFIED + " = ?, " + F_VALUE + " = ?, " + F_LABEL + " = ?, " + F_SEQUENCE + " = ?, " + F_ENABLE + " = ?, " + F_PARENT_ID + " = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        Object[][] param = new Object[dictDOList.size()][15];
        for (int i = 0; i < dictDOList.size(); i++) {
            DictDO dictDO = dictDOList.get(i);
            String fieldDomain = dictDO.getFieldDomain();
            String fieldItem = dictDO.getFieldItem();
            String fieldValue = dictDO.getFieldValue();
            String fieldLabel = dictDO.getFieldLabel();
            String fieldSequence = dictDO.getFieldSequence();
            String fieldEnable = dictDO.getFieldEnable();
            String fieldParentId = dictDO.getFieldParentId();
            param[i][0] = UUIDUtils.compress();
            param[i][1] = param[i][9] = new Date();
            param[i][2] = fieldDomain;
            param[i][3] = fieldItem;
            param[i][4] = param[i][10] = fieldValue;
            param[i][5] = param[i][11] = fieldLabel;
            param[i][6] = param[i][12] = fieldSequence;
            param[i][7] = param[i][13] = fieldEnable;
            param[i][8] = param[i][14] = fieldParentId;

//            param[i][5] = fieldValue;
//            param[i][6] = fieldLabel;
//            param[i][7] = fieldSequence;
//            param[i][8] = fieldEnable;
//            param[i][9] = fieldParentId;
        }
        qr.batch(sql, param);
    }

    @Override
    public void findByApplication(String domain, String item, String value, String label) {
        
    }
    @Override
    public DictDO findById(String id) throws SQLException {
        String sql = " select * from " + TABLE_NAME + " where " + F_ID + " = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanHandler<>(DictDO.class, new QueryRunnerRowProcessor()), id);
    }
    @Override
    public void updateDomainNameOrItemName(String oldDomain, String newDomain, String oldItem, String newItem) throws SQLException {
        String sql = " update " + TABLE_NAME + " set " + F_DOMAIN + " = ?," + F_ITEM + " = ? where " + F_DOMAIN + " = ? and " + F_ITEM + " = ? ";
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        qr.update(sql, newDomain, newItem, oldDomain, oldItem);
    }

}
