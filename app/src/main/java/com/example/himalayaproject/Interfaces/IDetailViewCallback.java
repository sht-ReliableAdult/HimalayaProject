package com.example.himalayaproject.Interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IDetailViewCallback {
    /**
     * 专辑详细信息已获取
     * @param tracks
     */
    void onDetailLoaded(List<Track> tracks);

    /**
     * UI能通过这里拿到presenter正在处理的目标album，然后通过这个回调设置标题图片作者等
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 获取详情网络错误的回调
     */
    void onNetworkError();
}
