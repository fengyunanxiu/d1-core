package io.g740.d1.dict.service;

import io.g740.d1.dict.entity.FormDictConfigurationDO;
import io.g740.d1.dict.vo.FormDictConfigurationVO;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 10:34
 * @description :
 */
public interface FormDictConfigurationService {
    FormDictConfigurationVO queryByForm(String formDfKey, String formFieldKey) throws Exception;

    void allocateFormDictConfiguration(FormDictConfigurationDO formDictConfigurationDO) throws Exception;
}
