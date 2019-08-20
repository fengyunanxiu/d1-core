package io.g740.d1.executor.impl;

import io.g740.d1.executor.Executor;
import io.g740.d1.executor.ExportExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * @function:
 * @author:   dengam
 * @date:    2019/7/18 17:25
 * @param:
 * @return:
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
