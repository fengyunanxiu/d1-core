package ai.sparklabinc.dto;

import java.util.List;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/4 15:57
 * @description:
 * @version: V1.0
 */
public class DbInforamtionDTO {
    private Long id;
    private String label;
    private Integer level;
    private String type;
    private List<DbInforamtionDTO> children;

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DbInforamtionDTO> getChildren() {
        return children;
    }

    public void setChildren(List<DbInforamtionDTO> children) {
        this.children = children;
    }
}
