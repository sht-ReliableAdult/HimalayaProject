package com.example.himalayaproject.Interfaces;

import com.example.himalayaproject.Bases.IBasePresenter;

/**
 * 这里是定义推荐页的逻辑动作的接口-逻辑执行者的动作
 */
public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback>{
    /**
     * 获取推荐内容
     */
    void getRecommendData();
    /**
     * 下拉刷新功能
     */
    void pull2Refresh();
    /**
     * 上拉加载更多
     */
    void loadMore();
    //注：此处注册取消注册的方法已抽取到IBasePresenter，可以删去
    /**
     * 用于注册UI的回调
     * @param callback
     */
    void registViewCallback(IRecommendViewCallback callback);

    /**
     * 用于取消注册UI的回调，防内存泄漏
     * @param callback
     */
    void unRegistViewCallback(IRecommendViewCallback callback);
}
