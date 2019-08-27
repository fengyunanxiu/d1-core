package io.g740.d1.defaults.dto;

import javax.validation.constraints.NotNull;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/26 16:47
 * @description :
 */
public class DefaultsConfigurationDTO {

    private String id;

    @NotNull
    private String formDfKey;

    @NotNull
    private String formFieldKey;

    @NotNull
    private String fieldType;

    private String pluginCron;

    private String pluginType;

    private String pluginJdbcUrl;

    private String pluginUsername;

    private String pluginPassword;

    private String pluginSQL;

    private String pluginEnable;

    private String manualConf;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormDfKey() {
        return formDfKey;
    }

    public void setFormDfKey(String formDfKey) {
        this.formDfKey = formDfKey;
    }

    public String getFormFieldKey() {
        return formFieldKey;
    }

    public void setFormFieldKey(String formFieldKey) {
        this.formFieldKey = formFieldKey;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getPluginCron() {
        return pluginCron;
    }

    public void setPluginCron(String pluginCron) {
        this.pluginCron = pluginCron;
    }

    public String getPluginType() {
        return pluginType;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    public String getPluginJdbcUrl() {
        return pluginJdbcUrl;
    }

    public void setPluginJdbcUrl(String pluginJdbcUrl) {
        this.pluginJdbcUrl = pluginJdbcUrl;
    }

    public String getPluginUsername() {
        return pluginUsername;
    }

    public void setPluginUsername(String pluginUsername) {
        this.pluginUsername = pluginUsername;
    }

    public String getPluginPassword() {
        return pluginPassword;
    }

    public void setPluginPassword(String pluginPassword) {
        this.pluginPassword = pluginPassword;
    }

    public String getPluginSQL() {
        return pluginSQL;
    }

    public void setPluginSQL(String pluginSQL) {
        this.pluginSQL = pluginSQL;
    }

    public String getPluginEnable() {
        return pluginEnable;
    }

    public void setPluginEnable(String pluginEnable) {
        this.pluginEnable = pluginEnable;
    }

    public String getManualConf() {
        return manualConf;
    }

    public void setManualConf(String manualConf) {
        this.manualConf = manualConf;
    }
}
