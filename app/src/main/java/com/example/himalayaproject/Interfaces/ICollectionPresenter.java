package com.example.himalayaproject.Interfaces;

import android.provider.MediaStore;

import com.example.himalayaproject.Bases.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ICollectionPresenter extends IBasePresenter<ICollectionCallback> {

    void addCollection(Album album);
    void delCollection(Album album);
    void getCollectionList();
}
