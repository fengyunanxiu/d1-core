package ai.sparklabinc.d1.controller;

import ai.sparklabinc.d1.dto.D1ParamsDTO;
import ai.sparklabinc.d1.exception.custom.IllegalParameterException;
import ai.sparklabinc.d1.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.d1.service.QueryFormTableService;
import ai.sparklabinc.d1.util.ApiUtils;
import ai.sparklabinc.d1.util.ParameterHandlerUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param d1Params  封装的d1 需要的参数，json化参数；如下面示意；前端需要统一处理这类查询（为了过滤掉某些字段使用，为了通用性）
     *  {
     *     "sql_param": {
     *       "id": [2,3],
     *       "field":["yield"]
     *     },
     *     "ds_key": "",
     *     "more_where_clauses": "",
     *     "page_size": "",
     *     "page": "",
     *     "sort": [{"properties":[],
     *       "direction": ""
     *     }]
     *   }
     * @return
     * @throws Exception
     */
    @GetMapping("/query")
    @ResponseBody
    public Object generalQuery(@RequestParam(name = "d1_params", required = true) String d1Params) throws Exception {

        if(StringUtils.isNullOrEmpty(d1Params)){
            throw new IllegalParameterException("parameter is empty");
        }
        JSONObject d1ParamsObj = JSON.parseObject(d1Params);

        String moreWhereClause = ParameterHandlerUtils.extractMoreClause(d1ParamsObj);
        String dataFacetKey = d1ParamsObj.getString("data_facet_key");
        Map<String, String[]> simpleParameters = ParameterHandlerUtils.extractParameterMap(d1ParamsObj);
        Pageable pageable = ParameterHandlerUtils.extractPageable(d1ParamsObj);

        return queryFormTableService.generalQuery(dataFacetKey, simpleParameters, pageable,moreWhereClause,false);
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
