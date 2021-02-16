package com.example.himalayaproject.Interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ICollectionCallback {
    void onAddResult(Boolean isSuccess);
    void onDelResult(Boolean isSuccess);
    void onCollectionLoaded(List<Album> albums);
}
