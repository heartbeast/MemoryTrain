package com.yao.memorytrain.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] mNumbers; // 存储数字，null表示没有数字
    private boolean mShowNumbers; // 是否显示数字

    public GridAdapter(Context context, Integer[] numbers, boolean showNumbers) {
        mContext = context;
        mNumbers = numbers;
        mShowNumbers = showNumbers;
    }

    @Override
    public int getCount() {
        return mNumbers.length;
    }

    @Override
    public Object getItem(int position) {
        return mNumbers[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder holder = null;

//        if (convertView == null) {
//            // 加载布局
//            view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
//            holder = new ViewHolder();
//            holder.numberTextView = view.findViewById(R.id.numberTextView);
//            view.setTag(holder);
//        } else {
//            view = convertView;
//            holder = (ViewHolder) view.getTag();
//        }

        // 设置数字显示
        Integer number = mNumbers[position];
        if (mShowNumbers && number != null) {
            holder.numberTextView.setVisibility(View.VISIBLE);
            holder.numberTextView.setText(String.valueOf(number));
        } else {
            holder.numberTextView.setVisibility(View.GONE);
        }

        return view;
    }

    // 更新是否显示数字
    public void setShowNumbers(boolean showNumbers) {
        mShowNumbers = showNumbers;
        notifyDataSetChanged();
    }

    // 内部类用于缓存视图
    static class ViewHolder {
        TextView numberTextView;
    }
}
