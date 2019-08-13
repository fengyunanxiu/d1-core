package ai.sparklabinc.d1.service.impl;

import ai.sparklabinc.d1.dao.DsBasicDictionaryDao;
import ai.sparklabinc.d1.dict.dao.DictRepository;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.dto.OptionDTO;
import ai.sparklabinc.d1.dto.OptionListAndDefaultValDTO;
import ai.sparklabinc.d1.entity.DsBasicDictionaryDO;
import ai.sparklabinc.d1.service.DsBasicDictionaryService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    @Resource(name = "DictRepository")
    private DictRepository dictRepository;


    @Override
    public OptionListAndDefaultValDTO getOptionListAndDefaultValDTOByDomainName(String domainName, String item) throws SQLException, IOException {
        List<DictDO> dictDOS = this.dictRepository.findByDomainAndItem(domainName, item);
        if(!CollectionUtils.isEmpty(dictDOS)){
            OptionListAndDefaultValDTO optionListAndDefaultValDTO = new OptionListAndDefaultValDTO();
            optionListAndDefaultValDTO.setDefaultVal(dictDOS.get(0).getFieldValue());
            optionListAndDefaultValDTO.setOptionDTOList(dictDOS.stream()
                    .map(e -> new OptionDTO(e.getFieldValue(), e.getFieldLabel())).collect(Collectors.toList()));

            return optionListAndDefaultValDTO;
        }
        return null;


    }
}
