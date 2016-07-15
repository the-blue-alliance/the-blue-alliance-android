package com.thebluealliance.androidclient;

import com.google.firebase.crash.FirebaseCrash;

/**
 * A wrapper class for {@link android.util.Log} that also interfaces with {@link FirebaseCrash}
 * This class should not keep any state
 */
public class Log {

    public static synchronized void d(String msg) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.d(callingClass, msg);
        } else {
            // Don't write debug logs to logcat in prod builds
            FirebaseCrash.log(String.format("%1$s: %2$s", callingClass, msg));
        }
    }

    public static synchronized void d(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.d(callingClass, msg, tr);
        } else {
            // Don't write debug logs to logcat in prod builds
            FirebaseCrash.log(String.format("%1$s: %2$s", callingClass, msg));
            FirebaseCrash.report(tr);
        }
    }

    public static synchronized void e(String msg) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.e(callingClass, msg);
        } else {
            FirebaseCrash.logcat(android.util.Log.ERROR, callingClass, msg);
        }
    }

    public static synchronized void e(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.e(callingClass, msg, tr);
        } else {
            FirebaseCrash.logcat(android.util.Log.ERROR, callingClass, msg);
            FirebaseCrash.report(tr);
        }
    }

    public static synchronized String getStackTraceString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }

    public static synchronized void i(String msg) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.i(callingClass, msg);
        } else {
            FirebaseCrash.logcat(android.util.Log.INFO, callingClass, msg);
        }
    }

    public static synchronized void i(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.i(callingClass, msg, tr);
        } else {
            FirebaseCrash.logcat(android.util.Log.INFO, callingClass, msg);
            FirebaseCrash.report(tr);
        }
    }

    public static synchronized void v(String msg) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.v(callingClass, msg);
        } else {
            // Don't print verbose to logcat in release builds
            FirebaseCrash.log(String.format("%1$s: %2$s", callingClass, msg));
        }
    }

    public static synchronized void v(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.v(callingClass, msg, tr);
        } else {
            // Don't print verbose to logcat in release builds
            FirebaseCrash.log(String.format("%1$s: %2$s", callingClass, msg));
            FirebaseCrash.report(tr);
        }
    }

    public static synchronized void w(String msg) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.w(callingClass, msg);
        } else {
            FirebaseCrash.logcat(android.util.Log.WARN, callingClass, msg);
        }
    }

    public static synchronized void w(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.w(callingClass, msg, tr);
        } else {
            FirebaseCrash.logcat(android.util.Log.WARN, callingClass, msg);
            FirebaseCrash.report(tr);
        }
    }

    public static synchronized void wtf(String msg) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.wtf(callingClass, msg);
        } else {
            FirebaseCrash.logcat(android.util.Log.ASSERT, callingClass, msg);
        }
    }

    public static synchronized void wtf(String msg, Throwable tr) {
        String callingClass = getCallerCallerClassName();
        if (BuildConfig.DEBUG) {
            android.util.Log.wtf(callingClass, msg, tr);
        } else {
            FirebaseCrash.logcat(android.util.Log.ASSERT, callingClass, msg);
            FirebaseCrash.report(tr);
        }
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
