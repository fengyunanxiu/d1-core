package io.g740.d1.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Calendar;
import java.util.Date;

import static java.time.temporal.ChronoField.*;

/**
 * @title: DateUtils
 * @description:
 * @author: zhangxw
 * @date: 2017/11/27 14:45
 * @params:
 * @returns
 */
public class DateUtils {

    static DateTimeFormatter format = new DateTimeFormatterBuilder().appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral("-")
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral("-")
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral(" ")
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(":")
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(":")
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendLiteral(":")
            .toFormatter();

    // 支持的日期格式
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATE_2_PATTERN = "yyyy/MM/dd";
    private static final String DATE_3_PATTERN = "dd/MM/yyyy";

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATETIME_2_PATTERN = "yyyy/MM/dd HH:mm:ss";
    private static final String DATETIME_3_PATTERN = "dd/MM/yyyy HH:mm:ss";

    private static final org.joda.time.format.DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern(DATE_PATTERN);
    private static final org.joda.time.format.DateTimeFormatter DATE_2_FORMAT = DateTimeFormat.forPattern(DATE_2_PATTERN);
    private static final org.joda.time.format.DateTimeFormatter DATE_3_FORMAT = DateTimeFormat.forPattern(DATE_3_PATTERN);

    private static final org.joda.time.format.DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern(DATETIME_PATTERN);
    private static final org.joda.time.format.DateTimeFormatter DATETIME_2_FORMAT = DateTimeFormat.forPattern(DATETIME_2_PATTERN);
    private static final org.joda.time.format.DateTimeFormatter DATETIME_3_FORMAT = DateTimeFormat.forPattern(DATETIME_3_PATTERN);


    public static String ofLongStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_PATTERN);
        return sdf.format(date);
    }

    public static Date ofLongDate(String source) {
        try {
            return DATETIME_FORMAT.parseDateTime(source).toDate();
        } catch (Exception e) {
            try {
                return DATETIME_2_FORMAT.parseDateTime(source).toDate();
            } catch (Exception e2) {
                try {
                    return DATETIME_3_FORMAT.parseDateTime(source).toDate();
                } catch (Exception e3) {
                    return null;
                }
            }
        }
    }

    public static Date ofShortDate(String source) {
        try {
            return DATE_FORMAT.parseLocalDate(source).toDate();
        } catch (Exception e) {
            try {
                return DATE_2_FORMAT.parseLocalDate(source).toDate();
            } catch (Exception e2) {
                try {
                    return DATE_3_FORMAT.parseLocalDate(source).toDate();
                } catch (Exception e3) {
                    return null;
                }
            }
        }
    }

    public static String ofShortDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(date);
    }

    public static Date beforeDays(Date currentDate, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, days);

        return calendar.getTime();
    }

    /**
     * 判断是否为系统支持的日期格式,简要通过长度判断,不准确
     * @param dateStr
     * @return
     */
    public static Boolean isDate(String dateStr) {
    	try {
    		DateTime.parse(dateStr, DATE_FORMAT);
    		return true;
		} catch (Exception e) {
			try {
			    DateTime.parse(dateStr, DATE_2_FORMAT);
			    return true;
            } catch (Exception e2) {
                try {
                    DateTime.parse(dateStr, DATE_3_FORMAT);
                    return true;
                } catch (Exception e3) {
                    return false;
                }
            }
		}
    }
    
    /**
     * 判断是否为yyyy-MM-dd HH:mm:ss格式的日期,简要通过长度判断,不准确
     *
     * @param dateTimeStr
     * @return
     */
    public static Boolean isDateTime(String dateTimeStr) {
        try {
            DateTime.parse(dateTimeStr, DATETIME_FORMAT);
            return true;
        } catch (Exception e) {
            try {
                DateTime.parse(dateTimeStr, DATETIME_2_FORMAT);
                return true;
            } catch (Exception e2) {
                try {
                    DateTime.parse(dateTimeStr, DATETIME_3_FORMAT);
                    return true;
                } catch (Exception e3) {
                    return false;
                }
            }
        }
    }


    /**
     * 获取两个日期之间的工作日天数. 不适合用于时间跨度较大的运算
     * @param d1
     * @param d2
     * @return
     */
    public static Integer ofDiffWorkdayByDays(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            throw new RuntimeException("date is null");
        }
        if (d1.getTime() == d2.getTime()) {
            return 0;
        }
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        if (d1.getTime() > d2.getTime()) {
            end.setTime(d1);
            start.setTime(d2);
        } else{
            start.setTime(d1);
            end.setTime(d2);
        }

        int diffDays = 0;
        do {
            start.add(Calendar.DAY_OF_MONTH, 1);
            if (start.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                    && start.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                diffDays ++;
            }
        } while (start.getTimeInMillis() < end.getTimeInMillis());
        return diffDays;
    }

    /**
     * 各种日期格式转换，"yyyy-MM-dd";"yyyy-MM-dd HH:mm:ss";"yyyy/MM/dd"; "yyyy/MM/dd HH:mm:ss"
     * @param stringDate
     * @return
     */
    public static DateTime stringToDateTime(String stringDate) {
        DateTime date = null;
        try {
            date = DateTime.parse(stringDate, DATETIME_FORMAT);
        } catch (Exception e) {
            try {
                date = DateTime.parse(stringDate, DATETIME_2_FORMAT);
            } catch (Exception e2) {
                try {
                    date = DateTime.parse(stringDate, DATETIME_3_FORMAT);
                } catch (Exception e3) {
                    return null;
                }
            }
        }
        return date;
    }


    public static DateTime stringToDateFormatTwo(String stringDate) {
        DateTime date = null;
        try {
            date = DateTime.parse(stringDate, DATE_2_FORMAT);
        } catch (Exception e3) {
        }
        return date;
    }

    /**
     * 通过异常来判断是否满足相应的时间格式 DATE_PATTERN = "yyyy-MM-dd";  与ofLongDate对应
     * */
    public static Date ofShortDate2(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
