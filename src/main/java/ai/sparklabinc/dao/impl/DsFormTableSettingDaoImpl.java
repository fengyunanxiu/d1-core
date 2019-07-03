package ai.sparklabinc.dao.impl;

import ai.sparklabinc.dao.DsFormTableSettingDao;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 07:46
 * @description :
 */
@Repository
public class DsFormTableSettingDaoImpl implements DsFormTableSettingDao {

    @Autowired
    private DataSourceFactory dataSourceFactory;

    private QueryRunner queryRunner;

    @PostConstruct
    public void initQueryRunner() throws IOException, SQLException {
        queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,0L));
    }



    @Override
    public List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKey(String dataSourceKey) throws SQLException {
        String querySql = "select * from ds_form_table_setting where ds_key = ? ";

        List<DsFormTableSettingDO>  dsFormTableSettingDOList = this.queryRunner.query(querySql, new ResultSetHandler<List<DsFormTableSettingDO>>() {
            @Override
            public List<DsFormTableSettingDO> handle(ResultSet resultSet) throws SQLException {
                List<DsFormTableSettingDO>  dsFormTableSettingDOS = new ArrayList<>();
                DsFormTableSettingDO dsFormTableSettingDO = null;
                while (resultSet.next()) {
                    Long id =  resultSet.getLong("id");
                    String gmtCreateStr =  resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");

                    String dsKey = resultSet.getString("ds_key");
                    String dsFieldName = resultSet.getString("db_field_name");

                }
                return dsFormTableSettingDOS;
            }
        },dataSourceKey);


        return null;
    }



}
