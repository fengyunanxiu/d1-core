package io.g740.d1.executor;

import io.g740.d1.entity.DfFormTableSettingDO;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @function:
 * @author:   dengam
 * @date:    2019/7/18 17:27
 * @param:
 * @return:
 */
public interface ExportExecutor {

    File exportExcel(DataSource dataSource, String querySql, List<Object> paramList, List<DfFormTableSettingDO> queryTableSettings, Path path);
}
