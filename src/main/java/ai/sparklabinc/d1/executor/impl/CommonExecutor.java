package ai.sparklabinc.d1.executor.impl;

import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
import ai.sparklabinc.d1.exception.custom.PropertyNotFoundException;
import ai.sparklabinc.d1.executor.Executor;
import ai.sparklabinc.d1.executor.ExportExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @function:
 * @author:   dengam
 * @date:    2019/7/18 17:24
 * @param:
 * @return:
 */
public class CommonExecutor implements Executor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExecutor.class);

    private DataSource dataSource;

    private ExportExecutor exportExecutor;

    /**
     * protect化构造函数，只允许通过Builder创建实例
     */
    CommonExecutor(ExecutorBuilder executorBuilder) {
        this.dataSource = executorBuilder.getDataSource();
        this.exportExecutor = executorBuilder.getExportExecutor();
    }

    @Override
    public File exportExcel(String querySql, List<Object> paramList, List<DfFormTableSettingDO> queryTableSettings, Path path) throws PropertyNotFoundException {
        if (exportExecutor != null) {
            // 查询sql
            return this.exportExecutor.exportExcel(dataSource, querySql,paramList, queryTableSettings, path);
        }
        throw new PropertyNotFoundException("ExportExecutor Not Found" );
    }
}
