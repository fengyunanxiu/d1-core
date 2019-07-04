package ai.sparklabinc.dao;

import ai.sparklabinc.dto.DbInforamtionDTO;
import ai.sparklabinc.entity.DbBasicConfigDO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author : Kingzer
 * @date : 2019-07-03 07:45
 * @description :
 */
public interface DbBasicConfigDao {


   DbBasicConfigDO findById(Long id) throws SQLException, IOException;

   Long add(DbBasicConfigDO dbBasicConfigDO) throws SQLException, IOException;

   Integer delete(Long dsId) throws SQLException, IOException;

   List<DbInforamtionDTO> selectDataSources(Long dsId) throws IOException, SQLException;

   List<Map<String,Object>> selectDataSourceProperty(Long dsId)throws IOException, SQLException;

   Integer editDataSourceProperty(DbBasicConfigDO dbBasicConfigDO) throws IOException, SQLException;
}
