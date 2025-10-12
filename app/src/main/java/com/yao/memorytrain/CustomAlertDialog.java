package com.yao.memorytrain;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable; // 用于创建形状 Drawable
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import java.util.ArrayList;
import java.util.List;

public class CustomAlertDialog extends AppCompatDialog {

    private String titleText;
    private String messageText;
    private List<ButtonConfig> buttonConfigs = new ArrayList<>();

    private TextView titleTextView;
    private TextView messageTextView;
    private LinearLayout buttonContainer;
    private LinearLayout dialogRootLayout; // 新增：引用根布局

    // 定义一些常用的颜色
    private static final int COLOR_PRIMARY_GREEN = Color.parseColor("#4CAF50");
    private static final int COLOR_SECONDARY_GREY = Color.parseColor("#E0E0E0");
    private static final int COLOR_BUTTON_TEXT_DARK = Color.parseColor("#424242");
    private static final int COLOR_BLACK = Color.BLACK;
    private static final int COLOR_WHITE = Color.WHITE;
    private static final int COLOR_DARKER_GRAY = Color.DKGRAY; // 或 Color.parseColor("#A9A9A9")
    private static final float BUTTON_TEXT_SIZE = 24f;

    // 按钮配置类
    public static class ButtonConfig {
        String text;
        View.OnClickListener onClickListener;
        int textColor;
        int backgroundColor; // 直接使用颜色值
        float textSizeSp;

        // 默认构造函数，使用默认的主要按钮样式
        public ButtonConfig(String text, View.OnClickListener onClickListener) {
            this(text, onClickListener, COLOR_WHITE, COLOR_PRIMARY_GREEN, BUTTON_TEXT_SIZE);
        }

        // 可以定制所有属性的构造函数
        public ButtonConfig(String text, View.OnClickListener onClickListener, int textColor, int backgroundColor, float textSizeSp) {
            this.text = text;
            this.onClickListener = onClickListener;
            this.textColor = textColor;
            this.backgroundColor = backgroundColor;
            this.textSizeSp = textSizeSp;
        }
    }

    public CustomAlertDialog(@NonNull Context context) {
        super(context);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_alert_dialog_layout);

        dialogRootLayout = findViewById(R.id.dialog_root_layout); // 获取根布局
        titleTextView = findViewById(R.id.dialog_title);
        messageTextView = findViewById(R.id.dialog_message);
        buttonContainer = findViewById(R.id.dialog_button_container);

        // 设置对话框主体背景
        setDialogBackground();

        if (titleText != null && !titleText.isEmpty()) {
            titleTextView.setText(titleText);
            titleTextView.setVisibility(View.VISIBLE);
        } else {
            titleTextView.setVisibility(View.GONE);
        }

        if (messageText != null && !messageText.isEmpty()) {
            messageTextView.setText(messageText);
            messageTextView.setVisibility(View.VISIBLE);
        } else {
            messageTextView.setVisibility(View.GONE);
        }

        addButtons();

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
        }
    }

    // 设置对话框圆角背景
    private void setDialogBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(COLOR_WHITE); // 白色背景
        drawable.setCornerRadius(dpToPx(getContext(), 16)); // 16dp 圆角
        dialogRootLayout.setBackground(drawable);
    }

    // 创建按钮背景 Drawable
    private GradientDrawable createButtonBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(dpToPx(getContext(), 8)); // 8dp 圆角
        return drawable;
    }

    public CustomAlertDialog setTitle(String title) {
        this.titleText = title;
        return this;
    }

    public CustomAlertDialog setMessage(String message) {
        this.messageText = message;
        return this;
    }

    // 默认按钮样式 (主色调按钮)
    public CustomAlertDialog addButton(String text, View.OnClickListener onClickListener) {
        buttonConfigs.add(new ButtonConfig(text, onClickListener, COLOR_WHITE, COLOR_PRIMARY_GREEN, BUTTON_TEXT_SIZE));
        return this;
    }

    // 定制文字颜色和背景颜色的按钮
    public CustomAlertDialog addButton(String text, View.OnClickListener onClickListener, int textColor, int backgroundColor) {
        buttonConfigs.add(new ButtonConfig(text, onClickListener, textColor, backgroundColor, BUTTON_TEXT_SIZE));
        return this;
    }

    // 定制所有属性的按钮
    public CustomAlertDialog addButton(String text, View.OnClickListener onClickListener, int textColor, int backgroundColor, float textSizeSp) {
        buttonConfigs.add(new ButtonConfig(text, onClickListener, textColor, backgroundColor, textSizeSp));
        return this;
    }

    private void addButtons() {
        buttonContainer.removeAllViews();

        if (buttonConfigs.isEmpty()) {
            buttonContainer.setVisibility(View.GONE);
            return;
        } else {
            buttonContainer.setVisibility(View.VISIBLE);
        }

        LinearLayout.LayoutParams layoutParams;
        int buttonMargin = dpToPx(getContext(), 18); // 8dp 的按钮间距

        for (int i = 0; i < buttonConfigs.size(); i++) {
            ButtonConfig config = buttonConfigs.get(i);
            Button button = new Button(getContext());
            button.setText(config.text);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.textSizeSp);
            button.setTextColor(config.textColor);
            button.setBackground(createButtonBackground(config.backgroundColor)); // **使用代码创建的背景**

            // 统一设置按钮内边距
            int verticalPadding = dpToPx(getContext(), 2);
            int horizontalPadding = dpToPx(getContext(), 2);
            button.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);

            if (buttonConfigs.size() == 1) {
                layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            } else {
                layoutParams = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1.0f);
            }

            if (buttonConfigs.size() > 1) {
                if (i == 0) {
                    layoutParams.setMarginEnd(buttonMargin / 2);
                } else if (i == buttonConfigs.size() - 1) {
                    layoutParams.setMarginStart(buttonMargin / 2);
                } else {
                    layoutParams.setMarginStart(buttonMargin / 2);
                    layoutParams.setMarginEnd(buttonMargin / 2);
                }
            }
            button.setLayoutParams(layoutParams);

            button.setOnClickListener(v -> {
                if (config.onClickListener != null) {
                    config.onClickListener.onClick(v);
                }
                dismiss();
            });

            buttonContainer.addView(button);
        }
    }

    private int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}