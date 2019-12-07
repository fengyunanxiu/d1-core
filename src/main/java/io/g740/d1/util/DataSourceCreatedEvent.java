package io.g740.d1.util;

import javax.sql.DataSource;

/**
 * @author : Kingzer
 * @date : 2019-12-07 16:09
 * @description :
 */
public interface DataSourceCreatedEvent {

    void postDeal(DataSource dataSource);
}
