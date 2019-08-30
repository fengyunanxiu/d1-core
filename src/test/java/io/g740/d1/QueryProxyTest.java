package io.g740.d1;

import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.sqlbuilder.DictDOJpaRepository;
import io.g740.d1.sqlbuilder.QueryProxy;
import io.g740.d1.util.DataSourcePoolUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/30 10:31
 * @description :
 */

public class QueryProxyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryProxyTest.class);

    private DictDOJpaRepository dictDOJpaRepository;

    @Before
    public void init() throws Exception {
        Properties properties = new Properties();
        properties.put("Driver", "org.postgresql.Driver");
        properties.put("User", "postgres");
        properties.put("Password", "123456");
        properties.put("Url", "jdbc:postgresql://192.168.199.231:5432/d1_core?useSSL=false");
        DataSource datasource = DataSourcePoolUtils.createDatasource(properties);
        QueryProxy queryProxy = new QueryProxy();
        queryProxy.setD1BasicDataSource(datasource);
        dictDOJpaRepository = queryProxy.instance(DictDOJpaRepository.class);
    }

    @Test
    public void testFindByString() {
        List<DictDO> byFieldId = dictDOJpaRepository.findByFieldId("bNhPaDaDSyeTkunuUvcmKQ==");
        System.out.println("测试 findByFieldId: " + byFieldId);
    }

    @Test
    public void TestFindAll() {
        List<DictDO> all = dictDOJpaRepository.findAll();
        System.out.println("测试 findAll: " + all);
    }

    @Test
    public void testFindByDate() {
        List<DictDO> byGmtCreate = dictDOJpaRepository.findByGmtCreate(new Date());
        System.out.println("测试 findByGmtCreate: " + byGmtCreate);
    }

    @Test
    public void testFindByDateAndString() {
        List<DictDO> address = dictDOJpaRepository.findByGmtCreateAndFieldDomain(new Date(), "area");
        System.out.println("测试 findByGmtCreateAndFieldDomain：" + address);
    }

    @Test
    public void testFindByAndOr() {
        List<DictDO> byFieldDomainAndFieldItemOrFieldDomainAndFieldItem = dictDOJpaRepository.findByFieldDomainAndFieldItemOrFieldDomainAndFieldItem("area", "nation", "area", "towns");
        System.out.println("测试 findByFieldDomainAndFieldItemOrFieldDomainAndFieldItem：" + byFieldDomainAndFieldItemOrFieldDomainAndFieldItem);

    }

}
