package ai.sparklabinc.controller;

import ai.sparklabinc.entity.DataExportTaskDO;
import ai.sparklabinc.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.service.DataExportService;
import ai.sparklabinc.service.impl.QueryFormTableServiceImpl;
import ai.sparklabinc.util.ApiUtils;
import ai.sparklabinc.util.DateUtils;
import com.mysql.jdbc.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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
@RestController
@RequestMapping("d1/export")
public class DataExportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFormTableServiceImpl.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Autowired
    private DataExportService dataExportService;

    @ApiOperation(value = "Async export Inventory Turnover task report")
    @GetMapping("/async-export")
    @ResponseBody
    public Object asyncExportInventoryTurnoverTaskReport(@RequestParam(name = "data_source_key", required = true) String dataSourceKey,
                                                         HttpServletRequest request)throws Exception {
        if (StringUtils.isNullOrEmpty(dataSourceKey)) {
            throw new ResourceNotFoundException("Empty data source key " + dataSourceKey);
        }
        //参数配置
        Map<String, String[]> params = request.getParameterMap();
        Pageable pageable = QueryFormTableApiController.extractPageable(params);
        String moreWhereClause = QueryFormTableApiController.extractMoreClause(params);
        Map<String, String[]> simpleParameters = ApiUtils.removeReservedParameters(params);

        DataExportTaskDO dataExportTask = new DataExportTaskDO();
        String now = DateUtils.ofShortDate(new Date());
        dataExportTask.setStartAt(now);
        dataExportTask.setFileName("demo.xlsx");
        long start = System.currentTimeMillis();
        LOGGER.info("async export 开始调用data export record");
        final DataExportTaskDO toWaitSaveExportTask = dataExportTask;
        LOGGER.info("async export 开始调用data export record:{}", (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        this.executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File exportFile = dataExportService.export(dataSourceKey,simpleParameters,pageable,moreWhereClause);

                    toWaitSaveExportTask.setFileName(exportFile.getName());
                    toWaitSaveExportTask.setEndAt(DateUtils.ofShortDate(new Date()));
                  //  dsisClient.saveExportTask(toWaitSaveExportTask);
                } catch (Exception e) {
                    LOGGER.error("导出文件失败,id: {}", dataExportTask.getId(), e);

                    toWaitSaveExportTask.setFailedAt(DateUtils.ofShortDate(new Date()));
                    toWaitSaveExportTask.setDetails(e.getMessage());
                    try {
                     //   dsisClient.saveExportTask(toWaitSaveExportTask);
                    } catch (Exception e1) {
                        LOGGER.info("Save user export record failure");
                    }
                }
            }
        });
        return toWaitSaveExportTask.getId();
    }
}
