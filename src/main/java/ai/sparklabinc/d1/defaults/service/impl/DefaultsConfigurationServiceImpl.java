package ai.sparklabinc.d1.defaults.service.impl;

import ai.sparklabinc.d1.defaults.dao.DefaultsConfigurationRepository;
import ai.sparklabinc.d1.defaults.entity.DefaultsConfigurationDO;
import ai.sparklabinc.d1.defaults.service.DefaultsConfigurationService;
import ai.sparklabinc.d1.exception.ServiceException;
import ai.sparklabinc.d1.exception.custom.DuplicateResourceException;
import ai.sparklabinc.d1.exception.custom.IllegalParameterException;
import ai.sparklabinc.d1.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 15:42
 * @description :
 */
@Service
public class DefaultsConfigurationServiceImpl implements DefaultsConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultsConfigurationServiceImpl.class);

    @Resource(name = "DefaultsConfigurationRepository")
    private DefaultsConfigurationRepository defaultsConfigurationRepository;

    @Override
    public DefaultsConfigurationDO queryByDfKeyAndFieldKey(String dfKey, String fieldKey) throws Exception {
        if (StringUtils.isNullOrEmpty(dfKey)
                || StringUtils.isNullOrEmpty(fieldKey)) {
            throw new ServiceException("df key and field key 不能为空");
        }
        List<DefaultsConfigurationDO> defaultsConfigurationList = this.defaultsConfigurationRepository.queryByDfKeyAndFieldKey(dfKey, fieldKey);
        if (defaultsConfigurationList == null || defaultsConfigurationList.isEmpty()) {
            return null;
        }
        // 理论上一个dfKey，fieldKey对只有一个配置信息
        return defaultsConfigurationList.get(0);
    }

    @Override
    public void allocateDefaultsConfiguration(DefaultsConfigurationDO defaultsConfigurationDO) throws Exception {
        if (defaultsConfigurationDO == null) {
            return;
        }
        String fieldFormDfKey = defaultsConfigurationDO.getFieldFormDfKey();
        String fieldFormFieldKey = defaultsConfigurationDO.getFieldFormFieldKey();
        if (StringUtils.isNullOrEmpty(fieldFormDfKey)
                || StringUtils.isNullOrEmpty(fieldFormFieldKey)) {
            throw new IllegalParameterException("field_form_df_key and field_form_field_key不能为空");
        }
        String fieldId = defaultsConfigurationDO.getFieldId();
        if (fieldId != null) {
            DefaultsConfigurationDO existDefaultConfiguration = this.defaultsConfigurationRepository.queryById(fieldId);
            if (existDefaultConfiguration != null) {
                this.defaultsConfigurationRepository.update(defaultsConfigurationDO);
                return;
            }
        }
        // 写入新的数据，需要判断是否有重复数据
        List<DefaultsConfigurationDO> duplicateDefaultConfigurationList =
                this.defaultsConfigurationRepository.queryByDfKeyAndFieldKey(fieldFormDfKey, fieldFormFieldKey);
        if (duplicateDefaultConfigurationList != null && !duplicateDefaultConfigurationList.isEmpty()) {
            throw new DuplicateResourceException(String.format("find duplicate field_form_df_key =%s and field_form_field_key = %s", fieldFormDfKey, fieldFormFieldKey));
        }
        this.defaultsConfigurationRepository.insert(defaultsConfigurationDO);
    }

}
