package com.thebluealliance.androidclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;

import com.thebluealliance.androidclient.activities.HomeActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
     * <p/>
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


    public static Intent getIntentForTBAUrl(Context c, Uri data) {
        Log.d(Constants.LOG_TAG, "Uri: " + data.toString());
        List<String> urlParts = data.getPathSegments();
        Intent intent = null;
        if (urlParts != null) {
            if (urlParts.isEmpty()) {
                //we caught the homepage (so there's no next part of the URL.
                //open the home screen
                //TODO once we get "glancables" up, make this link to special, dynamic content
                return HomeActivity.newInstance(c, R.id.nav_item_events);
            }
            System.out.println(urlParts.get(0));
            switch (urlParts.get(0)) {
                //switch on areas of tba that we can view here
                case "teams":
                    intent = HomeActivity.newInstance(c, R.id.nav_item_teams);
                    break;
                case "team":
                    if (indexExists(urlParts, 1) && TeamHelper.validateTeamKey("frc" + urlParts.get(1))) {
                        if (indexExists(urlParts, 2) && urlParts.get(2).matches("\\d\\d\\d\\d")) {
                            intent = ViewTeamActivity.newInstance(c, "frc" + urlParts.get(1), Integer.parseInt(urlParts.get(2)));
                        } else {
                            intent = ViewTeamActivity.newInstance(c, "frc" + urlParts.get(1));
                        }
                    }
                    break;
                case "":
                case "events":
                    intent = HomeActivity.newInstance(c, R.id.nav_item_events);
                    break;
                case "event":
                    if (indexExists(urlParts, 1) && EventHelper.validateEventKey(urlParts.get(1))) {
                        intent = ViewEventActivity.newInstance(c, urlParts.get(1));
                    }
                    break;
                case "match":
                    if (indexExists(urlParts, 1) && MatchHelper.validateMatchKey(urlParts.get(1))) {
                        intent = ViewMatchActivity.newInstance(c, urlParts.get(1));
                    }
                    break;
                case "insights":
                    intent = HomeActivity.newInstance(c, R.id.nav_item_insights);
                    break;
                case "gameday":
                    break;
                default:
                    intent = null;
            }
        }
        return intent;
    }

    public static boolean indexExists(List<String> data, int index) {
        return data != null &&
                !data.isEmpty() &&
                data.size() >= (index + 1) &&
                data.get(index) != null &&
                !data.get(index).isEmpty();
    }

    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    public static int getCurrentCompWeek() {
        return EventHelper.competitionWeek(new Date());
    }

    public static String getOrdinalFor(int value) {
        int hundredRemainder = value % 100;
        int tenRemainder = value % 10;
        if (hundredRemainder - tenRemainder == 10) {
            return "th";
        }

        switch (tenRemainder) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static void showStatsHelpDialog(Context c) {
        String helpText;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getResources().openRawResource(R.raw.stats_help)));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
                line = br.readLine();
            }
            helpText = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            helpText = "Error reading help file.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(c.getString(R.string.stats_help_title));
        builder.setMessage(Html.fromHtml(helpText));
        builder.setCancelable(true);
        builder.setNeutralButton(c.getString(R.string.close_stats_help),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        builder.create().show();
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static String readLocalProperty(Context c, String property) {
        Properties properties;
        properties = new Properties();
        try {
            InputStream fileStream = c.getAssets().open("tba.properties");
            properties.load(fileStream);
            fileStream.close();
            return properties.getProperty(property, "");
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Unable to read from tba.properties");
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isDebuggable(Context c){
        return (0 != (c.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

}
