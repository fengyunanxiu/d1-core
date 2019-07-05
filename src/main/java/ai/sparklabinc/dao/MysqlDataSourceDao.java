package ai.sparklabinc.dao;

import ai.sparklabinc.dto.DbInforamtionDTO;
import ai.sparklabinc.dto.TableAndViewInfoDTO;
import ai.sparklabinc.dto.TableColumnsDetailDTO;

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
public interface MysqlDataSourceDao {

    List<DbInforamtionDTO> selectAllSchema(Long dsId) throws IOException, SQLException;

    List<TableAndViewInfoDTO> selectAllTableAndView(Long dsId) throws IOException, SQLException;

    List<TableColumnsDetailDTO> selectTableColumnsDetail(Long dsId, String schema, String table) throws IOException, SQLException;
}
