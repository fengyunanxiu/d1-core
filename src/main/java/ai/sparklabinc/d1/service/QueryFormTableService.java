package ai.sparklabinc.d1.service;

import ai.sparklabinc.d1.dto.AssemblyResultDTO;
import ai.sparklabinc.d1.dto.PageResultDTO;
import ai.sparklabinc.d1.dto.SQLGenerResultDTO;
import ai.sparklabinc.d1.vo.DsKeyQueryFormSettingVO;
import ai.sparklabinc.d1.vo.DsKeyQueryTableSettingVO;
import ai.sparklabinc.d1.vo.DsKeyQueryVO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author : Kingzer
 * @date : 2019-07-02 21:49
 * @description :
 */
public interface QueryFormTableService {
    List<DsKeyQueryTableSettingVO> getDsKeyQueryTableSetting(String dataSourceKey) throws Exception;

    List<DsKeyQueryFormSettingVO> getDsKeyQueryFormSetting(String dataSourceKey) throws Exception;

    DsKeyQueryVO getDsKeyQuerySetting(String dataSourceKey) throws Exception;

    AssemblyResultDTO generalQuery(String dataSourceKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause, boolean returnDatasource) throws Exception;

    PageResultDTO executeQuery(String dataSourceKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause) throws Exception;

    SQLGenerResultDTO generalSQL(String dataSourceKey, Map<String, String[]> requestParams) throws Exception;
}
