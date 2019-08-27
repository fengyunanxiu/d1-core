package io.g740.d1.entity;

public class DfFormTableSettingDO {

    public static final String TABLE_NAME = "df_form_table_setting";
    public static final String F_ID = "id";
    public static final String F_GMT_CREATE = "gmt_create";
    public static final String F_GMT_MODIFIED = "gmt_modified";
    public static final String F_DF_KEY = "df_key";
    public static final String F_DB_FIELD_NAME = "db_field_name";
    public static final String F_DB_FIELD_TYPE = "db_field_type";
    public static final String F_VIEW_FIELD_LABEL = "view_field_label";
    public static final String F_DB_FIELD_COMMENT = "db_field_comment";
    public static final String F_FORM_FIELD_VISIBLE = "form_field_visible";
    public static final String F_FORM_FIELD_SEQUENCE = "form_field_sequence";
    public static final String F_FORM_FIELD_QUERY_TYPE = "form_field_query_type";
    public static final String F_FORM_FIELD_CHILD_FIELD_NAME = "form_field_child_field_name";
    public static final String F_FORM_FIELD_DICT_DOMAIN_NAME = "form_field_dict_domain_name";
    public static final String F_FORM_FIELD_DICT_ITEM = "form_field_dict_item";
    public static final String F_FORM_FIELD_USE_DICT = "form_field_use_dic";
    public static final String F_FORM_FIELD_DEF_VAL_STRATEGY = "form_field_def_val_strategy";
    public static final String F_TABLE_FIELD_VISIBLE = "table_field_visible";
    public static final String F_TABLE_FIELD_ORDER_BY = "table_field_order_by";
    public static final String F_TABLE_PARENT_LABEL = "table_parent_label";
    public static final String F_TABLE_FIELD_QUERY_REQUIRED = "table_field_query_required";
    public static final String F_TABLE_FIELD_SEQUENCE = "table_field_sequence";
    public static final String F_TABLE_FIELD_COLUMN_WIDTH = "table_field_column_width";
    public static final String F_EXPORT_FIELD_VISIBLE = "export_field_visible";
    public static final String F_EXPORT_FIELD_SEQUENCE = "export_field_sequence";
    public static final String F_EXPORT_FIELD_WIDTH = "export_field_width";
    public static final String F_FORM_FIELD_USE_DEFAULT_VAL = "form_field_use_default_val";
    public static final String F_FORM_FIELD_DEFAULT_VAL = "form_field_default_val";
    public static final String F_FORM_FIELD_DEFAULT_VAL_SQL = "form_field_default_val_sql";

    private Long id;

    private String gmtCreate;

    private String gmtModified;

    private String dfKey;

    private String dbFieldName;

    private String dbFieldType;

    private String viewFieldLabel;

    private String dbFieldComment;

    private Boolean formFieldVisible;

    private Integer formFieldSequence;

    private String formFieldQueryType;


    private String formFieldChildFieldName;

    private String formFieldDictDomainName;

    private String formFieldDictItem;


    private String formFieldDefValStrategy;

    private Boolean tableFieldVisible;

    private String tableFieldOrderBy;

    private String tableParentLabel;

    private Boolean tableFieldQueryRequired;

    private Integer tableFieldSequence;

    private Integer tableFieldColumnWidth;

    private Boolean exportFieldVisible;

    private Integer exportFieldSequence;

    private Integer exportFieldWidth;


    private Boolean formFieldUseDefaultVal;

    private String formFieldDefaultVal;


    private Boolean columnIsExist;

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

    public String getDfKey() {
        return dfKey;
    }

    public void setDfKey(String dfKey) {
        this.dfKey = dfKey;
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

    public String getViewFieldLabel() {
        return viewFieldLabel;
    }

    public void setViewFieldLabel(String viewFieldLabel) {
        this.viewFieldLabel = viewFieldLabel;
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


    public String getFormFieldChildFieldName() {
        return formFieldChildFieldName;
    }

    public void setFormFieldChildFieldName(String formFieldChildFieldName) {
        this.formFieldChildFieldName = formFieldChildFieldName;
    }



    public String getFormFieldDictItem() {
        return formFieldDictItem;
    }

    public void setFormFieldDictItem(String formFieldDictItem) {
        this.formFieldDictItem = formFieldDictItem;
    }


    public String getFormFieldDefValStrategy() {
        return formFieldDefValStrategy;
    }

    public void setFormFieldDefValStrategy(String formFieldDefValStrategy) {
        this.formFieldDefValStrategy = formFieldDefValStrategy;
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

    public String getTableParentLabel() {
        return tableParentLabel;
    }

    public void setTableParentLabel(String tableParentLabel) {
        this.tableParentLabel = tableParentLabel;
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

    public Boolean getFormFieldUseDefaultVal() {
        return formFieldUseDefaultVal;
    }

    public void setFormFieldUseDefaultVal(Boolean formFieldUseDefaultVal) {
        this.formFieldUseDefaultVal = formFieldUseDefaultVal;
    }

    public String getFormFieldDictDomainName() {
        return formFieldDictDomainName;
    }

    public void setFormFieldDictDomainName(String formFieldDictDomainName) {
        this.formFieldDictDomainName = formFieldDictDomainName;
    }

    public String getFormFieldDefaultVal() {
        return formFieldDefaultVal;
    }

    public void setFormFieldDefaultVal(String formFieldDefaultVal) {
        this.formFieldDefaultVal = formFieldDefaultVal;
    }



    public Boolean getColumnIsExist() {
        return columnIsExist;
    }

    public void setColumnIsExist(Boolean columnIsExist) {
        this.columnIsExist = columnIsExist;
    }
}
