package ai.sparklabinc.d1.dao.impl.oracle;

import ai.sparklabinc.d1.dao.DataDaoType;
import ai.sparklabinc.d1.dao.DbSecurityConfigDao;
import ai.sparklabinc.d1.datasource.DataSourceFactory;
import ai.sparklabinc.d1.entity.DbSecurityConfigDO;
import ai.sparklabinc.d1.util.DateUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.*;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/3 14:00
 * @description:
 */
@Repository("OracleDbSecurityConfigDaoImpl")
public class OracleDbSecurityConfigDaoImpl implements DbSecurityConfigDao {
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;


    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.ORACLE;
    }

    @Override
    public DbSecurityConfigDO findById(Long id) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);

        String querySql = "select * from db_security_config where id = ? ";

        DbSecurityConfigDO dbSecurityConfigDO = queryRunner.query(querySql, new ResultSetHandler<DbSecurityConfigDO>() {
            @Override
            public DbSecurityConfigDO handle(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    DbSecurityConfigDO dbSecurityConfigDO = new DbSecurityConfigDO();
                    Long id = resultSet.getLong("id");
                    String gmtCreateStr = resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");
                    Integer useSsl = resultSet.getInt("use_ssl");
                    Integer useSshTunnel = resultSet.getInt("use_ssh_tunnel");
                    String sslCaFile = resultSet.getString("ssl_ca_file");
                    String sslClientCertificateFile = resultSet.getString("ssl_client_certificate_file");
                    String sslClientKeyFile = resultSet.getString("ssl_client_key_file");
                    String sshProxyHost = resultSet.getString("ssh_proxy_host");
                    Integer sshProxyPort = resultSet.getInt("ssh_proxy_port");
                    String sshProxyUser = resultSet.getString("ssh_proxy_user");
                    Integer sshLocalPort = resultSet.getInt("ssh_local_port");
                    String sshAuthType = resultSet.getString("ssh_auth_type");
                    String sshProxyPassword = resultSet.getString("ssh_proxy_password");
                    String sshKeyFile = resultSet.getString("ssh_key_file");
                    String sshPassPhrase = resultSet.getString("ssh_pass_phrase");
                    //封装数据
                    dbSecurityConfigDO.setId(id);
                    dbSecurityConfigDO.setGmtCreate(gmtCreateStr);
                    dbSecurityConfigDO.setGmtModified(gmtModifiedStr);
                    dbSecurityConfigDO.setUseSsl(useSsl == 1);
                    dbSecurityConfigDO.setUseSshTunnel(useSshTunnel == 1);
                    dbSecurityConfigDO.setSslCaFile(sslCaFile);
                    dbSecurityConfigDO.setSslClientCertificateFile(sslClientCertificateFile);
                    dbSecurityConfigDO.setSslClientKeyFile(sslClientKeyFile);
                    dbSecurityConfigDO.setSshProxyHost(sshProxyHost);
                    dbSecurityConfigDO.setSshProxyPort(sshProxyPort);
                    dbSecurityConfigDO.setSshProxyUser(sshProxyUser);
                    dbSecurityConfigDO.setSshLocalPort(sshLocalPort);
                    dbSecurityConfigDO.setSshAuthType(sshAuthType);
                    dbSecurityConfigDO.setSshProxyPassword(sshProxyPassword);
                    dbSecurityConfigDO.setSshKeyFile(sshKeyFile);
                    dbSecurityConfigDO.setSshPassPhrase(sshPassPhrase);
                    return dbSecurityConfigDO;
                }
                return null;
            }
        }, id);
        return dbSecurityConfigDO;
    }

    @Override
    public Integer add(DbSecurityConfigDO dbSecurityConfigDO) throws IOException, SQLException {
        Connection conn = null;
        int update = 0;
        try {
            String sql = "insert into db_security_config (id,gmt_create, gmt_modified, use_ssl," +
                    " use_ssh_tunnel, ssl_ca_file, " +
                    "ssl_client_certificate_file, " +
                    "ssl_client_key_file, " +
                    "ssh_proxy_host," +
                    " ssh_proxy_port, " +
                    "ssh_proxy_user, " +
                    "ssh_local_port," +
                    " ssh_auth_type, " +
                    "ssh_proxy_password," +
                    "ssh_key_file," +
                    "ssh_pass_phrase)" +
                    " values (?, ?, ?, ?, ?, ?," +
                    "         ?, ?, ?, ?, ?," +
                    "         ?, ?, ?, ?, ?)";
            DataSource dataSource = d1BasicDataSource;
            conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            String now = DateUtils.ofLongStr(new java.util.Date());
            //绑定参数
            bindParameters(preparedStatement,
                    dbSecurityConfigDO.getId(),
                    now, now,
                    dbSecurityConfigDO.getUseSsl() ? 1 : 0,
                    dbSecurityConfigDO.getUseSshTunnel() ? 1 : 0,
                    dbSecurityConfigDO.getSslCaFile(),
                    dbSecurityConfigDO.getSslClientCertificateFile(),
                    dbSecurityConfigDO.getSslClientKeyFile(),
                    dbSecurityConfigDO.getSshProxyHost(),
                    dbSecurityConfigDO.getSshProxyPort(),
                    dbSecurityConfigDO.getSshProxyUser(),
                    dbSecurityConfigDO.getSshLocalPort(),
                    dbSecurityConfigDO.getSshAuthType(),
                    dbSecurityConfigDO.getSshProxyPassword(),
                    dbSecurityConfigDO.getSshKeyFile(),
                    dbSecurityConfigDO.getSshPassPhrase()
            );
            update = preparedStatement.executeUpdate();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return update;
    }

    @Override
    public Integer delete(Long dsId) throws SQLException, IOException {
        Connection conn = null;
        int update = 0;
        try {
            String sql = "delete from db_security_config where id = ?";
            DataSource dataSource = d1BasicDataSource;
            conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            //绑定参数
            bindParameters(preparedStatement, dsId);
            update = preparedStatement.executeUpdate();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return update;
    }

    @Override
    public Integer editDataSourceProperty(DbSecurityConfigDO dbSecurityConfigDO) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql = "update db_security_config set  gmt_modified  = ?," +
                "use_ssl = ?," +
                "use_ssh_tunnel = ?," +
                "ssl_ca_file = ?," +
                "ssl_client_certificate_file = ?," +
                "ssl_client_key_file = ?," +
                "ssh_proxy_host = ?," +
                "ssh_proxy_port = ?," +
                "ssh_proxy_user = ?," +
                "ssh_local_port = ?," +
                "ssh_auth_type = ?," +
                "ssh_proxy_password = ?," +
                "ssh_key_file = ?," +
                "ssh_pass_phrase = ?" +
                " where id = ?";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectsParams = {now,
                dbSecurityConfigDO.getUseSsl(),
                dbSecurityConfigDO.getUseSshTunnel(),
                dbSecurityConfigDO.getSslCaFile(),
                dbSecurityConfigDO.getSslClientCertificateFile(),
                dbSecurityConfigDO.getSslClientKeyFile(),
                dbSecurityConfigDO.getSshProxyHost(),
                dbSecurityConfigDO.getSshProxyPort(),
                dbSecurityConfigDO.getSshProxyUser(),
                dbSecurityConfigDO.getSshLocalPort(),
                dbSecurityConfigDO.getSshAuthType(),
                dbSecurityConfigDO.getSshProxyPassword(),
                dbSecurityConfigDO.getSshKeyFile(),
                dbSecurityConfigDO.getSshPassPhrase(),
                dbSecurityConfigDO.getId()
        };
        int update = queryRunner.update(sql, objectsParams);
        return update;
    }

    /**
     * 绑定参数方法
     *
     * @param stmt
     * @param params
     * @throws SQLException
     */
    private void bindParameters(PreparedStatement stmt, Object... params) throws SQLException {
        //绑定参数
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
