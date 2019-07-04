package ai.sparklabinc.controller;

import ai.sparklabinc.dto.DbBasicConfigDTO;
import ai.sparklabinc.dto.DbSecurityConfigDTO;
import ai.sparklabinc.service.DataSourceService;
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
@RequestMapping("/datasource")
public class DataSourceController {
    @Autowired
    private DataSourceService dataSourceService;

    @ResponseBody
    @GetMapping("/connection")
    public Object Connection2DataSource(@RequestParam(required = true) Long dsId) throws IOException, SQLException {
       return  dataSourceService.Connection2DataSource(dsId);
    }

    @ResponseBody
    @PostMapping("/add")
    public Object addDataSources(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO)throws IOException, SQLException {
        return dataSourceService.addDataSources(dbBasicConfigDTO,dbSecurityConfigDTO);
    }

    @ResponseBody
    @PostMapping("/delete")
    public Object deleteDataSources(@RequestParam(required = true) Long dsId)throws IOException, SQLException {
        return dataSourceService.deleteDataSources(dsId);
    }

    @ResponseBody
    @GetMapping("/select")
    public Object selectDataSources(@RequestParam(required = false) Long dsId )throws IOException, SQLException {
       return dataSourceService.selectDataSources(dsId);
    }

    @ResponseBody
    @GetMapping("/select-property")
    public Object selectDataSourceProperty(Long dsId)throws IOException, SQLException {
        return dataSourceService.selectDataSourceProperty(dsId);
    }

    @ResponseBody
    @PostMapping("/edit-property")
    public Object editDataSourceProperty(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO)throws IOException, SQLException {
        return dataSourceService.editDataSourceProperty(dbBasicConfigDTO,dbSecurityConfigDTO);
    }
}
