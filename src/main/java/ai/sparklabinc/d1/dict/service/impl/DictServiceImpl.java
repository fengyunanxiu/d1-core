package ai.sparklabinc.d1.dict.service.impl;

import ai.sparklabinc.d1.dict.dao.DictRepository;
import ai.sparklabinc.d1.dict.dto.DictDTO;
import ai.sparklabinc.d1.dict.service.DictService;
import ai.sparklabinc.d1.dict.vo.DictQueryVO;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.dto.PageResultDTO;
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
        List<DictDO> byDomainAndItem = this.dictRepository.findByDomainAndItemAndValue(dictDOList);
        if (byDomainAndItem != null && !byDomainAndItem.isEmpty()) {
            String existMsg = byDomainAndItem.stream().map((item) -> String.format("domain:%s, item:%s, value:%s", item.getFieldDomain(), item.getFieldItem(), item.getFieldValue())).collect(Collectors.joining(";"));
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
        List<DictDO> existDictList = this.dictRepository.findByDomainAndItemAndValue(dictDOList);
        if (existDictList != null && !existDictList.isEmpty()) {
            String existMsg = existDictList.stream().map((item) -> String.format("domain:%s, item:%s, value:%s", item.getFieldDomain(), item.getFieldItem(), item.getFieldValue())).collect(Collectors.joining(";"));
            throw new DuplicateResourceException("Find Duplicate Domain And Item , " + existMsg);
        }
        this.dictRepository.batchUpdate(dictDOList);
    }

    @Override
    public PageResultDTO<DictQueryVO> query(DictDTO dictDTO, Long offset, Integer pageSize) throws Exception {
        if (offset == null) {
            offset = 0L;
        }
        if (pageSize == null) {
            pageSize = 0;
        }
        String domain = dictDTO.getFieldDomain();
        String item = dictDTO.getFieldItem();
        String value = dictDTO.getFieldValue();
        String label = dictDTO.getFieldLabel();
        String sequence = dictDTO.getFieldSequence();
        String enable = dictDTO.getFieldEnable();
        String parentId = dictDTO.getFieldParentId();
        Map<String, String> paramMap = new HashMap<>();
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_DOMAIN, domain);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_ITEM, item);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_VALUE, value);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_LABEL, label);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_SEQUENCE, sequence);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_ENABLE, enable);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_PARENT_ID, parentId);

        // 查询不重复的domain和item总数
        long count = this.dictRepository.countByDomainAndItem(paramMap);

        // 先分页查询出需要的domain和item
        List<DictDO> queryResult = this.dictRepository.queryLimitByDomainAndItem(paramMap, offset, pageSize);
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
        Map<String, DictQueryVO> resultMap = new LinkedHashMap<>();
        for (DictDO dictDO : queryResult) {
            String resultDomain = dictDO.getFieldDomain();
            String resultItem = dictDO.getFieldItem();
            String resultMapKey = resultDomain + resultItem;
            DictQueryVO dictQueryVO = resultMap.computeIfAbsent(resultMapKey, (k) -> {
                DictQueryVO tmpDictQueryVO = new DictQueryVO();
                tmpDictQueryVO.setDictList(new ArrayList<>());
                tmpDictQueryVO.setFieldDomain(resultDomain);
                tmpDictQueryVO.setFieldItem(resultItem);
                return tmpDictQueryVO;
            });
            List<DictDO> dictDOList = dictQueryVO.getDictList();
            dictDO.setFieldDomain(null);
            dictDO.setFieldItem(null);
            dictDOList.add(dictDO);
        }
        return new PageResultDTO<DictQueryVO>(new ArrayList<>(resultMap.values()), count);

    }

}
