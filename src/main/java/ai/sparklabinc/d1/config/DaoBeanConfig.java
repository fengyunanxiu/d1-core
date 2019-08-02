package ai.sparklabinc.d1.config;

import ai.sparklabinc.d1.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/31 19:27
 * @description:
 * @version: V1.0
 */
@Component
public class DaoBeanConfig {
    @Autowired
    private BasicDbConfig basicDbConfig;

    @Autowired
    private DataDaoFactory dataDaoFactory;


    @Bean("DsFormTableSettingDao")
    public DsFormTableSettingDao getDsFormTableSettingDao(){
        return dataDaoFactory.getDaoBean(DsFormTableSettingDao.class, basicDbConfig.getType());
    }

    @Bean("DataSourceDao")
    public DataSourceDao getDataSourceDao(){
        return dataDaoFactory.getDaoBean(DataSourceDao.class, basicDbConfig.getType());
    }


    @Bean("DbBasicConfigDao")
    public DbBasicConfigDao getDbBasicConfigDao(){
        return dataDaoFactory.getDaoBean(DbBasicConfigDao.class, basicDbConfig.getType());
    }


    @Bean("DbSecurityConfigDao")
    public DbSecurityConfigDao getDbSecurityConfigDao(){
        return dataDaoFactory.getDaoBean(DbSecurityConfigDao.class, basicDbConfig.getType());
    }


    @Bean("DsBasicDictionaryDao")
    public DsBasicDictionaryDao getDsBasicDictionaryDao(){
        return dataDaoFactory.getDaoBean(DsBasicDictionaryDao.class, basicDbConfig.getType());
    }


    @Bean("DsKeyBasicConfigDao")
    public DsKeyBasicConfigDao getDsKeyBasicConfigDao(){
        return dataDaoFactory.getDaoBean(DsKeyBasicConfigDao.class, basicDbConfig.getType());
    }


    @Bean("DsQueryDao")
    public DsQueryDao getDsQueryDao(){
        return dataDaoFactory.getDaoBean(DsQueryDao.class, basicDbConfig.getType());
    }

    @Bean("DataExportTaskDao")
    public DataExportTaskDao getDataExportTaskDao(){
        return dataDaoFactory.getDaoBean(DataExportTaskDao.class, basicDbConfig.getType());
    }



}