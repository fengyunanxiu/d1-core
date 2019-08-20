package io.g740.d1.dict.service;

import io.g740.d1.dict.dto.DictDTO;
import io.g740.d1.dict.vo.DictQueryVO;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.dto.PageResultDTO;
import io.g740.d1.exception.ServiceException;

import java.sql.SQLException;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 16:47
 * @description :
 */
public interface DictService {

    List<DictDO> batchInsert(List<DictDO> dictDOList) throws Exception;

    void batchDelete(List<String> idList) throws Exception;

    void batchUpdate(List<DictDO> dictDOList) throws Exception;

    PageResultDTO<DictQueryVO> query(DictDTO dictDTO, Long offset, Integer pageSize) throws Exception;
}
