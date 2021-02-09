package com.example.himalayaproject.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.himalayaproject.Bases.BaseApplication;
import com.example.himalayaproject.Presenters.RecommendPresenter;
import com.example.himalayaproject.R;
import com.example.himalayaproject.Utils.LogUtils;

/**
 * 本View加载堆叠了四种页面，分别是成功，网络错误，加载中，空内容，根据数据获取的情况选择哪种页面是可见状态
 * ，其中成功页面的加载是抽象的，交由要显示本View的View去实现创建相应的成功界面（本项目中是fragment在使用本View）
 */
public abstract class UILoader extends FrameLayout {
    /**
     * 前两构造方法改为this，把入口限定在本类特定构造方法，也就是走前俩构造方法不会调用到父类去，最终都会跳转到本类的第三个构造方法
     * @param context
     */
    private View mLoadingView;
    private View mSuccessView;
    private View mNetworkErrorView;
    private View mEmptyView;


    public UIStatus mCurrentStatus = UIStatus.NONE;

    public UILoader(@NonNull Context context) {
        this(context, null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 修改状态，这里保证就算在其他线程调用更新状态，也会回到主线程更新UI
     */
    public void updateStatus(UIStatus status) {
        mCurrentStatus = status;
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                swichUIByCurrentStatus();
            }
        });
    }

    /**
     * 初始化UI
     */
    private void init() {
        swichUIByCurrentStatus();
    }

    /**
     * 解析几种状态页，根据目前的状态设置这些状态页是否可见
     */
    private void swichUIByCurrentStatus() {
        //添加网络错误的View
        if (mNetworkErrorView == null) {
            mNetworkErrorView = getNetWorkErrorView();
            addView(mNetworkErrorView);
        }
        //根据状态设置是否可见
        mNetworkErrorView.setVisibility(mCurrentStatus == UIStatus.NETWORKE_RROR ? VISIBLE : GONE);

        //添加加载中的View
        if (mLoadingView == null) {
            mLoadingView = getLoadingView();
            addView(mLoadingView);
        }
        //根据状态设置是否可见
        mLoadingView.setVisibility(mCurrentStatus == UIStatus.LOADING ? VISIBLE : GONE);

        //添加获取成功的View
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //根据状态设置是否可见
        mSuccessView.setVisibility(mCurrentStatus == UIStatus.SUCCESS ? VISIBLE : GONE);


        //添加内容为空的View
        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        //根据状态设置是否可见
        mEmptyView.setVisibility(mCurrentStatus == UIStatus.NONE ? VISIBLE : GONE);

    }


    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view, this, false);
    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
    }

    private View getNetWorkErrorView() {
        View networkErrorView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_networkerror_view, this, false);
        //注意这里没有抽取，意味着搜索界面也使用了重试获取推荐数据，搜索失败重试会显示推荐数据
        networkErrorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RecommendPresenter.getInstance().getRecommendData();
            }
        });
        return networkErrorView;
    }

    private void exportLogInfo(String tag, String s) {
        LogUtils.d(tag, s);
    }

    public View getErrorView(){
        return mNetworkErrorView;
    }

    protected abstract View getSuccessView(ViewGroup container);


    public enum UIStatus {
        NONE, LOADING, SUCCESS, NETWORKE_RROR, EMPTY
    }

}
