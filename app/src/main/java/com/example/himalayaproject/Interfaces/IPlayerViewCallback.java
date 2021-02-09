package com.example.himalayaproject.Interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerViewCallback {
    /**
     * 开始播放
     */
    void onPlayStart();
    /**
     * 播放暂停
     */
    void onPlayPause();
    /**
     * 播放停止
     */
    void onPlayStop();
    /**
     * 播放错误
     */
    void onPlayError();
    /**
     * 下一集
     */
    void onPlayNext(Track track);
    /**
     * 上一集
     */
    void onPlayPre(Track track);
    /**
     * 播放进度切换
     */
    void onProgressChange(long currentProgress, long total);
    /**
     * 广告加载
     */
    void onAdLoading();
    /**
     * 广告时间结束
     */
    void onAdFinished();

    /**
     * 切换播放音频,更新标题,pager
     */
    void onTrackUpdate(Track track, int position);

    /**
     * 播放列表加载回传
     */
    void onListLoaded(List<Track> playList);

    /**
     * 播放模式改变回调
     */
    void onPlayModeChanged(XmPlayListControl.PlayMode playMode);
}
