package io.g740.d1.sqlbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/30 11:41
 * @description :
 */
public class SimpleJpaRepository<T, ID extends Serializable> implements JpaRepository<T, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJpaRepository.class);

    private DataSource dataSource;

    private Class<T> entityClazz;

    private Insert<T, ID> insert;

    private String tableName;

    private String idFieldName;

    private String queryField;

    /**
     * SimpleJpaRepository 实现的方法不需要经过代理，直接执行即可
     */
    public static final Set<String> METHOD_NAMES = new HashSet<>();

    static {
        Method[] methods = SimpleJpaRepository.class.getMethods();
        for (Method method : methods) {
            METHOD_NAMES.add(method.getName());
        }
    }

    public SimpleJpaRepository(DataSource dataSource, Class<T> entityClazz) throws Exception {
        this.dataSource = dataSource;
        this.entityClazz = entityClazz;
        this.insert = new Insert<>();
        tableName = BeanParser.parseTableName(entityClazz);
        idFieldName = BeanParser.getIdFieldName(entityClazz);
        queryField = JdbcHelper.buildQueryField(entityClazz);
    }

    @Override
    public void delete(ID id) {

    }

    @Override
    public void delete(List<ID> idIterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public T findOne(ID id) throws Exception {
        String sql = "select " + queryField + " from " + tableName + " where " + idFieldName + " = ?";
        List<T> result = JdbcHelper.query(dataSource, sql, Collections.singletonList(id), entityClazz);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public T getOne(ID id) throws Exception {
        return findOne(id);
    }

    @Override
    public boolean exists(ID id) throws Exception {
        return null != findOne(id);
    }

    @Override
    public List<T> findAll() throws Exception {
        LOGGER.info("===== findAll() ====");
        String sql = "select " + queryField + " from " + tableName;
        return JdbcHelper.query(dataSource, sql, null, entityClazz);
    }

    @Override
    public List<T> findAll(List<ID> idList) throws Exception {
        LOGGER.info("==== findAll(Iterable<ID> idIterable) ====");
        if (null == this.idFieldName) {
            return null;
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select ").append(queryField).append(" from ").append(tableName);
        if (idList != null && !idList.isEmpty()) {
            for (int i = 0; i < idList.size(); i++) {
                if (i == 0) {
                    sqlBuilder.append("?");
                } else {
                    sqlBuilder.append(",?");
                }
            }
            sqlBuilder.insert(0, " where " + idFieldName + " in (");
            sqlBuilder.insert(sqlBuilder.length(), " )");
        }
        return JdbcHelper.query(dataSource, sqlBuilder.toString(), idList, entityClazz);
    }

    @Override
    public List<T> findAllSort(Sort sort) throws Exception {
        LOGGER.info("==== findAllSort(Sort sort) ====");
        StringBuilder sqlBuilder = new StringBuilder("select " + queryField + " from " + tableName);
        if (sort != null) {
            Iterator<Sort.Order> orderIterator = sort.iterator();
            int i = 0;
            while (orderIterator.hasNext()) {
                Sort.Order order = orderIterator.next();
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();
                if (i == 0) {
                    sqlBuilder.append(" order by ").append(property).append(" ").append(direction.name());
                } else {
                    sqlBuilder.append(",").append(property).append(" ").append(direction.name());
                }
                i++;
            }
        }
        return JdbcHelper.query(dataSource, sqlBuilder.toString(), null, entityClazz);
    }

    @Override
    public long count() throws Exception {
        String sql = "select count(*) from " + tableName;
        return JdbcHelper.count(dataSource, sql, null);
    }

    @Override
    public void save(T t) throws Exception {
        insert.insert(entityClazz, t, dataSource, true);
    }

    @Override
    public void save(List<T> list) throws Exception {
        insert.insert(entityClazz, list, dataSource);
    }
}
