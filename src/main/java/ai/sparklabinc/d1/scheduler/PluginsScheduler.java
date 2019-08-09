package ai.sparklabinc.d1.scheduler;

import ai.sparklabinc.d1.defaults.task.DefaultsConfigurationTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/9 9:39
 * @description :
 */
@Component
public class PluginsScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginsScheduler.class);

    @Autowired
    private DefaultsConfigurationTaskManager defaultsConfigurationTaskManager;

    public void init() {
        this.defaultsConfigurationTaskManager.run();
    }

}
