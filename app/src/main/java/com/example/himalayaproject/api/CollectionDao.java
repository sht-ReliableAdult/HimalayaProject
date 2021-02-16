package com.example.himalayaproject.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.himalayaproject.Bases.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class CollectionDao implements ISubDao{
    private static CollectionDao sInstance;
    private final DbHelper mHelper;
    private ICollectionDaoCallback mCallback;

    public static CollectionDao getInstance(){
        if (sInstance == null) {
            synchronized (CollectionDao.class) {
                if (sInstance == null) {
                    sInstance = new CollectionDao();
                }
            }
        }
        return sInstance;
    }

    private CollectionDao() {
        mHelper = new DbHelper(BaseApplication.getAppContext());
    }

    @Override
    public void addAlbum(Album album) {
//        SQLiteDatabase db = null;
//        try{
//            db = mHelper.getWritableDatabase();
//            db.beginTransaction();
//            ContentValues entrys = new ContentValues();
//            entrys.put("coverUrl", album.getCoverUrlLarge());
//            entrys.put("title", album.getAlbumTitle());
//            entrys.put("description", album.getAlbumIntro());
//            entrys.put("playCount", album.getPlayCount());
//            entrys.put("tracksCount", album.getIncludeTrackCount());
//            entrys.put("authorName", album.getAnnouncer().getNickname());
//            entrys.put("albumId", album.getId());
//            db.insert("collectionTb", null, entrys);
//            db.setTransactionSuccessful();
//            if (mCallback != null) {
//                mCallback.onAddResult(true);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            if (mCallback != null) {
//                mCallback.onAddResult(false);
//            }
//        }finally {
//            if (db != null) {
//                db.endTransaction();
//                db.close();
//            }
//        }
    }

    @Override
    public void delAlbum(Album album) {
        SQLiteDatabase db = null;
        try{
            db = mHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete("collectionTb", "_id=? ", new String[]{album.getId() + ""});
            db.setTransactionSuccessful();
            if (mCallback != null) {
                mCallback.onDelResult(true);
            }
        }catch (Exception e) {
            e.printStackTrace();
            if (mCallback != null) {
                mCallback.onDelResult(false);
            }
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

    }

    @Override
    public void listAlbums() {
//        SQLiteDatabase db = null;
//        List<Album> albums = new ArrayList<>();
//        try{
//            db = mHelper.getReadableDatabase();
//            Cursor query = db.query("collectionTb", null, null, null, null, null, null, null);
//            while(query.moveToNext()) {
//                Album album = new Album();
//                album.setCoverUrlLarge(query.getString(query.getColumnIndex("coverUrl")));
//                album.setAlbumTitle(query.getString(query.getColumnIndex("title")));
//                album.setAlbumIntro(query.getString(query.getColumnIndex("description")));
//                album.setPlayCount(query.getInt(query.getColumnIndex("playCount")));
//                album.setIncludeTrackCount(query.getInt(query.getColumnIndex("tracksCount")));
//                album.setId(query.getInt(query.getColumnIndex("albumId")));
//                String authorName = query.getString(query.getColumnIndex("authorName"));
//                Announcer announcer = new Announcer();
//                announcer.setNickname(authorName);
//                album.setAnnouncer(announcer);
//                albums.add(album);
//            }
//            query.close();
//            if (mCallback != null) {
//                mCallback.onCollectionListLoaded(albums);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            if (db != null) {
//                db.close();
//            }
//        }
    }

    @Override
    public void setCallback(ICollectionDaoCallback callback) {
        this.mCallback = callback;
    }
}
