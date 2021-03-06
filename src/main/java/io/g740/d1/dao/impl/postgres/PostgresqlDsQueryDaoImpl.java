package io.g740.d1.dao.impl.postgres;

import io.g740.d1.dao.DataDaoType;
import io.g740.d1.dao.DbBasicConfigDao;
import io.g740.d1.dao.DsQueryDao;
import io.g740.d1.datasource.DataSourceFactory;
import io.g740.d1.dto.PageResultDTO;
import io.g740.d1.dto.SQLGenerResultDTO;
import io.g740.d1.entity.DbBasicConfigDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/8 15:25
 * @description:
 * @version: V1.0
 */
@Repository("PostgresqlDsQueryDaoImpl")
public class PostgresqlDsQueryDaoImpl implements DsQueryDao {
    @Autowired
    @Qualifier("PostgresqlDbBasicConfigDaoImpl")
    private DbBasicConfigDao dbBasicConfigDao;

    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.POSTGRESQL;
    }


    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public PageResultDTO excuteQuery(SQLGenerResultDTO assemblyResultDTO, Long fkDbId) throws Exception {
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(fkDbId);
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(dbBasicConfigDO.getDbType(), dbBasicConfigDO.getId()));

        String countSql=assemblyResultDTO.getCountSql();
        String querySql=assemblyResultDTO.getQuerySql();
        List<Object> paramList = assemblyResultDTO.getParamsValue();
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
