package com.yao.memorytrain.game;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.yao.memorytrain.Utils.Logd;

import com.yao.memorytrain.CustomAlertDialog;
import com.yao.memorytrain.R;

public class FlipCardGameActivity extends AppCompatActivity implements CardView.CardAnimationListener {
    private GridLayout gameGridLayout;
    private TextView tvClickCount;
    private Button btnRestart;
    private Button btnSettings;

    private int columnCount = 4; // 默认列数
    private int rowCount = 3; // 默认行数
    private int totalCards = columnCount * rowCount;
    private int screenWidth,screenHeight;

    private List<Integer> cardImageIds; // 卡片正面图片资源ID列表
    private List<Integer> selectedImageIds; // 每一轮游戏被选择的卡片

    private CardView firstFlippedCard = null;
    private CardView secondFlippedCard = null;

    private int matchedPairsCount = 0;
    private int clickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flipcard);

        gameGridLayout = findViewById(R.id.game_grid_layout);
        tvClickCount = findViewById(R.id.tv_click_count);
        btnRestart = findViewById(R.id.btn_restart);
        btnSettings = findViewById(R.id.btn_settings);

        btnRestart.setOnClickListener(v -> startGame());
        btnSettings.setOnClickListener(v -> showSettingsDialog());

        initCardImageResources(); // 初始化卡片正面图片资源
        startGame();
    }
    @Override
    protected void onResume() {
        super.onResume();
        screenWidth  = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        Logd("screen Width="+screenWidth+", Height="+screenHeight );
        if (matchedPairsCount == 0) { // 首次才重启游戏
            startGame();
        }
    }
    // 初始化所有可能的卡片正面图片资源
    private void initCardImageResources() {
        cardImageIds = Arrays.asList(
            R.drawable.card_front_101, R.drawable.card_front_102, R.drawable.card_front_ace1, R.drawable.card_front_ace2,
            R.drawable.card_front_21, R.drawable.card_front_22, R.drawable.card_front_31, R.drawable.card_front_32,
            R.drawable.card_front_41, R.drawable.card_front_42, R.drawable.card_front_51, R.drawable.card_front_52,
            R.drawable.card_front_61, R.drawable.card_front_62, R.drawable.card_front_71, R.drawable.card_front_72,
            R.drawable.card_front_81, R.drawable.card_front_82, R.drawable.card_front_91, R.drawable.card_front_92,
            R.drawable.card_front_jack1, R.drawable.card_front_jack2,
            R.drawable.card_front_queen1, R.drawable.card_front_queen2,
            R.drawable.card_front_king1, R.drawable.card_front_king2
        );
    }

    private void startGame() {
        matchedPairsCount = 0;
        clickCounter = 0;
        tvClickCount.setText("点击次数: 0");

        initGridLayout();
        initCardList();
        initCardsView();
    }

    private void initGridLayout() {
        gameGridLayout.removeAllViews(); // 清除之前的卡片
        gameGridLayout.setColumnCount(columnCount);
        gameGridLayout.setRowCount(rowCount);
    }
    private void initCardList() {
        selectedImageIds = new ArrayList<>();
        // 随机选择所需的图片ID
        Collections.shuffle(cardImageIds);
        for (int i = 0; i < totalCards / 2; i++) {
            selectedImageIds.add(cardImageIds.get(i));
            selectedImageIds.add(cardImageIds.get(i)); // 每张图片添加两次，形成一对
        }
        Collections.shuffle(selectedImageIds); // 打乱所有卡片的位置
    }

    private void initCardsView() {
        int cardWidth = (int) (screenWidth / columnCount * 0.9);
        int cardHeight = (int) (screenHeight / (rowCount + 1) * 0.8);
        Logd("cardWidth="+cardWidth+", cardHeight="+cardHeight);

        // 创建CardView实例并设置图片ID
        for (int i = 0; i < totalCards; i++) {
            CardView card = new CardView(this);
            card.setCard(selectedImageIds.get(i), selectedImageIds.get(i), R.drawable.card_back);
            card.setCardAnimationListener(this); // 设置动画监听器

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = cardWidth;
            layoutParams.height = cardHeight;
            layoutParams.setMargins(8, 4, 8, 4);
            card.setLayoutParams(layoutParams);
            gameGridLayout.addView(card);

            card.setOnClickListener(v -> {
                Logd("card clicked, id=" + card.getCardId());
                if (card.isFront()) {
                    return; // 正在动画或已翻开的卡片不响应点击。已配对会变成disable状态，不会有点击事件
                }
                if (firstFlippedCard != null && secondFlippedCard != null) {
                    return; // 已经有两张卡片翻开，等待动画结束回调
                }

                clickCounter++;
                tvClickCount.setText("点击次数: " + clickCounter);
                card.flipCard(); // 翻开卡片
            });
        }
    }

    @Override
    public void onFlipAnimationEnd(CardView card) {
        Logd("onFlipAnimationEnd...");
        if (firstFlippedCard == null) {
            firstFlippedCard = card;
        } else {
            secondFlippedCard = card;
            verifyMatch();
        }
    }
    @Override
    public void onFlipBackAnimationEnd(CardView card) {
        Logd("onFlipBackAnimationEnd...");
        if (firstFlippedCard == card) {
            firstFlippedCard = null;
        } else {
            secondFlippedCard = null;
        }
    }
    private void verifyMatch() {
        Logd("id1="+firstFlippedCard.getCardId()+", id2="+secondFlippedCard.getCardId());
        if (firstFlippedCard.getCardId() == secondFlippedCard.getCardId()) { // 匹配成功
            Logd(" ==matched==");
//            new Handler().postDelayed(() -> {
                firstFlippedCard.vanishCard();
                secondFlippedCard.vanishCard();
                // 等待消失动画结束再重置
//            }, 300); // 延迟消失
        } else { // 匹配失败，等待一段时间后翻回
            Logd(" ==no match,flip back==");
            // 匹配失败，等待一段时间后翻回
            new Handler().postDelayed(() -> {
                firstFlippedCard.flipBack();
                secondFlippedCard.flipBack();
            }, 300);
        }
    }

    @Override
    public void onVanishAnimationEnd(CardView cardView) {
        Logd("onVanishAnimationEnd...matchedPairsCount="+ matchedPairsCount);
        // 卡片消失动画结束时调用
        matchedPairsCount++;
        // 检查是否所有卡片都已配对
        if (matchedPairsCount == totalCards) {
            GameOver();
        }

        // 重置翻开的卡片引用
        if (cardView == firstFlippedCard) {
            firstFlippedCard = null;
        } else if (cardView == secondFlippedCard) {
            secondFlippedCard = null;
        }
    }

    public void GameOver() {
        if (columnCount == 6 && rowCount == 5) { // 已经是最高难度
            new CustomAlertDialog(this) .setTitle("恭喜过关")
                    .setMessage("点击次数: " + clickCounter)
                    .addButton("再来一次", (view) -> startGame())
                    .show();
        } else {
            new CustomAlertDialog(this) .setTitle("恭喜过关")
                    .setMessage("点击次数: " + clickCounter)
                    .addButton("再来一次", (view) -> startGame())
                    .addButton("更高难度", (view) -> {
                        // 根据当前难度级别设置RadioButton选中状态
                        columnCount += 1;
                        rowCount += 1;
                        totalCards = columnCount * rowCount;
                        Logd("new columnCount="+columnCount+",rowCount="+rowCount);
                        startGame();
                    })
                    .show();
//            new android.app.AlertDialog.Builder(this) .setTitle("恭喜过关")
//                    .setMessage("点击次数: " + clickCounter)
//                    .setPositiveButton("再来一次", (dialog, which) -> startGame())
//                    .setNegativeButton("更高难度", (dialog, which) -> {
//                        // 根据当前难度级别设置RadioButton选中状态
//                        columnCount += 1;
//                        rowCount += 1;
//                        totalCards = columnCount * rowCount;
//                        Logd("new columnCount="+columnCount+",rowCount="+rowCount);
//                        startGame();
//                        // dialog.dismiss();
//                    })
//                    .show();
        }
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_settings, null);
        builder.setView(dialogView);

        final RadioGroup radioGroupLevel = dialogView.findViewById(R.id.radio_group_level);
        Button btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        // 根据当前难度级别设置RadioButton选中状态
        if (columnCount == 3 && rowCount == 2) {
            radioGroupLevel.check(R.id.rb_level_easy);
        } else if (columnCount == 4 && rowCount == 3) {
            radioGroupLevel.check(R.id.rb_level_medium);
        } else if (columnCount == 5 && rowCount == 4) {
            radioGroupLevel.check(R.id.rb_level_hard);
        } else if (columnCount == 6 && rowCount == 5) {
            radioGroupLevel.check(R.id.rb_level_expert);
        }

        final AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(v -> {
            int selectedId = radioGroupLevel.getCheckedRadioButtonId();
            if (selectedId == R.id.rb_level_easy) {
                columnCount = 3;
                rowCount = 2;
            } else if (selectedId == R.id.rb_level_medium) {
                columnCount = 4;
                rowCount = 3;
            } else if (selectedId == R.id.rb_level_hard) {
                columnCount = 5;
                rowCount = 4;
            } else if (selectedId == R.id.rb_level_expert) {
                columnCount = 6;
                rowCount = 5;
            }
            totalCards = columnCount * rowCount;
            dialog.dismiss();
            Logd("new columnCount="+columnCount+",rowCount="+rowCount);
            startGame(); // 刷新游戏
        });

        dialog.show();
    }
}