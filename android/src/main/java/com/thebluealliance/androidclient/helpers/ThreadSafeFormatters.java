package com.thebluealliance.androidclient.helpers;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import static java.util.Locale.ENGLISH;

/**
 * {@link java.text.SimpleDateFormat} is not thread-safe, so wrap calls to formats in
 * {@code synchronized} blocks
 */
@ThreadSafe
public class ThreadSafeFormatters {

    private static final DateFormat sEventDateFormat = new SimpleDateFormat("yyyy-MM-dd", ENGLISH);
    private static final DateFormat sEventRenderFormat =
            new SimpleDateFormat("MMM d, yyyy", ENGLISH);
    private static final DateFormat sEventRenderShortFormat =
            new SimpleDateFormat("MMM d", ENGLISH);
    private static final DateFormat sMonthFormat = new SimpleDateFormat("MMM", ENGLISH);

    private static NumberFormat sDoubleOnePlaceFormat = new DecimalFormat("##.#");
    private static NumberFormat sDoubleTwoPlacesFormat = new DecimalFormat("###.##");

    public static synchronized Date parseEventDate(String dateString) throws ParseException {
        return sEventDateFormat.parse(dateString);
    }

    public static synchronized String renderEventDate(Date date) {
        return sEventRenderFormat.format(date);
    }

    public static synchronized String renderEventShortFormat(Date date) {
        return sEventRenderShortFormat.format(date);
    }

    public static synchronized String renderEventMonth(Date date) {
        return sMonthFormat.format(date);
    }

    public static synchronized String formatDoubleOnePlace(double input) {
        return sDoubleOnePlaceFormat.format(input);
    }

    public static synchronized String formatDoubleTwoPlaces(double input) {
        return sDoubleTwoPlacesFormat.format(input);
    }
}
