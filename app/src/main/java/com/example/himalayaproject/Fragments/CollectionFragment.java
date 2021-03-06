package com.example.himalayaproject.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.himalayaproject.Bases.BaseFragment;
import com.example.himalayaproject.R;

public class CollectionFragment extends BaseFragment {
    boolean isVisible;
    boolean isViewCreated;
    boolean isFirstLoaded = true;
    String TAG = "CollectionFragment";
    private TextView mTx;
    private ImageView mImg;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_collection,container,false);
        mTx = rootView.findViewById(R.id.collection_text);
        mImg = rootView.findViewById(R.id.collection_img);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        if (isVisible && isViewCreated && isFirstLoaded) {
            loadData();
            isFirstLoaded = false;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible && isViewCreated && isFirstLoaded) {
            loadData();
            isFirstLoaded = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        isVisible = false;
        isFirstLoaded = true;
    }

    private void loadData() {
        mTx.setText("登录并充值成为大会员后开启此功能！");
        mImg.setImageResource(R.mipmap.bg_view);
    }
}
