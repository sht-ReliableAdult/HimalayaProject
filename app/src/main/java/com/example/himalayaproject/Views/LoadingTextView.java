package com.example.himalayaproject.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoadingTextView extends androidx.appcompat.widget.AppCompatTextView {


    private boolean mNeedFlash;
    String text = "正在加载！别催";

    public LoadingTextView(@NonNull Context context) {
        this(context, null);
    }

    public LoadingTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public LoadingTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setTextSize(18);
        mNeedFlash = true;
        post(new Runnable() {
            @Override
            public void run() {
                text += ".";
                if (text.equals("正在加载！别催........")) text = "正在加载！别催";
                setText(text);
                if (mNeedFlash) {
                    postDelayed(this, 28);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mNeedFlash = false;
    }
}
