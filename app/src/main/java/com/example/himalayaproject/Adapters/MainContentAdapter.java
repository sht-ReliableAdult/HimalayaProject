package com.example.himalayaproject.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.himalayaproject.Utils.FragmentRecycler;

/**
 * 适配ViewPager与Fragment
 */
public class MainContentAdapter extends FragmentPagerAdapter {//适配viewPager与Fragment
    public MainContentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public MainContentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    //ViewPager走到哪页了，就获取哪页对应的Fragment显示,这里没有把fragment们放进adapter里用list保存，
    // 而是用一个外部结构，方便以后再加新页可以直接去外部结构里加
    @Override
    public Fragment getItem(int position) {
        //todo 试试这里能否写个回调触发下收藏和历史页面灰度动态变化
        return FragmentRecycler.getFragment(position);
    }

    @Override
    public int getCount() {
        return FragmentRecycler.PAGE_COUNT;
    }
}
