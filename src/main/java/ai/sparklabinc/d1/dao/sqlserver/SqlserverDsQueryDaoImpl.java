package ai.sparklabinc.d1.dao.sqlserver;

import ai.sparklabinc.d1.dao.DataDaoType;
import ai.sparklabinc.d1.dao.DbBasicConfigDao;
import ai.sparklabinc.d1.dao.DsQueryDao;
import ai.sparklabinc.d1.datasource.DataSourceFactory;
import ai.sparklabinc.d1.dto.AssemblyResultDTO;
import ai.sparklabinc.d1.dto.PageResultDTO;
import ai.sparklabinc.d1.entity.DbBasicConfigDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Repository("SqlserverDsQueryDaoImpl")
public class SqlserverDsQueryDaoImpl implements DsQueryDao {

    @Autowired
    @Qualifier("SqlserverDbBasicConfigDaoImpl")
    private DbBasicConfigDao dbBasicConfigDao;

    @Autowired
    private DataSourceFactory dataSourceFactory;
    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.SQLSERVER;
    }

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
