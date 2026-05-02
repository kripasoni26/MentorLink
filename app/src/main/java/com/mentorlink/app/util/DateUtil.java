package com.mentorlink.app.util;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());

    public static String format(long timestamp) {
        if (DateUtils.isToday(timestamp)) {
            return TIME_FORMAT.format(new Date(timestamp));
        } else {
            return DATE_TIME_FORMAT.format(new Date(timestamp));
        }
    }
}
