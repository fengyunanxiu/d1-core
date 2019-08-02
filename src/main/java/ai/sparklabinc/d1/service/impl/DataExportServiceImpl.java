package ai.sparklabinc.d1.service.impl;

import ai.sparklabinc.d1.dao.DsFormTableSettingDao;
import ai.sparklabinc.d1.dao.DsKeyBasicConfigDao;
import ai.sparklabinc.d1.dto.AssemblyResultDTO;
import ai.sparklabinc.d1.entity.DataExportTaskDO;
import ai.sparklabinc.d1.entity.DsFormTableSettingDO;
import ai.sparklabinc.d1.entity.DsKeyBasicConfigDO;
import ai.sparklabinc.d1.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.d1.executor.Executor;
import ai.sparklabinc.d1.executor.impl.CommonExecutor;
import ai.sparklabinc.d1.executor.impl.CommonExportExecutor;
import ai.sparklabinc.d1.executor.impl.ExecutorBuilder;
import ai.sparklabinc.d1.service.DataExportService;
import ai.sparklabinc.d1.service.QueryFormTableService;
import ai.sparklabinc.d1.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource(name = "DsFormTableSettingDao")
    private DsFormTableSettingDao dsFormTableSettingDao;

    @Resource(name = "DsKeyBasicConfigDao")
    private DsKeyBasicConfigDao dsKeyBasicConfigDao;

    @Value("${file.temp.path}")
    private String fileTempPath;

    @Override
    public File export(String dataSourceKey, Map<String, String[]> simpleParameters,
                       Pageable pageable, String moreWhereClause,
                       DataExportTaskDO dataExportTaskDO) throws Exception {
        DsKeyBasicConfigDO dsKeyBasicConfigDO = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dataSourceKey);
        if(dsKeyBasicConfigDO==null){
            throw new ResourceNotFoundException("data source key is not found!");
        }

        long now=System.currentTimeMillis();
        String fileName=dataExportTaskDO.getFileName()+"-"+now+".xlsx";
        String fullFilePathOfExportFile = FileUtils.contact(this.fileTempPath,fileName);
        //获取生成sql文件
        AssemblyResultDTO assemblyResultDTO = queryFormTableService.generalQuery(dataSourceKey, simpleParameters, pageable, moreWhereClause,true);

        List<DsFormTableSettingDO> queryTableSettings = dsFormTableSettingDao.getAllDsFormTableSettingByDsKeyForExport(dataSourceKey);

        String querySql=assemblyResultDTO.getQuerySql();
        List<Object> paramList = assemblyResultDTO.getParamList();

        DataSource dataSource = assemblyResultDTO.getDataSource();

        Executor build = ExecutorBuilder.getInstance().dataSource(dataSource).exportExecutor(new CommonExportExecutor()).build();

        return build.exportExcel(querySql,paramList, queryTableSettings, Paths.get(fullFilePathOfExportFile));

    }


    @Override
    public List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKeyForExport(String dataSourceKey) throws Exception {
        return dsFormTableSettingDao.getAllDsFormTableSettingByDsKeyForExport(dataSourceKey);
    }
}
