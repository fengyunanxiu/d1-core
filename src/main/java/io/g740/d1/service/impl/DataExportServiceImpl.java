package io.g740.d1.service.impl;

import io.g740.d1.dao.DfKeyBasicConfigDao;
import io.g740.d1.dao.DfFormTableSettingDao;
import io.g740.d1.dto.AssemblyResultDTO;
import io.g740.d1.entity.DataExportTaskDO;
import io.g740.d1.entity.DfFormTableSettingDO;
import io.g740.d1.entity.DfKeyBasicConfigDO;
import io.g740.d1.exception.custom.ResourceNotFoundException;
import io.g740.d1.executor.Executor;
import io.g740.d1.executor.impl.CommonExecutor;
import io.g740.d1.executor.impl.CommonExportExecutor;
import io.g740.d1.executor.impl.ExecutorBuilder;
import io.g740.d1.service.DataExportService;
import io.g740.d1.service.QueryFormTableService;
import io.g740.d1.util.FileUtils;
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

    @Resource(name = "DfFormTableSettingDao")
    private DfFormTableSettingDao dfFormTableSettingDao;

    @Resource(name = "DfKeyBasicConfigDao")
    private DfKeyBasicConfigDao dfKeyBasicConfigDao;

    @Value("${file.temp.path}")
    private String fileTempPath;

    @Override
    public File export(String dataFacetKey, Map<String, String[]> simpleParameters,
                       Pageable pageable, String moreWhereClause,
                       DataExportTaskDO dataExportTaskDO) throws Exception {
        DfKeyBasicConfigDO dfKeyBasicConfigDO = dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(dataFacetKey);
        if(dfKeyBasicConfigDO ==null){
            throw new ResourceNotFoundException("data facet key is not found!");
        }

        long now=System.currentTimeMillis();
        String fileName=dataExportTaskDO.getFileName()+"-"+now+".xlsx";
        String fullFilePathOfExportFile = FileUtils.contact(this.fileTempPath,fileName);
        //获取生成sql文件
        AssemblyResultDTO assemblyResultDTO = queryFormTableService.generalQuery(dataFacetKey, simpleParameters, pageable, moreWhereClause,true);

        List<DfFormTableSettingDO> queryTableSettings = dfFormTableSettingDao.getAllDfFormTableSettingByDfKeyForExport(dataFacetKey);

        //临时文件处理
        File tmpFile=new File(fullFilePathOfExportFile);
        File parentFile = tmpFile.getParentFile();
        // 如果没有文件夹则创建
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        String querySql=assemblyResultDTO.getQuerySql();
        List<Object> paramList = assemblyResultDTO.getParamList();

        DataSource dataSource = assemblyResultDTO.getDataSource();

        Executor build = ExecutorBuilder.getInstance().dataSource(dataSource).exportExecutor(new CommonExportExecutor()).build();

        return build.exportExcel(querySql,paramList, queryTableSettings, Paths.get(fullFilePathOfExportFile));
    }


    @Override
    public List<DfFormTableSettingDO> getAllDfFormTableSettingByDfKeyForExport(String dataFacetKey) throws Exception {
        return dfFormTableSettingDao.getAllDfFormTableSettingByDfKeyForExport(dataFacetKey);
    }
}
