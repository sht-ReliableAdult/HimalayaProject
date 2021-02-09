package com.example.himalayaproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果有其他入口，可根据入口以及附带信息判断链接向哪里，或者还可以在这个Ac里加载广告，放广告
        useSysLogPlugin();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void useSysLogPlugin() {
        try {
            Class<?> clazz = Class.forName("com.example.pluginmodule.SysLogTool");
            Method print = clazz.getMethod("print");
            print.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
