package ai.sparklabinc.dao.impl;

import ai.sparklabinc.dao.MysqlDataSourceDao;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.dto.DbInforamtionDTO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/4 16:35
 * @description:
 * @version: V1.0
 */
@Repository
public class MysqlDataSourceDaoImpl implements MysqlDataSourceDao {
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public List<DbInforamtionDTO> selectAllSchema(Long dsId) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_MYSQL, dsId));
        String sql="select" +
                "    distinct (table_schema) as label," +
                "     2 as level" +
                " from information_schema.tables" +
                " where table_schema not in ('information_schema','performance_schema')";
        List<DbInforamtionDTO> dbInforamtionDTOList = queryRunner.query(sql, new BeanListHandler<>(DbInforamtionDTO.class));
        return  dbInforamtionDTOList;
    }

    @Override
    public List<DbInforamtionDTO> selectAllTableAndView(Long dsId, String schema) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_MYSQL, dsId));
        String sql="select  table_name as label," +
                "       3 as level ," +
                "       case table_type when 'BASE TABLE' then 'table' else 'view' end as type" +
                " from information_schema.tables where table_schema=?";
        List<DbInforamtionDTO> dbInforamtionDTOList = queryRunner.query(sql, new BeanListHandler<>(DbInforamtionDTO.class),schema);
        return  dbInforamtionDTOList;
    }

}
