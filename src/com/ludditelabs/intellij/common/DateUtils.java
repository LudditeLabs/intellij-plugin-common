package com.ludditelabs.intellij.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class provides various date utils.
 */
public class DateUtils {
    private static final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");

    @NotNull
    private static SimpleDateFormat getUtcDateFormat() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        fmt.setTimeZone(UTC_TZ);
        return fmt;
    }

    /**
     * Return date from current UTC time.
     *
     * @return current UTC date with zero hour part.
     */
    @NotNull
    public static Date utcDateNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(UTC_TZ);
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Add days to the specific date.
     *
     * @param date base date.
     * @param days number of days to add (may be negative).
     * @return new date with added days.
     */
    @NotNull
    public static Date addDays(@NotNull Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(UTC_TZ);
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * Convert string date representation to UTC date object.
     *
     * @param text Date in format "yyyy-MM-dd".
     * @return Date object in UTC timezone.
     * @throws ParseException if text format is wrong.
     */
    public static Date dateFromString(String text) throws ParseException {
        return getUtcDateFormat().parse(text);
    }

    /**
     * Convert given date to string form "yyyy-MM-dd".
     *
     * @param date date object
     * @return string representation of the date.
     */
    @NotNull
    public static String toDateString(Date date) {
        return getUtcDateFormat().format(date);
    }

    /**
     * Convert time string in the format {@code EEE, dd MMM yyyy HH:mm:ss z}
     * to the UNIX timestamp.
     *
     * @param text Timestamp string.
     * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT.
     */
    public static long timestampToTime(@Nullable final String text) {
        if (text == null)
            return 0;

        final SimpleDateFormat format = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

        try {
            return format.parse(text).getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
