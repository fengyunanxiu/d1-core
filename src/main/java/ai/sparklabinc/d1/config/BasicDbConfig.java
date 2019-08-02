package ai.sparklabinc.d1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/31 14:52
 * @description:
 * @version: V1.0
 */

//@PropertySource("classpath:db.properties")
@Configuration
@ConfigurationProperties(prefix = "d1.basic.datasource")
public class BasicDbConfig {

    private String type;

    private String url;

    private String user;

    private String password;

    private Boolean useSsl=false;

    private Boolean useSshTunnel=false;

    private String sslCaFile;

    private String sslClientCertificateFile;

    private String sslClientKeyFile;

    private String sshProxyHost;

    private Integer sshProxyPort;

    private String sshProxyUser;

    private Integer sshLocalPort;

    private String sshAuthType;

    private String sshProxyPassword;

    private String sshKeyFile;

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
