package ai.sparklabinc.d1.executor;

import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
import ai.sparklabinc.d1.exception.custom.PropertyNotFoundException;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @function:
 * @author:   dengam
 * @date:    2019/7/18 17:26
 * @param:
 * @return:
 */
public interface Executor {

    File exportExcel(String querySql, List<Object> paramList, List<DfFormTableSettingDO> queryTableSettings, Path path) throws PropertyNotFoundException;
}
