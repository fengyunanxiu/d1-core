package ai.sparklabinc.dto;

import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 15:03
 * @description :
 */
public class OptionListAndDefaultValDTO {


    private List<OptionDTO> optionDTOList;

    private String defaultVal;

    public List<OptionDTO> getOptionDTOList() {
        return optionDTOList;
    }

    public void setOptionDTOList(List<OptionDTO> optionDTOList) {
        this.optionDTOList = optionDTOList;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(String defaultVal) {
        this.defaultVal = defaultVal;
    }
}
