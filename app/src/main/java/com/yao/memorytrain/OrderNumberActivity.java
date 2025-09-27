package com.yao.memorytrain;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrderNumberActivity extends AppCompatActivity {
    private TextView timeText;
    private GridView gridView;
    private Button actionButton;
    private TextView resultText;

    private int currentRound = 1;
    private int currentNumberCount = 3;
    private List<Integer> currentNumbers;
    private List<Integer> positions;
    private List<View> gridItems;
    private boolean isShowingNumbers = true;
    private boolean isGameActive = false;
    private int currentClickIndex = 0;
    private Handler handler;
    private int seconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordernumber);

        initViews();
        setupGridView();
        startGame();
    }

    private void initViews() {
        timeText = findViewById(R.id.time_text);
        gridView = findViewById(R.id.grid_view);
        actionButton = findViewById(R.id.action_button);
        resultText = findViewById(R.id.result_text);

        handler = new Handler();
    }

    private void setupGridView() {
        gridItems = new ArrayList<>();
        // 创建20个方块
        for (int i = 0; i < 8; i++) { // 创建8行，每行2个，实际是20个方块
            for (int j = 0; j < 2; j++) {
                // 这里会用自定义的适配器来填充
            }
        }
    }

    private void startGame() {
        resetGame();
        startTimer();
        generateRound();
    }

    private void resetGame() {
        currentRound = 1;
        currentNumberCount = 3;
        currentClickIndex = 0;
        isGameActive = false;
        isShowingNumbers = true;
        seconds = 0;
        timeText.setText("时间: 00:00");
        resultText.setVisibility(View.GONE);
    }

    private void startTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isGameActive) {
                    seconds++;
                    int minutes = seconds / 60;
                    int secs = seconds % 60;
                    timeText.setText(String.format("时间: %02d:%02d", minutes, secs));
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void generateRound() {
        // 生成随机数字序列
        currentNumbers = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < currentNumberCount; i++) {
            currentNumbers.add(random.nextInt(9) + 1); // 1-9的数字
        }

        // 生成随机位置
        List<Integer> allPositions = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            allPositions.add(i);
        }
        Collections.shuffle(allPositions);
        positions = new ArrayList<>();
        for (int i = 0; i < currentNumberCount; i++) {
            positions.add(allPositions.get(i));
        }

        setupGridViewAdapter();
        showNumbers();
    }

    private void setupGridViewAdapter() {
        OrderGameAdapter adapter = new OrderGameAdapter(this, 20, currentNumbers, positions);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            if (!isShowingNumbers && isGameActive) {
                handleItemClick(position);
            }
        });
    }

    private void showNumbers() {
        isShowingNumbers = true;
        actionButton.setText("开始");
        actionButton.setOnClickListener(v -> startRound());

        // 为数字方块添加高亮动画
        OrderGameAdapter adapter = (OrderGameAdapter) gridView.getAdapter();
        for (int pos : positions) {
            View view = gridView.getChildAt(pos);
            if (view != null) {
                animateHighlight(view, adapter.getNumberAtPosition(pos));
            }
        }
    }

    private void animateHighlight(View view, int number) {
        if (view instanceof ViewGroup) {
            View child = ((ViewGroup) view).getChildAt(0);
            if (child != null) {
                ObjectAnimator scaleUp = ObjectAnimator.ofFloat(child, "scaleX", 1f, 1.2f);
                ObjectAnimator scaleDown = ObjectAnimator.ofFloat(child, "scaleX", 1.2f, 1f);
                scaleUp.setDuration(300);
                scaleDown.setDuration(300);

                AnimatorSet set = new AnimatorSet();
                set.playSequentially(scaleUp, scaleDown);
                set.start();
            }
        }
    }

    private void startRound() {
        isShowingNumbers = false;
        actionButton.setVisibility(View.GONE);

        // 隐藏数字，显示统一的蓝色块
        hideNumbers();
    }

    private void hideNumbers() {
        OrderGameAdapter adapter = (OrderGameAdapter) gridView.getAdapter();
        adapter.hideNumbers();

        // 为所有方块添加淡入动画
        for (int i = 0; i < gridView.getChildCount(); i++) {
            View child = gridView.getChildAt(i);
            if (child != null) {
                animateFadeIn(child);
            }
        }

        isGameActive = true;
        currentClickIndex = 0;
    }

    private void animateFadeIn(View view) {
        view.setAlpha(0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.start();
    }

    private void handleItemClick(int position) {
        if (currentClickIndex >= currentNumbers.size()) return;

        int expectedPosition = positions.get(currentClickIndex);
        int expectedNumber = currentNumbers.get(currentClickIndex);

        if (position == expectedPosition) {
            // 点击正确
            animateCorrectClick(gridView.getChildAt(position));
            currentClickIndex++;

            if (currentClickIndex == currentNumbers.size()) {
                // 当前轮完成
                if (currentRound == 12) {
                    // 游戏完成
                    endGame(true);
                } else {
                    // 进入下一轮
                    nextRound();
                }
            }
        } else {
            // 点击错误
            animateWrongClick(gridView.getChildAt(position));
            endGame(false);
        }
    }

    private void animateCorrectClick(View view) {
        if (view != null) {
            ObjectAnimator scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f);
            ObjectAnimator scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f);
            ObjectAnimator colorChange = ObjectAnimator.ofArgb(view, "backgroundColor",
                    Color.parseColor("#2196F3"), Color.parseColor("#4CAF50"));

            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleUp, scaleDown, colorChange);
            set.setDuration(300);
            set.start();
        }
    }

    private void animateWrongClick(View view) {
        if (view != null) {
            ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 20f, -20f, 20f, -20f, 0f);
            ObjectAnimator colorChange = ObjectAnimator.ofArgb(view, "backgroundColor",
                    Color.parseColor("#2196F3"), Color.parseColor("#F44336"));

            AnimatorSet set = new AnimatorSet();
            set.playTogether(shake, colorChange);
            set.setDuration(500);
            set.start();
        }
    }

    private void nextRound() {
        currentRound++;
        currentNumberCount++;
        isGameActive = false;

        // 延迟进入下一轮
        handler.postDelayed(() -> {
            generateRound();
        }, 1000);
    }

    private void endGame(boolean success) {
        isGameActive = false;
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setText("重新开始");
        actionButton.setOnClickListener(v -> startGame());

        if (success) {
            resultText.setText("恭喜！您完成了所有12轮挑战！");
        } else {
            resultText.setText("本次游戏您成功记住了 " + (currentNumberCount - 1) + " 个数字的顺序");
        }
        resultText.setVisibility(View.VISIBLE);
    }
}