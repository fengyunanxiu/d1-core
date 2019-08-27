package io.g740.d1.dict.service.impl;

import io.g740.d1.dict.dao.DictRepository;
import io.g740.d1.dict.dto.DictDTO;
import io.g740.d1.dict.service.DictService;
import io.g740.d1.dict.vo.DictQueryVO;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.dto.PageResultDTO;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.exception.custom.DuplicateResourceException;
import io.g740.d1.exception.custom.IllegalParameterException;
import io.g740.d1.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
     * @param idList
     * @throws ServiceException
     * @throws SQLException
     */
    @Override
    public void batchDelete(List<String> idList) throws Exception {
        this.dictRepository.batchDelete(idList);
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
        return new PageResultDTO<>(new ArrayList<>(resultMap.values()), count);

    }

    @Override
    public void addBaseDictList(List<DictDTO> dictDTOS) throws Exception {
       String domain = dictDTOS.get(0).getFieldDomain();
       String item = dictDTOS.get(0).getFieldItem();
       List<DictDO> dictDOList = this.dictRepository.findByDomainAndItem(domain, item);
       if(!org.springframework.util.CollectionUtils.isEmpty(dictDOList)){
           throw new DuplicateResourceException(String.format("Domain:%s,Item:%s is duplicate",domain,item));
       }
       //遍历设置序号
       int index = 1;
       List<DictDO> dictDOS = new LinkedList<>();
       DictDO dictDO = null;

        for (DictDTO dictDTO : dictDTOS) {
            dictDO = new DictDO();
            BeanUtils.copyProperties(dictDTO,dictDO);
            dictDO.setFieldSequence("" + (index) );
            dictDO.setFieldEnable("1");
            index ++;
            dictDOS.add(dictDO);
        }
        this.dictRepository.batchInsert(dictDOS);
    }

    @Override
    public void addBaseDict(DictDTO dictDTO) throws SQLException, ServiceException {
        String domain = dictDTO.getFieldDomain();
        String item = dictDTO.getFieldItem();
        String value = dictDTO.getFieldValue();
        String label = dictDTO.getFieldLabel();
        List<DictDO> dictDOS = this.dictRepository.findByApplication(domain,item,value);
        if(!org.springframework.util.CollectionUtils.isEmpty(dictDOS)){
            throw new DuplicateResourceException("duplicate domain,item,value");
        }
        dictDOS = new LinkedList<>();
        DictDO dictDo = new DictDO();
        BeanUtils.copyProperties(dictDTO,dictDo);
        dictDOS.add(dictDo);
        this.dictRepository.batchInsert(dictDOS);
    }

    @Override
    public void updateBaseDict(DictDTO dictDTO) throws SQLException, ServiceException {
        String domain = dictDTO.getFieldDomain();
        String item = dictDTO.getFieldItem();
        String value = dictDTO.getFieldValue();
        String label = dictDTO.getFieldLabel();
        String fileId = dictDTO.getFieldId();
        List<DictDO> dictDOS = this.dictRepository.findByApplication(domain,item,value);
        if(!org.springframework.util.CollectionUtils.isEmpty(dictDOS)){
           DictDO dctDO = dictDOS.get(0);
           if(!fileId.equals(dctDO.getFieldId())){
               throw new DuplicateResourceException("duplicate domain,item,value");
           }
        }
        dictDOS = new LinkedList<>();
        DictDO dictDo = new DictDO();
        BeanUtils.copyProperties(dictDTO,dictDo);
        dictDOS.add(dictDo);
        this.dictRepository.batchUpdate(dictDOS);





    }

    @Override
    public void deleteDomain(DictDTO dictDTO) throws SQLException {
        String domain = dictDTO.getFieldDomain();
        String item = dictDTO.getFieldItem();
        this.dictRepository.deleteByDomainAndItem(domain,item);
    }

    /**
     * 更新Domain，Item的名字
     * @param domain
     * @param item
     * @param firstValueId
     */
    public void updateDomainNameOrItemName(String domain, String item, String firstValueId) throws Exception {
        DictDO existDictDO = this.dictRepository.findById(firstValueId);
        if (existDictDO == null) {
            throw new IllegalParameterException("Dict not found");
        }

        // 查询是否有相同的domain，item
        List<DictDO> existDictList = this.dictRepository.findByDomainAndItem(domain, item);
        if (existDictList != null && !existDictList.isEmpty()) {
            throw new DuplicateResourceException("find exists domain " + domain + " item " + item);
        }
        this.dictRepository.updateDomainNameOrItemName(existDictDO.getFieldDomain(), domain, existDictDO.getFieldItem(), item);
    }


}