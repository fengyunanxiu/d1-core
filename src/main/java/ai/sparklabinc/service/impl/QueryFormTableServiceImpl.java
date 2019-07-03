package ai.sparklabinc.service.impl;

import ai.sparklabinc.dao.DsFormTableSettingDao;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.service.QueryFormTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-02 21:49
 * @description :
 */
@Service
public class QueryFormTableServiceImpl implements QueryFormTableService {

    @Autowired
    private DsFormTableSettingDao dsFormTableSettingDao;

    @Override
    public Object getDsKeyQueryTableSetting(String dataSourceKey) {
        return null;
    }




    private List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKey(String dataSourceKey){
        return this.dsFormTableSettingDao.getAllDsFormTableSettingByDsKey(dataSourceKey);
    }
}
