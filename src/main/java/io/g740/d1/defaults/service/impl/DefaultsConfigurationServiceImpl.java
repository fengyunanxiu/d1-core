package io.g740.d1.defaults.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.g740.d1.defaults.dao.DefaultsConfigurationRepository;
import io.g740.d1.defaults.dto.DefaultsConfigurationDTO;
import io.g740.d1.defaults.entity.DefaultConfigurationType;
import io.g740.d1.defaults.entity.DefaultsConfigurationDO;
import io.g740.d1.defaults.service.DefaultsConfigurationService;
import io.g740.d1.engine.SQLEngine;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.exception.custom.DuplicateResourceException;
import io.g740.d1.exception.custom.IllegalParameterException;
import io.g740.d1.service.DataFacetKeyService;
import io.g740.d1.util.CollectionUtils;
import io.g740.d1.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private SQLEngine sqlEngine;

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

        String fieldId = defaultsConfigurationDTO.getId();
        DefaultsConfigurationDO existDefaultConfiguration = null;
        if (StringUtils.isNotNullNorEmpty(fieldId)) {
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
        // 填入form table中标识选择的默认值策略类型
        this.dataFacetKeyService.updateDefaultValueStrategyType(fieldFormDfKey, fieldFormFieldKey, fieldType.name());
    }


    @Override
    public Collection<String> executeSQLTest(DefaultsConfigurationDTO defaultsConfigurationDTO) {
        String pluginJdbcUrl = defaultsConfigurationDTO.getPluginJdbcUrl();
        String pluginUsername = defaultsConfigurationDTO.getPluginUsername();
        String pluginPassword = defaultsConfigurationDTO.getPluginPassword();
        String pluginSQL = defaultsConfigurationDTO.getPluginSQL();
        List<Map<String, String>> executeResult = this.sqlEngine.execute(pluginJdbcUrl, pluginUsername, pluginPassword, pluginSQL);
        Map<String, String> rowMap = executeResult.get(0);
        Collection<String> values = rowMap.values();
        return values;
    }


    // 要区分两种默认值，；一类只有fieldDomain和fieldItem ;一类是全的；这里根据fieldType有无进行区分
    @Override
    public void saveBatchListForForm(List<DefaultsConfigurationDTO> defaultsConfigurationDTOS) throws SQLException {
        if(!CollectionUtils.isEmpty(defaultsConfigurationDTOS)){

            List<DefaultsConfigurationDTO> updateManualConfByDomainItemConfigurationDTOS = defaultsConfigurationDTOS.stream().filter(e -> e.getFieldType() == null)
                    .collect(Collectors.toList());

            List<DefaultsConfigurationDTO> allInfoDefaultsConfigurationDTOS = defaultsConfigurationDTOS.stream().filter(e -> e.getFieldType() != null)
                    .collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(updateManualConfByDomainItemConfigurationDTOS)){
                this.defaultsConfigurationRepository.updateManualConfListByDomainItem(updateManualConfByDomainItemConfigurationDTOS);
            }

            if(!CollectionUtils.isEmpty(allInfoDefaultsConfigurationDTOS)){
                List<DefaultsConfigurationDO> defaultsConfigurationDOS = new LinkedList<>();
                DefaultsConfigurationDO defaultsConfigurationDO = null;
                for (DefaultsConfigurationDTO allInfoDefaultsConfigurationDTO : allInfoDefaultsConfigurationDTOS) {
                    defaultsConfigurationDO = new DefaultsConfigurationDO();
                    defaultsConfigurationDO.setFieldFormDfKey(allInfoDefaultsConfigurationDTO.getFormDfKey());
                    defaultsConfigurationDO.setFieldFormFieldKey(allInfoDefaultsConfigurationDTO.getFormFieldKey());
                    defaultsConfigurationDO.setFieldId(allInfoDefaultsConfigurationDTO.getId());
                    defaultsConfigurationDO.setFieldType(DefaultConfigurationType.valueOf(allInfoDefaultsConfigurationDTO.getFieldType()));
                    defaultsConfigurationDO.setFieldManualConf(allInfoDefaultsConfigurationDTO.getManualConf());
                    // 通过唯一主键 dfKey和fieldName进行更新，id其实用不上
                    defaultsConfigurationDO.setFieldId(allInfoDefaultsConfigurationDTO.getId());

                    String pluginJdbcUrl = allInfoDefaultsConfigurationDTO.getPluginJdbcUrl();
                    String pluginPassword = allInfoDefaultsConfigurationDTO.getPluginPassword();
                    String pluginSQL = allInfoDefaultsConfigurationDTO.getPluginSQL();
                    String pluginUsername = allInfoDefaultsConfigurationDTO.getPluginUsername();
                    String pluginCron = allInfoDefaultsConfigurationDTO.getPluginCron();
                    String pluginEnable = allInfoDefaultsConfigurationDTO.getPluginEnable();
                    String pluginType = allInfoDefaultsConfigurationDTO.getPluginType();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cron", pluginCron);
                    jsonObject.put("type", pluginType);
                    jsonObject.put("jdbc_url", pluginJdbcUrl);
                    jsonObject.put("username", pluginUsername);
                    jsonObject.put("password", pluginPassword);
                    jsonObject.put("sql", pluginSQL);
                    jsonObject.put("enable", pluginEnable);
                    defaultsConfigurationDO.setFieldPluginConf(jsonObject.toJSONString());
                    defaultsConfigurationDOS.add(defaultsConfigurationDO);
                }
                this.defaultsConfigurationRepository.saveOrUpdateList(defaultsConfigurationDOS);
            }
        }
    }

}
