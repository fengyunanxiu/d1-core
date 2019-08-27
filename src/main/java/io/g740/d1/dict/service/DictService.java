package io.g740.d1.dict.service;

import io.g740.d1.dict.dto.DictDTO;
import io.g740.d1.dict.dto.DictOptionCascadeQueryDTO;
import io.g740.d1.dict.vo.DictQueryVO;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.dto.PageResultDTO;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.exception.custom.DuplicateResourceException;

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


    void batchDelete(List<String> idList) throws Exception;

    PageResultDTO<DictQueryVO> query(DictDTO dictDTO, Long offset, Integer pageSize) throws Exception;

    void addBaseDictList(List<DictDTO> dictDTOS) throws SQLException, Exception;

    void addBaseDict(DictDTO dictDTO) throws SQLException, ServiceException;

    void updateBaseDict(DictDTO dictDTO) throws SQLException, ServiceException;

    void deleteDomain(DictDTO dictDTO) throws SQLException;

    /**
     * 级联查询字典数据
     * @param domain
     * @param item
     * @return
     * @throws Exception
     */
    List<DictOptionCascadeQueryDTO> cascadeQueryByDomainAndItem(String domain, String item) throws Exception;
}
