package ai.sparklabinc.d1.service;

import ai.sparklabinc.d1.dto.OptionListAndDefaultValDTO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author : Kingzer
 * @date : 2019-07-03 15:17
 * @description :
 */
public interface DsBasicDictionaryService {
    OptionListAndDefaultValDTO getOptionListAndDefaultValDTOByDomainName(String domainName) throws SQLException, IOException;
}
