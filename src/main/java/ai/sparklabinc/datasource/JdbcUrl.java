package ai.sparklabinc.datasource;

/**
 * 数据库连接配置信息类
 *
 * @author Damon
 */
public class JdbcUrl {
    /**
     * 定义ssh参数
     */
    private int sshPort;
    private String sshUser;
    private String sshPassword;
    private String sshHost;

    /**
     * 定义数据库参数
     */
    // 数据库类型
    private String dbType;
    // 数据库服务器IP
    private String dbIP;
    // 数据库服务器端口
    private int dbPort;
    // 数据库名称
    private String dbName;
    // 用户名
    private String dbUserName;
    // 密码
    private String dbPassWord;


    /**
     * 默认构造方法，连接默认数据库
     */
    public JdbcUrl() {
        // TODO Auto-generated constructor stub
        dbType = Constants.DATABASE_TYPE_SQLITE;
        dbIP = "";
        dbName = "d1-core/D1.db";
        dbPort = 0;
        dbUserName = "";
        dbPassWord = "";
    }

    /**
     * 连接指定数据库
     *
     * @param urlType 传入连接类型标识
     */
    public JdbcUrl(String urlType) {
        if ("mysql".equals(urlType)) {
            dbType = Constants.DATABASE_TYPE_MYSQL;
            dbIP = "b2b-prd-ipbm-ipbm-0.mysql.database.chinacloudapi.cn";
            dbName = "ipbm_yhisipbm_yhis?useSSL=true&requireSSL=false&useUnicode=true&characterEncoding=UTF-8";
            dbPort = 3306;
            dbUserName = "pgadmin@b2b-prd-ipbm-ipbm-0";
            dbPassWord = "dNxFDpsr3E61";
            sshHost="10.126.147.5";
            sshPort=22;
            sshUser="ruizhi.cao";
            sshPassword="zj7UPo7fXkh#f#CX3";

        }
    }

    /**
     * 获取连接句柄
     *
     * @return String
     */
    public String getJdbcUrl() {
        String sUrl = "";
        if (dbType.trim().toUpperCase().equals(Constants.DATABASE_TYPE_MYSQL)) {
            sUrl = "jdbc:mysql://localhost:3307/" + dbName;
        } else if (dbType.trim().toUpperCase().equals(Constants.DATABASE_TYPE_DB2)) {
            sUrl = "jdbc:db2://localhost:3307/" + dbName;
        } else if (dbType.trim().toUpperCase().equals(Constants.DATABASE_TYPE_ORACLE)) {
            sUrl = "jdbc:oracle:thin:@localhost:3307:" + dbName;
        } else if (dbType.trim().toUpperCase().equals(Constants.DATABASE_TYPE_SQLITE)) {
            sUrl = "jdbc:sqlite:" + dbName;
        }else {
            System.out.println("暂无对应数据库驱动");
        }
        return sUrl;
    }

    // getters and setters

    public int getSshPort() {
        return sshPort;
    }

    public void setSshPort(int sshPort) {
        this.sshPort = sshPort;
    }

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public String getSshHost() {
        return sshHost;
    }

    public void setSshHost(String sshHost) {
        this.sshHost = sshHost;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbIP() {
        return dbIP;
    }

    public void setDbIP(String dbIP) {
        this.dbIP = dbIP;
    }

    public int getDbPort() {
        return dbPort;
    }

    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassWord() {
        return dbPassWord;
    }

    public void setDbPassWord(String dbPassWord) {
        this.dbPassWord = dbPassWord;
    }
}