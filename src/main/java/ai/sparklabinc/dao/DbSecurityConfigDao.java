package ai.sparklabinc.dao;

import ai.sparklabinc.entity.DbSecurityConfigDO;

import java.sql.SQLException;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/3 14:40
 * @description:
 */
public interface DbSecurityConfigDao {
     DbSecurityConfigDO findById(Long id) throws SQLException;
}
