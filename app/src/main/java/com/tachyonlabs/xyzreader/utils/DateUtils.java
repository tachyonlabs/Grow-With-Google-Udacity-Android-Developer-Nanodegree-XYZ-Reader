package com.tachyonlabs.xyzreader.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();

    // Most time functions can only handle 1902 - 2037
    private static final GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
    // Use default locale format
    private static final SimpleDateFormat outputFormat = new SimpleDateFormat();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");

    public static Date stringToDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    public static String parsePublishedDate(String date) {
        Date pubDate = stringToDate(date);
        if (!pubDate.before(START_OF_EPOCH.getTime())) {
            return android.text.format.DateUtils.getRelativeTimeSpanString(
                    pubDate.getTime(),
                    System.currentTimeMillis(), android.text.format.DateUtils.HOUR_IN_MILLIS,
                    android.text.format.DateUtils.FORMAT_ABBREV_ALL).toString();
        } else {
            // If date is before 1902, just show the string
            return outputFormat.format(pubDate);
        }
    }

}
