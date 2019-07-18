package ai.sparklabinc.executor;

import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.exception.custom.PropertyNotFoundException;

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

    File exportExcel(String querySql, List<Object> paramList, List<DsFormTableSettingDO> queryTableSettings, Path path) throws PropertyNotFoundException;
}
