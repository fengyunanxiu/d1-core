package io.g740.d1.controller;

import io.g740.d1.dao.DataExportTaskDao;
import io.g740.d1.entity.DataExportTaskDO;
import io.g740.d1.exception.custom.ResourceNotFoundException;
import io.g740.d1.service.DataExportService;
import io.g740.d1.service.impl.QueryFormTableServiceImpl;
import io.g740.d1.util.ApiUtils;
import io.g740.d1.util.DateUtils;
import io.g740.d1.util.ParameterHandlerUtils;
import com.mysql.jdbc.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/9 15:32
 * @description:
 * @version: V1.0
 */
@Controller
@RequestMapping("d1/export")
@Api(tags = "DataExportController")
public class DataExportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFormTableServiceImpl.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Autowired
    private DataExportService dataExportService;

    @Resource(name = "DataExportTaskDao")
    private DataExportTaskDao dataExportTaskDao;



    @ApiOperation(value = "Async export Inventory Turnover task report")
    @GetMapping("/async-export")
    @ResponseBody
    public Object asyncExportInventoryTurnoverTaskReport(@RequestParam(name = "data_facet_key", required = true) String dataFacetKey,
                                                         HttpServletRequest request) throws Exception {
        if (StringUtils.isNullOrEmpty(dataFacetKey)) {
            throw new ResourceNotFoundException("Empty data facet key " + dataFacetKey);
        }
        //参数配置
        Map<String, String[]> params = request.getParameterMap();
        Pageable pageable = ParameterHandlerUtils.extractPageable(params);
        String moreWhereClause = ParameterHandlerUtils.extractMoreClause(params);
        Map<String, String[]> simpleParameters = ApiUtils.removeReservedParameters(params);

        DataExportTaskDO dataExportTask = new DataExportTaskDO();
        String now = DateUtils.ofLongStr(new Date());
        dataExportTask.setStartAt(now);
        dataExportTask.setFileName(dataFacetKey);
        long start = System.currentTimeMillis();
        LOGGER.info("async export 开始调用data export record");
        final DataExportTaskDO toWaitSaveExportTask = dataExportTaskDao.addDataExportTask(dataExportTask);
        LOGGER.info("async export 开始调用data export record:{}", (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        this.executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //生成导出文件
                    File exportFile = dataExportService.export(dataFacetKey, simpleParameters, pageable, moreWhereClause, dataExportTask);

                    toWaitSaveExportTask.setFileName(exportFile.getName());
                    toWaitSaveExportTask.setFilePath(exportFile.getAbsolutePath());
                    toWaitSaveExportTask.setEndAt(DateUtils.ofLongStr(new Date()));
                    dataExportTaskDao.updateDataExportTask(toWaitSaveExportTask);
                } catch (Exception e) {
                    LOGGER.error("导出文件失败,id: {}", dataExportTask.getId(), e);

                    toWaitSaveExportTask.setFailedAt(DateUtils.ofLongStr(new Date()));
                    toWaitSaveExportTask.setDetails(e.getMessage());
                    try {
                        dataExportTaskDao.updateDataExportTask(toWaitSaveExportTask);
                    } catch (Exception e1) {
                        LOGGER.info("Save user export record failure");
                    }
                }
            }
        });
        return toWaitSaveExportTask.getId();
    }


    @ApiOperation(value = "selectTaskStatus")
    @GetMapping("/task-status")
    @ResponseBody
    public Object selectTaskStatus(Long taskId) throws IOException, SQLException {
        return dataExportTaskDao.findById(taskId);
    }

    @ApiOperation(value = "fileDownload")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String fileDownload(Long taskId, HttpServletResponse res) throws Exception {
        DataExportTaskDO dataExportTaskDO = dataExportTaskDao.findById(taskId);
        if (dataExportTaskDO == null) {
            throw new ResourceNotFoundException("taskId is not found");
        }
        String filePath = dataExportTaskDO.getFilePath();
        File file = new File(filePath);
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "application/octet-stream;charset=UTF-8");
        res.setContentType("application/octet-stream;charset=UTF-8");
        //加上设置大小下载下来的.xlsx文件打开时才不会报“Excel 已完成文件级验证和修复。此工作簿的某些部分可能已被修复或丢弃”
        res.addHeader("Content-Length", String.valueOf(new FileInputStream(file).available()));
        try {
            res.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(file.getName(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("error:" + e);
        }
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = res.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
            LOGGER.info("下载成功,filePath={}", filePath);
        } catch (IOException e) {
            LOGGER.error("下载失败,filePath={}",filePath);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    System.out.println("error:" + e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                    os.flush();
                } catch (IOException e) {
                    System.out.println("error:" + e);
                }
            }
        }
        return null;
    }


    @ApiOperation(value = "getAllDfFormTableSettingByDfKeyForExport")
    @GetMapping("/form-table-setting")
    @ResponseBody
    public Object getAllDfFormTableSettingByDfKeyForExport(@RequestParam(required = true,name = "data_facet_key") String dataFacetKey) throws Exception {
        return dataExportService.getAllDfFormTableSettingByDfKeyForExport(dataFacetKey);
    }



    @ApiOperation(value = "addDataExportTask")
    @PostMapping("/add-task")
    @ResponseBody
    public Object addDataExportTask(@RequestBody DataExportTaskDO dataExportTask) throws Exception {
        return dataExportTaskDao.addDataExportTask(dataExportTask);
    }

    @ApiOperation(value = "updateDataExportTask")
    @PostMapping("/update-task")
    @ResponseBody
    public Object updateDataExportTask(@RequestBody DataExportTaskDO dataExportTask) throws Exception {
        return dataExportTaskDao.updateDataExportTask(dataExportTask);
    }


}
