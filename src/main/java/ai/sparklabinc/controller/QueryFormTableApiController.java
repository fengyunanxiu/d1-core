package ai.sparklabinc.controller;

import ai.sparklabinc.constant.QueryParamConstants;
import ai.sparklabinc.exception.custom.IllegalParameterException;
import ai.sparklabinc.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.service.QueryFormTableService;
import ai.sparklabinc.util.ApiUtils;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author : Kingzer
 * @date : 2019-07-02 21:44
 * @description :
 */
@RestController
@RequestMapping("/d1/query-suite")
public class QueryFormTableApiController {
    @Autowired
    private QueryFormTableService queryFormTableService;

    @GetMapping("/form-table-setting")
    @ResponseBody
    public Object queryDataSourceClassicQueryPageSetting(
            @RequestParam(name = "data_source_key", required = true) String dataSourceKey, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new IllegalParameterException("Empty data source key " + dataSourceKey);
        }
        Object result = this.queryFormTableService.getDsKeyQuerySetting(dataSourceKey);
        if (result == null) {
             throw new ResourceNotFoundException("Cannot find resource from data source key " + dataSourceKey);
        }
        return result;
    }

    @GetMapping("/form-setting")
    @ResponseBody
    public Object queryFormSetting(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new IllegalParameterException("Empty data source key " + dataSourceKey);
        }
        Object result = this.queryFormTableService.getDsKeyQueryFormSetting(dataSourceKey);
        if (result == null) {
            throw new ResourceNotFoundException("Cannot find resource from data source key " + dataSourceKey);
        }
        return result;
    }

    /**
     * 获取指定数据源的table设置
     *
     * @param dataSourceKey
     * @return
     * @throws Exception
     */
    @GetMapping("/table-setting")
    @ResponseBody
    public Object queryTableSetting(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new IllegalParameterException("Empty data source key " + dataSourceKey);
        }
        Object result = this.queryFormTableService.getDsKeyQueryTableSetting(dataSourceKey);
        if (result == null) {
            throw new ResourceNotFoundException("Cannot find resource from data source key " + dataSourceKey);
        }
        return result;
    }


    /**
     * 获取指定数据源的table设置
     *
     * @param dataSourceKey
     * @return
     * @throws Exception
     */
    @PostMapping("/query")
    @ResponseBody
    public Object generalQuery(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                        HttpServletRequest request, @RequestBody Map<String, String[]> params) throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
        }
        Pageable pageable = this.extractPageable(params);
        String moreWhereClause = this.extractMoreClause(params);
        Map<String, String[]> simpleParameters = ApiUtils.removeReservedParameters(params);
        return queryFormTableService.generalQuery(dataSourceKey, simpleParameters, pageable,moreWhereClause,false);
    }

    /**
     * 获取指定数据源的table设置
     *
     * @param dataSourceKey
     * @return
     * @throws Exception
     */
    @PostMapping("/query-and-datasource")
    @ResponseBody
    public Object generalQueryAndDataSource(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                               HttpServletRequest request, @RequestBody Map<String, String[]> params) throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
        }
        Pageable pageable = this.extractPageable(params);
        String moreWhereClause = this.extractMoreClause(params);
        Map<String, String[]> simpleParameters = ApiUtils.removeReservedParameters(params);
        return queryFormTableService.generalQuery(dataSourceKey, simpleParameters, pageable,moreWhereClause,true);
    }



    private Pageable extractPageable(Map<String, String[]> params) {
        String[] pages = params.get(QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_PAGE);
        String[] sizes = params.get(QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_SIZE);
        String[] sorts = params.get(QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_SORT);
        Integer page = null;
        Integer size = null;
        Sort sort = null;
        if (pages != null && pages.length > 0) {
            page = Integer.valueOf(pages[0]);
        }
        if (sizes != null && sizes.length > 0) {
            size = Integer.valueOf(sizes[0]);
        }
        if (sorts != null && sorts.length > 0) {
            for (int i = 0; i < sorts.length; i++) {
                String sortParam = sorts[i];
                String[] sortArray = sortParam.split(",");
                Sort sigleSort = null;
                if (sortArray.length == 1) {
                    sigleSort = new Sort(Sort.Direction.DESC, sorts[0]);
                } else if (sortArray.length == 2) {
                    String direction = sortArray[1];
                    String fieldName = sortArray[0];
                    if (QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_SORT_DESC.equalsIgnoreCase(direction)) {
                        sigleSort = new Sort(Sort.Direction.DESC, fieldName);
                    } else if (QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_SORT_ASC.equalsIgnoreCase(direction)) {
                        sigleSort = new Sort(Sort.Direction.ASC, fieldName);
                    }
                }
                if(sort == null) {
                    sort = sigleSort;
                }else {
                    sort = sort.and(sigleSort);
                }
            }
        }
        if (page != null && size != null) {
            return  PageRequest.of(page, size, sort);
        }
        return null;
    }

    private String extractMoreClause(Map<String, String[]> params) {
        String moreWhereClause = null;
        String[] moreWhereClauses = params.get(QueryParamConstants.SQL_PARAMS_KEY_FOR_CUSTOMER_SQL_CONDITION_CLAUSE);
        if (moreWhereClauses != null && moreWhereClauses.length > 0) {
            moreWhereClause = " and " + moreWhereClauses[0];
        }
        return moreWhereClause;
    }

}
