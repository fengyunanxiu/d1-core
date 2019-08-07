package ai.sparklabinc.d1.dict.controller;

import ai.sparklabinc.d1.dict.entity.FormDictConfigurationDO;
import ai.sparklabinc.d1.dict.service.FormDictConfigurationService;
import ai.sparklabinc.d1.dict.vo.FormDictConfigurationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 10:45
 * @description :
 */
@RestController
@RequestMapping("/d1/form-dict-configuration")
@Api("form dict configuration ")
public class FormDictConfigurationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormDictConfigurationController.class);

    @Autowired
    private FormDictConfigurationService formDictConfigurationService;

    @GetMapping("")
    @ResponseBody
    @ApiOperation("query by df key and field key")
    public FormDictConfigurationVO queryByDfKeyAndFieldKey(@RequestParam("field_form_df_key") String dfKey,
                                                           @RequestParam("field_form_field_key") String fieldKey) throws Exception {
        return this.formDictConfigurationService.queryByForm(dfKey, fieldKey);
    }

    @PostMapping("")
    @ResponseBody
    @ApiOperation("allocate form dict configuration")
    public void allocateFormDictConfiguration(@RequestBody FormDictConfigurationDO formDictConfigurationDO) throws Exception {
        this.formDictConfigurationService.allocateFormDictConfiguration(formDictConfigurationDO);
    }

}
