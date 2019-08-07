package ai.sparklabinc.d1.service;

import ai.sparklabinc.d1.dto.AssemblyResultDTO;
import ai.sparklabinc.d1.dto.PageResultDTO;
import ai.sparklabinc.d1.dto.SQLGenerResultDTO;
import ai.sparklabinc.d1.vo.DfKeyQueryFormSettingVO;
import ai.sparklabinc.d1.vo.DfKeyQueryTableSettingVO;
import ai.sparklabinc.d1.vo.DfKeyQueryVO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author : Kingzer
 * @date : 2019-07-02 21:49
 * @description :
 */
public interface QueryFormTableService {
    List<DfKeyQueryTableSettingVO> getDfKeyQueryTableSetting(String dataFacetKey) throws Exception;

    List<DfKeyQueryFormSettingVO> getDfKeyQueryFormSetting(String dataFacetKey) throws Exception;

    DfKeyQueryVO getDfKeyQuerySetting(String dataFacetKey) throws Exception;

    AssemblyResultDTO generalQuery(String dataFacetKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause, boolean returnDatasource) throws Exception;

    PageResultDTO executeQuery(String dataFacetKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause) throws Exception;

    SQLGenerResultDTO generalSQL(String dataFacetKey, Map<String, String[]> requestParams) throws Exception;
}
