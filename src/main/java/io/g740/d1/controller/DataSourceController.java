package io.g740.d1.controller;

import io.g740.d1.component.CacheComponent;
import io.g740.d1.dto.DbFullConfigDTO;
import io.g740.d1.service.DataSourceService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    public void Connection2DataSource(@RequestParam(required = true) Long dsId) throws Exception {
          dataSourceService.Connection2DataSource(dsId);
    }

    @ResponseBody
    @PostMapping(value = "/test-connection")
    public void dataSourceTestConnection( @RequestBody DbFullConfigDTO dbFullConfigDTO) throws Exception {
          dataSourceService.dataSourceTestConnection(dbFullConfigDTO.getDbBasicConfigDTO(),dbFullConfigDTO.getDbSecurityConfigDTO());
    }

    @ResponseBody
    @PostMapping(value = "/file-upLoad")
    public Object fileUpload(@RequestParam(required = true) MultipartFile multipartFile,HttpServletRequest request) throws Exception {
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
    public void deleteDataSources(@RequestParam(required = true) Long dsId)throws Exception {
         dataSourceService.deleteDataSources(dsId);
    }

    /**
     * @return
     * @throws IOException
     * @throws SQLException
     */

    @ResponseBody
    @GetMapping("/select")
    public Object selectDataSources()throws Exception {
       return dataSourceService.selectDataSources();
    }

    @ResponseBody
    @GetMapping("/refresh-datasource")
    public Object refreshDataSources(@RequestParam(required = true) Long dsId)throws Exception {
        return dataSourceService.refreshDataSources(dsId);
    }

    @ResponseBody
    @GetMapping("/select-property")
    public Object selectDataSourceProperty(Long dsId)throws Exception {
        return dataSourceService.selectDataSourceProperty(dsId);
    }

    @ResponseBody
    @PostMapping("/edit-property")
    public void editDataSourceProperty(@RequestBody DbFullConfigDTO dbFullConfigDTO )throws Exception {
        dataSourceService.editDataSourceProperty(dbFullConfigDTO.getDbBasicConfigDTO(),dbFullConfigDTO.getDbSecurityConfigDTO());
    }




    private String uploadFile(MultipartFile file, HttpServletRequest request) throws Exception {
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
