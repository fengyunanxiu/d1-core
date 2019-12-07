package io.g740.d1.util;

import javax.sql.DataSource;

/**
 * @author : Kingzer
 * @date : 2019-12-07 16:10
 * @description :
 */
public class DefaultDataSourceCreatedEvent implements DataSourceCreatedEvent {

    public static DefaultDataSourceCreatedEvent singleton = new DefaultDataSourceCreatedEvent();
    @Override
    public void postDeal(DataSource dataSource) {
        // nothing to do
    }
}
