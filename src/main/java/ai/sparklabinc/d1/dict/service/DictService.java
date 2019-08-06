package ai.sparklabinc.d1.dict.service;

import ai.sparklabinc.d1.dict.dto.DictDTO;
import ai.sparklabinc.d1.dict.dto.DictQueryVO;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.exception.ServiceException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 16:47
 * @description :
 */
public interface DictService {

    List<DictDO> batchInsert(List<DictDO> dictDOList) throws ServiceException, SQLException, Exception;

    void batchDelete(List<String> idList) throws Exception;

    void batchUpdate(List<DictDO> dictDOList) throws Exception;

    Collection<DictQueryVO> query(DictDTO dictDTO, Long offset, Integer pageSize) throws Exception;
}
