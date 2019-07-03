package ai.sparklabinc.entity;

import java.util.Date;

public class DsFormTableSettingDO {

    private Long id;

    private String gmtCreate;

    private String gmtModified;

    private String dsKey;


    private String dbFieldName;


    private String dbFieldType;


    private String viewFieldLable;


    private String dbFieldComment;

    private Boolean formFieldVisible;


    private Integer formFieldSequence;

    private String formFieldQueryType;

    private Boolean formFieldIsExactly;


    private String formFieldChildrenDbFieldName;


    private String formFieldDicDomainName;

    private Boolean formFieldAutoCollectDic;

    private String formFieldDefaultValStratege;


    private Boolean tableFieldVisible;

    private String tableFieldOrderBy;

    private String tableParentLable;

    private Boolean tableFieldQueryRequired;

    private Integer tableFieldSequence;

    private Integer tableFieldColumnWidth;

    private Boolean exportFieldVisible;

    private Integer exportFieldSequence;

    private Integer exportFieldWidth;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getDsKey() {
        return dsKey;
    }

    public void setDsKey(String dsKey) {
        this.dsKey = dsKey;
    }

    public String getDbFieldName() {
        return dbFieldName;
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }

    public String getDbFieldType() {
        return dbFieldType;
    }

    public void setDbFieldType(String dbFieldType) {
        this.dbFieldType = dbFieldType;
    }

    public String getViewFieldLable() {
        return viewFieldLable;
    }

    public void setViewFieldLable(String viewFieldLable) {
        this.viewFieldLable = viewFieldLable;
    }

    public String getDbFieldComment() {
        return dbFieldComment;
    }

    public void setDbFieldComment(String dbFieldComment) {
        this.dbFieldComment = dbFieldComment;
    }

    public Boolean getFormFieldVisible() {
        return formFieldVisible;
    }

    public void setFormFieldVisible(Boolean formFieldVisible) {
        this.formFieldVisible = formFieldVisible;
    }

    public Integer getFormFieldSequence() {
        return formFieldSequence;
    }

    public void setFormFieldSequence(Integer formFieldSequence) {
        this.formFieldSequence = formFieldSequence;
    }

    public String getFormFieldQueryType() {
        return formFieldQueryType;
    }

    public void setFormFieldQueryType(String formFieldQueryType) {
        this.formFieldQueryType = formFieldQueryType;
    }

    public Boolean getFormFieldIsExactly() {
        return formFieldIsExactly;
    }

    public void setFormFieldIsExactly(Boolean formFieldIsExactly) {
        this.formFieldIsExactly = formFieldIsExactly;
    }

    public String getFormFieldChildrenDbFieldName() {
        return formFieldChildrenDbFieldName;
    }

    public void setFormFieldChildrenDbFieldName(String formFieldChildrenDbFieldName) {
        this.formFieldChildrenDbFieldName = formFieldChildrenDbFieldName;
    }

    public String getFormFieldDicDomainName() {
        return formFieldDicDomainName;
    }

    public void setFormFieldDicDomainName(String formFieldDicDomainName) {
        this.formFieldDicDomainName = formFieldDicDomainName;
    }

    public Boolean getFormFieldAutoCollectDic() {
        return formFieldAutoCollectDic;
    }

    public void setFormFieldAutoCollectDic(Boolean formFieldAutoCollectDic) {
        this.formFieldAutoCollectDic = formFieldAutoCollectDic;
    }

    public String getFormFieldDefaultValStratege() {
        return formFieldDefaultValStratege;
    }

    public void setFormFieldDefaultValStratege(String formFieldDefaultValStratege) {
        this.formFieldDefaultValStratege = formFieldDefaultValStratege;
    }

    public Boolean getTableFieldVisible() {
        return tableFieldVisible;
    }

    public void setTableFieldVisible(Boolean tableFieldVisible) {
        this.tableFieldVisible = tableFieldVisible;
    }

    public String getTableFieldOrderBy() {
        return tableFieldOrderBy;
    }

    public void setTableFieldOrderBy(String tableFieldOrderBy) {
        this.tableFieldOrderBy = tableFieldOrderBy;
    }

    public Boolean getTableFieldQueryRequired() {
        return tableFieldQueryRequired;
    }

    public void setTableFieldQueryRequired(Boolean tableFieldQueryRequired) {
        this.tableFieldQueryRequired = tableFieldQueryRequired;
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

    public Boolean getExportFieldVisible() {
        return exportFieldVisible;
    }

    public void setExportFieldVisible(Boolean exportFieldVisible) {
        this.exportFieldVisible = exportFieldVisible;
    }

    public Integer getExportFieldSequence() {
        return exportFieldSequence;
    }

    public void setExportFieldSequence(Integer exportFieldSequence) {
        this.exportFieldSequence = exportFieldSequence;
    }

    public Integer getExportFieldWidth() {
        return exportFieldWidth;
    }

    public void setExportFieldWidth(Integer exportFieldWidth) {
        this.exportFieldWidth = exportFieldWidth;
    }


    public String getTableParentLable() {
        return tableParentLable;
    }

    public void setTableParentLable(String tableParentLable) {
        this.tableParentLable = tableParentLable;
    }
}
