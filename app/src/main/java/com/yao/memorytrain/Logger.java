package com.yao.memorytrain;

import android.util.Log;

public class Logger {
    private static final String TAG = "MemoryTrain";
    public static void Logd(String msg) {
        Log.d(TAG, msg);
    }
    public static void Loge(String msg) {
        Log.e(TAG, msg);
    }
}
