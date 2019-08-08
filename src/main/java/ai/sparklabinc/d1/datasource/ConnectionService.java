package ai.sparklabinc.d1.datasource;

import ai.sparklabinc.d1.dto.DbBasicConfigDTO;
import ai.sparklabinc.d1.dto.DbSecurityConfigDTO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @function:连接测试接口
 * @author: DAM
 * @date: 2019/7/11 10:11
 * @description:
 * @version: V1.0
 */
public interface ConnectionService {

    boolean createConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws IOException, SQLException, Exception;

    String generateSshKeyFile(Long dsId, String sshKeyFile, String keyText) throws IOException, SQLException, Exception;
}
