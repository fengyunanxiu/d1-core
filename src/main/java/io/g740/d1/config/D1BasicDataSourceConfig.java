package io.g740.d1.config;

import io.g740.d1.init.D1BasicDataService;
import io.g740.d1.init.D1BasicTableService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/9 14:41
 * @description :
 */
@Configuration
public class D1BasicDataSourceConfig {

    @Autowired
    private BasicDbConfig basicDbConfig;

    @Autowired
    private D1BasicDataService d1BasicDataService;

    @Autowired
    private D1BasicTableService d1BasicTableService;

    private static final Logger LOGGER = LoggerFactory.getLogger(D1BasicDataSourceConfig.class);

    @Bean("D1BasicDataSource")
    public DataSource createD1BasicDataSource(BasicDbConfig basicDbConfig) throws Exception {
        if (StringUtils.isBlank(basicDbConfig.getUrl()) || StringUtils.isBlank(basicDbConfig.getType())) {
            basicDbConfig.setUseSsl(false);
            basicDbConfig.setUseSshTunnel(false);
            basicDbConfig.setType("sqlite");
            String projectPath = System.getProperty("user.dir");
            String dbPath = projectPath + File.separator + "d1-data";
            File file = new File(dbPath);
            if (!file.exists()) {//不存在则创建文件夹
                file.mkdir();
            }
            basicDbConfig.setUrl("jdbc:sqlite:d1-data/D1.db");
        }
        DataSource dataSource = d1BasicDataService.createD1BasicDataSource(basicDbConfig);
        //创建初始表
        d1BasicTableService.createBasicTable(dataSource, basicDbConfig);
        return dataSource;
    }

}
