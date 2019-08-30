package io.g740.d1.controller;


import io.g740.d1.datasource.Constants;
import io.g740.d1.datasource.DataSourceFactory;
import io.g740.d1.dict.dto.DictOptionCascadeQueryDTO;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.dict.service.DictService;
import io.g740.d1.entity.DbBasicConfigDO;
import io.g740.d1.sqlbuilder.BeanParser;
import io.g740.d1.sqlbuilder.DictDOJpaRepository;
import io.g740.d1.sqlbuilder.QueryProxy;
import io.g740.d1.util.DateUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Resource(name = "D1BasicDataSource")
    private DataSource dataSource;

    @Autowired
    private DictService dictService;

    @Autowired
    private QueryProxy queryProxy;

    @ApiOperation("test cascade query")
    @GetMapping("/cascade-query")
    @ResponseBody
    public List<DictOptionCascadeQueryDTO> cascadeQueryByDomainAndItem(String domain, String item) throws Exception {
        return this.dictService.cascadeQueryByDomainAndItem(domain, item);
    }

    @ResponseBody
    @GetMapping("/test")
    public Object getInfo() throws Exception {
        DataSource dataSource = dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,0L);
        QueryRunner queryRunner = new QueryRunner(dataSource);
        List<DbBasicConfigDO> dbBasicConfigDOList =queryRunner.query("select * from db_basic_config", new ResultSetHandler<List<DbBasicConfigDO>>() {
            @Override
            public List<DbBasicConfigDO>  handle(ResultSet resultSet) throws SQLException {
                List<DbBasicConfigDO> dbBasicConfigDOS = new ArrayList<>();
                DbBasicConfigDO dbBasicConfigDO = null;
                while (resultSet.next()){
                    Long id = resultSet.getLong("id");
                    String gmtCreateStr =  resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");
                    String type = resultSet.getString("type");
                    String name = resultSet.getString("name");
                    String host = resultSet.getString("host");
                    Integer port = resultSet.getInt("port");
                    String user = resultSet.getString("user");
                    String password = resultSet.getString("password");
                    String url = resultSet.getString("url");

                    Date gmtCreate = Optional.ofNullable(gmtCreateStr).map(DateUtils::ofLongDate).orElse(null);
                    Date gmtModified = Optional.ofNullable(gmtModifiedStr).map(DateUtils::ofLongDate).orElse(null);

                    dbBasicConfigDO = new DbBasicConfigDO();
                    dbBasicConfigDO.setId(id);
                    dbBasicConfigDO.setGmtCreate(gmtCreateStr);
                    dbBasicConfigDO.setGmtModified(gmtModifiedStr);
                    dbBasicConfigDO.setDbType(type);
                    dbBasicConfigDO.setDbName(name);
                   // dbBasicConfigDO.setDBHost(host);
                    dbBasicConfigDO.setDbPort(port);
                    dbBasicConfigDO.setDbUser(user);
                    dbBasicConfigDO.setDbPassword(password);
                    dbBasicConfigDO.setDbUrl(url);
                    dbBasicConfigDOS.add(dbBasicConfigDO);
                }
                return dbBasicConfigDOS;
            }
        });



        System.out.println(dbBasicConfigDOList);
        return  dbBasicConfigDOList;
    }
    @ResponseBody
    @GetMapping("/test2")
    public Object getInfo2() throws Exception {
//        DataSource dataSource =dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,0L);
//        QueryRunner queryRunner = new QueryRunner(dataSource);
        long startTime=System.currentTimeMillis();
        Connection connection = dataSource.getConnection();
        System.out.println("============="+(System.currentTimeMillis()-startTime)+"");
        long re=System.currentTimeMillis()-startTime;
        connection.close();
        return re;
    }

    @PostMapping("/bean-parser")
    @ResponseBody
    public void testBeanParser(@RequestBody DictDO dictDO) throws Exception {
        BeanParser beanParser = new BeanParser();
//        dictDO.setFieldDomain("test bean parser domain");
//        dictDO.setFieldItem("test bean parser item");
//        beanParser.insert(DictDO.class, dictDO, dataSource, false);
    }

    @GetMapping("/proxy")
    @ResponseBody
    public List<DictDO> testProxy(String fieldId) {
        DictDOJpaRepository instance = this.queryProxy.instance(DictDOJpaRepository.class);
        List<DictDO> fieldIdList = instance.findByFieldId(fieldId);
        return fieldIdList;
    }


}
