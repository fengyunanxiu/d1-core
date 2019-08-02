package ai.sparklabinc.d1.dao;

import ai.sparklabinc.d1.entity.DbSecurityConfigDO;

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
     DataDaoType getDataDaoType();

     DbSecurityConfigDO findById(Long id) throws SQLException, IOException;

     Integer add(DbSecurityConfigDO dbSecurityConfigDO) throws IOException, SQLException;

     Integer delete(Long dsId) throws SQLException, IOException;

     Integer editDataSourceProperty(DbSecurityConfigDO dbSecurityConfigDO) throws IOException, SQLException;
}
