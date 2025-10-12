package com.yao.memorytrain;

import android.app.Application;

import com.yao.memorytrain.db.DBHelper;
import static com.yao.memorytrain.Utils.Logd;

// 在 Application 类中
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        Logd("=============Application onCreate enter");
        super.onCreate();
        // 预先初始化数据库实例
        DBHelper.getInstance(this);
        Utils.init(this);
        Logd("=============Application onCreate exit");
    }
}
