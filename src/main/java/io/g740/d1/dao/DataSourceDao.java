package io.g740.d1.dao;

import io.g740.d1.dto.DbInformationDTO;
import io.g740.d1.dto.TableAndViewInfoDTO;
import io.g740.d1.dto.TableColumnsDetailDTO;

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

    List<DbInformationDTO> selectAllSchema(Long dsId) throws Exception;

    List<TableAndViewInfoDTO> selectAllTableAndView(Long dsId) throws Exception;

    List<TableColumnsDetailDTO> selectTableColumnsDetail(Long dsId, String schema, String table) throws Exception;
}
