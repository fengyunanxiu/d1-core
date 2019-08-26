package io.g740.d1.dict.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/26 11:12
 * @description :
 */
public class DictPluginDTO {

    private String id;

    @NotNull
    private String domain;

    @NotNull
    private String item;

    @NotNull
    private Boolean enable;

    @NotNull
    private String type;

    @NotNull
    private String paramJdbcUrl;

    @NotNull
    private String paramUsername;

    @NotNull
    private String paramPassword;

    @NotNull
    private String paramSQL;

    @NotNull
    private String cron;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParamJdbcUrl() {
        return paramJdbcUrl;
    }

    public void setParamJdbcUrl(String paramJdbcUrl) {
        this.paramJdbcUrl = paramJdbcUrl;
    }

    public String getParamUsername() {
        return paramUsername;
    }

    public void setParamUsername(String paramUsername) {
        this.paramUsername = paramUsername;
    }

    public String getParamPassword() {
        return paramPassword;
    }

    public void setParamPassword(String paramPassword) {
        this.paramPassword = paramPassword;
    }

    public String getParamSQL() {
        return paramSQL;
    }

    public void setParamSQL(String paramSQL) {
        this.paramSQL = paramSQL;
    }


    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}
