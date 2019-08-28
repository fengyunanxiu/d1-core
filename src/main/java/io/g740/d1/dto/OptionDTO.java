package io.g740.d1.dto;

/**
 * @author : Kingzer
 * @date : 2019-07-03 15:03
 * @description :
 */
public class OptionDTO {

    private String optionValue;
    private String optionLabel;

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public OptionDTO() {
    }

    public OptionDTO(String optionValue, String optionLabel) {
        this.optionValue = optionValue;
        this.optionLabel = optionLabel;
    }
}
