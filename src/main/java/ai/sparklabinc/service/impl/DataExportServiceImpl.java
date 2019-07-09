package ai.sparklabinc.service.impl;

import ai.sparklabinc.dao.DsKeyBasicConfigDao;
import ai.sparklabinc.dto.AssemblyResultDTO;
import ai.sparklabinc.entity.DsKeyBasicConfigDO;
import ai.sparklabinc.exception.custom.PropertyNotFoundException;
import ai.sparklabinc.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.executor.Executor;
import ai.sparklabinc.executor.impl.CommonExecutor;
import ai.sparklabinc.executor.impl.CommonExportExecutor;
import ai.sparklabinc.executor.impl.ExecutorBuilder;
import ai.sparklabinc.service.DataExportService;
import ai.sparklabinc.service.QueryFormTableService;
import ai.sparklabinc.vo.DsKeyQueryTableSettingVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/9 14:58
 * @description:
 * @version: V1.0
 */
@Service
public class DataExportServiceImpl implements DataExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExecutor.class);
    @Autowired
    private QueryFormTableService queryFormTableService;

    @Autowired
    private DsKeyBasicConfigDao dsKeyBasicConfigDao;

    @Override
    public File export(String dataSourceKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause) throws Exception {
        DsKeyBasicConfigDO dsKeyBasicConfigDO = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dataSourceKey);
        if(dsKeyBasicConfigDO==null){
            throw new ResourceNotFoundException("data source key is not found!");
        }

        String qualifiedFileName="demo.xlsx";

        //获取生成sql文件
        AssemblyResultDTO assemblyResultDTO = queryFormTableService.generalQuery(dataSourceKey, simpleParameters, pageable, moreWhereClause,true);

        List<DsKeyQueryTableSettingVO> queryTableSettings = queryFormTableService.getDsKeyQueryTableSetting(dataSourceKey);

        String querySql=assemblyResultDTO.getQuerySql();

        DataSource dataSource = assemblyResultDTO.getDataSource();
        Executor build = ExecutorBuilder.getInstance().dataSource(dataSource).exportExecutor(new CommonExportExecutor()).build();

        try {
            return build.exportExcel(querySql, queryTableSettings, Paths.get(qualifiedFileName));
        } catch (PropertyNotFoundException e) {
            LOGGER.error("", e);
        }
        return null;
    }
}
