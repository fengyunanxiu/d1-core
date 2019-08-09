package ai.sparklabinc.d1.defaults.dao;

import ai.sparklabinc.d1.defaults.entity.DefaultsConfigurationDO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 15:12
 * @description :
 */
public interface DefaultsConfigurationRepository {

    List<DefaultsConfigurationDO> queryByDfKeyAndFieldKey(String dfKey, String fieldKey) throws SQLException;

    DefaultsConfigurationDO queryById(String id) throws SQLException;

    List<DefaultsConfigurationDO> queryAll() throws SQLException;

    List<DefaultsConfigurationDO> queryAllWithLockTransaction(Connection connection) throws SQLException;

    DefaultsConfigurationDO insert(DefaultsConfigurationDO defaultsConfigurationDO) throws Exception;

    void update(DefaultsConfigurationDO defaultsConfigurationDO) throws Exception;
}
