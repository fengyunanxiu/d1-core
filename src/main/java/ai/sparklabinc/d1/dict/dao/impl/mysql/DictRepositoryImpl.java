package ai.sparklabinc.d1.dict.dao.impl.mysql;

import ai.sparklabinc.d1.dao.convert.QueryRunnerRowProcessor;
import ai.sparklabinc.d1.dict.dao.DictRepository;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.exception.ServiceException;
import ai.sparklabinc.d1.util.StringUtils;
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

    @Resource(name = "D1BasicDataSoure")
    private DataSource d1BasicDataSoure;

    /**
     *
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
                paramList.add(" %" + value + "% ");
            }
        }
        // 分页信息
        sqlBuilder.append(" limit ?, ? ");
        paramList.add(offset);
        paramList.add(pageSize);

        QueryRunner qr = new QueryRunner(this.d1BasicDataSoure);
        return qr.query(sqlBuilder.toString(), new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), paramList.toArray(new Object[0]));
    }

    /**
     *
     * @param dictDOList
     * @return
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public List<DictDO> batchInsert(List<DictDO> dictDOList) throws ServiceException, SQLException {
        if (dictDOList == null || dictDOList.isEmpty()) {
            throw  new ServiceException("dict do list is null");
        }
        List<Object[]> paramList = new ArrayList<>();
        String sql = "insert into " + DictDO.TABLE_NAME +
                " (id, domain, item, value, label, sequence, enable, parent_id) values (?, ?, ?, ?, ?, ?, ?, ?)";
        for (DictDO dictDO : dictDOList) {
            if (dictDO == null
                    || StringUtils.isNullOrEmpty(dictDO.getDomain())
                    || StringUtils.isNotNullNorEmpty(dictDO.getItem())
                    || StringUtils.isNullOrEmpty(dictDO.getValue())
                    || StringUtils.isNullOrEmpty(dictDO.getSequence())
                    || StringUtils.isNullOrEmpty(dictDO.getEnable())) {
                throw new ServiceException("domain, item, value, sequence, enable不能为空");
            }
            Object[] param = new Object[]{
                    UUID.randomUUID().toString(),
                    dictDO.getDomain(),
                    dictDO.getItem(),
                    dictDO.getValue(),
                    dictDO.getLabel(),
                    dictDO.getSequence(),
                    dictDO.getEnable(),
                    dictDO.getParentId()
            };
            paramList.add(param);
        }
        QueryRunner qr = new QueryRunner(this.d1BasicDataSoure);

        return qr.insertBatch(sql, new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), paramList.toArray(new Object[0][0]));
    }


    /**
     *
     * @param dictDOList
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public void batchUpdate(List<DictDO> dictDOList) throws ServiceException, SQLException {
        if (dictDOList == null || dictDOList.isEmpty()) {
            throw  new ServiceException("dict do list is null");
        }
        String sql = "update " + DictDO.TABLE_NAME + " set domain = ?, item = ?, value = ?, label = ?, sequence = ?, enable = ?, parent_id = ? where id = ?";
        List<Object[]> paramList = new ArrayList<>();
        for (DictDO dictDO : dictDOList) {
            if (dictDO == null
                    || StringUtils.isNullOrEmpty(dictDO.getId())
                    || StringUtils.isNullOrEmpty(dictDO.getDomain())
                    || StringUtils.isNotNullNorEmpty(dictDO.getItem())
                    || StringUtils.isNullOrEmpty(dictDO.getValue())
                    || StringUtils.isNullOrEmpty(dictDO.getSequence())
                    || StringUtils.isNullOrEmpty(dictDO.getEnable())) {
                throw new ServiceException("id, domain, item, value, sequence, enable不能为空");
            }
            Object[] param = new Object[]{
                    dictDO.getDomain(),
                    dictDO.getItem(),
                    dictDO.getValue(),
                    dictDO.getLabel(),
                    dictDO.getSequence(),
                    dictDO.getEnable(),
                    dictDO.getParentId(),
                    dictDO.getId()
            };
            paramList.add(param);
        }
        QueryRunner qr = new QueryRunner(this.d1BasicDataSoure);
        qr.batch(sql, paramList.toArray(new Object[0][0]));
    }

    /**
     *
     * @param idList
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public void batchDelete(List<String> idList) throws ServiceException, SQLException {
        if (idList == null || idList.isEmpty()) {
            throw  new ServiceException("id is null");
        }
        StringBuilder sqlBuilder = new StringBuilder("delete from " + DictDO.TABLE_NAME + " where id in (");
        for (String id : idList) {
            sqlBuilder.append(id).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        QueryRunner qr = new QueryRunner(this.d1BasicDataSoure);
        qr.update(sqlBuilder.toString(), idList.toArray(new Object[0]));
    }

    /**
     * 
     * @param dictDOList
     * @return
     * @throws SQLException
     */
    @Override
    public List<DictDO> findByDomainAndItem(List<DictDO> dictDOList) throws SQLException {
        if (dictDOList == null || dictDOList.isEmpty()) {
            return null;
        }
        List<String[]> domainItemList = dictDOList.stream().map((item) -> new String[]{item.getDomain(), item.getItem()}).collect(Collectors.toList());
        StringBuilder sqlBuilder = new StringBuilder("select * from " + DictDO.TABLE_NAME );
        List<String> sqlParamList = new ArrayList<>();
        for (int i = 0; i < dictDOList.size(); i++) {
            DictDO dictDO = dictDOList.get(i);
            sqlParamList.add(dictDO.getDomain());
            sqlParamList.add(dictDO.getItem());
            if (i == 0) {
                sqlBuilder.append(" where (domain = ? and item = ?) ");
            }else {
                sqlBuilder.append(" or (domain = ? and item = ?) ");
            }
        }
        QueryRunner qr = new QueryRunner();
        return qr.query(sqlBuilder.toString(), new BeanListHandler<>(DictDO.class, new QueryRunnerRowProcessor()), sqlParamList.toArray(new Object[0]));
    }

}
