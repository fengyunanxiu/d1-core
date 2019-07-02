package ai.sparklabinc.datasource;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 数据库连接类，连接数据库
 *
 * @author Damon
 */
public class DBConn implements Connection {
    private final static Logger LOGGER = LoggerFactory.getLogger(DBConn.class);

    // 获取JdbcUrl信息
    private JdbcUrl JUrl;

    // 数据库连接
    public Connection con = null;

    // 连接是否已使用
    private boolean bNotInUse;

    public Session session = null;

    private CharArrayWriter m_buf = new CharArrayWriter();

    private PrintWriter m_pw = new PrintWriter(m_buf, true);

    // 默认连接
    public DBConn() {
        // TODO Auto-generated constructor stub
        this.JUrl = new JdbcUrl();
    }

    // 指定数据库连接
    public DBConn(String urlType) {
        this.JUrl = new JdbcUrl(urlType);
    }

    // 创建连接
    public boolean createConnection() {

        // 根据数据库类型加载驱动及连接
        try {
            // 连接MySQL数据库
            if (Constants.DATABASE_TYPE_MYSQL.equals(JUrl.getDbType())) {

                //Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jsch = new JSch();
                session = jsch.getSession(JUrl.getSshUser(), JUrl.getSshHost(), JUrl.getSshPort());
                session.setPassword(JUrl.getSshPassword());
                session.setConfig(config);
                session.connect();
                LOGGER.info("Connected");
                int assinged_port = session.setPortForwardingL(JUrl.getSshPort(), JUrl.getDbIP(), JUrl.getDbPort());
                LOGGER.info("localhost:" + assinged_port + " -> " + JUrl.getDbIP() + ":" + JUrl.getDbPort());
                LOGGER.info("Port Forwarded");

                // 加载数据库驱动
                Class.forName("com.mysql.jdbc.Driver");
                // 尝试连接数据库
                con = DriverManager.getConnection(JUrl.getJdbcUrl(), JUrl.getDbUserName(), JUrl.getDbPassWord());
            }
            // 其他数据库类型判断及处理
            // SQLSERVER
            else if (Constants.DATABASE_TYPE_SQLITE.equals(JUrl.getDbType())) {
                Class.forName("org.sqlite.JDBC");
                con = DriverManager.getConnection(JUrl.getJdbcUrl(), JUrl.getDbUserName(), JUrl.getDbPassWord());
            }
            // DB2
            else if (Constants.DATABASE_TYPE_DB2.equals(JUrl.getDbType())) {
                Class.forName("com.ibm.db2.jcc.DB2Driver");
                con = DriverManager.getConnection(JUrl.getJdbcUrl(), JUrl.getDbUserName(), JUrl.getDbPassWord());
            }
            // ORACLE
            else if (Constants.DATABASE_TYPE_ORACLE.equals(JUrl.getDbType())) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                // 一个是缓存取到的记录数，一个是设置默认的批量提交数
                Properties props = new Properties();
                props.setProperty("user", JUrl.getDbUserName());
                props.setProperty("password", JUrl.getDbPassWord());
                props.setProperty("defaultRowPrefetch", "50");
                props.setProperty("defaultExecuteBatch", "50");
                con = DriverManager.getConnection(JUrl.getJdbcUrl(), props);
            } else {
                System.out.println("未匹配到数据库类型！");
                return false;
            }

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("加载驱动失败！");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("创建连接失败..." + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("创建连接失败..." + e.getMessage());
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if (con != null && !con.isClosed()) {
                    System.out.println("Closing Database Connection");
                    con.close();
                }
            } catch (SQLException e) {
                LOGGER.error("e>>>{}", e.getMessage());
            }
            if (session != null && session.isConnected()) {
                LOGGER.info("Closing SSH Connection");
                session.disconnect();
            }
        }
        return true;
    }

    protected void setInUse() {
        /**
         * Record stack information when each connection is get We reassian
         * System.err, so Thread.currentThread().dumpStack() can dump stack info
         * into our class FilterPrintStream.
         */
        new Throwable().printStackTrace(m_pw);

        bNotInUse = false;

        /**
         * record lastest access time
         */
    }

    /* 下面都是 实现Connection的方法，返回conn的实现 */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        return con.unwrap(null);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Statement createStatement() throws SQLException {
        // TODO Auto-generated method stub
        return con.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return con.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return con.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return con.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        // TODO Auto-generated method stub
        con.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        // TODO Auto-generated method stub
        return con.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        // TODO Auto-generated method stub
        con.commit();
    }

    @Override
    public void rollback() throws SQLException {
        // TODO Auto-generated method stub
        con.rollback();
    }

    @Override
    public void close() throws SQLException {
        // TODO Auto-generated method stub
        con.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        // TODO Auto-generated method stub

        return con.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        // TODO Auto-generated method stub
        return con.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        // TODO Auto-generated method stub
        con.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        // TODO Auto-generated method stub
        return con.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        // TODO Auto-generated method stub
        con.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        // TODO Auto-generated method stub
        return con.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        // TODO Auto-generated method stub
        con.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        // TODO Auto-generated method stub
        return con.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        // TODO Auto-generated method stub
        return con.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        // TODO Auto-generated method stub
        con.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        // TODO Auto-generated method stub
        return con.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        // TODO Auto-generated method stub
        return con.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        // TODO Auto-generated method stub
        return con.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        // TODO Auto-generated method stub
        return con.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        // TODO Auto-generated method stub
        con.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        // TODO Auto-generated method stub
        return con.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        // TODO Auto-generated method stub
        return con.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        // TODO Auto-generated method stub
        return con.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        // TODO Auto-generated method stub
        con.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        // TODO Auto-generated method stub
        con.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        // TODO Auto-generated method stub
        return con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        // TODO Auto-generated method stub
        return con.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        // TODO Auto-generated method stub
        return con.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        // TODO Auto-generated method stub
        return con.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        // TODO Auto-generated method stub
        return con.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        // TODO Auto-generated method stub
        return con.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        // TODO Auto-generated method stub
        return con.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        // TODO Auto-generated method stub
        return con.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        // TODO Auto-generated method stub
        return con.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        // TODO Auto-generated method stub
        return con.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        // TODO Auto-generated method stub
        con.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        // TODO Auto-generated method stub
        con.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        // TODO Auto-generated method stub
        return con.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        // TODO Auto-generated method stub
        return con.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        // TODO Auto-generated method stub
        return con.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        // TODO Auto-generated method stub
        return con.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        con.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return con.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        con.abort(executor);

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        con.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return con.getNetworkTimeout();
    }

}