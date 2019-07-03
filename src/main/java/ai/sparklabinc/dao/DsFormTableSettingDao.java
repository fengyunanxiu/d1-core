package ai.sparklabinc.dao;

import ai.sparklabinc.entity.DsFormTableSettingDO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 07:45
 * @description :
 */
public interface DsFormTableSettingDao {


    List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKey(String dataSourceKey) throws SQLException, IOException;
}
