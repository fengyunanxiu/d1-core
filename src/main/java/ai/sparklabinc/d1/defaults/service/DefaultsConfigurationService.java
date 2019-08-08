package ai.sparklabinc.d1.defaults.service;

import ai.sparklabinc.d1.defaults.entity.DefaultsConfigurationDO;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 15:42
 * @description :
 */
public interface DefaultsConfigurationService {
    DefaultsConfigurationDO queryByDfKeyAndFieldKey(String dfKey, String fieldKey) throws Exception;

    void allocateDefaultsConfiguration(DefaultsConfigurationDO defaultsConfigurationDO) throws Exception;
}
