package ai.sparklabinc.d1.defaults.plugin;

import ai.sparklabinc.d1.defaults.dao.DefaultsConfigurationRepository;
import ai.sparklabinc.d1.defaults.entity.DefaultConfigurationType;
import ai.sparklabinc.d1.defaults.entity.DefaultsConfigurationDO;
import ai.sparklabinc.d1.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
public class DefaultsConfigurationTaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultsConfigurationTaskManager.class);

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
    private Map<String, ScheduledFuture<?>> runningScheduleMap = new ConcurrentHashMap<>();

    public void run() {
        LOGGER.info("start default value task schedule");
        this.taskScheduler.scheduleWithFixedDelay(
                () -> task(),
                new Date(), 10 * 1000L);
    }

    private void task() {
        LOGGER.info("start default value task");
        try (
                Connection connection = this.d1BasicDataSource.getConnection();
        ) {
            // 开启事务
            connection.setAutoCommit(false);
            List<DefaultsConfigurationDO> all = this.defaultsConfigurationRepository.queryAllWithLockTransaction(connection);
            if (all == null || all.isEmpty()) {
                return;
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
                                if (Boolean.TRUE.equals(enable)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toMap((tmp) -> generateRunningScheduleMapKey(tmp), (tmp) -> tmp));

            if (allSchedule.isEmpty()) {
                LOGGER.info("no new default value task schedule");
            }
            // 当前不应该再运行的schedule
            List<Map.Entry<String, ScheduledFuture<?>>> needDeleteConfigurationEntryList = new ArrayList<>();
            for (Map.Entry<String, ScheduledFuture<?>> existSchedule : runningScheduleMap.entrySet()) {
                String key = existSchedule.getKey();
                ScheduledFuture<?> value = existSchedule.getValue();
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
                        runningScheduleMap.remove(key);
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
                        runningScheduleMap.put(key, scheduledFuture);
                    }
                }
            }
            Thread.sleep(300000L);
            connection.commit();

        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private ScheduledFuture<?> schedule(DefaultsConfigurationDO defaultsConfigurationDO) {
        DefaultConfigurationType fieldType = defaultsConfigurationDO.getFieldType();
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
        String fieldFormDfKey = defaultsConfigurationDO.getFieldFormDfKey();
        String fieldFormFieldKey = defaultsConfigurationDO.getFieldFormFieldKey();
        String fieldPluginConf = defaultsConfigurationDO.getFieldPluginConf();
        JSONObject pluginConf = JSON.parseObject(fieldPluginConf);
        String cron = pluginConf.getString("cron");
        return fieldFormDfKey + "_" + fieldFormFieldKey + "_" + cron;
    }

}
