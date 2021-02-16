package com.example.himalayaproject.api;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ICollectionDaoCallback {
    void onAddResult(Boolean isSucess);
    void onDelResult(Boolean isSucess);
    void onCollectionListLoaded(List<Album> albums);
    Boolean isCollected(Album album);
}
