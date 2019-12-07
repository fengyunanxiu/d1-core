package io.g740.d1.sqlbuilder;

import com.alibaba.fastjson.JSON;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 16:45
 * @description :
 */
public class QueryProxyHandler<T> implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryProxyHandler.class);

    private DataSource dataSource;

    private Class<T> entityClazz;

    public QueryProxyHandler(DataSource dataSource, Class<T> entityClazz) {
        this.dataSource = dataSource;
        this.entityClazz = entityClazz;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (SimpleJpaRepository.METHOD_NAMES.contains(name)) {
            SimpleJpaRepository simpleJpaRepository = new SimpleJpaRepository<>(dataSource, entityClazz);
            return method.invoke(simpleJpaRepository, args);
        }
        MethodQueryHelper<T> methodQueryHelper = new MethodQueryHelper<>(dataSource, entityClazz);
        return methodQueryHelper.query(name, args);
    }
}
