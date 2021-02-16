package com.example.himalayaproject.api;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubDao {
    void addAlbum(Album album);
    void delAlbum(Album album);
    void listAlbums();
    void setCallback(ICollectionDaoCallback callback);
}
