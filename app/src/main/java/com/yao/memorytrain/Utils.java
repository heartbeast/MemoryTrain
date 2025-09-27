package com.yao.memorytrain;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import java.util.UUID;

public class Utils {
    private static final String TAG = "MemTrain";

    private static final String PREFS_FILE_SCORE = "device_score";
    private static final String PREFS_USER = "id";
    private static final String PREFS_TYPE = "type";
    private static final String PREFS_SCORE = "score";



    private static final String PREFS_FILE_ID = "device_id";
    private static final String PREFS_DEVICE_ID = "device_id";
    private static String uuid = null;
    public static synchronized String getDeviceId(Context context) {
        if (uuid == null) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_ID, Context.MODE_PRIVATE);
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
