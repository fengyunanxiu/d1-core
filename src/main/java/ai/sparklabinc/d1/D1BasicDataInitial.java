package ai.sparklabinc.d1;

import ai.sparklabinc.d1.config.BasicDbConfig;
import ai.sparklabinc.d1.defaults.service.DefaultsConfigurationService;
import ai.sparklabinc.d1.init.D1BasicDataService;
import ai.sparklabinc.d1.init.D1BasicTableService;
import ai.sparklabinc.d1.scheduler.PluginsScheduler;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @function: 在项目启动完成后执行
 * @author: dengam
 * @date: 2019/8/1 15:25
 * @param:
 * @return:
 */
@Component
public class D1BasicDataInitial implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private PluginsScheduler pluginsScheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.pluginsScheduler.init();
    }

}
