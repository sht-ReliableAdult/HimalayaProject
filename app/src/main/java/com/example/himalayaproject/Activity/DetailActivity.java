package com.example.himalayaproject.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalayaproject.Adapters.DetailListRVAdapter;
import com.example.himalayaproject.Bases.BaseActivity;
import com.example.himalayaproject.Bases.BaseApplication;
import com.example.himalayaproject.Interfaces.IDetailViewCallback;
import com.example.himalayaproject.Interfaces.IPlayerViewCallback;
import com.example.himalayaproject.Presenters.DetailPresenter;
import com.example.himalayaproject.Presenters.PlayerPresenter;
import com.example.himalayaproject.R;
import com.example.himalayaproject.Views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Method;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class DetailActivity extends BaseActivity implements IDetailViewCallback, DetailListRVAdapter.ItemClickListener, IPlayerViewCallback {
    private ImageView mBarImageView;
    private ImageView mCoverImageView;
    private TextView mAlbumTitleView;
    private TextView mAuthorView;
    private DetailPresenter mDetailPresenter;
    private int mCurrentPage = 1;
    private RecyclerView mDetailList;
    private DetailListRVAdapter mDetailListAdapter;
    private FrameLayout mDetailLoadingView;
    private UILoader mUILoader;
    private Button mBtPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurTracks;
    private TwinklingRefreshLayout mRefreshLayout;
    private TextView mFavorBtn;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //把系统状态栏设置为透明风格，不遮盖下面的界面
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //初始化专辑描述信息
        initView();
        //专辑详情的presenter
        mDetailPresenter = DetailPresenter.getInstance();
        mDetailPresenter.registViewCallback(this);
        //播放器的presenter
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registViewCallback(this);
        //初始化播放按钮状态
        updatePlayState(mPlayerPresenter.isPlaying());
        //设置非RV项的点击事件
        initEvent();
        //initAnimation();
        BaseApplication.useSysLogPlugin();
    }

    private void initEvent() {
        //播放按钮控制播放器状态
        mBtPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    //判断是否有播放列表,没有默认播放第一条
                    if (!mPlayerPresenter.hasPlayList()) {
                        mPlayerPresenter.setPlayList(mCurTracks, 0);
                        Toast.makeText(DetailActivity.this, "正在播放：" + mPlayerPresenter.getCurTrack().getTrackTitle(), Toast.LENGTH_LONG).show();
                    } else{//有的话链接功能
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        } else{
                            mPlayerPresenter.play();
                            Toast.makeText(DetailActivity.this, "正在播放：" + mPlayerPresenter.getCurTrack().getTrackTitle(), Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
        });
        mFavorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailActivity.this, "登录并充值成为大会员后开启此功能！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 设置动画，进入页面灰度值从暗到亮
     */

//    private void initAnimation() {
//        ValueAnimator enterBgAnimator = ValueAnimator.ofFloat(0.5f, 1.0f);
//        enterBgAnimator.setDuration(800);
//        enterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                Window window = getWindow();
//                WindowManager.LayoutParams attributes = window.getAttributes();
//                attributes.alpha =(float) animation.getAnimatedValue();
//                window.setAttributes(attributes);
//            }
//        });
//        enterBgAnimator.start();
//    }



    /**
     * 在这初始化数据把已有的专辑信息设置给控件，并实现UILoader的定制成功页，即详细信息-分集RV
     */
    private void initView() {
        mDetailLoadingView = this.findViewById(R.id.detail_loading_view);
        mUILoader = new UILoader(this) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(container);
            }
        };
        mBarImageView = this.findViewById(R.id.detail_bar_view);
        mCoverImageView = this.findViewById(R.id.detail_cover_view);
        mAlbumTitleView = this.findViewById(R.id.album_title);
        mAlbumTitleView.setSelected(true);
        mAuthorView = this.findViewById(R.id.album_author);
        mDetailLoadingView.removeAllViews();
        mDetailLoadingView.addView(mUILoader);
        mBtPlayControl = this.findViewById(R.id.detail_play_control);
        mFavorBtn = this.findViewById(R.id.detail_favor_btn);
    }
    /**
     * 实现UILoader成功页面的逻辑抽取
     * @param container
     * @return
     */
    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetailList.setLayoutManager(layoutManager);
        //设置适配器
        mDetailListAdapter = new DetailListRVAdapter();
        mDetailList.setAdapter(mDetailListAdapter);
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);//用工具(这是magicindicator的一个类)把dp转像素做屏幕适配
                outRect.left = UIUtil.dip2px(view.getContext(), 3);
                outRect.right = UIUtil.dip2px(view.getContext(), 3);
            }
        });
        mDetailListAdapter.setItemClickListener(this);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                }, 1200);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "暂无更多", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishLoadmore();
                    }
                }, 1200);
            }
        });
        return detailListView;
    }

    /**
     * 获取专辑内容列表成功的回调，这里给RV设置分集信息
     * @param tracks
     */
    @Override
    public void onDetailLoaded(List<Track> tracks) {
        this.mCurTracks = tracks;
        if (tracks == null || tracks.size() == 0) {
            if (mUILoader != null) {
                mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        mDetailListAdapter.setData(tracks);

    }

    /**
     * 本页获取专辑成功的回调，注册进Presenter时触发，拿到在推荐页点击album时设置进presenter的mAlbum，在这设置总体信息给UI
     * @param album
     */
    @Override
    public void onAlbumLoaded(final Album album) {
        //提前给网络错误页覆盖点击事件(点击请求获取详细数据)
        mUILoader.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDetailPresenter.getDetailData(album.getId(), mCurrentPage);
            }
        });
        //请求获取本页专辑的详细数据
        mDetailPresenter.getDetailData(album.getId(), mCurrentPage);
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if (mAlbumTitleView != null) {
            mAlbumTitleView.setText(album.getAlbumTitle());
        }
        if (mAuthorView != null) {
            mAuthorView.setText(album.getAnnouncer().getNickname());
        }
        /*
         if (mBarImageView != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mBarImageView);
        }
         */

        if (mCoverImageView != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mCoverImageView);
        }
    }

    /**
     * 获取详细数据失败时的回调，修改UILoader状态位显示网络错误页
     */
    @Override
    public void onNetworkError() {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORKE_RROR);
        }
    }

    /**
     * 点击item触发的点击事件回调
     * @param detailData
     * @param position
     */
    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //给播放器设置数据
        PlayerPresenter.getInstance().setPlayList(detailData, position);
        //跳转对应播放器
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDetailPresenter != null) {
            mDetailPresenter.unRegistViewCallback(this);
            mDetailPresenter = null;
        }
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegistViewCallback(this);
            mPlayerPresenter = null;
        }
        mAuthorView = null;
    }

    @Override
    public void onPlayStart() {
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
        updatePlayState(false);
    }

    /**
     * 根据播放状态更新播放按钮状态
     * @param playing
     */
    private void updatePlayState(boolean playing) {
        if (mBtPlayControl != null) {
            if (playing) {
                mBtPlayControl.setText("暂停");
            }else {
                mBtPlayControl.setText("继续");
            }
        }

    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onPlayNext(Track track) {

    }

    @Override
    public void onPlayPre(Track track) {

    }

    @Override
    public void onProgressChange(long currentProgress, long total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int position) {

    }

    @Override
    public void onListLoaded(List<Track> playList) {

    }

    @Override
    public void onPlayModeChanged(XmPlayListControl.PlayMode playMode) {

    }
}
