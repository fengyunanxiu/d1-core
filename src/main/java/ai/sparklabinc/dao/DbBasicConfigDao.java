package ai.sparklabinc.dao;

import ai.sparklabinc.entity.DbBasicConfigDO;
import ai.sparklabinc.entity.DsFormTableSettingDO;

import java.sql.SQLException;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 07:45
 * @description :
 */
public interface DbBasicConfigDao {


   DbBasicConfigDO findById(Long id) throws SQLException;
}
