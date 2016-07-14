package com.thebluealliance.androidclient;

import com.google.firebase.crash.FirebaseCrash;

/**
 * A wrapper class for {@link android.util.Log} that also interfaces with {@link FirebaseCrash}
 */
public class Log {

    public static synchronized int d(String tag, String msg, Throwable tr) {
        return android.util.Log.d(tag, msg, tr);
    }

    public static synchronized int d(String tag, String msg) {
        return android.util.Log.d(tag, msg);
    }

    public static synchronized int e(String tag, String msg) {
        return android.util.Log.e(tag, msg);
    }

    public static synchronized int e(String tag, String msg, Throwable tr) {
        return android.util.Log.e(tag, msg, tr);
    }

    public static synchronized String getStackTraceString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }

    public static synchronized int i(String tag, String msg, Throwable tr) {
        return android.util.Log.i(tag, msg, tr);
    }

    public static synchronized int i(String tag, String msg) {
        return android.util.Log.i(tag, msg);
    }

    public static synchronized boolean isLoggable(String tag, int level) {
        return android.util.Log.isLoggable(tag, level);
    }

    public static synchronized int println(int priority, String tag, String msg) {
        return android.util.Log.println(priority, tag, msg);
    }

    public static synchronized int v(String tag, String msg) {
        return android.util.Log.v(tag, msg);
    }

    public static synchronized int v(String tag, String msg, Throwable tr) {
        return android.util.Log.v(tag, msg, tr);
    }

    public static synchronized int w(String tag, Throwable tr) {
        return android.util.Log.w(tag, tr);
    }

    public static synchronized int w(String tag, String msg, Throwable tr) {
        return android.util.Log.w(tag, msg, tr);
    }

    public static synchronized int w(String tag, String msg) {
        return android.util.Log.w(tag, msg);
    }

    public static synchronized int wtf(String tag, String msg) {
        return android.util.Log.wtf(tag, msg);
    }

    public static synchronized int wtf(String tag, Throwable tr) {
        return android.util.Log.wtf(tag, tr);
    }

    public static synchronized int wtf(String tag, String msg, Throwable tr) {
        return android.util.Log.wtf(tag, msg, tr);
    }

    /**
     * Some magic to get the name of the class calling into this one
     * From: http://stackoverflow.com/posts/11306854/revisions
     * @return Name of the class one higher in the stack trace, or a default value
     */
    private static synchronized String getCallerCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Log.class.getName())
                && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                if (callerClassName==null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    return ste.getClassName();
                }
            }
        }
        return "TheBlueAlliance4Android";
    }
}
