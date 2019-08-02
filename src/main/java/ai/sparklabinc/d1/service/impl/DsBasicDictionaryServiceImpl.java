package ai.sparklabinc.d1.service.impl;

import ai.sparklabinc.d1.dao.DsBasicDictionaryDao;
import ai.sparklabinc.d1.dto.OptionDTO;
import ai.sparklabinc.d1.dto.OptionListAndDefaultValDTO;
import ai.sparklabinc.d1.entity.DsBasicDictionaryDO;
import ai.sparklabinc.d1.service.DsBasicDictionaryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Kingzer
 * @date : 2019-07-03 15:20
 * @description :
 */
@Service
public class DsBasicDictionaryServiceImpl implements DsBasicDictionaryService {

    @Resource(name = "DsBasicDictionaryDao")
    private DsBasicDictionaryDao dsBasicDictionaryDao;


    @Override
    public OptionListAndDefaultValDTO getOptionListAndDefaultValDTOByDomainName(String domainName) throws SQLException, IOException {
        List<DsBasicDictionaryDO> dsBasicDictionaryDOList = this.dsBasicDictionaryDao.findListByDomainName(domainName);
        if(dsBasicDictionaryDOList != null && !dsBasicDictionaryDOList.isEmpty()){
            OptionListAndDefaultValDTO optionListAndDefaultValDTO = new OptionListAndDefaultValDTO();
            optionListAndDefaultValDTO.setDefaultVal(dsBasicDictionaryDOList.get(0).getItemId());
            optionListAndDefaultValDTO.setOptionDTOList(dsBasicDictionaryDOList.stream()
                    .map(e -> new OptionDTO(e.getItemId(), e.getItemVal())).collect(Collectors.toList()));

            return optionListAndDefaultValDTO;
        }
        return null;


    }
}
