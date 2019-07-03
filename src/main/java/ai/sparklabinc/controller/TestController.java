package ai.sparklabinc.controller;


import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.entity.DbBasicConfigDO;
import ai.sparklabinc.util.DateUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
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

    @ResponseBody
    @RequestMapping("/test")
    public Object getInfo() throws SQLException {
        DataSource dataSource = dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE);
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
                    dbBasicConfigDO.setType(type);
                    dbBasicConfigDO.setName(name);
                   // dbBasicConfigDO.setHost(host);
                    dbBasicConfigDO.setPort(port);
                    dbBasicConfigDO.setUser(user);
                    dbBasicConfigDO.setPassword(password);
                    dbBasicConfigDO.setUrl(url);
                    dbBasicConfigDOS.add(dbBasicConfigDO);
                }
                return dbBasicConfigDOS;
            }
        });



        System.out.println(dbBasicConfigDOList);
        return  dbBasicConfigDOList;
    }



    @ResponseBody
    @RequestMapping("/test2")
    public Object getInfo2() throws SQLException {
        DataSource dataSource =dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE);
        QueryRunner queryRunner = new QueryRunner(dataSource);

        String sql = "insert into db_basic_config" +
                "(gmt_create,gmt_modified,type,name,host,port,user,password,url) values (datetime('now'),datetime('now'),?,?,?,?,?,?,?) ";

        DbBasicConfigDO basicConfigDO = new DbBasicConfigDO();

        basicConfigDO.setHost("test");
        Object[] params = new Object[]{basicConfigDO.getType(),basicConfigDO.getName(),basicConfigDO.getHost(),basicConfigDO.getPort(),basicConfigDO.getUser(),basicConfigDO.getPassword(),basicConfigDO.getUrl()};
        queryRunner.update(sql,params);
        return null;
    }



}
