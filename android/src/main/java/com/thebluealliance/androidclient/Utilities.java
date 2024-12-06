package com.thebluealliance.androidclient;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_UNSPECIFIED;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.common.base.Predicate;
import com.thebluealliance.androidclient.activities.HomeActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.xuender.unidecode.Unidecode;

public final class Utilities {

    private Utilities() {
        // not used
    }

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

    public static int getFirstCompWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getFirstCompWeek(cal.get(Calendar.YEAR));
    }

    public static int getFirstCompWeek(int year) {
        int offset = year - 1992;
        if (Constants.FIRST_COMP_WEEK.length > offset && year != -1) {
            return offset >= Constants.FIRST_COMP_WEEK.length || offset < 0
                    ? Constants.FIRST_COMP_WEEK[Constants.FIRST_COMP_WEEK.length - 1]
                    : Constants.FIRST_COMP_WEEK[offset];
        } else {
            //if no data for this year, return the most recent data
            TbaLogger.w("No first competition week data available for " + year + ". Using most recent year.");
            return Constants.FIRST_COMP_WEEK[Constants.FIRST_COMP_WEEK.length - 1];
        }
    }

    public static int getCmpWeek(int year) {
        int offset = year - 1992;
        if (Constants.CMP_WEEK.length > offset) {
            return Constants.CMP_WEEK[offset];
        } else {
            //if no data for this year, return the most recent data
            TbaLogger.w("No first championship week data available for " + year + ". Using most recent year.");
            return Constants.CMP_WEEK[Constants.CMP_WEEK.length - 1];
        }
    }

    public static String getAsciiApproximationOfUnicode(String input) {
        return Unidecode.decode(input);
    }

    /**
     * Replaces unicode characters with their ASCII equivalents and appends an asterisk to each
     * term.
     * <p>
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
        TbaLogger.d("Uri: " + data.toString());
        List<String> urlParts = data.getPathSegments();

        // Check if this is actually a TBA URL
        // Simply checks if the host matches (*.)thebluealliance.com
        String tbaHostPattern = "(.*\\.?)thebluealliance.com";
        Pattern pattern = Pattern.compile(tbaHostPattern);
        if (!pattern.matcher(data.getHost()).matches()) {
            return null;
        }

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
                        // Some sources (such as ChiefDelphi) will include leading 0s in the team #
                        // The API doesn't accept those and we shouldn't display those, so strip them
                        String teamNumber = urlParts.get(1).replaceFirst("^0+", "");
                        if (indexExists(urlParts, 2) && urlParts.get(2).matches("\\d\\d\\d\\d")) {
                            intent = ViewTeamActivity.newInstance(c, "frc" + teamNumber, Integer.parseInt(urlParts.get(2)));
                        } else {
                            intent = ViewTeamActivity.newInstance(c, "frc" + teamNumber);
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
                case "gameday":
                    intent = HomeActivity.newInstance(c, R.id.nav_item_gameday);
                    break;
                default:
                    intent = null;
            }
        }
        return intent;
    }

    public static boolean indexExists(List<String> data, int index) {
        return data != null
                && !data.isEmpty()
                && data.size() >= (index + 1)
                && data.get(index) != null
                && !data.get(index).isEmpty();
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

    public static void showHelpDialog(Context c, @RawRes int rawText, String dialogTitle) {
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
                (dialog, which) -> {
                    dialog.cancel();
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

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    /**
     * Get the <a href="http://developer.android.com/reference/android/os/Build.html#SERIAL">hardware
     * serial number</a> I hope this actually works universally, android UUIDs are irritatingly
     * difficult
     *
     * @return UUID
     */
    public static String getUUID() {
        return Build.SERIAL;
    }

    /**
     * Utility method to create a comma separated list of strings. Useful when you have a list of
     * things that you want to express in a human-readable list, e.g. teams in a match.
     * <p>
     * If the length of the list is 1, this method will return the input string verbatim.
     * <p>
     * If the length of the list is 2, the returned string will be formatted like "XXXX and YYYY".
     * <p>
     * If the length of the list is 3 or more, the returned string will be formatted like "XXXX,
     * YYYY, and ZZZZ".
     * <p>
     * This uses a localized "and" string.
     */
    public static String stringifyListOfStrings(Context context, List<String> strings) {
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

    /**
     * @return a comma-separated CharSequence of the given names, applying bold style to names that
     * satisfy the given predicate.
     */
    public static CharSequence boldNameList(Iterable<? extends CharSequence> names,
                                            Predicate<String> beBold) {
        final SpannableStringBuilder result = new SpannableStringBuilder();
        boolean first = true;

        for (CharSequence name : names) {
            if (first) {
                first = false;
            } else {
                result.append(", ");
            }

            if (beBold.apply(name.toString())) {
                supportAppend(result, name, new StyleSpan(Typeface.BOLD), 0);
            } else {
                result.append(name);
            }
        }
        return result;
    }

    private static SpannableStringBuilder supportAppend(SpannableStringBuilder builder, CharSequence text, Object what, int flags) {
        int start = builder.length();
        builder.append(text);
        builder.setSpan(what, start, builder.length(), flags);
        return builder;
    }

    final protected static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        String output = new String(hexChars);
        return output.toLowerCase();
    }

    public static String sha256(String input) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(input.getBytes());

            hash = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            TbaLogger.e("Can't find SHA-256 algorithm.");
            e.printStackTrace();
        }
        return hash;
    }

    public static String getDeviceUUID(Context context) {
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getBuildTimestamp(Context c) {
        /* Check the last modified time of classes.dex,
         * which was when the app was last built
         */
        try {
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
        } catch (Exception e) {
            return null;
        }
    }

    public static String getVersionNumber() {
        /* If this changes, make sure to also change it in SettingsActivity */
        if (BuildConfig.VERSION_NAME.contains("/")) {
            return BuildConfig.VERSION_NAME.split("/")[0];
        } else {
            return BuildConfig.VERSION_NAME;
        }
    }

    /**
     * On API 23+, this allows us to set the color of the status bar icons (either light or dark)
     * to look better with the status bar background. If the background is light, the icons will be
     * tinted gray/black; otherwise, they will be the default white.
     * <p>
     * This is safe to be called from any API level, as this method checks the API level before
     * trying to use the new feature.
     *
     * @param window          the window to be modified
     * @param lightBackground if the background of the status bar is light
     */
    public static void setLightStatusBar(Window window, boolean lightBackground) {
        int vis = window.getDecorView().getSystemUiVisibility();
        // Set light
        if (lightBackground) {
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        window.getDecorView().setSystemUiVisibility(vis);
    }

    public static int getCurrentDarkModePreference(String prefValue) {
        int newValue = AppCompatDelegate.MODE_NIGHT_UNSPECIFIED;
        switch (prefValue) {
            case "dark":
                return MODE_NIGHT_YES;
            case "light":
                return MODE_NIGHT_NO;
            case "system":
                return MODE_NIGHT_FOLLOW_SYSTEM;
            case "battery":
                return MODE_NIGHT_AUTO_BATTERY;
            default:
                return MODE_NIGHT_UNSPECIFIED;
        }
    }

    public static void configureActivityForEdgeToEdge(AppCompatActivity activity) {
        EdgeToEdge.enable(activity);

        Window window = activity.getWindow();
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(window, window.getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
    }
}