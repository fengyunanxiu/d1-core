package ai.sparklabinc.d1.dao.impl.sqlserver;

import ai.sparklabinc.d1.dao.DataDaoType;
import ai.sparklabinc.d1.dao.DataSourceDao;
import ai.sparklabinc.d1.datasource.Constants;
import ai.sparklabinc.d1.datasource.DataSourceFactory;
import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.TableAndViewInfoDTO;
import ai.sparklabinc.d1.dto.TableColumnsDetailDTO;
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
@Repository("SqlserverDataSourceDaoImpl")
public class SqlserverDataSourceDaoImpl implements DataSourceDao {
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.SQLSERVER;
    }

    @Override
    public List<DbInforamtionDTO> selectAllSchema(Long dsId) throws Exception {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_MYSQL, dsId));
        String sql = "select" +
                "   schema_name as label," +
                "    2 as level" +
                "   from information_schema.schemata" +
                "   where schema_name not in ('information_schema','performance_schema','tmp','sys','mysql')";
        List<DbInforamtionDTO> dbInforamtionDTOList = queryRunner.query(sql, new BeanListHandler<>(DbInforamtionDTO.class));
        return dbInforamtionDTOList;
    }


    @Override
    public List<TableAndViewInfoDTO> selectAllTableAndView(Long dsId) throws Exception {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_MYSQL, dsId));
        String sql = "select  table_schema as tableSchema," +
                "   table_name as tableName," +
                "   3 as level ," +
                "   case table_type when 'BASE TABLE' then 'table' else 'view' end as type" +
                " from information_schema.tables" +
                " where table_schema not in ('information_schema','performance_schema','tmp','sys','mysql')";
        List<TableAndViewInfoDTO> tableAndViewInfoDTOS = queryRunner.query(sql, new BeanListHandler<>(TableAndViewInfoDTO.class));
        return tableAndViewInfoDTOS;
    }


    @Override
    public List<TableColumnsDetailDTO> selectTableColumnsDetail(Long dsId, String schema, String table) throws Exception {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_MYSQL, dsId));
        String sql = "select column_name as columnName," +
                "   column_type as dataType," +
                "   character_maximum_length as characterMaximumLength," +
                "   column_key as columnKey," +
                "   extra as extra," +
                "   ordinal_position as ordinalPosition," +
                "   column_comment as columnComment" +
                " from information_schema.columns" +
                " where table_schema = ?" +
                " and table_name = ?";
        List<TableColumnsDetailDTO> tableColumnsDetailDTOList = queryRunner.query(sql, new BeanListHandler<>(TableColumnsDetailDTO.class), schema,table);
        return  tableColumnsDetailDTOList;
    }

}
