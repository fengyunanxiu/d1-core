package io.g740.d1.defaults.controller;

import io.g740.d1.defaults.entity.DefaultsConfigurationDO;
import io.g740.d1.defaults.service.DefaultsConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public DefaultsConfigurationDO query(@RequestParam("field_form_df_key") String dfKey,
                                               @RequestParam("field_form_field_key") String fieldKey) throws Exception {
        return this.defaultsConfigurationService.queryByDfKeyAndFieldKey(dfKey, fieldKey);
    }

    @PostMapping("")
    @ResponseBody
    @ApiOperation("allocate")
    public void allocate(@RequestBody DefaultsConfigurationDO defaultsConfigurationDO) throws Exception {
        this.defaultsConfigurationService.allocateDefaultsConfiguration(defaultsConfigurationDO);
    }

}
