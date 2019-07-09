package ai.sparklabinc.executor.impl;

import ai.sparklabinc.exception.custom.PropertyNotFoundException;
import ai.sparklabinc.executor.Executor;
import ai.sparklabinc.executor.ExportExecutor;
import ai.sparklabinc.vo.DsKeyQueryTableSettingVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author :  zxiuwu
 * @date : 2019-03-21 15:33
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
    public File exportExcel(String querySql, List<DsKeyQueryTableSettingVO> queryTableSettings, Path path) throws PropertyNotFoundException {
        if (exportExecutor != null) {
            // 查询sql
            return this.exportExecutor.exportExcel(dataSource, querySql, queryTableSettings, path);
        }
        throw new PropertyNotFoundException("ExportExecutor Not Found" );
    }
}
