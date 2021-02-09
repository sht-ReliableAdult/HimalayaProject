package com.example.himalayaproject.Presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.himalayaproject.Bases.BaseApplication;
import com.example.himalayaproject.Interfaces.IPlayerPresenter;
import com.example.himalayaproject.Interfaces.IPlayerViewCallback;
import com.example.himalayaproject.Utils.LogUtils;
import com.example.himalayaproject.api.AiTingBuTingApi;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static PlayerPresenter sPlayerPresenter;
    private XmPlayerManager mXmPlayerManager;
    private String TAG = "PlayerPresenter";
    private List<IPlayerViewCallback> mCallbacks = new ArrayList<>();
    private Track mCurTrack;
    private int mCurPosition;
    private final SharedPreferences mSp;
    private XmPlayListControl.PlayMode mCurMode = PLAY_MODEL_LIST;

    public static final int PLAY_MODE_INT_LIST = 0;
    public static final int PLAY_MODE_INT_SINGLE_LIST = 1;
    public static final int PLAY_MODE_INT_SINGLE = 2;
    private int mCurProgressPosition;
    private int mProgressDuration;

    private PlayerPresenter() {
        mXmPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        mXmPlayerManager.addPlayerStatusListener(this);
        //注册广告相关监听
        mXmPlayerManager.addAdsStatusListener(this);
        mSp = BaseApplication.getAppContext().getSharedPreferences("PlayMode", Context.MODE_PRIVATE);
    }

    public static PlayerPresenter getInstance(){
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    /**
     * 设置播放器的播放列表
     * @param list
     * @param playIndex
     */
    private boolean isPlayListSetted;
    public void setPlayList(List<Track> list, int playIndex) {
        if (mXmPlayerManager != null) {
            mXmPlayerManager.setPlayList(list, playIndex);
            isPlayListSetted = true;
            exportLogInfo(TAG, "设置播放列表成功" + list.size());
            //获取第一个音频
            mCurTrack = list.get(playIndex);
            mCurPosition = playIndex;
        }else {
            exportLogInfo(TAG, "设置播放列表出错 --> mXmplayerManager为空");
        }
    }

    private void exportLogInfo(String tag, String s) {
        LogUtils.d(tag, s);
    }

    @Override
    public void play() {
        if (isPlayListSetted) {
            exportLogInfo(TAG, "播放成功");
            mXmPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (isPlayListSetted) {
            exportLogInfo(TAG, "播放暂停");
            mXmPlayerManager.pause();
        }
    }

    @Override
    public void playPre() {
        if (mXmPlayerManager != null) {
            mXmPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (mXmPlayerManager != null) {
            mXmPlayerManager.playNext();
        }
    }

    @Override
    public void seekTo(int progress) {
        //更新播放进度到指定位置，拖动进度条时在这同步音频进度
        mXmPlayerManager.seekTo(progress);
    }

    /**
     * 返回当前是否正在播放
     * @return
     */
    @Override
    public boolean isPlaying() {
        return mXmPlayerManager.isPlaying();
    }

    /**
     * 获取播放任务列表
     */
    @Override
    public void getPlayList() {
        if (mXmPlayerManager != null) {
            List<Track> playList = mXmPlayerManager.getPlayList();
            for (IPlayerViewCallback callback : mCallbacks) {
                callback.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int position) {
        if (mXmPlayerManager != null) {
            mXmPlayerManager.play(position);
        }
    }

    /**
     * 切换播放模式
     * @param playMode
     */
    @Override
    public void swichPlayMode(XmPlayListControl.PlayMode playMode) {
        if (mXmPlayerManager != null) {
            mCurMode = playMode;
            mXmPlayerManager.setPlayMode(playMode);
            exportLogInfo(TAG, "切换为" + playMode);
        }
        for (IPlayerViewCallback callback : mCallbacks) {
            callback.onPlayModeChanged(playMode);
        }
        //保存本次设置的播放模式到sp里去
        SharedPreferences.Editor edit = mSp.edit();
        edit.putInt("PlayMode", getIntByPlayMode(playMode));
        edit.commit();
    }

    /**
     * 播放列表是否已有内容
     * @return
     */
    @Override
    public boolean hasPlayList() {
        return isPlayListSetted;
    }

    /**
     * 只提供专辑Id，默认播放第一个音频的方法
     * @param id
     */
    @Override
    public void playByAlbumId(long id) {
        AiTingBuTingApi.getInstance().getDetailData(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> tracks = trackList.getTracks();
                if (tracks != null && tracks.size() != 0) {
                    mXmPlayerManager.setPlayList(tracks, 0);
                    isPlayListSetted = true;
                    exportLogInfo(TAG, "设置播放列表成功" + tracks.size());
                    //获取第一个音频
                    mCurTrack = tracks.get(0);
                    mCurPosition = 0;
                }
                Toast.makeText(BaseApplication.getAppContext(),"默认为您播放第一条内容！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(),"播放失败，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        }, (int) id, 1);
    }

    /**
     * 把播放模式转换为int代号
     * @param playMode
     * @return
     */
    private int getIntByPlayMode(XmPlayListControl.PlayMode playMode) {
        switch (playMode) {
            case PLAY_MODEL_LIST:
                return PLAY_MODE_INT_LIST;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODE_INT_SINGLE_LIST;
            case PLAY_MODEL_SINGLE_LOOP:
                return  PLAY_MODE_INT_SINGLE;
        }
        return PLAY_MODE_INT_LIST;
    }

    /**
     * 把int代号转为播放模式
     * @param intMode
     * @return
     */
    private XmPlayListControl.PlayMode getModeByInt(int intMode) {
        switch (intMode) {
            case PLAY_MODE_INT_LIST:
                return PLAY_MODEL_LIST;
            case PLAY_MODE_INT_SINGLE_LIST:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODE_INT_SINGLE:
                return  PLAY_MODEL_SINGLE_LOOP;
        }
        return PLAY_MODEL_LIST;
    }

    /**
     * 获取当前的音频
     * @return
     */
    public Track getCurTrack() {
        return mCurTrack;
    }

    /**
     * Ac调用在Presenter里注册自己，方便回调通知
     * @param iPlayerViewCallback
     */
    @Override
    public void registViewCallback(IPlayerViewCallback iPlayerViewCallback) {
        //这里顺便把默认播放第一个音频的title与pager变量设置了
        iPlayerViewCallback.onTrackUpdate(mCurTrack, mCurPosition);
        //更新状态,使各种播放数据在页面间同步
        iPlayerViewCallback.onProgressChange(mCurProgressPosition, mProgressDuration);
        handlePlayStatus(iPlayerViewCallback);
        //从sp里拿保存的播放状态
        int modeInt = mSp.getInt("PlayMode", PLAY_MODE_INT_LIST);
        mCurMode = getModeByInt(modeInt);
        iPlayerViewCallback.onPlayModeChanged(mCurMode);
        if (!mCallbacks.contains(iPlayerViewCallback)) {
            mCallbacks.add(iPlayerViewCallback);
        }
    }

    /**
     * 播放状态变化通知回调
     * @param iPlayerViewCallback
     */
    private void handlePlayStatus(IPlayerViewCallback iPlayerViewCallback) {
        int playerStatus = mXmPlayerManager.getPlayerStatus();
        if (PlayerConstants.STATE_STARTED == playerStatus) {
            iPlayerViewCallback.onPlayStart();
        }else{
            iPlayerViewCallback.onPlayPause();
        }
    }

    @Override
    public void unRegistViewCallback(IPlayerViewCallback iPlayerViewCallback) {
        mCallbacks.remove(iPlayerViewCallback);
    }

    //===================播放器播放正文相关回调接口 start===================
    /**
     * 喜马拉雅播放器回调接口，开始播放正文
     */
    @Override
    public void onPlayStart() {
        useSysLogPlugin();
        for (IPlayerViewCallback callback : mCallbacks) {
            callback.onPlayStart();
        }
    }
    /**
     * 喜马拉雅播放器回调接口，暂停播放
     */
    @Override
    public void onPlayPause() {
        for (IPlayerViewCallback callback : mCallbacks) {
            callback.onPlayPause();
        }
    }
    /**
     * 喜马拉雅播放器回调接口，播放停止
     */
    @Override
    public void onPlayStop() {
        for (IPlayerViewCallback callback : mCallbacks) {
            callback.onPlayStop();
        }
    }
    /**
     * 喜马拉雅播放器回调接口，播放完成
     */
    @Override
    public void onSoundPlayComplete() {

    }
    /**
     * 喜马拉雅播放器回调接口，音频准备完毕
     */
    @Override
    public void onSoundPrepared() {
        mXmPlayerManager.setPlayMode(mCurMode);
        if (mXmPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            mXmPlayerManager.play();
        }
    }
    /**
     * 喜马拉雅播放器回调接口，切换分集内容，model1代表当前播放内容(track等) modle代表前一个播放内容
     */
    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
        if (playableModel != null) {
            //todo
        }
        //这里一直更新当前播放的音频在播放列表里的索引
        mCurPosition = mXmPlayerManager.getCurrentIndex();
        if (playableModel1 instanceof Track) {
            Track curTrack = (Track) playableModel1;
            //这里一直在更新当前音频
            mCurTrack = curTrack;
            for (IPlayerViewCallback callback : mCallbacks) {
                callback.onTrackUpdate(mCurTrack, mCurPosition);
            }
        }
    }
    /**
     * 喜马拉雅播放器回调接口，缓冲开始
     */
    @Override
    public void onBufferingStart() {

    }
    /**
     * 喜马拉雅播放器回调接口，缓冲停止
     */
    @Override
    public void onBufferingStop() {

    }
    /**
     * 喜马拉雅播放器回调接口，缓冲进度
     */
    @Override
    public void onBufferProgress(int i) {

    }

    /**
     * 喜马拉雅播放器回调接口，当前播放进度
     */
    @Override
    public void onPlayProgress(int curPos, int duration) {
        this.mCurProgressPosition = curPos;
        this.mProgressDuration = duration;
        for (IPlayerViewCallback callback : mCallbacks) {
            callback.onProgressChange(curPos, duration);
        }

    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }


    private void useSysLogPlugin() {
        try {
            Class<?> clazz = Class.forName("com.example.pluginmodule.SysLogTool");
            Method print = clazz.getMethod("print");
            print.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //=======================播放器播放正文相关接口 end======================







    //=======================广告相关回调 start=============================

    /**
     * 喜马拉雅播放器回调接口，开始获取广告
     */
    @Override
    public void onStartGetAdsInfo() {

    }
    /**
     * 喜马拉雅播放器回调接口，获取广告成功
     */
    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {

    }
    /**
     * 喜马拉雅播放器回调接口，广告开始缓冲
     */
    @Override
    public void onAdsStartBuffering() {

    }
    /**
     * 喜马拉雅播放器回调接口，广告结束缓冲
     */
    @Override
    public void onAdsStopBuffering() {

    }
    /**
     * 喜马拉雅播放器回调接口，广告开始播放
     */
    @Override
    public void onStartPlayAds(Advertis advertis, int i) {

    }
    /**
     * 喜马拉雅播放器回调接口，广告播放完成
     */
    @Override
    public void onCompletePlayAds() {

    }
    /**
     * 喜马拉雅播放器回调接口，广告出错
     */
    @Override
    public void onError(int i, int i1) {

    }

    //=======================广告相关回调 end=============================
}
