package ai.sparklabinc.controller;

import ai.sparklabinc.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/3 17:25
 * @description:
 */
@RestController
@RequestMapping("/datasource")
public class DataSourceController {
    @Autowired
    private DataSourceService dataSourceService;

    @ResponseBody
    @GetMapping("/test-connection")
    public Object dataSourcesTestConnection(@RequestParam(required = true) Long dsId){
       return  dataSourceService.dataSourcesTestConnection(dsId);
    }
}
