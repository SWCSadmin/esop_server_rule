package com.swcs.esop.api.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author 阮程
 * @date 2022/12/21
 */
public class DateUtil {

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date getLaterDate(Date d1, Date d2) {
        return d1.compareTo(d2) > 0 ? d1 : d2;
    }

    public static Date getEarlierDate(Date d1, Date d2) {
        return d1.compareTo(d2) > 0 ? d2 : d1;
    }
}
