package io.g740.d1.sqlbuilder;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 20:05
 * @description :
 */
@Component
public class QuerProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuerProxy.class);

    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;

    public <T extends JpaRepository> T instance(Class<T> clazz) {
        // 获取接口上的泛型
        java.lang.reflect.Type[] genericInterfaces = clazz.getGenericInterfaces();
        ParameterizedType parameterized = (ParameterizedType) genericInterfaces[0];
        Class entityClass = (Class) parameterized.getActualTypeArguments()[0];
        return (T) Proxy.newProxyInstance(JpaRepository.class.getClassLoader(), new Class[]{clazz}, new QueryProxyHandler(d1BasicDataSource, entityClass));
    }

}
