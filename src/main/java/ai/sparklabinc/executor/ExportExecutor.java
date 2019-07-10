package ai.sparklabinc.executor;

import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.vo.DsKeyQueryTableSettingVO;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * 查询结果导出处理器
 * @author :  zxiuwu
 * @date : 2019-03-21 15:14
 */
public interface ExportExecutor {

    File exportExcel(DataSource dataSource, String querySql, List<DsFormTableSettingDO>  queryTableSettings, Path path);
}
