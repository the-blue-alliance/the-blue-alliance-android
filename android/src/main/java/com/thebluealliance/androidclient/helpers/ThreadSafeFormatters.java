package com.thebluealliance.androidclient.helpers;

import static java.util.Locale.ENGLISH;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

/**
 * {@link java.text.SimpleDateFormat} is not thread-safe, so wrap calls to formats in
 * {@code synchronized} blocks
 */
@ThreadSafe
public final class ThreadSafeFormatters {

    private ThreadSafeFormatters() {
        // unused
    }

    private static final DateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", ENGLISH);
    private static final DateFormat EVENT_RENDER_FORMAT =
      new SimpleDateFormat("MMM d, yyyy", ENGLISH);
    private static final DateFormat EVENT_RENDER_SHORT_FORMAT =
      new SimpleDateFormat("MMM d", ENGLISH);
    private static final DateFormat MONTH_FORMAT = new SimpleDateFormat("MMM", ENGLISH);

    private static final NumberFormat DOUBLE_NO_PLACE_FORMAT = new DecimalFormat("###");
    private static final NumberFormat DOUBLE_ONE_PLACE_FORMAT = new DecimalFormat("###.#");
    private static final NumberFormat DOUBLE_TWO_PLACES_FORMAT = new DecimalFormat("###.##");

    public static synchronized Date parseEventDate(String dateString) throws ParseException{
        return EVENT_DATE_FORMAT.parse(dateString);
    }

    public static synchronized String renderEventDate(Date date) {
        return EVENT_RENDER_FORMAT.format(date);
    }

    public static synchronized String renderEventShortFormat(Date date) {
        return EVENT_RENDER_SHORT_FORMAT.format(date);
    }

    public static synchronized String renderEventMonth(Date date) {
        return MONTH_FORMAT.format(date);
    }

    public static synchronized String formatDoubleNoPlaces(double input) {
        return DOUBLE_NO_PLACE_FORMAT.format(input);
    }

    public static synchronized String formatDoubleOnePlace(double input) {
        return DOUBLE_ONE_PLACE_FORMAT.format(input);
    }

    public static synchronized String formatDoubleTwoPlaces(double input) {
        return DOUBLE_TWO_PLACES_FORMAT.format(input);
    }
}
