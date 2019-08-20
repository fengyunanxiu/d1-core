package io.g740.d1.dict.dao;

import io.g740.d1.dict.entity.FormDictConfigurationDO;

import java.sql.SQLException;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/7 10:06
 * @description :
 */
public interface FormDictConfigurationRepository {
    List<FormDictConfigurationDO> queryByFrom(String formDfKey, String formFieldKey) throws SQLException;

    FormDictConfigurationDO queryById(String id) throws SQLException;

    FormDictConfigurationDO add(FormDictConfigurationDO formDictConfigurationDO) throws Exception;

    void update(FormDictConfigurationDO formDictConfigurationDO) throws Exception;
}
