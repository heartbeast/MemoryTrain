package com.yao.memorytrain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import static com.yao.memorytrain.Utils.*;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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
        Intent intent = new Intent(StartActivity.this, OrderNumberActivity.class);
        startActivity(intent);
    }
}