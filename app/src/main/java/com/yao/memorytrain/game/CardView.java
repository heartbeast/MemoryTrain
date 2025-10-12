package com.yao.memorytrain.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import static com.yao.memorytrain.Utils.Logd;

import androidx.appcompat.widget.AppCompatImageView;

import com.yao.memorytrain.R;

// 自定义CardView，继承自AppCompatImageView，封装了翻牌动画
public class CardView extends AppCompatImageView {
    private int cardId; // 用于判断是否匹配的唯一ID
    private int frontImageResId;
    private int backImageResId;
    private boolean isFrontShowing = false; // 当前是否显示正面
    private volatile boolean isAnimate = false;    // 是否正在动画中

    private ObjectAnimator flipOutAnimator;
    private ObjectAnimator flipInAnimator;
    private ObjectAnimator vanishAnimator; // 消失动画

    public CardView(Context context) {
        super(context);
        init();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 监听器接口，用于通知外部动画完成
    public interface CardAnimationListener {
        void onFlipAnimationEnd(CardView cardView);
        void onFlipBackAnimationEnd(CardView cardView);
        void onVanishAnimationEnd(CardView cardView);
    }

    private CardAnimationListener animationListener;

    public void setCardAnimationListener(CardAnimationListener listener) {
        this.animationListener = listener;
    }

    private void init() {
        // 初始化翻牌动画
        setupFlipAnimations();
        // 初始化消失动画
        setupVanishAnimation();
        // 默认显示背面
        setImageResource(R.drawable.card_back); // 确保你有这个资源
    }

    public void setCard(int cardId, int frontImageResId, int backImageResId) {
//        Logd("CardView.setCard: cardId=" + cardId + ", frontResId="+frontImageResId+", backResId="+backImageResId);
        this.cardId = cardId;
        this.frontImageResId = frontImageResId;
        this.backImageResId = backImageResId;
        this.isFrontShowing = false; // 初始化为背面
        this.isAnimate = false;
        // 确保视图在设置新卡片时重置状态
        this.setScaleX(1f);
        this.setScaleY(1f);
        this.setAlpha(1f);
        this.setRotationY(0f);
        setImageResource(backImageResId);
        setVisibility(VISIBLE);
    }

    private void setupFlipAnimations() {
        flipOutAnimator = ObjectAnimator.ofFloat(this, "rotationY", 0f, 90f);
        flipOutAnimator.setDuration(200);
        flipOutAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        flipOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                Logd("onFlipAnimationStart...");
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isFrontShowing = !isFrontShowing;
                setImageResource(isFrontShowing ? frontImageResId : backImageResId);
                flipInAnimator.start();
            }
        });

        flipInAnimator = ObjectAnimator.ofFloat(this, "rotationY", -90f, 0f);
        flipInAnimator.setDuration(200);
        flipInAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        flipInAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Logd("onFlipAnimationEnd...");
                isAnimate = false;
                if (isFrontShowing) {
                    setEnabled(false); // 翻开后禁止点击
                    animationListener.onFlipAnimationEnd(CardView.this);
                } else {
                    setEnabled(true); // 翻回后恢复可点击翻开后禁止点击
                    animationListener.onFlipBackAnimationEnd(CardView.this);
                }
            }
        });
    }

    private void setupVanishAnimation() {
        vanishAnimator = ObjectAnimator.ofPropertyValuesHolder(
                this,
                PropertyValuesHolder.ofFloat( View.ROTATION, 0f, 360f),
                PropertyValuesHolder.ofFloat("scaleX", 1f, 0f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 0f),
                PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        );
        vanishAnimator.setDuration(800);
        vanishAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                Logd("onAnimationStart...");
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Logd("onAnimationEnd...");
                isAnimate = false;
                setVisibility(INVISIBLE); // 动画结束后隐藏
                if (animationListener != null) {
                    animationListener.onVanishAnimationEnd(CardView.this);
                }
            }
        });
    }

    public void flipCard() {
        if (isAnimate || isFrontShowing) {
            return;
        }
        flipOutAnimator.setFloatValues(0f, 90f);
        flipInAnimator.setFloatValues(-90f, 0f);
        flipOutAnimator.start();
    }

    // 翻回背面
    public void flipBack() {
        if (isAnimate || !isFrontShowing) {
            return;
        }
        flipOutAnimator.setFloatValues(0f, -90f);
        flipInAnimator.setFloatValues(90f, 0f);
        flipOutAnimator.start();
    }

    public void vanishCard() {
        if (isAnimate) {
            return;
        }
        setEnabled(false); // 不再响应点击
        vanishAnimator.start();
    }

    // Getters
    public int getCardId() {
        return cardId;
    }

    public boolean isFront() {
        return isFrontShowing;
    }

    public boolean isAnimating() {
        return isAnimate;
    }
}