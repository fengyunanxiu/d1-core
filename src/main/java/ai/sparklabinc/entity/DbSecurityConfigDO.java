package ai.sparklabinc.entity;

import java.util.Date;

public class DbSecurityConfigDO {
    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer useSsl;

    private Integer useSshTunnel;

    private String sslCaFile;

    private String sslClientCertificateFile;

    private String sslClientKeyFile;

    private String sshProxyHost;

    private Integer sshProxyPort;

    private String sshProxyUser;

    private Integer sshLocalPort;

    private String sshAuthType;

    private String sshProxyPassword;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getUseSsl() {
        return useSsl;
    }

    public void setUseSsl(Integer useSsl) {
        this.useSsl = useSsl;
    }

    public Integer getUseSshTunnel() {
        return useSshTunnel;
    }

    public void setUseSshTunnel(Integer useSshTunnel) {
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
}
