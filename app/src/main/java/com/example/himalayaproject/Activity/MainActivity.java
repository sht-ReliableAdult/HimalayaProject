package com.example.himalayaproject.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.himalayaproject.Adapters.IndicatorAdapter;
import com.example.himalayaproject.Adapters.MainContentAdapter;
import com.example.himalayaproject.Interfaces.IPlayerViewCallback;
import com.example.himalayaproject.Presenters.PlayerPresenter;
import com.example.himalayaproject.Presenters.RecommendPresenter;
import com.example.himalayaproject.R;
import com.example.himalayaproject.Utils.LogUtils;
import com.example.himalayaproject.api.DbHelper;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * 这里是推荐页，indicator关联ViewPage，ViewPage里面关联显示对应Fragment
 */
public class MainActivity extends FragmentActivity implements IPlayerViewCallback {
    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private MainContentAdapter mainContentAdapter;
    private IndicatorAdapter mIndicatorAdaptor;
    private ImageView mCoverView;
    private TextView mTitleView;
    private TextView mAuthorView;
    private ImageView mPlayControlView;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayerBar;
    private ImageView mSearchBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registViewCallback(this);
    }

    private void initView() {
        //创建自己的indicator
        mMagicIndicator = this.findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(Color.BLACK);

        //创建indicator的适配器
        mIndicatorAdaptor = new IndicatorAdapter(this);

        //组装indicator+adaptor
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(mIndicatorAdaptor);
        commonNavigator.setAdjustMode(true);
        //把构造好的indicator核心装给自己的indicator
        mMagicIndicator.setNavigator(commonNavigator);

        //ViewPager
        mContentPager = this.findViewById(R.id.content_pager);

        //创建主内容适配器（Fragment适配Viewpager）
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mainContentAdapter);
        mContentPager.setOffscreenPageLimit(2);
        //绑定indicator与对应的viewpager
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);
        //播放条相关
        mCoverView = this.findViewById(R.id.main_track_cover);
        mTitleView = this.findViewById(R.id.main_track_title);
        mTitleView.setSelected(true);
        mAuthorView = this.findViewById(R.id.main_track_author);
        mPlayControlView = this.findViewById(R.id.main_play_control);
        mPlayerBar = this.findViewById(R.id.main_player_bar);
        mSearchBtn = this.findViewById(R.id.search_btn);
    }

    //在这里设置indicator点击事件的监听
    private void initEvent() {
        mIndicatorAdaptor.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index);
                }
            }
        });
        mPlayControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没播放过内容，这时候点击后播放推荐列表第一个专辑的第一个内容
                        playFirstRecommend();
                    }else{
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        }else {
                            mPlayerPresenter.play();
                        }
                    }
                }
            }
        });
        mPlayerBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没播放过内容，这时候点击后播放推荐列表第一个专辑的第一个内容
                        playFirstRecommend();
                    }
                    startActivity(new Intent(MainActivity.this, PlayerActivity.class));
                }
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void exportLogInfo(String tag, String s) {
        LogUtils.d(tag, s);
    }

    /**
     * 未设置过播放列表，播放推荐内容第一个专辑的第一个音频
     */
    private void playFirstRecommend() {
        List<Album> recommendList = RecommendPresenter.getInstance().getRecommendList();
        if (recommendList != null) {
            long albumId = recommendList.get(0).getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegistViewCallback(this);
        }
    }

    /**
     * 播放开始的回调
     */
    @Override
    public void onPlayStart() {
        if (mPlayControlView != null) {
            mPlayControlView.setImageResource(R.mipmap.pause_icon);
        }
    }

    /**
     * 暂停的回调
     */
    @Override
    public void onPlayPause() {
        if (mPlayControlView != null) {
            mPlayControlView.setImageResource(R.mipmap.play_icon);
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

    /**
     * 专辑内容更新的回调，保持主页面的播放条与实际播放内容同步
     * @param track
     * @param position
     */
    @Override
    public void onTrackUpdate(Track track, int position) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (mTitleView != null) {
                mTitleView.setText(trackTitle);
            }
            if (mAuthorView != null) {
                mAuthorView.setText(nickname);
            }
            Picasso.with(this).load(coverUrlMiddle).into(mCoverView);
        }
    }

    @Override
    public void onListLoaded(List<Track> playList) {

    }

    @Override
    public void onPlayModeChanged(XmPlayListControl.PlayMode playMode) {

    }
}