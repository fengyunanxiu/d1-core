package ai.sparklabinc.controller;


import ai.sparklabinc.component.DefaultDataSourceComponent;
import ai.sparklabinc.entity.DbBasicConfigDO;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.SQLException;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private DefaultDataSourceComponent defaultDataSourceComponent;


    @RequestMapping("/test")
    public Object getInfo() throws SQLException {
        DataSource dataSource = defaultDataSourceComponent.getDataSource();

        QueryRunner queryRunner = new QueryRunner(dataSource);
        Object o =queryRunner.query("select * from db_basic_config",new BeanListHandler<DbBasicConfigDO>(DbBasicConfigDO.class));
        return  o;
    }
}
