package com.example.himalayaproject.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.himalayaproject.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * 适配indicator标题栏的内容
 */
public class IndicatorAdapter extends CommonNavigatorAdapter {//适配Incicater与ViewPager

    private final String[] mTitles;
    private OnIndicatorTapClickListener mOnTapClickListener;

    public IndicatorAdapter(Context context) {
        mTitles = context.getResources().getStringArray(R.array.indicator_name);
    }

    @Override
    public int getCount() {
        if (mTitles != null){
            return mTitles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        //创建View
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置未选中情况的颜色
        simplePagerTitleView.setNormalColor(Color.GRAY);
        //设置选中时颜色
        simplePagerTitleView.setSelectedColor(Color.WHITE);
        //设置显示内容
        simplePagerTitleView.setTextSize(18);
        simplePagerTitleView.setText(mTitles[index]);
        //设置不同title的点击事件，跳转对应viewpager
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//把点击事件的监听器包一层接口给外部
                if (mOnTapClickListener != null) {
                    mOnTapClickListener.onTabClick(index);
                }
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }
    //暴露给外部，设置点击监听
    public void setOnIndicatorTapClickListener(OnIndicatorTapClickListener listener) {
        mOnTapClickListener = listener;
    }
    //对外暴露的listener
    public interface OnIndicatorTapClickListener{
        void onTabClick(int index);
    }
}
