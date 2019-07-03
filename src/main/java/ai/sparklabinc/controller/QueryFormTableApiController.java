package ai.sparklabinc.controller;

import ai.sparklabinc.service.QueryFormTableService;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
//        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
//            // TODO 自定义异常体系
//            //throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
//        }
//        Object result = this.queryFormTableService.getDsKeyQuerySetting(dataSourceKey);
//        if (result == null) {
//            // TODO 自定义异常体系
//            // throw new ResourceNotFoundException("Cannot find resource from data source key " + dataSourceKey);
//        }
//        return result;
        return null;
    }


    @GetMapping("/form-setting")
    @ResponseBody
    public Object queryFormSetting(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
//        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
//           // throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
//        }
//        Object result = this.queryFormTableService.getDsKeyQueryFormSetting(dataSourceKey);
//        if (result == null) {
//           // throw new ResourceNotFoundException("Cannot find resource from data source key " + dataSourceKey);
//        }
//        return result;
        return null;
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
            //throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
        }
        Object result = this.queryFormTableService.getDsKeyQueryTableSetting(dataSourceKey);
        if (result == null) {
            // throw new ResourceNotFoundException("Cannot find resource from data source key " + dataSourceKey);
        }
        return result;

    }

}
