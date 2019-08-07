package ai.sparklabinc.d1.dict.service.impl;

import ai.sparklabinc.d1.dict.dao.DictRepository;
import ai.sparklabinc.d1.dict.dao.FormDictConfigurationRepository;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.dict.entity.FormDictConfigurationDO;
import ai.sparklabinc.d1.dict.service.FormDictConfigurationService;
import ai.sparklabinc.d1.dict.vo.FormDictConfigurationVO;
import ai.sparklabinc.d1.exception.ServiceException;
import ai.sparklabinc.d1.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 10:34
 * @description :
 */
@Service
public class FormDictConfigurationServiceImpl implements FormDictConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormDictConfigurationServiceImpl.class);

    @Resource(name = "DictRepository")
    private DictRepository dictRepository;

    @Resource(name = "FormDictConfigurationRepository")
    private FormDictConfigurationRepository formDictConfigurationRepository;

    @Override
    public FormDictConfigurationVO queryByForm(String formDfKey, String formFieldKey) throws Exception {
        if (StringUtils.isNullOrEmpty(formDfKey) || StringUtils.isNullOrEmpty(formFieldKey)) {
            return null;
        }
        List<FormDictConfigurationDO> formDictConfigurationList = this.formDictConfigurationRepository.queryByFrom(formDfKey, formFieldKey);
        if (formDictConfigurationList == null || formDictConfigurationList.isEmpty()) {
            return null;
        }
        // 一个form facet key，form field key原则上只能有一个domain和item
        FormDictConfigurationDO formDictConfigurationDO = formDictConfigurationList.get(0);
        String domain = formDictConfigurationDO.getFieldDomain();
        String item = formDictConfigurationDO.getFieldItem();
        if (StringUtils.isNullOrEmpty(domain) || StringUtils.isNullOrEmpty(item)) {
           return null;
        }
        List<DictDO> dictDOList = this.dictRepository.findByDomainAndItem(domain, item);
        if (dictDOList == null || dictDOList.isEmpty()) {
            return null;
        }
        // 构造前端数据结构
        FormDictConfigurationVO formDictConfigurationVO = new FormDictConfigurationVO();
        formDictConfigurationVO.setDictList(dictDOList);
        formDictConfigurationVO.setFieldDomain(domain);
        formDictConfigurationVO.setFieldItem(item);
        formDictConfigurationVO.setFieldFormDfKey(formDfKey);
        formDictConfigurationVO.setFieldFormFieldKey(formFieldKey);
        return formDictConfigurationVO;
    }


    @Override
    public void allocateFormDictConfiguration(FormDictConfigurationDO formDictConfigurationDO) throws Exception {
        if (formDictConfigurationDO == null) {
            return;
        }
        String id = formDictConfigurationDO.getFieldId();
        if (id != null) {
            FormDictConfigurationDO existFormDictConfigurationDO = this.formDictConfigurationRepository.queryById(id);
            if (existFormDictConfigurationDO != null) {
                this.formDictConfigurationRepository.update(formDictConfigurationDO);
                return;
            }
        }
        this.formDictConfigurationRepository.add(formDictConfigurationDO);
    }

}
