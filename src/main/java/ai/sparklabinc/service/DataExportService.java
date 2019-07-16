package ai.sparklabinc.service;

import ai.sparklabinc.entity.DataExportTaskDO;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/9 16:02
 * @description:
 * @version: V1.0
 */
public interface DataExportService {

    File export(String dataSourceKey, Map<String, String[]> simpleParameters,
                Pageable pageable, String moreWhereClause,
                DataExportTaskDO dataExportTaskDO) throws Exception;

    List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKeyForExport(String dataSourceKey) throws Exception;
}
