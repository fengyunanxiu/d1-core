package io.g740.d1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/31 14:52
 * @description:
 * @version: V1.0
 */

//@PropertySource("classpath:db.properties")
//@Configuration
//@ConfigurationProperties(prefix = "d1.basic.datasource")
@Component
public class BasicDbConfig {
    @Value("${d1.basic.datasource.type:}")
    private String type;

    @Value("${d1.basic.datasource.url:}")
    private String url;

    @Value("${d1.basic.datasource.user:}")
    private String user;

    @Value("${d1.basic.datasource.password:}")
    private String password;

    @Value("${d1.basic.datasource.useSsl:}")
    private Boolean useSsl=false;

    @Value("${d1.basic.datasource.useSshTunnel:}")
    private Boolean useSshTunnel=false;

    @Value("${d1.basic.datasource.sslCaFile:}")
    private String sslCaFile;

    @Value("${d1.basic.datasource.sslClientCertificateFile:}")
    private String sslClientCertificateFile;

    @Value("${d1.basic.datasource.sslClientKeyFile:}")
    private String sslClientKeyFile;

    @Value("${d1.basic.datasource.sshProxyHost:}")
    private String sshProxyHost;

    @Value("${d1.basic.datasource.sshProxyPort:}")
    private Integer sshProxyPort;

    @Value("${d1.basic.datasource.sshProxyUser:}")
    private String sshProxyUser;

    @Value("${d1.basic.datasource.sshLocalPort:}")
    private Integer sshLocalPort;

    @Value("${d1.basic.datasource.sshAuthType:}")
    private String sshAuthType;

    @Value("${d1.basic.datasource.sshProxyPassword:}")
    private String sshProxyPassword;

    @Value("${d1.basic.datasource.sshKeyFile:}")
    private String sshKeyFile;

    @Value("${d1.basic.datasource.sshPassPhrase:}")
    private String sshPassPhrase;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUseSsl() {
        return useSsl;
    }

    public void setUseSsl(Boolean useSsl) {
        this.useSsl = useSsl;
    }

    public Boolean getUseSshTunnel() {
        return useSshTunnel;
    }

    public void setUseSshTunnel(Boolean useSshTunnel) {
        this.useSshTunnel = useSshTunnel;
    }

    public String getSslCaFile() {
        return sslCaFile;
    }

    public void setSslCaFile(String sslCaFile) {
        this.sslCaFile = sslCaFile;
    }

    public String getSslClientCertificateFile() {
        return sslClientCertificateFile;
    }

    public void setSslClientCertificateFile(String sslClientCertificateFile) {
        this.sslClientCertificateFile = sslClientCertificateFile;
    }

    public String getSslClientKeyFile() {
        return sslClientKeyFile;
    }

    public void setSslClientKeyFile(String sslClientKeyFile) {
        this.sslClientKeyFile = sslClientKeyFile;
    }

    public String getSshProxyHost() {
        return sshProxyHost;
    }

    public void setSshProxyHost(String sshProxyHost) {
        this.sshProxyHost = sshProxyHost;
    }

    public Integer getSshProxyPort() {
        return sshProxyPort;
    }

    public void setSshProxyPort(Integer sshProxyPort) {
        this.sshProxyPort = sshProxyPort;
    }

    public String getSshProxyUser() {
        return sshProxyUser;
    }

    public void setSshProxyUser(String sshProxyUser) {
        this.sshProxyUser = sshProxyUser;
    }

    public Integer getSshLocalPort() {
        return sshLocalPort;
    }

    public void setSshLocalPort(Integer sshLocalPort) {
        this.sshLocalPort = sshLocalPort;
    }

    public String getSshAuthType() {
        return sshAuthType;
    }

    public void setSshAuthType(String sshAuthType) {
        this.sshAuthType = sshAuthType;
    }

    public String getSshProxyPassword() {
        return sshProxyPassword;
    }

    public void setSshProxyPassword(String sshProxyPassword) {
        this.sshProxyPassword = sshProxyPassword;
    }

    public String getSshKeyFile() {
        return sshKeyFile;
    }

    public void setSshKeyFile(String sshKeyFile) {
        this.sshKeyFile = sshKeyFile;
    }

    public String getSshPassPhrase() {
        return sshPassPhrase;
    }

    public void setSshPassPhrase(String sshPassPhrase) {
        this.sshPassPhrase = sshPassPhrase;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
