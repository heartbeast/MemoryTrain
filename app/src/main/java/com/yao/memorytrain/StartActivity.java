package com.yao.memorytrain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import static com.yao.memorytrain.Utils.*;

import com.yao.memorytrain.db.Best;
import com.yao.memorytrain.db.DBHelper;
import com.yao.memorytrain.game.FlipCardGameActivity;
import com.yao.memorytrain.game.MaxNumberActivity;
import com.yao.memorytrain.game.OrderNumberActivity;

public class StartActivity extends AppCompatActivity {
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // 预先初始化数据库实例
        dbHelper = DBHelper.getInstance(this);
        //Setting click listener for start button
    }
    public void startFlipGame(View view) {
        Intent intent = new Intent(StartActivity.this, FlipCardGameActivity.class);
        startActivity(intent);
    }
    public void startMaxNumberGame(View view) {
        Intent intent = new Intent(StartActivity.this, MaxNumberActivity.class);
        startActivity(intent);
    }

    public void startOrderNumber(View view) {
        Logd("startOrderNumber");
        Intent intent = new Intent(StartActivity.this, OrderNumberActivity.class);
        startActivity(intent);

        // 插入一条新的最佳记录
//        Best newBest = new Best();
//        newBest.gametype = "puzzle";
//        newBest.level = 5;
//        newBest.best = 12500;
//        newBest.info = "Completed in 3 minutes";
//        newBest.date = "2025-09-29";
//        dbHelper.insertBest(newBest);
//        Logd("startOrderNumber");

    }
}