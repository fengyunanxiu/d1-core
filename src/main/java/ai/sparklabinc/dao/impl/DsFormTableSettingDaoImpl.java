package ai.sparklabinc.dao.impl;

import ai.sparklabinc.dao.DsFormTableSettingDao;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
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
    public void initQueryRunner(){
        queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE));
    }



    @Override
    public List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKey(String dataSourceKey) {
        String querySql = "select * from ds_form_table_setting";

        return null;
    }



}
