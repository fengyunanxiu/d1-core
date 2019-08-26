package io.g740.d1.dict.controller;

import io.g740.d1.dict.dto.DictPluginDTO;
import io.g740.d1.dict.service.DictPluginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/26 10:54
 * @description :
 */
@Api("dict plugin")
@RestController
@RequestMapping("/dict/plugin")
public class DictPluginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictPluginController.class);

    @Autowired
    private DictPluginService dictPluginService;

    @GetMapping("/domain-item")
    @ResponseBody
    @ApiOperation("query")
    public DictPluginDTO query(@RequestParam String domain, @RequestParam String item) throws SQLException {
        return this.dictPluginService.query(domain, item);
    }

    @PostMapping("")
    @ResponseBody
    @ApiOperation("allocate plugin")
    public void allocate(@Validated @RequestBody DictPluginDTO dictPluginDTO) throws Exception {
        this.dictPluginService.allocateSQLPluginByDomainAndItem(dictPluginDTO);
    }

    @PostMapping("/test-sql")
    @ResponseBody
    @ApiOperation("test sql")
    public Map<String, Object> executeSQLTest(@RequestBody DictPluginDTO dictPluginDTO) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> testResult = this.dictPluginService.executeSQLTest(dictPluginDTO);
        boolean allMatch = testResult.stream().limit(5).allMatch(rowMap -> rowMap.get("value") != null && rowMap.get("sequence") != null && rowMap.get("label") != null);
        result.put("success", allMatch);
        result.put("list", testResult);
        return result;

    }

}
