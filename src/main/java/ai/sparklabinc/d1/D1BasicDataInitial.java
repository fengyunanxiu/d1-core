package ai.sparklabinc.d1;

import ai.sparklabinc.d1.config.BasicDbConfig;
import ai.sparklabinc.d1.init.D1BasicDataService;
import ai.sparklabinc.d1.init.D1BasicTableService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextStartedEvent;

import java.io.File;

/**
 * @function: 在项目启动完成后执行
 * @author:   dengam
 * @date:    2019/8/1 15:25
 * @param:
 * @return:
 */
public class D1BasicDataInitial implements ApplicationListener<ContextStartedEvent>{

    @Autowired
    private BasicDbConfig basicDbConfig;

    @Autowired
    private D1BasicDataService d1BasicDataService;

    @Autowired
    private D1BasicTableService d1BasicTableService;

    private final static Logger LOGGER = LoggerFactory.getLogger(D1BasicDataService.class);

    @Override
    public void onApplicationEvent(ContextStartedEvent contextRefreshedEvent){
        if(contextRefreshedEvent.getApplicationContext().getParent() == null){
            //建表初始化语句
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
        if(StringUtils.isBlank(basicDbConfig.getUrl())||StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setUseSsl(false);
            basicDbConfig.setUseSshTunnel(false);
            basicDbConfig.setType("sqlite");
            String projectPath = System.getProperty("user.dir");
            String dbPath=projectPath+File.separator+"d1-data";
            File file = new File(dbPath);
            if(!file.exists()){//不存在则创建文件夹
                file.mkdir();
            }
            basicDbConfig.setUrl("jdbc:sqlite:d1-data/D1.db");
        }
         DataSource dataSource = d1BasicDataService.createD1BasicDataSoure(basicDbConfig);
         //创建初始表
         d1BasicTableService.createBasicTable(dataSource,basicDbConfig);

         return dataSource;
     }


}
