package ai.sparklabinc.executor;

import ai.sparklabinc.exception.custom.PropertyNotFoundException;
import ai.sparklabinc.vo.DsKeyQueryTableSettingVO;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author :  zxiuwu
 * @date : 2019-03-21 15:41
 */
public interface Executor {

    File exportExcel(String querySql, List<DsKeyQueryTableSettingVO> queryTableSettings, Path path) throws PropertyNotFoundException;
}
