package io.g740.d1.datasource;

import io.g740.d1.dto.DbBasicConfigDTO;
import io.g740.d1.dto.DbSecurityConfigDTO;

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

    void createConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception;

    String generateSshKeyFile(Long dsId, String sshKeyFile, String keyText) throws Exception;
}
