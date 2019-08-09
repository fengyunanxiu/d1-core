package ai.sparklabinc.d1.controller;

import ai.sparklabinc.d1.component.CacheComponent;
import ai.sparklabinc.d1.dto.DbFullConfigDTO;
import ai.sparklabinc.d1.dto.DfKeyBasicConfigDTO;
import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
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

    @Autowired
    private CacheComponent cacheComponent;

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
     * @param dfKeyFilter 0查询所有的table、view；
     *                    1查询配置了data facet key的table view
     *                    2查询没有配置data facet key的table view
     * @return
     * @throws IOException
     * @throws SQLException
     */

    @ResponseBody
    @GetMapping("/select")
    public Object selectDataSources(@RequestParam(required = false) Long dsId,
                                    @RequestParam(defaultValue = "0") Integer dfKeyFilter)throws IOException, SQLException {
       return dataSourceService.selectDataSources(dsId,dfKeyFilter);
    }

    @ResponseBody
    @GetMapping("/refresh-datasource")
    public Object srefreshDataSources(@RequestParam(required = false) Long dsId,
                                    @RequestParam(defaultValue = "0") Integer dfKeyFilter)throws IOException, SQLException {
        //清除缓存
        cacheComponent.clearDataSourceTreeAllCache(dsId);
        return dataSourceService.selectDataSources(dsId,dfKeyFilter);
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




    private String uploadFile(MultipartFile file, HttpServletRequest request) throws IOException {
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
