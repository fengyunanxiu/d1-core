package io.g740.d1.dict.service.impl;

import io.g740.d1.dict.dao.DictRepository;
import io.g740.d1.dict.dto.DictDTO;
import io.g740.d1.dict.dto.DictOptionCascadeQueryDTO;
import io.g740.d1.dict.service.DictService;
import io.g740.d1.dict.vo.DictQueryVO;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.dto.PageResultDTO;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.exception.custom.DuplicateResourceException;
import io.g740.d1.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * @param idList
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public void batchDelete(List<String> idList) throws Exception {
        this.dictRepository.batchDelete(idList);
    }

    @Override
    public void batchUpdate(List<DictDO> dictDOList) throws Exception {
        try {
            this.dictRepository.batchUpdate(dictDOList);
        } catch (SQLException e) {
            // 有重复数据时，查询重复的数据
            if (e.getMessage().contains("Duplicate")) {
                throw new DuplicateResourceException("find duplicate data");
            }
            throw e;
        }
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
        String parentId = dictDTO.getFieldParentId();
        Map<String, String> paramMap = new HashMap<>();
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_DOMAIN, domain);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_ITEM, item);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_VALUE, value);
        CollectionUtils.putIfKVNotNull(paramMap, DictDO.F_LABEL, label);

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
        return new PageResultDTO<>(new ArrayList<>(resultMap.values()), count);

    }

    @Override
    public void addDictList(List<DictDTO> dictDTOS) throws Exception {
        String domain = dictDTOS.get(0).getFieldDomain();
        String item = dictDTOS.get(0).getFieldItem();
        List<DictDO> dictDOList = this.dictRepository.findByDomainAndItem(domain, item);

        List<String> values = dictDTOS.stream().map(DictDTO::getFieldValue).collect(Collectors.toList());
        long maxSequence = 0L;
        // domainItem创建时间
        Date domainItemGmtCreate = new Date();
        if (!CollectionUtils.isEmpty(dictDOList)) {
            List<DictDO> byValueDictDOList = this.dictRepository.findByApplication(domain, item, values);
            if (!org.springframework.util.CollectionUtils.isEmpty(byValueDictDOList)) {
                throw new DuplicateResourceException(String.format("domain : %s, item:%s ,value :%s is duplicate", domain, item, byValueDictDOList.get(0).getFieldValue()));
            }
            domainItemGmtCreate = dictDOList.get(0).getDomainItemGmtCreate();
            maxSequence = dictDOList.stream().map(DictDO::getFieldSequence).mapToLong(Long::valueOf).max().orElse(maxSequence);
        }
        //
        List<DictDO> dictDOS = new LinkedList<>();
        for (DictDTO dictDTO : dictDTOS) {
            maxSequence++;
            dictDTO.setDomainItemGmtCreate(domainItemGmtCreate);
            dictDTO.setFieldSequence(String.valueOf(maxSequence));
            dictDOS.add(dictDTO);
        }
        this.dictRepository.batchInsert(dictDOS);
    }

    @Override
    public void deleteDomain(DictDTO dictDTO) throws SQLException {
        String domain = dictDTO.getFieldDomain();
        String item = dictDTO.getFieldItem();
        this.dictRepository.deleteByDomainAndItem(domain, item);
    }

    @Override
    public List<DictOptionCascadeQueryDTO> cascadeQueryByDomainAndItem(String domain, String item) throws Exception {
        // 查询当前的domain，item
        List<DictDO> dictDOList = this.dictRepository.findByDomainAndItem(domain, item);
        if (dictDOList == null || dictDOList.isEmpty()) {
            return null;
        }
        // 查询当前domain，item的下一级
        List<String> dictIdList = dictDOList.stream().map(DictDO::getFieldId).collect(Collectors.toList());
        List<DictDO> childDictDOList = this.dictRepository.findByParentIdList(dictIdList);
        if (childDictDOList == null || childDictDOList.isEmpty()) {
            // 只有一级结构
            return dictDOList.stream().map(dictDO -> {
                String fieldLabel = dictDO.getFieldLabel();
                String fieldValue = dictDO.getFieldValue();
                DictOptionCascadeQueryDTO dictOptionCascadeQueryDTO = new DictOptionCascadeQueryDTO();
                dictOptionCascadeQueryDTO.setOptionLabel(fieldLabel);
                dictOptionCascadeQueryDTO.setOptionValue(fieldValue);
                return dictOptionCascadeQueryDTO;
            }).collect(Collectors.toList());
        }
        // 根据当前domain，item的value分类下一级
        Map<String, List<DictDO>> parentIdDictMapList = childDictDOList.stream().collect(Collectors.groupingBy(DictDO::getFieldParentId, Collectors.toList()));
        return dictDOList.stream().map(dictDO -> {
            String fieldLabel = dictDO.getFieldLabel();
            String fieldValue = dictDO.getFieldValue();
            DictOptionCascadeQueryDTO dictOptionCascadeQueryDTO = new DictOptionCascadeQueryDTO();
            dictOptionCascadeQueryDTO.setOptionLabel(fieldLabel);
            dictOptionCascadeQueryDTO.setOptionValue(fieldValue);
            List<DictDO> tmpChildDictDOList = parentIdDictMapList.get(dictDO.getFieldId());
            if (tmpChildDictDOList != null && !tmpChildDictDOList.isEmpty()) {
                dictOptionCascadeQueryDTO.setChildren(tmpChildDictDOList.stream().map(childDictDO -> {
                    String childFieldLabel = childDictDO.getFieldLabel();
                    String childFieldValue = childDictDO.getFieldValue();
                    DictOptionCascadeQueryDTO childDictOptionCascadeQueryDTO = new DictOptionCascadeQueryDTO();
                    childDictOptionCascadeQueryDTO.setOptionLabel(childFieldLabel);
                    childDictOptionCascadeQueryDTO.setOptionValue(childFieldValue);
                    return childDictOptionCascadeQueryDTO;
                }).collect(Collectors.toList()));
            }
            return dictOptionCascadeQueryDTO;
        }).collect(Collectors.toList());
    }
}
