package ai.sparklabinc.vo;

import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-02 21:54
 * @description :
 */
public class DsKeyQueryTableSettingVO {


    private String viewFieldLabel;

    private String dbFieldName;

    private Integer tableFieldSequence;

    private Integer tableFieldColumnWidth;


    private List<DsKeyQueryTableSettingVO> children;

    /**
     * 枚举值，默认为NONE, DESC,ASC
     */
    private String tableFieldOrderBy;

    public String getViewFieldLabel() {
        return viewFieldLabel;
    }

    public void setViewFieldLabel(String viewFieldLabel) {
        this.viewFieldLabel = viewFieldLabel;
    }

    public String getDbFieldName() {
        return dbFieldName;
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }

    public Integer getTableFieldSequence() {
        return tableFieldSequence;
    }

    public void setTableFieldSequence(Integer tableFieldSequence) {
        this.tableFieldSequence = tableFieldSequence;
    }

    public Integer getTableFieldColumnWidth() {
        return tableFieldColumnWidth;
    }

    public void setTableFieldColumnWidth(Integer tableFieldColumnWidth) {
        this.tableFieldColumnWidth = tableFieldColumnWidth;
    }

    public String getTableFieldOrderBy() {
        return tableFieldOrderBy;
    }

    public void setTableFieldOrderBy(String tableFieldOrderBy) {
        this.tableFieldOrderBy = tableFieldOrderBy;
    }

    public List<DsKeyQueryTableSettingVO> getChildren() {
        return children;
    }

    public void setChildren(List<DsKeyQueryTableSettingVO> children) {
        this.children = children;
    }

}
