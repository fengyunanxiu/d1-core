package ai.sparklabinc.d1.defaults.schedule;

import ai.sparklabinc.d1.defaults.dao.DefaultsConfigurationRepository;
import ai.sparklabinc.d1.defaults.entity.DefaultConfigurationType;
import ai.sparklabinc.d1.defaults.entity.DefaultsConfigurationDO;
import ai.sparklabinc.d1.defaults.plugin.DefaultValueSQLPlugin;
import ai.sparklabinc.d1.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 17:43
 * @description :
 */
@Component
public class DefaultsConfigurationSchedulerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultsConfigurationSchedulerManager.class);

    @Resource(name = "DefaultsConfigurationRepository")
    private DefaultsConfigurationRepository defaultsConfigurationRepository;

    @Autowired
    private DefaultValueSQLPlugin defaultValueSQLPlugin;

    /**
     * key: dfKey_fieldName_cron
     */
    private Map<String, ScheduledFuture<?>> runningScheduleMap = new ConcurrentHashMap<>();

    public void schedule() throws SQLException {
        List<DefaultsConfigurationDO> all = this.defaultsConfigurationRepository.queryAll();
        if (all == null || all.isEmpty()) {
            return;
        }
        // 筛选出自动执行的配置信息
        Map<String, DefaultsConfigurationDO> allSchedule = all.stream().filter((tmp) -> DefaultConfigurationType.AUTO.equals(tmp.getFieldType())).collect(Collectors.toMap((tmp) -> {
            String fieldFormDfKey = tmp.getFieldFormDfKey();
            String fieldFormFieldKey = tmp.getFieldFormFieldKey();
            String fieldPluginConf = tmp.getFieldPluginConf();
            JSONObject pluginConf = JSON.parseObject(fieldPluginConf);
            String cron = pluginConf.getString("cron");
            return fieldFormDfKey + "_" + fieldFormFieldKey + "_" + cron;
        }, (tmp) -> tmp));

        List<ScheduledFuture> needDeleteConfiguration = new ArrayList<>();
        for (Map.Entry<String, ScheduledFuture<?>> existSchedule : runningScheduleMap.entrySet()) {
            String key = existSchedule.getKey();
            ScheduledFuture<?> value = existSchedule.getValue();
            // 当前不应该再运行的schedule
            if (!allSchedule.containsKey(key)) {
                needDeleteConfiguration.add(value);
            } else {
                allSchedule.remove(key);
            }
        }
    }

    public void schedule(DefaultsConfigurationDO defaultsConfigurationDO) {
        String fieldPluginConf = defaultsConfigurationDO.getFieldPluginConf();
        if (StringUtils.isNullOrEmpty(fieldPluginConf)) {
            return;
        }
        // JSON格式
        JSONObject pluginParamsJSONObject = JSON.parseObject(fieldPluginConf);
        // 执行周期的cron表达式
        String type = pluginParamsJSONObject.getString("type");
        switch (type) {
            case "SQL":
                break;
            case "CUSTOMER":
                break;
            default:
                break;
        }

    }

}
