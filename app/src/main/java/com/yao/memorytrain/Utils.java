package com.yao.memorytrain;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class Utils {
    private static final String TAG = "memtrain";

    private static final String PREFS_FILE = "pref_all";
    private static final String PREFS_DEVICE_ID = "device_id";
    private static String uuid = null;
    private static SharedPreferences prefs = null;

    public static void init(Context context){
        prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }
    public static String getPrefs(String key, String defaultVal) {
        return prefs.getString(key, defaultVal);
    }
    public static void setPrefs(String key, String val) {
        prefs.edit().putString(key, val).apply();;
    }
    public static int getPrefs(String key, int defaultVal) {
        Logd("=============before");
        return prefs.getInt(key, defaultVal);
    }
    public static void setPrefs(String key, int val) {
        prefs.edit().putInt(key, val).apply();;
    }

    public static synchronized String getDeviceId(Context context) {
        if (uuid == null) {
            String storedId = prefs.getString(PREFS_DEVICE_ID, null);
            if (storedId != null) { // 从存储中读取已生成的UUID
                Logd("=====getDeviceId======== use stored id");
                uuid = storedId;
            } else { // 首次生成UUID并存储
                String androidID = getAndroidID(context);
                if (androidID == "") {
                    Logd("=====getDeviceId======== use randomUUID");
                    String orgUuid = UUID.randomUUID().toString();
                    // uuid = originalUuid.replace("-", "");
                    uuid = orgUuid.substring(0, 8) + orgUuid.substring(24, 36); // 提取前8位和后12位
                } else {
                    Logd("=====getDeviceId======== use androidID");
                    uuid = processTo20Length(androidID);
                }
                prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).apply();
            }
        }
        Logd("=====getDeviceId====" + uuid);
        return uuid;
    }
    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context)
    {
        try{
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception e){
            return "";
        }
    }
    public static void Logd(String msg) {
        Log.d(TAG, msg);
    }
    public static void Loge(String msg) {
        Log.e(TAG, msg);
    }
    public static String processTo20Length(String input) {
        int targetLen = 20;
        // 处理null情况，视为空字符串
        String str = (input == null) ? "" : input;
        int length = str.length();
        if (length == targetLen) {
            // 长度正好32位，直接返回
            return str;
        } else if (length > targetLen) {
            // 长度超过32位，截取前32位
            return str.substring(0, targetLen);
        } else {
            // 长度不足32位，末尾补0至32位
            StringBuilder sb = new StringBuilder(str);
            int needZero = targetLen - length;
            for (int i = 0; i < needZero; i++) {
                sb.append('0');
            }
            return sb.toString();
        }
    }
}
