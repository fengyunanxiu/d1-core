package ai.sparklabinc.d1.dict.service;

import ai.sparklabinc.d1.dict.dao.DictRepository;
import ai.sparklabinc.d1.dict.dto.DictDTO;
import ai.sparklabinc.d1.dict.dto.DictQueryVO;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.exception.ServiceException;
import ai.sparklabinc.d1.exception.custom.DuplicateResourceException;
import ai.sparklabinc.d1.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 16:47
 * @description :
 */
@Service
public class DictServiceImpl implements DictService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictServiceImpl.class);

    @Resource(name = "DictRepository")
    private DictRepository dictRepository;

    /**
     * @param dictDOList
     * @return
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public List<DictDO> batchInsert(List<DictDO> dictDOList) throws Exception {
        List<DictDO> byDomainAndItem = this.dictRepository.findByDomainAndItem(dictDOList);
        if (byDomainAndItem != null && !byDomainAndItem.isEmpty()) {
            String existMsg = byDomainAndItem.stream().map((item) -> String.format("domain:%s, item:%s", item.getDomain(), item.getItem())).collect(Collectors.joining(";"));
            throw new DuplicateResourceException("Find Duplicate Domain And Item , " + existMsg);
        }
        return this.dictRepository.batchInsert(dictDOList);
    }

    /**
     * @param idList
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public void batchDelete(List<String> idList) throws Exception {
        this.dictRepository.batchDelete(idList);
    }

    /**
     * @param dictDOList
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public void batchUpdate(List<DictDO> dictDOList) throws Exception {
        this.dictRepository.batchUpdate(dictDOList);
    }

    @Override
    public Collection<DictQueryVO> query(DictDTO dictDTO, Long offset, Integer pageSize) throws Exception {
        if (offset == null) {
            offset = 0L;
        }
        if (pageSize == null) {
            pageSize = 0;
        }
        String domain = dictDTO.getDomain();
        String item = dictDTO.getItem();
        String value = dictDTO.getValue();
        String label = dictDTO.getLabel();
        String sequence = dictDTO.getSequence();
        String enable = dictDTO.getEnable();
        String parentId = dictDTO.getParentId();
        Map<String, String> paramMap = new HashMap<>();
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_DOMAIN, domain);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_ITEM, item);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_VALUE, value);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_LABEL, label);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_SEQUENCE, sequence);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_ENABLE, enable);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_PARENT_ID, parentId);
        List<DictDO> queryResult = this.dictRepository.query(paramMap, offset, pageSize);
        // 构造前端格式
        /*[
                {
                    "id":5,
                        "domain_name":"test1",
                        "item_name":"Cateogry",
                        "item_value":null,
                        "item_label":null,
                        "children":[
                    {
                        "id":8,
                            "domain_name":null,
                            "item_name":null,
                            "item_value":1,
                            "item_label":"Oral Care"
                    },
                    {
                        "id":10,
                            "domain_name":null,
                            "item_name":null,
                            "item_value":3,
                            "item_label":"Hair Care"
                    }
                ]
                }
        ]*/
        if (queryResult == null) {
            return null;
        }
        Map<String, DictQueryVO> resultMap = new HashMap<>();
        for (DictDO dictDO : queryResult) {
            String resultDomain = dictDO.getDomain();
            String resultItem = dictDO.getItem();
            String resultMapKey = resultDomain + resultItem;
            DictQueryVO dictQueryVO = resultMap.computeIfAbsent(resultMapKey, (k) -> {
                DictQueryVO tmpDictQueryVO = new DictQueryVO();
                tmpDictQueryVO.setDictDOList(new ArrayList<>());
                tmpDictQueryVO.setDomain(resultDomain);
                tmpDictQueryVO.setItem(resultItem);
                return tmpDictQueryVO;
            });
            List<DictDO> dictDOList = dictQueryVO.getDictDOList();
            dictDOList.add(dictDO);
        }
        return resultMap.values();
    }

}