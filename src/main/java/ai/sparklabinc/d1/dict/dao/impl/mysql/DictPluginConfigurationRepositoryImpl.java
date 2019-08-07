package ai.sparklabinc.d1.dict.dao.impl.mysql;

import ai.sparklabinc.d1.dict.dao.DictPluginConfigurationRepository;
import ai.sparklabinc.d1.dict.entity.DictPluginConfigurationDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 15:22
 * @description :
 */
@Repository
public class DictPluginConfigurationRepositoryImpl implements DictPluginConfigurationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictPluginConfigurationRepositoryImpl.class);

    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;

    /**
     * 获取所有的可执行插件
     * @return
     */
    public List<DictPluginConfigurationDO> findAllEnable() throws SQLException {
        String sql = String.format("select * from %s where %s = True", DictPluginConfigurationDO.TABLE_NAME,
                DictPluginConfigurationDO.F_ENABLE);
        QueryRunner qr = new QueryRunner(this.d1BasicDataSource);
        return qr.query(sql, new BeanListHandler<DictPluginConfigurationDO>(DictPluginConfigurationDO.class));
    }


}
