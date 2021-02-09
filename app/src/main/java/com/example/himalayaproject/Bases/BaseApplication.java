package com.example.himalayaproject.Bases;

import android.app.Application;
import android.app.VoiceInteractor;
import android.content.Context;
import android.os.Handler;

import com.example.himalayaproject.Utils.LoadUtil;
import com.example.himalayaproject.Utils.LogUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.lang.reflect.Method;


public class BaseApplication extends Application {
    public static Handler mHandler;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        LoadUtil.loadPluginClass(this);
        mHandler = new Handler();
        //与后端通讯
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if(DTransferConstants.isRelease) {
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
            mXimalaya.init(this ,mAppSecret);
        } else {
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this ,mAppSecret);
        }
        mContext = getBaseContext();
        //初始化播放器服务进程
        XmPlayerManager.getInstance(this).init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        XmPlayerManager.release();
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static Context getAppContext(){
        return mContext;
    }


}

