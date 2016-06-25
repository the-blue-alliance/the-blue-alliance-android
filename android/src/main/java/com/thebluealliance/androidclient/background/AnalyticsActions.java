package com.thebluealliance.androidclient.background;

import com.google.android.gms.analytics.GoogleAnalytics;

import android.app.Activity;

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
