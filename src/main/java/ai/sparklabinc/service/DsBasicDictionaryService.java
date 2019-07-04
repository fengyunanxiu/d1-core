package ai.sparklabinc.service;

import ai.sparklabinc.dto.OptionListAndDefaultValDTO;

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
