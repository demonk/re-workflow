package cn.demonk.initflow.utils;

import android.util.Log;

/**
 * Created by ligs on 8/10/17.
 */

public class L {
    private static final String TAG = "InitFlow";

    public static final void e(String msg) {
        Log.e(TAG, msg);
    }

    public static final void w(String msg) {
        Log.w(TAG, msg);
    }

    public static final void i(String msg) {
        Log.i(TAG, msg);
    }

    public static final void d(String msg) {
        Log.d(TAG, msg);
    }
}
