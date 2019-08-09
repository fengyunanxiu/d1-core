package ai.sparklabinc.d1.dict.plugin;

import ai.sparklabinc.d1.dict.dao.DictPluginConfigurationRepository;
import ai.sparklabinc.d1.dict.entity.DictPluginConfigurationDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    private Map<String, ScheduledFuture<?>> runningScheduleMap = new ConcurrentHashMap<>();

    public void run() {
        LOGGER.info(" start dict task schedule");
        this.taskScheduler.scheduleWithFixedDelay(() -> task(),
                new Date(), 10 * 1000L);
    }

    private void task() {
        try {
            List<DictPluginConfigurationDO> allEnableList = this.dictPluginConfigurationRepository.findAllEnable();
            if (allEnableList == null || allEnableList.isEmpty()) {
                return;
            }
            Map<String, DictPluginConfigurationDO> allEnableMap = allEnableList.stream().collect(Collectors.toMap((tmp) -> generateRunningScheduleMapKey(tmp), (tmp) -> tmp));

            List<ScheduledFuture<?>> needDeleteScheduleList = new ArrayList<>();
            for (Map.Entry<String, ScheduledFuture<?>> existSchedule : runningScheduleMap.entrySet()) {
                String key = existSchedule.getKey();
                ScheduledFuture<?> value = existSchedule.getValue();
                if (allEnableMap.containsKey(key)) {
                    allEnableMap.remove(key);
                } else {
                    needDeleteScheduleList.add(value);
                }
            }
            // 删除不在执行的schedule
            if (!needDeleteScheduleList.isEmpty()) {
                LOGGER.info("begin to cancel unused dict sql plugin, size:{}", needDeleteScheduleList.size());
                for (ScheduledFuture<?> scheduledFuture : needDeleteScheduleList) {
                    scheduledFuture.cancel(false);
                }
                LOGGER.info("end to cancel unused dict sql plugin, size:{}", needDeleteScheduleList.size());
            }

            // 执行新的schedule
            if (!allEnableMap.isEmpty()) {
                LOGGER.info("begin to start new dict sql plugin, size: {}", allEnableMap.size());
                for (DictPluginConfigurationDO dictPluginConfigurationDO : allEnableMap.values()) {
                    ScheduledFuture<?> scheduledFuture = this.dictSQLPlugin.run(dictPluginConfigurationDO);
                    if (scheduledFuture != null) {
                        String key = generateRunningScheduleMapKey(dictPluginConfigurationDO);
                        runningScheduleMap.put(key, scheduledFuture);
                    }
                }
                LOGGER.info("end to start new dict sql plugin, size: {}", allEnableMap.size());
            }

        } catch (SQLException e) {
            LOGGER.error("", e);
        }
    }


    private String generateRunningScheduleMapKey(DictPluginConfigurationDO dictPluginConfigurationDO) {
        return String.valueOf(dictPluginConfigurationDO.hashCode());
    }


}
