package ai.sparklabinc.d1.config;

import ai.sparklabinc.d1.dao.*;
import ai.sparklabinc.d1.defaults.dao.DefaultsConfigurationRepository;
import ai.sparklabinc.d1.dict.dao.DictPluginConfigurationRepository;
import ai.sparklabinc.d1.dict.dao.DictRepository;
import ai.sparklabinc.d1.dict.dao.FormDictConfigurationRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/31 19:27
 * @description:
 * @version: V1.0
 */
@Configuration
public class DaoBeanConfig {

    @Autowired
    private BasicDbConfig basicDbConfig;

    @Autowired
    private DataDaoFactory dataDaoFactory;

    @Bean("DfFormTableSettingDao")
    public DfFormTableSettingDao getDfFormTableSettingDao(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DfFormTableSettingDao.class, basicDbConfig.getType());
    }

    @Bean("DataSourceDao")
    public DataSourceDao getDataSourceDao(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DataSourceDao.class, basicDbConfig.getType());
    }


    @Bean("DbBasicConfigDao")
    public DbBasicConfigDao getDbBasicConfigDao(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DbBasicConfigDao.class, basicDbConfig.getType());
    }


    @Bean("DbSecurityConfigDao")

    public DbSecurityConfigDao getDbSecurityConfigDao(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DbSecurityConfigDao.class, basicDbConfig.getType());
    }


    @Bean("DsBasicDictionaryDao")
    public DsBasicDictionaryDao getDsBasicDictionaryDao(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DsBasicDictionaryDao.class, basicDbConfig.getType());
    }


    @Bean("DfKeyBasicConfigDao")
    public DfKeyBasicConfigDao getDfKeyBasicConfigDao(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DfKeyBasicConfigDao.class, basicDbConfig.getType());
    }


    @Bean("DsQueryDao")
    public DsQueryDao getDsQueryDao(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DsQueryDao.class, basicDbConfig.getType());
    }

    @Bean("DataExportTaskDao")
    public DataExportTaskDao getDataExportTaskDao(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DataExportTaskDao.class, basicDbConfig.getType());
    }


    @Bean("DictRepository")
    public DictRepository getDataDictRepository(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DictRepository.class, basicDbConfig.getType());
    }


    @Bean("FormDictConfigurationRepository")
    public FormDictConfigurationRepository formDictConfigurationRepository(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(FormDictConfigurationRepository.class, basicDbConfig.getType());
    }

    @Bean("DefaultsConfigurationRepository")
    public DefaultsConfigurationRepository defaultsConfigurationRepository(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DefaultsConfigurationRepository.class, basicDbConfig.getType());
    }

    @Bean("DictPluginConfigurationRepository")
    public DictPluginConfigurationRepository dictPluginConfigurationRepository(){
        if(StringUtils.isBlank(basicDbConfig.getType())){
            basicDbConfig.setType("sqlite");
        }
        return dataDaoFactory.getDaoBean(DictPluginConfigurationRepository.class, basicDbConfig.getType());
    }

}
