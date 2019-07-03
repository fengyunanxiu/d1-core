package ai.sparklabinc.dao;

import ai.sparklabinc.entity.DsKeyBasicConfigDO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author : Kingzer
 * @date : 2019-07-03 20:49
 * @description :
 */
public interface DsKeyBasicConfigDao {
    DsKeyBasicConfigDO getDsKeyBasicConfigByDsKey(String dataSourceKey) throws SQLException, IOException;
}
