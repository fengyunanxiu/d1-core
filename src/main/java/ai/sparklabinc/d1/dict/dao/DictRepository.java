package ai.sparklabinc.d1.dict.dao;

import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.exception.ServiceException;

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

    long count(Map<String, String> params) throws SQLException;

    List<DictDO> batchInsert(List<DictDO> dictDOList) throws ServiceException, SQLException;

    void batchUpdate(List<DictDO> dictDOList) throws ServiceException, SQLException;

    void batchDelete(List<String> idList) throws ServiceException, SQLException;

    List<DictDO> findByDomainAndItemAndValue(List<DictDO> dictDOList) throws SQLException;

    List<DictDO> findByDomainAndItem(String domain, String item) throws SQLException;
}
