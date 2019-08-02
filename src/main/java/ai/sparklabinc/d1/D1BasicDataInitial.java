package ai.sparklabinc.d1;

import ai.sparklabinc.d1.config.BasicDbConfig;
import ai.sparklabinc.d1.init.D1BasicDataService;
import ai.sparklabinc.d1.init.D1BasicTableService;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @function: 在项目启动完成后执行
 * @author:   dengam
 * @date:    2019/8/1 15:25
 * @param:
 * @return:
 */
@Component
public class D1BasicDataInitial implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private BasicDbConfig basicDbConfig;

    @Autowired
    private D1BasicDataService d1BasicDataService;

    @Autowired
    private D1BasicTableService d1BasicTableService;

    private final static Logger LOGGER = LoggerFactory.getLogger(D1BasicDataService.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent){
        if(contextRefreshedEvent.getApplicationContext().getParent() == null){
            //需要执行的代码
            try {
               this.createD1BasicDataSoure(basicDbConfig);
            } catch (Exception e) {
                LOGGER.error("D1 Basic Data Initial is falied!");
                LOGGER.error("",e);
            }

        }
    }

     @Bean("D1BasicDataSoure")
     public DataSource createD1BasicDataSoure(BasicDbConfig basicDbConfig) throws Exception {
         DataSource dataSource = d1BasicDataService.createD1BasicDataSoure(basicDbConfig);
         //创建初始表
         d1BasicTableService.createBasicTable(dataSource,basicDbConfig);
         return dataSource;
     }


}
