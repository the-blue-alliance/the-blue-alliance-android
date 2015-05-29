package com.thebluealliance.androidclient.background;


import android.app.Activity;

import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * Created by phil on 1/16/15.
 */
public class AnalyticsActions {

    public static class ReportActivityStart implements Runnable {

        private Activity activity;

        public ReportActivityStart(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            GoogleAnalytics.getInstance(activity).reportActivityStart(activity);
        }
    }

    public static class ReportActivityStop implements Runnable {

        private Activity activity;

        public ReportActivityStop(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            GoogleAnalytics.getInstance(activity).reportActivityStop(activity);
        }
    }
}
