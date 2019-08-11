package ai.sparklabinc.d1.dict.dao;

import ai.sparklabinc.d1.dict.entity.DictPluginConfigurationDO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 15:29
 * @description :
 */
public interface DictPluginConfigurationRepository {
    List<DictPluginConfigurationDO> findAllEnable() throws SQLException;

    List<DictPluginConfigurationDO> findAllEnableWithLockTransaction(Connection connection) throws SQLException;

    DictPluginConfigurationDO findByDomainAndItem(String domain, String item) throws SQLException;
}
