package com.sohu.agent.utils;

import android.util.Log;

/**
 * Created by wangyan on 2019/1/10
 */
public class LogUtils {

    private static final String DEFAULT_TAG = "SohuAop";

    private static final boolean DEBUG = true;

    public static void i(String tag, String message) {
        if (DEBUG) {
            Log.i(DEFAULT_TAG, tag + " [" + message + "]");
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            Log.i(DEFAULT_TAG, message);
        }
    }

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(DEFAULT_TAG, tag + " [" + message + "]");
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            Log.d(DEFAULT_TAG, message);
        }
    }

    public static void w(String tag, String message) {
        if (DEBUG) {
            Log.w(DEFAULT_TAG, tag + " [" + message + "]");
        }
    }

    public static void w(String message) {
        if (DEBUG) {
            Log.w(DEFAULT_TAG, message);
        }
    }

    public static void e(String tag, String message, Throwable e) {
        if (DEBUG) {
            Log.e(DEFAULT_TAG, tag + " [" + message + "]", e);
        }
    }

    public static void e(String tag, Throwable e) {
        if (DEBUG) {
            Log.e(DEFAULT_TAG, tag + " [" + Log.getStackTraceString(e) + "]", e);
        }
    }
}
