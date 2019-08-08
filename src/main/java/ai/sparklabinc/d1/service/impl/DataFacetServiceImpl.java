package ai.sparklabinc.d1.service.impl;

import ai.sparklabinc.d1.dao.DfFormTableSettingDao;
import ai.sparklabinc.d1.service.DataFacetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/8 10:02
 * @description :
 */
@Service
public class DataFacetServiceImpl implements DataFacetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFacetServiceImpl.class);

    @Resource(name = "DfFormTableSettingDao")
    private DfFormTableSettingDao dfFormTableSettingDao;

    public void updateDefaultValueByDfKeyAndFieldKey(String dfKey, String fieldKey, String jsonValue) {
        
    }



}
