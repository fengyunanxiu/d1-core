package io.g740.d1.dict.service;

import io.g740.d1.dict.dto.DictPluginDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/26 11:07
 * @description :
 */
public interface DictPluginService {

    DictPluginDTO query(String domain, String item) throws SQLException;

    void allocateSQLPluginByDomainAndItem(DictPluginDTO dictPluginDTO) throws Exception;

    List<Map<String, String>> executeSQLTest(DictPluginDTO dictPluginDTO);
}
