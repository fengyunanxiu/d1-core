package io.g740.d1.defaults.controller;

import io.g740.d1.defaults.dto.DefaultsConfigurationDTO;
import io.g740.d1.defaults.entity.DefaultsConfigurationDO;
import io.g740.d1.defaults.service.DefaultsConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 15:59
 * @description :
 */
@RestController
@RequestMapping("/d1/defaults-configuration")
@Api("defaults configuration controller")
public class DefaultsConfigurationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultsConfigurationController.class);

    @Autowired
    private DefaultsConfigurationService defaultsConfigurationService;

    @GetMapping("")
    @ResponseBody
    @ApiOperation("query")
    public DefaultsConfigurationDTO query(@RequestParam("field_form_df_key") String dfKey,
                                          @RequestParam("field_form_field_key") String fieldKey) throws Exception {
        return this.defaultsConfigurationService.queryByDfKeyAndFieldKey(dfKey, fieldKey);
    }

    @PostMapping("")
    @ResponseBody
    @ApiOperation("allocate")
    public void allocate(@RequestBody @Validated DefaultsConfigurationDTO defaultsConfigurationDTO) throws Exception {
        this.defaultsConfigurationService.allocateDefaultsConfiguration(defaultsConfigurationDTO);
    }

    @PostMapping("/test-sql")
    @ResponseBody
    @ApiOperation("test sql")
    public Collection executeTestSQL(@RequestBody DefaultsConfigurationDTO defaultsConfigurationDTO) {
        return this.defaultsConfigurationService.executeSQLTest(defaultsConfigurationDTO);
    }


}
