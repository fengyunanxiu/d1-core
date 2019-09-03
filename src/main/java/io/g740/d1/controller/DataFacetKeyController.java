package io.g740.d1.controller;

import io.g740.d1.dto.DfKeyBasicConfigDTO;
import io.g740.d1.entity.DfFormTableSettingDO;
import io.g740.d1.exception.custom.IllegalParameterException;
import io.g740.d1.service.DataFacetKeyService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @function:
 * @author: DAM
 * @date: 2019/8/8 9:33
 * @description:
 * @version: V1.0
 */
@Controller
@RequestMapping("d1/datasource")
@Api(tags = "DataFacetKeyController")
public class DataFacetKeyController {

    @Autowired
    private DataFacetKeyService dataFacetKeyService;

    @ResponseBody
    @PostMapping("/add-dfkey")
    public Object add(@RequestBody DfKeyBasicConfigDTO dfKeyBasicConfigDTO) throws Exception{
        if(StringUtils.isBlank(dfKeyBasicConfigDTO.getDfKey())){
            throw new IllegalParameterException("dfKey can not be null!");
        }
        return dataFacetKeyService.addDataFacetKey(dfKeyBasicConfigDTO);
    }

    @ResponseBody
    @DeleteMapping("/delete-dfkey")
    public void deleteDataFacetKey(String dfKey) throws Exception{
         dataFacetKeyService.deleteDataFacetKey(dfKey);
    }


    @ResponseBody
    @GetMapping ("/select-df-form-table-setting")
    public Object selectAllDfFormTableSettingByDfKey(String dfKey) throws Exception{
        return dataFacetKeyService.selectAllDfFormTableSettingByDfKey(dfKey);
    }


    @ResponseBody
    @PostMapping("/save-df-form-table-setting")
    public void saveDfFormTableSetting(@RequestBody List<DfFormTableSettingDO> dfFormTableSettingDOS) throws Exception {
        if(CollectionUtils.isEmpty(dfFormTableSettingDOS)){
            throw new IllegalParameterException("form infomation can not be null!");
        }
         dataFacetKeyService.saveDfFormTableSetting(dfFormTableSettingDOS);
    }


    @ResponseBody
    @PostMapping("/refresh-df-form-table-setting")
    public Object refreshDfFormTableSetting(String dfKey) throws Exception {
        if(StringUtils.isBlank(dfKey)){
            throw new IllegalParameterException("dfKey can not be null!");
        }
        return dataFacetKeyService.refreshDfFormTableSetting(dfKey);
    }


    @ResponseBody
    @PostMapping("/update-dfkey")
    public void updateDataFacetKey(String dfKey, String newDfKey, String description) throws Exception{
         dataFacetKeyService.updateDataFacetKey(dfKey,newDfKey,description);
    }


    @ResponseBody
    @GetMapping("/basic-dfkey-info")
    public Object getDfKeyBasicInfo(String dfKey) throws Exception{
        return dataFacetKeyService.getDfKeyBasicInfo(dfKey);
    }

}
