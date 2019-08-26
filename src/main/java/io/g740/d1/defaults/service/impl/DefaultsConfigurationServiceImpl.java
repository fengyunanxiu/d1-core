package io.g740.d1.defaults.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.g740.d1.defaults.dao.DefaultsConfigurationRepository;
import io.g740.d1.defaults.dto.DefaultsConfigurationDTO;
import io.g740.d1.defaults.entity.DefaultConfigurationType;
import io.g740.d1.defaults.entity.DefaultsConfigurationDO;
import io.g740.d1.defaults.service.DefaultsConfigurationService;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.exception.custom.DuplicateResourceException;
import io.g740.d1.exception.custom.IllegalParameterException;
import io.g740.d1.service.DataFacetKeyService;
import io.g740.d1.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Autowired
    private DataFacetKeyService dataFacetKeyService;

    @Override
    public DefaultsConfigurationDTO queryByDfKeyAndFieldKey(String dfKey, String fieldKey) throws Exception {
        if (StringUtils.isNullOrEmpty(dfKey)
                || StringUtils.isNullOrEmpty(fieldKey)) {
            throw new ServiceException("df key and field key 不能为空");
        }
        List<DefaultsConfigurationDO> defaultsConfigurationList = this.defaultsConfigurationRepository.queryByDfKeyAndFieldKey(dfKey, fieldKey);
        if (defaultsConfigurationList == null || defaultsConfigurationList.isEmpty()) {
            return null;
        }
        // 理论上一个dfKey，fieldKey对只有一个配置信息
        DefaultsConfigurationDO defaultsConfigurationDO = defaultsConfigurationList.get(0);
        DefaultsConfigurationDTO defaultsConfigurationDTO = new DefaultsConfigurationDTO();
        if (defaultsConfigurationDO != null) {
            defaultsConfigurationDTO.setFieldType(defaultsConfigurationDO.getFieldType().name());
            defaultsConfigurationDTO.setFormDfKey(defaultsConfigurationDO.getFieldFormDfKey());
            defaultsConfigurationDTO.setFormFieldKey(defaultsConfigurationDO.getFieldFormFieldKey());
            defaultsConfigurationDTO.setId(defaultsConfigurationDO.getFieldId());
            defaultsConfigurationDTO.setManualConf(defaultsConfigurationDO.getFieldManualConf());
            String fieldPluginConf = defaultsConfigurationDO.getFieldPluginConf();
            if (StringUtils.isNotNullNorEmpty(fieldPluginConf)) {
                JSONObject jsonObject = JSON.parseObject(fieldPluginConf);
                defaultsConfigurationDTO.setPluginCron(jsonObject.getString("cron"));
                defaultsConfigurationDTO.setPluginEnable(jsonObject.getString("enable"));
                defaultsConfigurationDTO.setPluginJdbcUrl(jsonObject.getString("jdbc_url"));
                defaultsConfigurationDTO.setPluginPassword(jsonObject.getString("password"));
                defaultsConfigurationDTO.setPluginSQL(jsonObject.getString("sql"));
                defaultsConfigurationDTO.setPluginType(jsonObject.getString("type"));
                defaultsConfigurationDTO.setPluginUsername(jsonObject.getString("username"));
            }
        }
        return defaultsConfigurationDTO;
    }

    @Override
    public void allocateDefaultsConfiguration(DefaultsConfigurationDTO defaultsConfigurationDTO) throws Exception {
        if (defaultsConfigurationDTO == null) {
            return;
        }
        String fieldFormDfKey = defaultsConfigurationDTO.getFormDfKey();
        String fieldFormFieldKey = defaultsConfigurationDTO.getFormFieldKey();
        if (StringUtils.isNullOrEmpty(fieldFormDfKey)
                || StringUtils.isNullOrEmpty(fieldFormFieldKey)) {
            throw new IllegalParameterException("field_form_df_key and field_form_field_key不能为空");
        }
        DefaultsConfigurationDO defaultsConfigurationDO = new DefaultsConfigurationDO();
        defaultsConfigurationDO.setFieldFormDfKey(defaultsConfigurationDTO.getFormDfKey());
        defaultsConfigurationDO.setFieldFormFieldKey(defaultsConfigurationDTO.getFormFieldKey());
        defaultsConfigurationDO.setFieldId(defaultsConfigurationDTO.getId());
        defaultsConfigurationDO.setFieldType(DefaultConfigurationType.valueOf(defaultsConfigurationDTO.getFieldType()));
        defaultsConfigurationDO.setFieldManualConf(defaultsConfigurationDTO.getManualConf());

        String pluginJdbcUrl = defaultsConfigurationDTO.getPluginJdbcUrl();
        String pluginPassword = defaultsConfigurationDTO.getPluginPassword();
        String pluginSQL = defaultsConfigurationDTO.getPluginSQL();
        String pluginUsername = defaultsConfigurationDTO.getPluginUsername();
        String pluginCron = defaultsConfigurationDTO.getPluginCron();
        String pluginEnable = defaultsConfigurationDTO.getPluginEnable();
        String pluginType = defaultsConfigurationDTO.getPluginType();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cron", pluginCron);
        jsonObject.put("type", pluginType);
        jsonObject.put("jdbc_url", pluginJdbcUrl);
        jsonObject.put("username", pluginUsername);
        jsonObject.put("password", pluginPassword);
        jsonObject.put("sql", pluginSQL);
        jsonObject.put("enable", pluginEnable);
        defaultsConfigurationDO.setFieldPluginConf(jsonObject.toJSONString());

        String fieldId = defaultsConfigurationDO.getFieldId();
        DefaultsConfigurationDO existDefaultConfiguration = null;
        if (fieldId != null) {
            existDefaultConfiguration = this.defaultsConfigurationRepository.queryById(fieldId);
        }
        if (existDefaultConfiguration != null) {
            this.defaultsConfigurationRepository.update(defaultsConfigurationDO);
        } else {
            // 写入新的数据，需要判断是否有重复数据
            List<DefaultsConfigurationDO> duplicateDefaultConfigurationList =
                    this.defaultsConfigurationRepository.queryByDfKeyAndFieldKey(fieldFormDfKey, fieldFormFieldKey);
            if (duplicateDefaultConfigurationList != null && !duplicateDefaultConfigurationList.isEmpty()) {
                throw new DuplicateResourceException(String.format("find duplicate field_form_df_key =%s and field_form_field_key = %s", fieldFormDfKey, fieldFormFieldKey));
            }
            this.defaultsConfigurationRepository.insert(defaultsConfigurationDO);
        }
        // 手动指定值的数据，需要将值更新到form table中。
        DefaultConfigurationType fieldType = defaultsConfigurationDO.getFieldType();
        if (DefaultConfigurationType.MANUAL.equals(fieldType)) {
            String fieldManualConf = defaultsConfigurationDO.getFieldManualConf();
            this.dataFacetKeyService.updateDefaultValueByDfKeyAndFieldKey(fieldFormDfKey, fieldFormFieldKey, fieldManualConf);
        }
    }

}
