package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

/**
 * File created by phil on 5/7/14.
 */
public class PreferenceHelper {
    private static SharedPreferences prefs;
    private static Context context;

    public static void setAppVersion(Context c){
        context = c;
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            prefs.edit().putString("app_version",info.versionName).putInt("version_code",info.versionCode).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAppVersion(){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("app_version","");
    }
}
