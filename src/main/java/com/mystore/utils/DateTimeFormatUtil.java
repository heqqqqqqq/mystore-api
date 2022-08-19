package com.mystore.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//日期格式化
public class DateTimeFormatUtil {

    private static final String DATETIME_PATTERN="yyyy年MM月dd日 HH:mm:ss";

    private DateTimeFormatUtil(){};

    public static String format(LocalDateTime dateTime){
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        if (dateTime==null){
            return "";
        }
        return dateTimeFormatter.format(dateTime);
    }
}
