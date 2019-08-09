package ai.sparklabinc.d1.defaults.plugin;

import ai.sparklabinc.d1.defaults.entity.DefaultsConfigurationDO;
import ai.sparklabinc.d1.defaults.service.DefaultsConfigurationService;
import ai.sparklabinc.d1.engine.SQLEngine;
import ai.sparklabinc.d1.service.DataFacetKeyService;
import ai.sparklabinc.d1.util.StringUtils;
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
                LOGGER.info("begin to process default value sql plugin task");
                process(defaultsConfigurationDO);
                LOGGER.info("end to process default value sql plugin task");
            } catch (Exception e) {
                LOGGER.error("", e);
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
        defaultsConfigurationDO = this.defaultsConfigurationService.queryByDfKeyAndFieldKey(fieldFormDfKey, fieldFormFieldKey);
        JSONObject pluginParamsJSONObject = JSON.parseObject(fieldPluginConf);
        String jdbcUrl = pluginParamsJSONObject.getString("jdbc_url");
        String username = pluginParamsJSONObject.getString("username");
        String password = pluginParamsJSONObject.getString("password");
        String sql = pluginParamsJSONObject.getString("sql");
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
