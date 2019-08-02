package ai.sparklabinc.d1.dto;

import java.util.Map;

public class DbBasicConfigDTO {
    private Long id;
    /**
     * 枚举类型，MySQL,Sql Server等（见DsConstant）
     */

    private String type;

    private String name;

    private String host;

    private Integer port;

    private String user;

    private String password;

    private String url;

    private Map<String,String> otherParams;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, String> getOtherParams() {
        return otherParams;
    }

    public void setOtherParams(Map<String, String> otherParams) {
        this.otherParams = otherParams;
    }
}
