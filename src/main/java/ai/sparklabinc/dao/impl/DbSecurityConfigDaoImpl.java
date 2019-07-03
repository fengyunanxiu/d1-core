package ai.sparklabinc.dao.impl;

import ai.sparklabinc.dao.DbSecurityConfigDao;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.entity.DbSecurityConfigDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @version V1.0
 * @function:
 * @author: DAM
 * @date: 2019/7/3 14:00
 * @description:
 */
@Repository
public class DbSecurityConfigDaoImpl implements DbSecurityConfigDao {
    @Autowired
    private DataSourceFactory dataSourceFactory;

    private QueryRunner queryRunner;

    @PostConstruct
    public void initQueryRunner() throws IOException, SQLException {
        queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,0L));
    }

    @Override
    public DbSecurityConfigDO findById(Long id) throws SQLException {
        String querySql = "select * from db_security_config where id = ? ";

        DbSecurityConfigDO dbSecurityConfigDO = this.queryRunner.query(querySql, new ResultSetHandler<DbSecurityConfigDO>() {
            @Override
            public DbSecurityConfigDO handle(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    DbSecurityConfigDO dbSecurityConfigDO = new DbSecurityConfigDO();
                    Long id = resultSet.getLong("id");
                    String gmtCreateStr = resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");
                    Integer  useSsl= resultSet.getInt("use_ssl");
                    Integer  useSshTunnel= resultSet.getInt("use_ssh_tunnel");
                    String  sslCaFile= resultSet.getString("ssl_ca_file");
                    String  sslClientCertificateFile= resultSet.getString("ssl_client_certificate_file");
                    String  sslClientKeyFile= resultSet.getString("ssl_client_key_file");
                    String  sshProxyHost= resultSet.getString("ssh_proxy_host");
                    Integer  sshProxyPort= resultSet.getInt("ssh_proxy_port");
                    String  sshProxyUser= resultSet.getString("ssh_proxy_user");
                    Integer  sshLocalPort= resultSet.getInt("ssh_local_port");
                    String  sshAuthType= resultSet.getString("ssh_auth_type");
                    String  sshProxyPassword= resultSet.getString("ssh_proxy_password");
                    //封装数据
                    dbSecurityConfigDO.setId(id);
                    dbSecurityConfigDO.setGmtCreate(gmtCreateStr);
                    dbSecurityConfigDO.setGmtModified(gmtModifiedStr);
                    dbSecurityConfigDO.setUseSsl(useSsl==1);
                    dbSecurityConfigDO.setUseSshTunnel(useSshTunnel==1);
                    dbSecurityConfigDO.setSslCaFile(sslCaFile);
                    dbSecurityConfigDO.setSslClientCertificateFile(sslClientCertificateFile);
                    dbSecurityConfigDO.setSslClientKeyFile(sslClientKeyFile);
                    dbSecurityConfigDO.setSshProxyHost(sshProxyHost);
                    dbSecurityConfigDO.setSshProxyPort(sshProxyPort);
                    dbSecurityConfigDO.setSshProxyUser(sshProxyUser);
                    dbSecurityConfigDO.setSshLocalPort(sshLocalPort);
                    dbSecurityConfigDO.setSshAuthType(sshAuthType);
                    dbSecurityConfigDO.setSshProxyPassword( sshProxyPassword);
                    return dbSecurityConfigDO;
                }
                return  null;
            }
        },id);
        return  dbSecurityConfigDO;
    }
}
