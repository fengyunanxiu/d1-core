package ai.sparklabinc.controller;

import ai.sparklabinc.constant.QueryParamConstants;
import ai.sparklabinc.exception.custom.IllegalParameterException;
import ai.sparklabinc.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.service.QueryFormTableService;
import ai.sparklabinc.util.ApiUtils;
import ai.sparklabinc.util.ParameterHandlerUtils;
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
    @GetMapping("/query")
    @ResponseBody
    public Object generalQuery(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                               HttpServletRequest request) throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
        }
        Map<String, String[]> params = request.getParameterMap();

        Pageable pageable = ParameterHandlerUtils.extractPageable(params);
        String moreWhereClause = ParameterHandlerUtils.extractMoreClause(params);
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
    @GetMapping("/query-and-datasource")
    @ResponseBody
    public Object generalQueryAndDataSource(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                               HttpServletRequest request) throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
        }
        Map<String, String[]> params = request.getParameterMap();
        Pageable pageable = ParameterHandlerUtils.extractPageable(params);
        String moreWhereClause = ParameterHandlerUtils.extractMoreClause(params);
        Map<String, String[]> simpleParameters = ApiUtils.removeReservedParameters(params);
        return queryFormTableService.generalQuery(dataSourceKey, simpleParameters, pageable,moreWhereClause,true);
    }




    /**
     * 执行查询
     * @param dataSourceKey
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/execute-query")
    @ResponseBody
    public Object executeQuery(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                               HttpServletRequest request) throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
        }
        Map<String, String[]> params = request.getParameterMap();

        Pageable pageable = ParameterHandlerUtils.extractPageable(params);
        String moreWhereClause = ParameterHandlerUtils.extractMoreClause(params);
        Map<String, String[]> simpleParameters = ApiUtils.removeReservedParameters(params);
        return  queryFormTableService.executeQuery(dataSourceKey, simpleParameters, pageable,moreWhereClause);
    }


    /**
     * 获取指定数据源的table设置(新)
     *
     * @param dataSourceKey
     * @return
     * @throws Exception
     */
    @GetMapping("/general-sql")
    @ResponseBody
    public Object generalSQL(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                               HttpServletRequest request) throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
        }
        Map<String, String[]> requestParams = ApiUtils.restructureParameter(request.getParameterMap());
        return queryFormTableService.generalSQL(dataSourceKey,requestParams);
    }

}
