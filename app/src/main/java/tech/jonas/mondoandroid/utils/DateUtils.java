package tech.jonas.mondoandroid.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DATE_FORMAT_WO_MS__STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final Calendar CALENDAR = Calendar.getInstance();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.UK);
    private static final SimpleDateFormat DATE_FORMAT_WO_MS = new SimpleDateFormat(DATE_FORMAT_WO_MS__STRING, Locale.UK);

    private static Date parse(String date) throws ParseException {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            return DATE_FORMAT_WO_MS.parse(date);
        }
    }

    public static int getDayOfYear(final String date) throws ParseException {
        CALENDAR.setTime(parse(date));
        return CALENDAR.get(Calendar.DAY_OF_YEAR);
    }

    public static String getDateString(final String date, Context context) throws ParseException {
        CALENDAR.setTime(parse(date));
        return android.text.format.DateUtils.formatDateTime(context, CALENDAR.getTimeInMillis(),
                android.text.format.DateUtils.FORMAT_ABBREV_MONTH);
    }

}
