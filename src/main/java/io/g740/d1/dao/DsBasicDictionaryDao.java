package io.g740.d1.dao;

import io.g740.d1.entity.DsBasicDictionaryDO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 15:18
 * @description :
 */
public interface DsBasicDictionaryDao {
    DataDaoType getDataDaoType();

    List<DsBasicDictionaryDO> findListByDomainName(String domainName) throws SQLException, IOException;
}
