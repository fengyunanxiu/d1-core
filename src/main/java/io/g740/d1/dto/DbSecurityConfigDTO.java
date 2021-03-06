package io.g740.d1.dto;

public class DbSecurityConfigDTO {

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

    private String sshKeyContent;

    private String sshPassPhrase;


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

    public String getSshKeyContent() {
        return sshKeyContent;
    }

    public void setSshKeyContent(String sshKeyContent) {
        this.sshKeyContent = sshKeyContent;
    }

    public String getSshPassPhrase() {
        return sshPassPhrase;
    }

    public void setSshPassPhrase(String sshPassPhrase) {
        this.sshPassPhrase = sshPassPhrase;
    }
}
