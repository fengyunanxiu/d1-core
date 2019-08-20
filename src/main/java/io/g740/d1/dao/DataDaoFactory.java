package io.g740.d1.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/31 16:13
 * @description:
 * @version: V1.0
 */
@Component
public class DataDaoFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public <T> T getDaoBean(Class<T> javaType ,String sqlType){
        T t=null;
        Map<String, T> beansOfType = applicationContext.getBeansOfType(javaType);
        Set<String> keys = beansOfType.keySet();
        for (String e: keys ) {
            if(e.toUpperCase().indexOf(sqlType.toUpperCase())!=-1){
                t = beansOfType.get(e);
            }
        }
        return t;
    }


}
