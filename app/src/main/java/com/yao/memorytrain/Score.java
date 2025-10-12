package com.yao.memorytrain;

import android.content.Context;
import android.content.SharedPreferences;

public class Score {
    private static final int FLIP_BASE_SCORE_32 = 10;  // 3*2难度的基础分
    private static final int FLIP_BASE_SCORE_43 = 40;
    private static final int FLIP_BASE_SCORE_54 = 80;
    private static final int FLIP_BASE_SCORE_65 = 200;
    private static final int FLIP_MIN_SCORE_32 = 1;  // 3*2难度过关可得到的保底分
    private static final int FLIP_MIN_SCORE_43 = 10;
    private static final int FLIP_MIN_SCORE_54 = 40;
    private static final int FLIP_MIN_SCORE_65 = 80;

    // 翻牌游戏得分计算：基础分 - 点击次数增加会导致的得分下降
    public int getFlipScore(int totalCards, int clickNum) {
        int delta = clickNum - totalCards;
        int score = 0;
        switch (totalCards) {
            case 6:
                score = FLIP_BASE_SCORE_32 - delta * delta / 4;  // 点击增加导致的分数下降更快
                if (score < FLIP_MIN_SCORE_32)
                    score = FLIP_MIN_SCORE_32;
                break;
            case 12:
                score = FLIP_BASE_SCORE_43 - delta * delta / 4;
                if (score < FLIP_MIN_SCORE_43)
                    score = FLIP_MIN_SCORE_43;
                break;
            case 20:
                score = FLIP_BASE_SCORE_54 - delta * delta / 6;  // 点击增加导致的分数下降更慢
                if (score < FLIP_MIN_SCORE_54)
                    score = FLIP_MIN_SCORE_54;
                break;
            case 30:
                score = FLIP_BASE_SCORE_65 - delta * delta / 8;  // 点击增加导致的分数下降更慢
                if (score < FLIP_MIN_SCORE_65)
                    score = FLIP_MIN_SCORE_65;
                break;
            default:
                System.out.println("Error");
        }
        return score;
    }

    // 最长数字游戏得分计算：首次：数字长度平方/2.5，同样级别每多玩一次，得分会下降（减去数字长度/3）
    public int getMaxNumScore(int maxNum) {
        int score = (int) (maxNum * maxNum / 2.5);
        int times = getMaxNumTimes();
        score = score - (maxNum/3) * times;
        return score;
    }
    private int getMaxNumTimes() {
        return 1;
    }

    private static final String PREFS_FILE_SCORE = "device_score";
    private static final String PREFS_USER = "id";
    private static final String PREFS_GAME = "type";
    private static final String PREFS_LEVEL = "type";
    private static final String PREFS_SCORE = "score";
    public void readPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_SCORE, Context.MODE_PRIVATE);
        String storedId = prefs.getString(PREFS_GAME, null);
    }
}
