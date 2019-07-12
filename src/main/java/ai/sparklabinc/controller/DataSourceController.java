package ai.sparklabinc.controller;

import ai.sparklabinc.dto.DbFullConfigDTO;
import ai.sparklabinc.dto.DsKeyBasicConfigDTO;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.exception.custom.IllegalParameterException;
import ai.sparklabinc.service.DataSourceService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/3 17:25
 * @description:
 */
@RestController
@RequestMapping("/d1/datasource")
@Api(tags = "DataSourceController")
public class DataSourceController {
    @Autowired
    private DataSourceService dataSourceService;

    @ResponseBody
    @GetMapping("/connection")
    public Object Connection2DataSource(@RequestParam(required = true) Long dsId) throws IOException, SQLException {
       return  dataSourceService.Connection2DataSource(dsId);
    }

    @ResponseBody
    @PostMapping("/test-connection")
    public Object dataSourceTestConnection(@RequestBody DbFullConfigDTO dbFullConfigDTO) throws Exception {
        return  dataSourceService.dataSourceTestConnection(dbFullConfigDTO.getDbBasicConfigDTO(),dbFullConfigDTO.getDbSecurityConfigDTO());
    }

    @ResponseBody
    @PostMapping("/add")
    public Object addDataSources(@RequestBody DbFullConfigDTO dbFullConfigDTO )throws IOException, SQLException {
        return dataSourceService.addDataSources(dbFullConfigDTO.getDbBasicConfigDTO(),dbFullConfigDTO.getDbSecurityConfigDTO());
    }

    @ResponseBody
    @DeleteMapping("/delete")
    public Object deleteDataSources(@RequestParam(required = true) Long dsId)throws IOException, SQLException {
        return dataSourceService.deleteDataSources(dsId);
    }

    /**
     *
     * @param dsId
     * @param dsKeyFilter 0查询所有的table、view；
     *                    1查询配置了data source key的table view
     *                    2查询没有配置data source key的table view
     * @return
     * @throws IOException
     * @throws SQLException
     */

    @ResponseBody
    @GetMapping("/select")
    public Object selectDataSources(@RequestParam(required = false) Long dsId,
                                    @RequestParam(defaultValue = "0") Integer dsKeyFilter)throws IOException, SQLException {
       return dataSourceService.selectDataSources(dsId,dsKeyFilter);
    }

    @ResponseBody
    @GetMapping("/select-property")
    public Object selectDataSourceProperty(Long dsId)throws IOException, SQLException {
        return dataSourceService.selectDataSourceProperty(dsId);
    }

    @ResponseBody
    @PostMapping("/edit-property")
    public Object editDataSourceProperty(@RequestBody DbFullConfigDTO dbFullConfigDTO )throws IOException, SQLException {
        return dataSourceService.editDataSourceProperty(dbFullConfigDTO.getDbBasicConfigDTO(),dbFullConfigDTO.getDbSecurityConfigDTO());
    }

    @ResponseBody
    @PostMapping("/add-dskey")
    public Object addDataSourceKey(@RequestBody DsKeyBasicConfigDTO dsKeyBasicConfigDTO) throws Exception{
        if(StringUtils.isBlank(dsKeyBasicConfigDTO.getDsKey())){
            throw new IllegalParameterException("dsKey can not be null!");
        }
        return dataSourceService.addDataSourceKey(dsKeyBasicConfigDTO);
    }

    @ResponseBody
    @DeleteMapping("/delete-dskey")
    public Object deleteDataSourceKey(String dsKey) throws IOException, SQLException{
        return dataSourceService.deleteDataSourceKey(dsKey);
    }


    @ResponseBody
    @GetMapping ("/select-ds-form-table-setting")
    public Object selectAllDsFormTableSettingByDsKey(String dsKey) throws IOException, SQLException{
        return dataSourceService.selectAllDsFormTableSettingByDsKey(dsKey);
    }


    @ResponseBody
    @PostMapping("/update-ds-form-table-setting")
    public Object updateDsFormTableSetting(@RequestBody DsFormTableSettingDO dsFormTableSettingDO) throws Exception {
        if(dsFormTableSettingDO.getId()==null||dsFormTableSettingDO.getId()<=0){
            throw new IllegalParameterException("Id can not be null!");
        }
        return dataSourceService.updateDsFormTableSetting(dsFormTableSettingDO);
    }


    @ResponseBody
    @PostMapping("/refresh-ds-form-table-setting")
    public Object RefreshDsFormTableSetting(String dsKey) throws Exception {
        if(StringUtils.isBlank(dsKey)){
            throw new IllegalParameterException("dsKey can not be null!");
        }
        return dataSourceService.RefreshDsFormTableSetting(dsKey);
    }


    @ResponseBody
    @PostMapping("/update-dskey")
    public Object updateDataSourceKey(String dsKey, String newDsKey, String description) throws IOException, SQLException{
        return dataSourceService.updateDataSourceKey(dsKey,newDsKey,description);
    }


}
