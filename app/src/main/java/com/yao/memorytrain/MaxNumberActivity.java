package com.yao.memorytrain;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MaxNumberActivity extends AppCompatActivity {
    private static final int MAX_ROUNDS = 20; // 最多20轮 (意味着最多测试 3+19 = 22 位数字)
    private static final int STARTING_DIGITS = 3; // 第一轮从3位数字开始

    private TextView roundTextView;
    private TextView numberDisplayTextView;
    private TextView userInputTextView; // 修改为TextView作为显示用户输入的框
    private Button submitButton;
    private Button startRoundButton; // 仅在第一轮显示，后续隐藏
    private GridLayout numberPadLayout; // 数字按钮布局
    private Button deleteButton; // 删除按钮

    private int currentRound = 1;
    private int currentDigitCount = STARTING_DIGITS;
    private String correctDigits; // 本轮需要记住的数字串
    private String currentUserInput = ""; // 存储当前用户输入的数字串
    private int maxMemorySpan = 0;

    private Handler handler = new Handler();
    private List<Integer> displayedNumbers = new ArrayList<>();
    private int currentDisplayIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maxnumber);

        roundTextView = findViewById(R.id.roundTextView);
        numberDisplayTextView = findViewById(R.id.numberDisplayTextView);
        userInputTextView = findViewById(R.id.userInputTextView); // 对应布局文件中的TextView
        submitButton = findViewById(R.id.submitButton);
        startRoundButton = findViewById(R.id.startButton);
        numberPadLayout = findViewById(R.id.numberPadLayout);
        deleteButton = findViewById(R.id.deleteButton);

        setupNumberPad(); // 设置数字按钮的点击事件

        updateRoundDisplay();
        maxMemorySpan = STARTING_DIGITS - 1; // 初始最大记忆广度设置为起始位数-1

        // 第一轮需要用户点击开始
        startRoundButton.setVisibility(View.VISIBLE);
        numberPadLayout.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        userInputTextView.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);

        startRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewRound(false); // 第一次启动，不需要延迟
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserInput();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserInput.length() > 0) {
                    currentUserInput = currentUserInput.substring(0, currentUserInput.length() - 1);
                    userInputTextView.setText(currentUserInput);
                }
            }
        });
    }

    private void setupNumberPad() {
        // 为0-9的数字按钮设置点击监听器
        for (int i = 0; i < numberPadLayout.getChildCount(); i++) {
            View child = numberPadLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                if (!"DEL".equals(button.getText().toString())) { // 排除DEL按钮，DEL有单独监听器
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button clickedButton = (Button) v;
                            appendDigitToInput(clickedButton.getText().toString());
                        }
                    });
                }
            }
        }
    }

    private void appendDigitToInput(String digit) {
        if (currentUserInput.length() < currentDigitCount) { // 限制输入长度不超过当前轮次数字位数
            currentUserInput += digit;
            userInputTextView.setText(currentUserInput);
        } else {
            Toast.makeText(this, "已达到本轮最大输入位数", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateRoundDisplay() {
        roundTextView.setText("第 " + currentRound + " 轮 ( " + currentDigitCount + " 位数字 )");
    }

    // `startNewRound` 方法增加了 `withDelay` 参数来控制是否需要延迟开始
    private void startNewRound(boolean withDelay) {
        startRoundButton.setVisibility(View.GONE); // 隐藏开始本轮按钮
        userInputTextView.setText(""); // 清空输入框
        currentUserInput = ""; // 重置用户输入字符串
        userInputTextView.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        numberPadLayout.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        numberDisplayTextView.setText("等待开始...");
        numberDisplayTextView.setVisibility(View.VISIBLE);


        Runnable startSequence = new Runnable() {
            @Override
            public void run() {
                maxMemorySpan = currentDigitCount - 1; // 假设本轮是新的最高记忆广度，如果过关则会更新
                generateAndDisplayNumbers();
            }
        };

        if (withDelay) {
            // 显示“本轮即将开始”的提示
            numberDisplayTextView.setText("本轮即将开始 (2s)");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    numberDisplayTextView.setText("本轮即将开始 (1s)");
                }
            }, 1000);
            handler.postDelayed(startSequence, 2000); // 延迟2秒后开始数字显示
        } else {
            startSequence.run(); // 立即开始
        }
    }


    private void generateAndDisplayNumbers() {
        displayedNumbers.clear();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < currentDigitCount; i++) {
            int digit = random.nextInt(10); // 生成0-9的随机数字
            displayedNumbers.add(digit);
            sb.append(digit);
        }
        correctDigits = sb.toString();

        currentDisplayIndex = 0;
        numberDisplayTextView.setVisibility(View.VISIBLE);
        displayNextNumber();
    }

    private void displayNextNumber() {
        if (currentDisplayIndex < displayedNumbers.size()) {
            int numberToShow = displayedNumbers.get(currentDisplayIndex);
            numberDisplayTextView.setText(String.valueOf(numberToShow));

            // 添加动画效果
            numberDisplayTextView.setScaleX(0.5f);
            numberDisplayTextView.setScaleY(0.5f);
            numberDisplayTextView.setAlpha(0f);

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(numberDisplayTextView, "scaleX", 0.5f, 1.2f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(numberDisplayTextView, "scaleY", 0.5f, 1.2f, 1.0f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(numberDisplayTextView, "alpha", 0f, 1f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY, alpha);
            animatorSet.setDuration(400); // 动画持续时间
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    currentDisplayIndex++;
                    displayNextNumber(); // 递归调用显示下一个数字
                }
            }, 1000); // 每个数字显示1秒
        } else {
            // 所有数字显示完毕
            numberDisplayTextView.setVisibility(View.GONE);
            userInputTextView.setVisibility(View.VISIBLE);
            numberPadLayout.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    private void checkUserInput() {
        if (TextUtils.isEmpty(currentUserInput)) {
            Toast.makeText(this, "请输入数字！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserInput.equals(correctDigits)) {
            Toast.makeText(this, "恭喜，回答正确！", Toast.LENGTH_SHORT).show();
            maxMemorySpan = currentDigitCount; // 更新最大记忆广度

            if (currentRound < MAX_ROUNDS) {
                currentRound++;
                currentDigitCount++;
                updateRoundDisplay();
                // 准备下一轮，除第一轮外，后面每轮都是等两秒自动开始游戏
                startNewRound(true);
            } else {
                // 所有轮次完成，游戏结束
//                endGame();
            }
        } else {
            Toast.makeText(this, "回答错误！", Toast.LENGTH_SHORT).show();
//            endGame(); // 匹配失败，游戏结束
        }
    }

//    private void endGame() {
//        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
//        intent.putExtra("MAX_MEMORY_SPAN", maxMemorySpan);
//        startActivity(intent);
//        finish(); // 结束游戏Activity
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // 防止内存泄漏
    }
}