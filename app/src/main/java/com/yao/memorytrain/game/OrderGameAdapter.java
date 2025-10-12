package com.yao.memorytrain.game;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class OrderGameAdapter extends BaseAdapter {
    private Context context;
    private int totalItems;
    private List<Integer> numbers;
    private List<Integer> positions;
    private boolean numbersHidden = false;

    public OrderGameAdapter(Context context, int totalItems, List<Integer> numbers, List<Integer> positions) {
        this.context = context;
        this.totalItems = totalItems;
        this.numbers = numbers;
        this.positions = positions;
    }

    @Override
    public int getCount() {
        return totalItems;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(context);
            textView.setLayoutParams(new android.widget.GridView.LayoutParams(
                    android.widget.GridView.LayoutParams.MATCH_PARENT,
                    120)); // 固定高度
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(24);
            textView.setTextColor(Color.WHITE);
        } else {
            textView = (TextView) convertView;
        }

        // 设置背景颜色
        if (numbersHidden) {
            textView.setBackgroundColor(Color.parseColor("#2196F3")); // 蓝色
            textView.setText("");
        } else {
            // 检查当前位置是否包含数字
            int numberIndex = positions.indexOf(position);
            if (numberIndex != -1) {
                textView.setBackgroundColor(Color.parseColor("#FF9800")); // 橙色
                textView.setText(numbers.get(numberIndex).toString());
            } else {
                textView.setBackgroundColor(Color.parseColor("#2196F3")); // 蓝色
                textView.setText("");
            }
        }

        return textView;
    }

    public void hideNumbers() {
        numbersHidden = true;
        notifyDataSetChanged();
    }

    public int getNumberAtPosition(int position) {
        int numberIndex = positions.indexOf(position);
        if (numberIndex != -1) {
            return numbers.get(numberIndex);
        }
        return -1;
    }
}