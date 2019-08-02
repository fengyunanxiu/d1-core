package ai.sparklabinc.d1.dao;

import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.TableAndViewInfoDTO;
import ai.sparklabinc.d1.dto.TableColumnsDetailDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/4 16:35
 * @description:
 * @version: V1.0
 */
public interface DataSourceDao {

    DataDaoType getDataDaoType();

    List<DbInforamtionDTO> selectAllSchema(Long dsId) throws IOException, SQLException;

    List<TableAndViewInfoDTO> selectAllTableAndView(Long dsId) throws IOException, SQLException;

    List<TableColumnsDetailDTO> selectTableColumnsDetail(Long dsId, String schema, String table) throws IOException, SQLException;
}
