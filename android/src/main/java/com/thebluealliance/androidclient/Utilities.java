package com.thebluealliance.androidclient;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.io.PrintWriter;
import java.io.StringWriter;

import me.xuender.unidecode.Unidecode;

/**
 * Created by Nathan on 5/20/2014.
 */
public class Utilities {

    public static int getPixelsFromDp(Context c, int dipValue) {
        Resources r = c.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
                r.getDisplayMetrics());
    }

    public static String exceptionStacktraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static int getFirstCompWeek(int year) {
        int offset = year - 1992;
        if (Constants.FIRST_COMP_WEEK.length > offset && year != -1) {
            return Constants.FIRST_COMP_WEEK[offset];
        } else {
            //if no data for this year, return the most recent data
            Log.w(Constants.LOG_TAG, "No first competition week data available for " + year + ". Using most recent year.");
            return Constants.FIRST_COMP_WEEK[Constants.FIRST_COMP_WEEK.length - 1];
        }
    }

    public static int getCmpWeek(int year) {
        int offset = year - 1992;
        if (Constants.CMP_WEEK.length > offset) {
            return Constants.CMP_WEEK[offset];
        } else {
            //if no data for this year, return the most recent data
            Log.w(Constants.LOG_TAG, "No first championship week data available for " + year + ". Using most recent year.");
            return Constants.CMP_WEEK[Constants.CMP_WEEK.length - 1];
        }
    }

    public static String getAsciiApproximationOfUnicode(String input) {
        return Unidecode.decode(input);
    }

    /**
     * Replaces unicode characters with their ASCII equivalents and appends an asterisk to each term.
     *
     * For example, an input of "Über" would result in the string "Uber*".
     *
     * @param query the query from the user to prepare
     * @return the prepared query
     */
    public static String getPreparedQueryForSearch(String query) {
// Prepare text for query. We will split the query by spaces, append an asterisk to the end of
        // each component, and the put the string back together.
        query = getAsciiApproximationOfUnicode(query);

        String[] splitQuery = query.split("\\s+");

        for (int i = 0; i < splitQuery.length; i++) {
            splitQuery[i] = splitQuery[i] + "*";
        }

        String finalQuery = "";
        for (String aSplitQuery : splitQuery) {
            finalQuery += (aSplitQuery + " ");
        }
        return finalQuery;
    }
}
