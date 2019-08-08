package ai.sparklabinc.d1.controller;

import ai.sparklabinc.d1.dto.DfKeyBasicConfigDTO;
import ai.sparklabinc.d1.entity.DsFormTableSettingDO;
import ai.sparklabinc.d1.exception.custom.IllegalParameterException;
import ai.sparklabinc.d1.service.DataSourceService;
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
@RequestMapping("d1/dfkey")
@Api(tags = "DataFacetKeyController")
public class DataFacetKeyController {

    @Autowired
    private DataSourceService dataSourceService;

    @ResponseBody
    @GetMapping("/basic-dfkey-info")
    public Object getDfKeyBasicInfo(String dfKey) throws Exception{
        return dataSourceService.getDsKeyBasicInfo(dfKey);
    }


    @ResponseBody
    @PostMapping("/update-dfkey")
    public Object updateDataFacetKey(String dfKey, String newDfKey, String description) throws IOException, SQLException {
        return dataSourceService.updateDataSourceKey(dfKey,newDfKey,description);
    }




    @ResponseBody
    @PostMapping("/refresh-ds-form-table-setting")
    public Object refreshDsFormTableSetting(String dfKey) throws Exception {
        if(StringUtils.isBlank(dfKey)){
            throw new IllegalParameterException("dsKey can not be null!");
        }
        return dataSourceService.refreshDsFormTableSetting(dfKey);
    }



    @ResponseBody
    @GetMapping ("/select-ds-form-table-setting")
    public Object selectAllDsFormTableSettingByDfKey(String dfKey) throws Exception{
        return dataSourceService.selectAllDsFormTableSettingByDsKey(dfKey);
    }


    @ResponseBody
    @PostMapping("/save-ds-form-table-setting")
    public boolean saveDsFormTableSetting(@RequestBody List<DsFormTableSettingDO> dsFormTableSettingDOS) throws Exception {
        if(CollectionUtils.isEmpty(dsFormTableSettingDOS)){
            throw new IllegalParameterException("form infomation can not be null!");
        }
        List<DsFormTableSettingDO> dsFormTableSettingDOSForUpdate = dsFormTableSettingDOS.stream()
                .filter(e -> e.getId() != null && e.getId() > 0)
                .collect(Collectors.toList());

        List<DsFormTableSettingDO> dsFormTableSettingDOSForAdd= dsFormTableSettingDOS.stream()
                .filter((e) -> e.getId() == null || e.getId() <= 0)
                .collect(Collectors.toList());

        return dataSourceService.saveDsFormTableSetting(dsFormTableSettingDOSForUpdate,dsFormTableSettingDOSForAdd);
    }

    @ResponseBody
    @DeleteMapping("/delete-dfkey")
    public Object deleteDataFacetKey(String dfKey) throws IOException, SQLException{
        return dataSourceService.deleteDataSourceKey(dfKey);
    }



    @ResponseBody
    @PostMapping("/add-dskey")
    public Object addDataSourceKey(@RequestBody DfKeyBasicConfigDTO dfKeyBasicConfigDTO) throws Exception{
        if(StringUtils.isBlank(dfKeyBasicConfigDTO.getDfKey())){
            throw new IllegalParameterException("dsKey can not be null!");
        }

        return dataSourceService.addDataSourceKey(dfKeyBasicConfigDTO);
    }









}
