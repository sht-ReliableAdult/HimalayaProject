package com.example.himalayaproject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.himalayaproject.Bases.BaseApplication;
import com.example.himalayaproject.Utils.LogUtils;
import com.example.himalayaproject.api.DbHelper;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.registReference(this);
        //如果有其他入口，可根据入口以及附带信息判断链接向哪里，或者还可以在这个Ac里加载广告，放广告
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        exportLogInfo("Splash", "onCreate------------------------------------------------------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void exportLogInfo(String tag, String s) {
        LogUtils.d(tag, s);
    }

}
