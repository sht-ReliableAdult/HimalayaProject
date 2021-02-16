package com.example.himalayaproject.Presenters;

import android.os.AsyncTask;

import com.example.himalayaproject.Bases.BaseApplication;
import com.example.himalayaproject.Interfaces.ICollectionCallback;
import com.example.himalayaproject.Interfaces.ICollectionPresenter;
import com.example.himalayaproject.api.CollectionDao;
import com.example.himalayaproject.api.ICollectionDaoCallback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CollectionPresenter implements ICollectionPresenter, ICollectionDaoCallback {

    private static CollectionPresenter sInstance;
    private final CollectionDao mCollectionDao;
    private HashMap<Long, Album> map = new HashMap<>();
    private List<ICollectionCallback> mCallbacks = new ArrayList<>();

    private CollectionPresenter(){
        mCollectionDao = CollectionDao.getInstance();
        mCollectionDao.setCallback(this);
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mCollectionDao.listAlbums();
                return null;
            }
        };
        task.execute();
    }

    public static CollectionPresenter getInstance() {
        if (sInstance == null) {
            synchronized (CollectionPresenter.class) {
                if (sInstance == null) {
                    sInstance = new CollectionPresenter();
                }
            }
        }
        return sInstance;
    }



    @Override
    public void addCollection(Album album) {

    }

    @Override
    public void delCollection(Album album) {

    }

    @Override
    public void getCollectionList() {

    }

    @Override
    public void registViewCallback(ICollectionCallback iCollectionCallback) {
        if (!mCallbacks.contains(iCollectionCallback)) {
            mCallbacks.add(iCollectionCallback);
        }

    }

    @Override
    public void unRegistViewCallback(ICollectionCallback iCollectionCallback) {
        if (mCallbacks.contains(iCollectionCallback)) {
            mCallbacks.remove(iCollectionCallback);
        }
    }

    @Override
    public void onAddResult(final Boolean isSucess) {
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ICollectionCallback callback : mCallbacks) {
                    callback.onAddResult(isSucess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final Boolean isSucess) {
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ICollectionCallback callback : mCallbacks) {
                    callback.onDelResult(isSucess);
                }
            }
        });

    }

    @Override
    public void onCollectionListLoaded(final List<Album> albums) {
        for (Album album : albums) {
            map.put(album.getId(), album);
        }
        //数据全部查询完读入内存，通知UI回调更新界面
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ICollectionCallback callback : mCallbacks) {
                    callback.onCollectionLoaded(albums);
                }
            }
        });
    }

    @Override
    public Boolean isCollected(Album album) {
        if (map.containsKey(album.getId())) {
            return true;
        }
        return false;
    }
}
