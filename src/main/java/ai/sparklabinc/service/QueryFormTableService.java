package ai.sparklabinc.service;

import ai.sparklabinc.dto.AssemblyResultDTO;
import ai.sparklabinc.vo.DsKeyQueryFormSettingVO;
import ai.sparklabinc.vo.DsKeyQueryTableSettingVO;
import ai.sparklabinc.vo.DsKeyQueryVO;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author : Kingzer
 * @date : 2019-07-02 21:49
 * @description :
 */
public interface QueryFormTableService {
    List<DsKeyQueryTableSettingVO> getDsKeyQueryTableSetting(String dataSourceKey) throws SQLException, IOException;

    List<DsKeyQueryFormSettingVO> getDsKeyQueryFormSetting(String dataSourceKey) throws SQLException, IOException;

    DsKeyQueryVO getDsKeyQuerySetting(String dataSourceKey) throws SQLException, IOException;

    AssemblyResultDTO generalQuery(String dataSourceKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause) throws SQLException, Exception;
}
