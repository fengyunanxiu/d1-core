package io.g740.d1.sqlbuilder;

import io.g740.d1.util.DateUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZoneId;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 11:38
 * @description :
 */
public class Type {

    public enum Java {
        STRING,
        INTEGER,
        BIG_DECIMAL,
        SQL_DATE,
        SQL_TIME,
        SQL_TIMESTAMP,
        DATE,
        LONG,
    }

    public enum SQL {
        VARCHAR,
        LONG_TEXT,
        INT,
        DATE,
        TIME,
        DATETIME,
        TIMESTAMP,
    }


    public static Object typeTransform(Java javaType, Object obj) {
        if (obj == null) {
            return null;
        }
        switch (javaType) {
            case DATE:
                return Type.objToUtilDate(obj);
            case LONG:
                return Long.valueOf(obj.toString());
            case INTEGER:
                return Integer.valueOf(obj.toString());
            case SQL_DATE:
                return Type.objToSqlDate(obj);
            case SQL_TIME:
                return Type.objToSqlTime(obj);
            case SQL_TIMESTAMP:
                return Type.objToSqlTimestamp(obj);
            case BIG_DECIMAL:
                return Type.objToBigDecimal(obj);
            case STRING:
            default:
                return obj.toString();
        }
    }


    public static Date utilDateToSqlDate(java.util.Date date) {
        return Date.valueOf(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public static Time utilDateToSqlTime(java.util.Date date) {
        return Time.valueOf(date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
    }

    public static Timestamp utilDateToSqlTimestamp(java.util.Date date) {
        return Timestamp.valueOf(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static Date objToSqlDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        if (obj instanceof java.util.Date) {
            return utilDateToSqlDate((java.util.Date) obj);
        }
        String dateStr = obj.toString();
        java.util.Date date = DateUtils.ofShortDate(dateStr);
        if (date == null) {
            return null;
        }
        return utilDateToSqlDate(date);
    }

    public static Time objToSqlTime(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Time) {
            return (Time) obj;
        }
        if (obj instanceof java.util.Date) {
            return utilDateToSqlTime((java.util.Date) obj);
        }
        String dateStr = obj.toString();
        java.util.Date date = DateUtils.ofTime(dateStr);
        if (date == null) {
            return null;
        }
        return utilDateToSqlTime(date);
    }

    public static Timestamp objToSqlTimestamp(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Timestamp) {
            return (Timestamp) obj;
        }
        if (obj instanceof java.util.Date) {
            return utilDateToSqlTimestamp((java.util.Date) obj);
        }
        String dateStr = obj.toString();
        java.util.Date date = DateUtils.ofLongDate(dateStr);
        if (date == null) {
            return null;
        }
        return utilDateToSqlTimestamp(date);
    }

    public static java.util.Date objToUtilDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof java.util.Date) {
            return (java.util.Date) obj;
        }
        return DateUtils.ofLongDate(obj.toString());
    }

    public static BigDecimal objToBigDecimal(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        return new BigDecimal(obj.toString());
    }

}
