package io.g740.d1.defaults.plugin;

import io.g740.d1.defaults.dto.DefaultsConfigurationDTO;
import io.g740.d1.defaults.entity.DefaultsConfigurationDO;
import io.g740.d1.defaults.service.DefaultsConfigurationService;
import io.g740.d1.engine.SQLEngine;
import io.g740.d1.service.DataFacetKeyService;
import io.g740.d1.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/8 17:11
 * @description :
 */
@Component
public class DefaultValueSQLPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultValueSQLPlugin.class);

    @Autowired
    private SQLEngine sqlEngine;

    @Autowired
    private DataFacetKeyService dataFacetKeyService;

    @Autowired
    private DefaultsConfigurationService defaultsConfigurationService;

    @Autowired
    private TaskScheduler taskScheduler;

    public ScheduledFuture<?> run(DefaultsConfigurationDO defaultsConfigurationDO) {
        String fieldPluginConf = defaultsConfigurationDO.getFieldPluginConf();
        if (StringUtils.isNullOrEmpty(fieldPluginConf)) {
            return null;
        }
        JSONObject pluginParamsJSONObject = JSON.parseObject(fieldPluginConf);
        String cron = pluginParamsJSONObject.getString("cron");
        CronTrigger cronTrigger = new CronTrigger(cron);
        return this.taskScheduler.schedule(() -> {
            try {
                LOGGER.info("begin to process default value sql plugin task, id: {}", defaultsConfigurationDO.getFieldId());
                process(defaultsConfigurationDO);
                LOGGER.info("end to process default value sql plugin task, id: {}", defaultsConfigurationDO.getFieldId());
            } catch (Exception e) {
                LOGGER.error("failed to process default value sql plugin task id: " + defaultsConfigurationDO.getFieldId(), e);
            }
        }, cronTrigger);
    }

    private void process(DefaultsConfigurationDO defaultsConfigurationDO) throws Exception {
        String fieldPluginConf = defaultsConfigurationDO.getFieldPluginConf();
        if (StringUtils.isNullOrEmpty(fieldPluginConf)) {
            return;
        }
        String fieldFormDfKey = defaultsConfigurationDO.getFieldFormDfKey();
        String fieldFormFieldKey = defaultsConfigurationDO.getFieldFormFieldKey();
        // 每次执行任务，都要查询最新的任务信息
        DefaultsConfigurationDTO defaultsConfigurationDTO = this.defaultsConfigurationService.queryByDfKeyAndFieldKey(fieldFormDfKey, fieldFormFieldKey);
        String sql = defaultsConfigurationDTO.getPluginSQL();
        String jdbcUrl = defaultsConfigurationDTO.getPluginJdbcUrl();
        String username = defaultsConfigurationDTO.getPluginUsername();
        String password = defaultsConfigurationDTO.getPluginPassword();
        if (StringUtils.isNullOrEmpty(sql)) {
            return;
        }
        List<Map<String, String>> result = sqlEngine.execute(jdbcUrl, username, password, sql);
        if (result == null || result.isEmpty()) {
            return;
        }

        Map<String, String> rowMap = result.get(0);
        Collection<String> values = rowMap.values();
        String defaultValue = JSON.toJSONString(values);
        // 更新默认值到FormTableSetting
        this.dataFacetKeyService.updateDefaultValueByDfKeyAndFieldKey(fieldFormDfKey, fieldFormFieldKey, defaultValue);
    }

}
