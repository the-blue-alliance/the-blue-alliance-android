package com.thebluealliance.androidclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.format.DateFormat;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

    public static int getFirstompWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getFirstCompWeek(cal.get(Calendar.YEAR));
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

    public static void showHelpDialog(Context c, int rawText, String dialogTitle) {
        String helpText;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getResources().openRawResource(rawText)));
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
        builder.setTitle(dialogTitle);
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
            if(isDebuggable() && properties.containsKey(property + ".debug")){
                return properties.getProperty(property + ".debug");
            }
            return properties.getProperty(property, "");
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Unable to read from tba.properties");
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    /**
     * Get the <a href="http://developer.android.com/reference/android/os/Build.html#SERIAL">hardware serial number</a>
     * I hope this actually works universally, android UUIDs are irritatingly difficult
     *
     * @return UUID
     */
    public static String getUUID() {
        return Build.SERIAL;
    }

    /**
     * Utility method to create a comma separated list of strings. Useful when you have a list of things
     * that you want to express in a human-readable list, e.g. teams in a match.
     * <p/>
     * If the length of the list is 1, this method will return the input string verbatim.
     * <p/>
     * If the length of the list is 2, the returned string will be formatted like "XXXX and YYYY".
     * <p/>
     * If the length of the list is 3 or more, the returned string will be formatted like "XXXX, YYYY, and ZZZZ".
     * <p/>
     * This uses a localized "and" string.
     */
    public static String stringifyListOfStrings(Context context, ArrayList<String> strings) {
        String finalString = "";
        Resources r = context.getResources();
        int size = strings.size();
        if (size == 0) {
            finalString = "";
        } else if (size == 1) {
            finalString = strings.get(0);
        } else if (size == 2) {
            finalString = strings.get(0) + " " + r.getString(R.string.and) + " " + strings.get(1);
            // e.g. "111 and 1114"
        } else if (size > 2) {
            finalString += strings.get(0);
            for (int i = 1; i < size; i++) {
                if (i < size - 1) {
                    finalString += ", " + strings.get(i);
                } else {
                    finalString += ", " + r.getString(R.string.and) + " " + strings.get(i);
                }
            }
            // e.g. "111, 1114, and 254
        }
        return finalString;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        String output = new String(hexChars);
        return output.toLowerCase();
    }

    public static String sha256(String input) {
        MessageDigest digest = null;
        String hash = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(input.getBytes());

            hash = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            Log.e(Constants.LOG_TAG, "Can't find SHA-256 algorithm.");
            e.printStackTrace();
        }
        return hash;
    }

    public static boolean hasLApis() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
    
    public static String getDeviceUUID(Context context){
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getBuildTimestamp(Context c){
        /* Check the last modified time of classes.dex,
         * which was when the app was last built
         */
        try{
            ApplicationInfo ai = c.getPackageManager().getApplicationInfo(c.getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            Format dateFormat = DateFormat.getDateFormat(c);
            Format timeFormat = DateFormat.getTimeFormat(c);
            Date date = new java.util.Date(time);
            String s = dateFormat.format(date) + " " + timeFormat.format(date);
            zf.close();
            return s;
        }catch(Exception e){
            return null;
        }
    }

}
