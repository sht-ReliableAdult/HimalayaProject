package com.example.himalayaproject.Presenters;

import com.example.himalayaproject.Interfaces.IDetailPresenter;
import com.example.himalayaproject.Interfaces.IDetailViewCallback;
import com.example.himalayaproject.Utils.LogUtils;
import com.example.himalayaproject.api.AiTingBuTingApi;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专辑的详情页逻辑执行者
 */
public class DetailPresenter implements IDetailPresenter {
    private static String TAG = "DetailPresenter";
    private static DetailPresenter sInstance;
    private Album mAlbum;
    private List<IDetailViewCallback> mCallbacks = new ArrayList<>();
    private AiTingBuTingApi mAiTingBuTingApi;

    private DetailPresenter() {
    }

    public static DetailPresenter getInstance(){
        if (sInstance == null) {
            synchronized (DetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new DetailPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 根据专辑和页码
     * @param albumId
     * @param page
     */
    @Override
    public void getDetailData(long albumId, int page) {
        mAiTingBuTingApi = AiTingBuTingApi.getInstance();
        mAiTingBuTingApi.getDetailData(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    exportLogInfo(TAG, tracks.size() + "条");
                    handleAlbumDetailResult(tracks);
                }
            }
            private void exportLogInfo(String tag, String s) {
                LogUtils.d(tag, s);
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                exportLogInfo(TAG, errorCode + "" + errorMsg + "");
                handleNetworkError();
            }
        }, albumId, page);
    }

    private void handleNetworkError() {
        if (mCallbacks != null) {
            for (IDetailViewCallback mCallback : mCallbacks) {
                mCallback.onNetworkError();
            }
        }
    }

    private void handleAlbumDetailResult(List<Track> tracks) {
        for (IDetailViewCallback mCallback : mCallbacks) {
            mCallback.onDetailLoaded(tracks);
        }
    }

    @Override
    public void registViewCallback(IDetailViewCallback implAc) {
        if (!mCallbacks.contains(implAc)) {
            mCallbacks.add(implAc);
            if (implAc != null) implAc.onAlbumLoaded(mAlbum);
        }
    }

    @Override
    public void unRegistViewCallback(IDetailViewCallback implAc) {
        if (mCallbacks.contains(implAc)) {
            mCallbacks.remove(implAc);
        }
    }

    @Override
    public void pull2Rresh() {

    }

    @Override
    public void loadMore() {

    }

    /**
     * 设置本执行者要处理的是哪个专辑
     * @param album
     */
    public void setAlbum(Album album) {
        this.mAlbum = album;
    }
    public Album getAlbum(){return mAlbum;}
}
