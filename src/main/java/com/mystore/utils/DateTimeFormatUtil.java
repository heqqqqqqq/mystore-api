package com.mystore.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//日期格式化
public class DateTimeFormatUtil {

    private static final String DATETIME_PATTERN="yyyy年MM月dd日 HH:mm:ss";
    private static final String DATETIME_PATTERN_GMT="yyyy-MM-dd HH:mm:ss";

    private DateTimeFormatUtil(){};

    public static String format(LocalDateTime dateTime){
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        if (dateTime==null){
            return "";
        }
        return dateTimeFormatter.format(dateTime);
    }

    public static LocalDateTime parseGMT(String gmtString){
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern(DATETIME_PATTERN_GMT);
        if (gmtString==null){
            return null;
        }
        return LocalDateTime.from(dateTimeFormatter.parse(gmtString));
    }
}
