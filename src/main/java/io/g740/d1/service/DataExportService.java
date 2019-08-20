package io.g740.d1.service;

import io.g740.d1.entity.DataExportTaskDO;
import io.g740.d1.entity.DfFormTableSettingDO;
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

    File export(String dataFacetKey, Map<String, String[]> simpleParameters,
                Pageable pageable, String moreWhereClause,
                DataExportTaskDO dataExportTaskDO) throws Exception;

    List<DfFormTableSettingDO> getAllDfFormTableSettingByDfKeyForExport(String dataFacetKey) throws Exception;
}
