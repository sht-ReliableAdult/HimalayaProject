package com.example.himalayaproject.api;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(@Nullable Context context) {
        super(context, "db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlLine = "create table collectionTb" + "(" +
                "_id integer primary key autoincrement," +
                "coverUrl varchar," +
                "title varchar," +
                "description varchar," +
                "playCount integer," +
                "tracksCount integer," +
                "authorName varchar," +
                "albumId integer" + ")";
        db.execSQL(sqlLine);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
