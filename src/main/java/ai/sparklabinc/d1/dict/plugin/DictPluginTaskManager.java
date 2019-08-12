package ai.sparklabinc.d1.dict.plugin;

import ai.sparklabinc.d1.defaults.entity.DefaultConfigurationType;
import ai.sparklabinc.d1.defaults.entity.DefaultsConfigurationDO;
import ai.sparklabinc.d1.dict.dao.DictPluginConfigurationRepository;
import ai.sparklabinc.d1.dict.entity.DictPluginConfigurationDO;
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
 * @date : 2019/8/9 16:34
 * @description :
 */
@Component
public class DictPluginTaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictPluginTaskManager.class);

    @Resource(name = "DictPluginConfigurationRepository")
    private DictPluginConfigurationRepository dictPluginConfigurationRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private DictSQLPlugin dictSQLPlugin;

    @Resource(name = "D1BasicDataSource")
    private DataSource d1BasicDataSource;

    private Map<String, ScheduledFuture<?>> runningScheduleMap = new ConcurrentHashMap<>();

    public void run() {
        LOGGER.info(" start dict task schedule");
        this.taskScheduler.scheduleWithFixedDelay(() -> task(),
                new Date(), 10 * 1000L);
    }

    private void task() {
        LOGGER.info(" start to process dict task ");
        Connection connection = null;
        try{
            connection = this.d1BasicDataSource.getConnection();
            connection.setAutoCommit(false);
            List<DictPluginConfigurationDO> allEnableList = this.dictPluginConfigurationRepository.findAllEnableWithLockTransaction(connection);
            if (allEnableList == null || allEnableList.isEmpty()) {
                return;
            }
            Map<String, DictPluginConfigurationDO> allEnableMap = allEnableList.stream().collect(Collectors.toMap((tmp) -> generateRunningScheduleMapKey(tmp), (tmp) -> tmp));

            List<Map.Entry<String, ScheduledFuture<?>>> needDeleteScheduleEntryList = new ArrayList<>();
            for (Map.Entry<String, ScheduledFuture<?>> existSchedule : runningScheduleMap.entrySet()) {
                String key = existSchedule.getKey();
                if (allEnableMap.containsKey(key)) {
                    allEnableMap.remove(key);
                } else {
                    needDeleteScheduleEntryList.add(existSchedule);
                }
            }
            // 删除不在执行的schedule
            if (!needDeleteScheduleEntryList.isEmpty()) {
                LOGGER.info("begin to cancel unused dict sql plugin, size:{}", needDeleteScheduleEntryList.size());
                for (Map.Entry<String, ScheduledFuture<?>> needDeleteSchedule : needDeleteScheduleEntryList) {
                    String key = needDeleteSchedule.getKey();
                    ScheduledFuture<?> scheduledFuture = needDeleteSchedule.getValue();
                    boolean cancel = scheduledFuture.cancel(false);
                    if (cancel) {
                        runningScheduleMap.remove(key);
                    }
                }
                LOGGER.info("end to cancel unused dict sql plugin, size:{}", needDeleteScheduleEntryList.size());
            }

            // 执行新的schedule
            if (!allEnableMap.isEmpty()) {
                LOGGER.info("begin to start new dict sql plugin, size: {}", allEnableMap.size());
                for (DictPluginConfigurationDO dictPluginConfigurationDO : allEnableMap.values()) {
                    ScheduledFuture<?> scheduledFuture = this.schedule(dictPluginConfigurationDO);
                    if (scheduledFuture != null) {
                        String key = generateRunningScheduleMapKey(dictPluginConfigurationDO);
                        runningScheduleMap.put(key, scheduledFuture);
                    }
                }
                LOGGER.info("end to start new dict sql plugin, size: {}", allEnableMap.size());
            }
            connection.commit();
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    private ScheduledFuture<?> schedule(DictPluginConfigurationDO dictPluginConfigurationDO) {
        String fieldType = dictPluginConfigurationDO.getFieldType();
        switch (fieldType) {
            case "SQL":
                return this.dictSQLPlugin.run(dictPluginConfigurationDO);
            case "CUSTOMER":
                break;
            default:
                break;
        }
        return null;
    }

    private String generateRunningScheduleMapKey(DictPluginConfigurationDO dictPluginConfigurationDO) {
        return String.valueOf(dictPluginConfigurationDO.hashCode());
    }


}
