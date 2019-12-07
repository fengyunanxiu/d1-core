package io.g740.d1.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

/**
 * @author : Kingzer
 * @date : 2019-12-07 16:11
 * @description :
 */
public class DataSourceCreatedEventFactory {


    public  static  DataSourceCreatedEvent getDataSourceCreatedEvent(){
        try{
            return SpringD1ContextUtil.getBean(DataSourceCreatedEvent.class);
        }catch(Exception e){
            return DefaultDataSourceCreatedEvent.singleton;
        }
    }

}
