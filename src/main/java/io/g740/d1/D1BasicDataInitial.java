package io.g740.d1;

import io.g740.d1.scheduler.PluginsScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

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
//       this.pluginsScheduler.init();
    }

}
