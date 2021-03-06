package io.g740.d1.defaults.plugin;

import io.g740.d1.defaults.dao.DefaultsConfigurationRepository;
import io.g740.d1.defaults.entity.DefaultConfigurationType;
import io.g740.d1.defaults.entity.DefaultsConfigurationDO;
import io.g740.d1.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
public class DefaultsPluginTaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultsPluginTaskManager.class);

    @Resource(name = "DefaultsConfigurationRepository")
    private DefaultsConfigurationRepository defaultsConfigurationRepository;

    @Autowired
    private DefaultValueSQLPlugin defaultValueSQLPlugin;

    @Autowired
    private TaskScheduler taskScheduler;

    @Resource(name = "D1BasicDataSource")
    private DataSource d1BasicDataSource;

    /**
     * key: dfKey_fieldName_cron
     */
    private static final Map<String, ScheduledFuture<?>> RUNNING_SCHEDULE_MAP = new ConcurrentHashMap<>();

    public void run() {
        LOGGER.info("start to initialize default value task schedule");
        this.taskScheduler.scheduleWithFixedDelay(
                () -> task(),
                new Date(), 10 * 1000L);
    }

    private void task() {
        LOGGER.info("start default value schedule");
        try {
            List<DefaultsConfigurationDO> all = this.defaultsConfigurationRepository.queryAll();
            if (all == null ) {
                all = new ArrayList<>();
            }
            // 筛选出自动执行的配置信息
            Map<String, DefaultsConfigurationDO> allSchedule = all.stream()
                    .filter((tmp) -> {
                        boolean auto = DefaultConfigurationType.AUTO.equals(tmp.getFieldType());
                        if (auto) {
                            String fieldPluginConf = tmp.getFieldPluginConf();
                            if (StringUtils.isNotNullNorEmpty(fieldPluginConf)) {
                                JSONObject pluginConfJSON = JSON.parseObject(fieldPluginConf);
                                Boolean enable = pluginConfJSON.getBoolean("enable");
                                return Boolean.TRUE.equals(enable);
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toMap((tmp) -> generateRunningScheduleMapKey(tmp), (tmp) -> tmp));

            if (allSchedule.isEmpty()) {
                LOGGER.info("default task schedule is empty");
            }
            // 当前不应该再运行的schedule
            List<Map.Entry<String, ScheduledFuture<?>>> needDeleteConfigurationEntryList = new ArrayList<>();
            for (Map.Entry<String, ScheduledFuture<?>> existSchedule : RUNNING_SCHEDULE_MAP.entrySet()) {
                String key = existSchedule.getKey();
                if (allSchedule.containsKey(key)) {
                    allSchedule.remove(key);
                } else {
                    needDeleteConfigurationEntryList.add(existSchedule);
                }
            }
            // 删除不应再执行的schedule
            if (!needDeleteConfigurationEntryList.isEmpty()) {
                LOGGER.info("begin to cancel unused default value task, size: {}", needDeleteConfigurationEntryList.size());
                for (Map.Entry<String, ScheduledFuture<?>> needDeleteConfiguration : needDeleteConfigurationEntryList) {
                    String key = needDeleteConfiguration.getKey();
                    ScheduledFuture<?> scheduledFuture = needDeleteConfiguration.getValue();
                    boolean cancel = scheduledFuture.cancel(false);
                    if (cancel) {
                        RUNNING_SCHEDULE_MAP.remove(key);
                        LOGGER.info("success to cancel unused default value task, key: {}" + key);
                    }
                }
                LOGGER.info("end to cancel unused default value task, size: {}", needDeleteConfigurationEntryList.size());
            }
            // 执行新的schedule
            if (!allSchedule.isEmpty()) {
                LOGGER.info("begin start new default value task, size: {}", allSchedule.size());
                for (DefaultsConfigurationDO defaultsConfigurationDO : allSchedule.values()) {
                    ScheduledFuture<?> scheduledFuture = this.schedule(defaultsConfigurationDO);
                    if (scheduledFuture != null) {
                        String key = generateRunningScheduleMapKey(defaultsConfigurationDO);
                        RUNNING_SCHEDULE_MAP.put(key, scheduledFuture);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private ScheduledFuture<?> schedule(DefaultsConfigurationDO defaultsConfigurationDO) {
        String fieldPluginConf = defaultsConfigurationDO.getFieldPluginConf();
        JSONObject pluginJSON = JSON.parseObject(fieldPluginConf);
        String type = pluginJSON.getString("type");
        switch (type) {
            case "SQL":
                return this.defaultValueSQLPlugin.run(defaultsConfigurationDO);
            case "CUSTOMER":
                break;
            default:
                break;
        }
        return null;
    }

    private String generateRunningScheduleMapKey(DefaultsConfigurationDO defaultsConfigurationDO) {
        return String.valueOf(defaultsConfigurationDO.hashCode());
    }

}
