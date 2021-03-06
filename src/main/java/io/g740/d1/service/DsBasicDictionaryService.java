package io.g740.d1.service;

import io.g740.d1.dto.OptionListAndDefaultValDTO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author : Kingzer
 * @date : 2019-07-03 15:17
 * @description :
 */
public interface DsBasicDictionaryService {
    OptionListAndDefaultValDTO getOptionListAndDefaultValDTOByDomainName(String domainName, String item) throws SQLException, IOException;
}
