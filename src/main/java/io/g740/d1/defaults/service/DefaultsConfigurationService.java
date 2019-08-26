package io.g740.d1.defaults.service;

import io.g740.d1.defaults.dto.DefaultsConfigurationDTO;
import io.g740.d1.defaults.entity.DefaultsConfigurationDO;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 15:42
 * @description :
 */
public interface DefaultsConfigurationService {
    DefaultsConfigurationDTO queryByDfKeyAndFieldKey(String dfKey, String fieldKey) throws Exception;

    void allocateDefaultsConfiguration(DefaultsConfigurationDTO defaultsConfigurationDTO) throws Exception;
}
