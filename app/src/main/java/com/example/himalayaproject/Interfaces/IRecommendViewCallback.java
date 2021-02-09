package com.example.himalayaproject.Interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * 这里是定义推荐页逻辑动作结果回调动作的接口-逻辑执行者完事后回调主角做的动作
 */
public interface IRecommendViewCallback {
    /**
     * 获取推荐内容的回调
     */
    void onRecommendDataLoaded(List<Album> result);

    /**
     * 网络错误的回调
     */
    void onNetworkError();

    /**
     *请求回来的数据为空的回调
     */
    void onEmpty();

    /**
     * 加载中的回调
     */
    void onLoading();


    /**
     * 下拉刷新的回调，推荐页不实现
     */
    void onRefresh(List<Album> result);
    /**
     * 上拉加载更多的回调，推荐页不实现
     */
    void onLoadedMore(List<Album> result);



}
