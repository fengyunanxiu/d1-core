package ai.sparklabinc.dao.impl;

import ai.sparklabinc.dao.DbBasicConfigDao;
import ai.sparklabinc.dao.DsQueryDao;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.dto.AssemblyResultDTO;
import ai.sparklabinc.dto.PageResultDTO;
import ai.sparklabinc.entity.DbBasicConfigDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/8 15:25
 * @description:
 * @version: V1.0
 */
@Repository
public class DsQueryDaoImpl implements DsQueryDao {
    @Autowired
    private DbBasicConfigDao dbBasicConfigDao;

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public PageResultDTO excuteQuery(AssemblyResultDTO assemblyResultDTO, Long fkDbId) throws IOException, SQLException {
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(fkDbId);
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(dbBasicConfigDO.getType(), dbBasicConfigDO.getId()));

        String countSql=assemblyResultDTO.getCountSql();
        String querySql=assemblyResultDTO.getQuerySql();
        List<Object> paramList = assemblyResultDTO.getParamList();
        long total=0L;
        List<Map<String, Object>> content=null;
        if(!CollectionUtils.isEmpty(paramList)){
            //如果有参数
            total = queryRunner.query(countSql, new ScalarHandler<Long>(),paramList.toArray());
            content = queryRunner.query(querySql, new MapListHandler(), paramList.toArray());

        }else {
            //没有参数
            total = queryRunner.query(countSql, new ScalarHandler<Long>());
            content = queryRunner.query(querySql, new MapListHandler());
        }
        PageResultDTO pageResultDTO = new PageResultDTO(content, total);
        return pageResultDTO;
    }
}
