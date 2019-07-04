package ai.sparklabinc.dao;

import ai.sparklabinc.dto.DbSecurityConfigDTO;
import ai.sparklabinc.entity.DbSecurityConfigDO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/3 14:40
 * @description:
 */
public interface DbSecurityConfigDao {
     DbSecurityConfigDO findById(Long id) throws SQLException, IOException;

     Integer add(DbSecurityConfigDO dbSecurityConfigDO) throws IOException, SQLException;

     Integer delete(Long dsId) throws SQLException, IOException;

     Integer editDataSourceProperty(DbSecurityConfigDO dbSecurityConfigDO) throws IOException, SQLException;
}
