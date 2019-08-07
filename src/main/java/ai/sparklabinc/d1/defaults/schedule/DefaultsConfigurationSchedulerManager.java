package ai.sparklabinc.d1.defaults.schedule;

import ai.sparklabinc.d1.defaults.dao.DefaultsConfigurationRepository;
import ai.sparklabinc.d1.defaults.entity.DefaultsConfigurationDO;
import ai.sparklabinc.d1.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private Map<String, DefaultsConfigurationDO> runningScheduleMap = new ConcurrentHashMap<>();

    public void schedule() throws SQLException {
        List<DefaultsConfigurationDO> all = this.defaultsConfigurationRepository.queryAll();
        if (all == null || all.isEmpty()) {
            return;
        }



        for (DefaultsConfigurationDO defaultsConfigurationDO : all) {

        }
    }

    public void schedule(DefaultsConfigurationDO defaultsConfigurationDO) {
        String fieldPluginConf = defaultsConfigurationDO.getFieldPluginConf();
        if (StringUtils.isNullOrEmpty(fieldPluginConf)) {
            return;
        }

    }


}
