package com.example.himalayaproject.Utils;

import com.example.himalayaproject.Bases.BaseFragment;
import com.example.himalayaproject.Fragments.CollectionFragment;
import com.example.himalayaproject.Fragments.HistoryFragment;
import com.example.himalayaproject.Fragments.RecommendFragment;
import com.example.himalayaproject.Fragments.RecommendFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentRecycler {//工厂模式？fragment工厂？
    public final static int PAGE_COUNT = 3;
    public final static int RECOMMEND_INDEX = 0;
    public final static int COLLECTION_INDEX = 1;
    public final static int HISTORY_INDEX = 2;

    private static Map<Integer, BaseFragment> fragmentCache = new HashMap<>();

    public static BaseFragment getFragment(int index) {
        BaseFragment baseFragment = fragmentCache.get(index);
        if (baseFragment != null) {
            return baseFragment;
        }
        switch (index) {
            case RECOMMEND_INDEX://注意swich里case后必须使用常量，所以得final
                baseFragment = new RecommendFragment();
                break;
            case COLLECTION_INDEX:
                baseFragment = new CollectionFragment();
                break;
            case HISTORY_INDEX:
                baseFragment = new HistoryFragment();
                break;
        }
        fragmentCache.put(index, baseFragment);
        return baseFragment;
    }
}
