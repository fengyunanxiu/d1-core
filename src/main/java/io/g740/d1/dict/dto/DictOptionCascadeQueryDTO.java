package io.g740.d1.dict.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/27 15:23
 * @description :
 */
public class DictOptionCascadeQueryDTO {

    private String optionLabel;

    private String optionValue;

    private List<DictOptionCascadeQueryDTO> children;

    public String getOptionLabel() {
        return optionLabel;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public List<DictOptionCascadeQueryDTO> getChildren() {
        return children;
    }

    public void setChildren(List<DictOptionCascadeQueryDTO> children) {
        this.children = children;
    }
}
