package io.g740.d1.dto;

import java.util.List;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/4 15:57
 * @description:
 * @version: V1.0
 */
public class DbInformationDTO {
    private Long id;
    private String label;
    private Long level;
    private String type;
    private List<DbInformationDTO> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DbInformationDTO> getChildren() {
        return children;
    }

    public void setChildren(List<DbInformationDTO> children) {
        this.children = children;
    }
}
