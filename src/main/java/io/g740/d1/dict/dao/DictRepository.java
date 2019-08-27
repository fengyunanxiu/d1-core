package io.g740.d1.dict.dao;

import io.g740.d1.dict.dto.DictDTO;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.exception.ServiceException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 15:41
 * @description :
 */
public interface DictRepository {
    List<DictDO> query(Map<String, String> params, long offset, int pageSize) throws SQLException;

    List<Map<String, String>> queryDistinctDomainItemLimit(Map<String, String> params, long offset, int pageSize) throws SQLException;

    long countByDomainAndItem(Map<String, String> params) throws SQLException;

    List<DictDO> batchInsert(List<DictDO> dictDOList) throws ServiceException, SQLException;

    void batchUpdate(List<DictDO> dictDOList) throws ServiceException, SQLException;

    void batchDelete(List<String> idList) throws ServiceException, SQLException;

    List<DictDO> findByDomainAndItem(String domain, String item) throws SQLException;

    List<DictDO> queryLimitByDomainAndItem(Map<String, String> params, long offset, int pageSize) throws SQLException;

    void updateValueByDomainAndItem(List<DictDO> dictDOList) throws SQLException;


    DictDO findById(String id) throws SQLException;

    void updateDomainNameOrItemName(String oldDomain, String newDomain, String oldItem, String newItem) throws SQLException;
    List<DictDO>  findByApplication(String domain, String item, String value) throws SQLException;

    void deleteByDomainAndItem(String domain, String item) throws SQLException;
}
