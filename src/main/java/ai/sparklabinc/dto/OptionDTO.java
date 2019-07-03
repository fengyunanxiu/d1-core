package ai.sparklabinc.dto;

/**
 * @author : Kingzer
 * @date : 2019-07-03 15:03
 * @description :
 */
public class OptionDTO {

    private String optionId;
    private String optionValue;

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public OptionDTO() {
    }

    public OptionDTO(String optionId, String optionValue) {
        this.optionId = optionId;
        this.optionValue = optionValue;
    }
}
