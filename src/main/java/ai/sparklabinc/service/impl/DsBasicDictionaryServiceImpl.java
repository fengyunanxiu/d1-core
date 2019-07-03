package ai.sparklabinc.service.impl;

import ai.sparklabinc.dao.DsBasicDictionaryDao;
import ai.sparklabinc.dto.OptionDTO;
import ai.sparklabinc.dto.OptionListAndDefaultValDTO;
import ai.sparklabinc.entity.DsBasicDictionaryDO;
import ai.sparklabinc.service.DsBasicDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Kingzer
 * @date : 2019-07-03 15:20
 * @description :
 */
@Service
public class DsBasicDictionaryServiceImpl implements DsBasicDictionaryService {
    @Autowired
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
