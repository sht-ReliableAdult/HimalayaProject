package com.example.himalayaproject.Presenters;

import com.example.himalayaproject.Interfaces.IRecommendPresenter;
import com.example.himalayaproject.Interfaces.IRecommendViewCallback;
import com.example.himalayaproject.Utils.Constant;
import com.example.himalayaproject.Utils.LogUtils;
import com.example.himalayaproject.api.AiTingBuTingApi;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这里是推荐页的逻辑层执行者，来执行整理后端请求后的数据-逻辑执行者本人
 * 在这里把动作与执行者组装起来并实现
 */
public class RecommendPresenter implements IRecommendPresenter {

    private String TAG = "RecommendPresenter";
    //可能有多个类似Fragment使用这个presenter，所以用集合装回调方便都通知回调,里面的callback实现类是Fragment
    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private static RecommendPresenter sInstance;
    private AiTingBuTingApi mAiTingBuTingApi;
    private List<Album> mRecommendList;

    private RecommendPresenter(){}
    /**
     * 单例双重检查锁
     */
    public static RecommendPresenter getInstance(){
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }
    @Override
    public void getRecommendData() {
        handleLoadingDisplay();
        mAiTingBuTingApi = AiTingBuTingApi.getInstance();
        mAiTingBuTingApi.getRecommendData(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //获取成功的回调
                if (gussLikeAlbumList != null) {
                    //获取数据
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //通知UI更新
                    handleRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //获取出错的回调
                handleNetworkError();
            }
        });
    }

    private void handleLoadingDisplay() {
        for (IRecommendViewCallback mCallback : mCallbacks) {
            mCallback.onLoading();
        }
    }

    private void handleNetworkError() {//通知各Fragment回调网络失败
        for (IRecommendViewCallback mCallback : mCallbacks) {
            mCallback.onNetworkError();
        }
    }

    /**
     * 数据为空通知空回调，不为空通知加载数据成功的回调
     * @param albumList
     */
    private void handleRecommendResult(List<Album> albumList) {
        if (albumList.size() == 0) {
            for (IRecommendViewCallback mCallback : mCallbacks) {
                mCallback.onEmpty();
            }
        } else {
            this.mRecommendList = albumList;
            for (IRecommendViewCallback mCallback : mCallbacks) {
                mCallback.onRecommendDataLoaded(albumList);
            }
        }
    }

    /**
     * 获取当前推荐内容list
     * @return
     */
    public List<Album> getRecommendList() {
        return mRecommendList;
    }

    /**
     * 加载更多和下拉刷新，喜马拉雅接口在推荐页请求不了更多数据，所以无法实现
     */
    @Override
    public void pull2Refresh() {

    }

    @Override
    public void loadMore() {

    }

    /*@Override
    public void onRecommendDataLoaded(List<Album> result) {

    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onRefresh(List<Album> result) {

    }

    @Override
    public void onLoadedMore(List<Album> result) {

    }
     */

    @Override
    public void registViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegistViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
