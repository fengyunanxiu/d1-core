package ai.sparklabinc.d1.init;

import ai.sparklabinc.d1.config.BasicDbConfig;
import ai.sparklabinc.d1.datasource.Constants;
import ai.sparklabinc.d1.exception.ServiceException;
import ai.sparklabinc.d1.util.FileReaderUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * @function:
 * @author: DAM
 * @date: 2019/8/1 16:51
 * @description:
 * @version: V1.0
 */
@Component
public class D1BasicTableService {
    private final static Logger LOGGER = LoggerFactory.getLogger(D1BasicTableService.class);

    public void createBasicTable(DataSource dataSource, BasicDbConfig basicDbConfig) throws ServiceException, IOException, SQLException {
        if (dataSource == null) {
            throw new ServiceException("dataSource cant not be null");
        }
        if (basicDbConfig == null || StringUtils.isBlank(basicDbConfig.getType())) {
            throw new ServiceException("db type cant not be null");
        }
        String sqlClassPath =  File.separator + "sql" + File.separator + basicDbConfig.getType().toUpperCase() + ".sql";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(sqlClassPath);
        String sql = String.join(System.getProperty("line.separator"), IOUtils.readLines(is, StandardCharsets.UTF_8.name()));
        if (StringUtils.isBlank(sql)) {
            throw new ServiceException("create table sql string can not be null");
        }

        Connection connection = null;
        PreparedStatement preparedStatement=null;
        try {
            connection=dataSource.getConnection();
            switch (basicDbConfig.getType().toUpperCase()) {
                case Constants.DATABASE_TYPE_MYSQL:
                case Constants.DATABASE_TYPE_POSTGRESQL:
                case Constants.DATABASE_TYPE_SQLSERVER:
                case Constants.DATABASE_TYPE_ORACLE:
                    //oralce分批执行plsql语句
                    String[] sqlBatchs = sql.split("###");
                    for (String s : sqlBatchs) {
                        LOGGER.info("creating table>>>>{}", s);
                        preparedStatement = connection.prepareStatement(s);
                        preparedStatement.executeUpdate();
                    }
                    break;
                default:
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.executeUpdate();
            }


        } catch (Exception e) {
            LOGGER.error("", e);
        }finally {
            if(connection!=null){
                connection.close();
            }

            if(preparedStatement!=null){
                preparedStatement.close();
            }
        }
    }


}
