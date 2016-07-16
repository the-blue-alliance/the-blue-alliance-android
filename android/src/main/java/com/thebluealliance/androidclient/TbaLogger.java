package com.thebluealliance.androidclient;

/**
 * A wrapper class for {@link android.util.Log} that we can extend to do other things in the future
 * (like integrate Firebase Crash Reporting logs)
 * This class should not keep any state
 */
public class TbaLogger {

    public static synchronized void d(String msg) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.d(callingClass, msg);
    }

    public static synchronized void d(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.d(callingClass, msg, tr);
    }

    public static synchronized void e(String msg) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.e(callingClass, msg);
    }

    public static synchronized void e(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.e(callingClass, msg, tr);
    }

    public static synchronized String getStackTraceString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }

    public static synchronized void i(String msg) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.i(callingClass, msg);
    }

    public static synchronized void i(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.i(callingClass, msg, tr);
    }

    public static synchronized void v(String msg) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.v(callingClass, msg);
    }

    public static synchronized void v(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.v(callingClass, msg, tr);
    }

    public static synchronized void w(String msg) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.w(callingClass, msg);
    }

    public static synchronized void w(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.w(callingClass, msg, tr);
    }

    public static synchronized void wtf(String msg) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.wtf(callingClass, msg);
    }

    public static synchronized void wtf(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        android.util.Log.wtf(callingClass, msg, tr);
    }

    /**
     * Some magic to get the name of the class calling into this one
     * From: http://stackoverflow.com/posts/11306854/revisions
     *
     * @return Name of the class one higher in the stack trace, or a default value
     */
    private static synchronized String getCallerCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(TbaLogger.class.getName())
                && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                if (callerClassName == null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    return ste.getClassName();
                }
            }
        }
        return "TheBlueAlliance4Android";
    }
}
