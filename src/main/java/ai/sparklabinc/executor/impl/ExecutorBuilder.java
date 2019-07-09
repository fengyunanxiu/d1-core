package ai.sparklabinc.executor.impl;

import ai.sparklabinc.executor.Executor;
import ai.sparklabinc.executor.ExportExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * @author :  zxiuwu
 * @date : 2019-03-21 15:27
 */
public class ExecutorBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorBuilder.class);

    private DataSource dataSource;

    private ExportExecutor exportExecutor;

    public static ExecutorBuilder getInstance() {
        return new ExecutorBuilder();
    }

    public ExecutorBuilder dataSource(DataSource dataSource){
        this.dataSource = dataSource;
        return this;
    }
    public ExecutorBuilder exportExecutor(ExportExecutor exportExecutor) {
        this.exportExecutor = exportExecutor;
        return this;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }


    protected ExportExecutor getExportExecutor() {
        return exportExecutor;
    }

    public Executor build() {
        return new CommonExecutor(this);
    }
}
