package com.example.himalayaproject.Interfaces;

import com.example.himalayaproject.Bases.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends IBasePresenter<IPlayerViewCallback> {
    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 上一集
     */
    void playPre();

    /**
     * 下一集
     */
    void playNext();
    /**
     * 拖动进度条切换进度
     */
    void seekTo(int progress);

    /**
     * 是否处于播放状态
     * @return
     */
    boolean isPlaying();

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 播放指定索引处节目
     */
    void playByIndex(int position);
    /**
     * 切换播放模式
     */
    void swichPlayMode(XmPlayListControl.PlayMode playMode);

    /**
     * 是否有播放列表
     * @return
     */
    boolean hasPlayList();
    /**
     * 根据大专辑ID播放第一个音频
     */
    void playByAlbumId(long id);
}
