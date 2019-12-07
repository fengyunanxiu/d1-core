package io.g740.d1.sqlbuilder;

import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.util.DataSourcePoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 20:05
 * @description :
 */
@Component
public class QueryProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryProxy.class);

    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;

    public <T extends JpaRepository> T instance(Class<T> clazz) {
        // 获取接口上的泛型
        java.lang.reflect.Type[] genericInterfaces = clazz.getGenericInterfaces();
        ParameterizedType parameterized = (ParameterizedType) genericInterfaces[0];
        Class entityClass = (Class) parameterized.getActualTypeArguments()[0];
        return (T) Proxy.newProxyInstance(JpaRepository.class.getClassLoader(), new Class[]{clazz}, new QueryProxyHandler(d1BasicDataSource, entityClass));
    }


    public void setD1BasicDataSource(DataSource d1BasicDataSource) {
        this.d1BasicDataSource = d1BasicDataSource;
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.put("Driver", "org.postgresql.Driver");
        properties.put("User","postgres");
        properties.put("Password", "123456");
        properties.put("Url", "jdbc:postgresql://192.168.199.231:5432/d1_core?useSSL=false");
        DataSource datasource = DataSourcePoolUtils.createDatasource(properties);
        QueryProxy queryProxy = new QueryProxy();
        queryProxy.d1BasicDataSource = datasource;
        DictDOJpaRepository dictDOJpaRepository = queryProxy.instance(DictDOJpaRepository.class);
        List<DictDO> byFieldId = dictDOJpaRepository.findByFieldId("bNhPaDaDSyeTkunuUvcmKQ==");
        System.out.println("测试 findByFieldId: " + byFieldId);
        List<DictDO> all = dictDOJpaRepository.findAll();
        System.out.println("测试 findAll: " + all);
        List<DictDO> byGmtCreate = dictDOJpaRepository.findByGmtCreate(new Date());
        System.out.println("测试 findByGmtCreate: " + byGmtCreate);
        List<DictDO> address = dictDOJpaRepository.findByGmtCreateAndFieldDomain(new Date(), "area");
        System.out.println("测试 findByGmtCreateAndFieldDomain：" + address);
        List<DictDO> byFieldDomainAndFieldItemOrFieldDomainAndFieldItem = dictDOJpaRepository.findByFieldDomainAndFieldItemOrFieldDomainAndFieldItem("area", "nation", "area", "towns");
        System.out.println("测试 findByFieldDomainAndFieldItemOrFieldDomainAndFieldItem：" + byFieldDomainAndFieldItemOrFieldDomainAndFieldItem);
    }



}
