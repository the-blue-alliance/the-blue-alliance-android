package com.crashlytics.android;

public final class Crashlytics {

    private Crashlytics() {}

    public static void log(String message) {
        // noop
    }

    public static void logcat(int leve, String tag, String message) {
        // noop
    }

    public static void logException(Throwable throwable) {
        // noop
    }
}
