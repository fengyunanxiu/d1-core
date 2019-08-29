package io.g740.d1.sqlbuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/5 17:32
 * @description :
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Column {
    String value();
    Type.Java javaType() default Type.Java.STRING;
    Type.SQL sqlType() default Type.SQL.VARCHAR;
}
