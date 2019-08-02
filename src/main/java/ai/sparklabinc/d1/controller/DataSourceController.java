package ai.sparklabinc.d1.controller;

import ai.sparklabinc.d1.dto.DbFullConfigDTO;
import ai.sparklabinc.d1.dto.DsKeyBasicConfigDTO;
import ai.sparklabinc.d1.entity.DsFormTableSettingDO;
import ai.sparklabinc.d1.exception.custom.IllegalParameterException;
import ai.sparklabinc.d1.service.DataSourceService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @PostMapping(value = "/test-connection")
    public Object dataSourceTestConnection( @RequestBody DbFullConfigDTO dbFullConfigDTO) throws Exception {
        return  dataSourceService.dataSourceTestConnection(dbFullConfigDTO.getDbBasicConfigDTO(),dbFullConfigDTO.getDbSecurityConfigDTO());
    }

    @ResponseBody
    @PostMapping(value = "/file-upLoad")
    public Object fileUpload(@RequestParam(required = true) MultipartFile multipartFile,HttpServletRequest request) throws IOException {
        String filePath = uploadFile(multipartFile, request);
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("file_path",filePath);
        return resultMap;
    }



    @ResponseBody
    @PostMapping("/add")
    public Object addDataSources(@RequestBody DbFullConfigDTO dbFullConfigDTO)throws Exception {
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
    public Object editDataSourceProperty(@RequestBody DbFullConfigDTO dbFullConfigDTO )throws Exception {
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
    public Object selectAllDsFormTableSettingByDsKey(String dsKey) throws Exception{
        return dataSourceService.selectAllDsFormTableSettingByDsKey(dsKey);
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
    @PostMapping("/refresh-ds-form-table-setting")
    public Object refreshDsFormTableSetting(String dsKey) throws Exception {
        if(StringUtils.isBlank(dsKey)){
            throw new IllegalParameterException("dsKey can not be null!");
        }
        return dataSourceService.refreshDsFormTableSetting(dsKey);
    }


    @ResponseBody
    @PostMapping("/update-dskey")
    public Object updateDataSourceKey(String dsKey, String newDsKey, String description) throws IOException, SQLException{
        return dataSourceService.updateDataSourceKey(dsKey,newDsKey,description);
    }


    @ResponseBody
    @GetMapping("/basic-dskey-info")
    public Object getDsKeyBasicInfo(String dsKey) throws Exception{
        return dataSourceService.getDsKeyBasicInfo(dsKey);
    }



    public String uploadFile(MultipartFile file, HttpServletRequest request) throws IOException {
        String fileName = file.getOriginalFilename();
        String projectPath = System.getProperty("user.dir");
        String savePath=projectPath+File.separator+"UploadFile";
        System.out.println(projectPath);

        File tempFile = new File(savePath, UUID.randomUUID()+"_"+String.valueOf(fileName));
        if (!tempFile.getParentFile().exists()) {    //创建文件夹
            tempFile.getParentFile().mkdir();
        }
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        file.transferTo(tempFile);
        return tempFile.getAbsolutePath();
    }


}
