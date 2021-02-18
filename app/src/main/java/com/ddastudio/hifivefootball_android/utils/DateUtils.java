package com.ddastudio.hifivefootball_android.utils;

import com.google.gson.Gson;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by hongmac on 2017. 9. 5..
 */

public class DateUtils {

    static PrettyTime prettyTime = new PrettyTime();

    public static String getPrettTime(Date date) {

        try {
            if ( prettyTime == null ) {
                prettyTime = new PrettyTime();
            }
            return prettyTime.format(date);

        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * String -> Date 타입 변환
     * @param date
     * @param format
     * @return
     */
    public static Date toDate(String date, String format) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return Calendar.getInstance().getTime();
        }
    }


    /**
     * Date -> String format 형태로 변환
     * @param date
     * @param format
     * @param addDay
     * @return
     */
    public static String convertDateToString(Date date, String format, int addDay) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, addDay);

        return convertDateToString(c.getTime(), format);
    }

    /**
     * Date -> String format 형태로 변환
     * @param date
     * @param format
     * @return
     */
    public static String convertDateToString(Date date, String format) {

        if ( date == null ) {
            return "";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        return simpleDateFormat.format(date);
    }

    public static String getNow(String format) {

        Calendar cal = Calendar.getInstance();
        return convertDateToString(cal.getTime(), format);
    }

    public static String getNow(String format, int addDay) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, addDay);
        return convertDateToString(cal.getTime(), format);
    }

    public static String getDatesBetweenJson(String start, String end, String fromat, String retFormat) {

        Gson gson = new Gson();
        Date startDate = toDate(start, fromat);
        Date endDate  = toDate(end, fromat);

        List<String> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        endCalendar.add(Calendar.DATE, 1);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(convertDateToString(result, retFormat));
            calendar.add(Calendar.DATE, 1);
        }

        return gson.toJson(datesInRange);
    }
}
