package io.g740.d1.sqlbuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/28 19:03
 * @description :
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Table {
    String value() ;
}
