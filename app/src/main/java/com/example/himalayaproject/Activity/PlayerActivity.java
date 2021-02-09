package com.example.himalayaproject.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.himalayaproject.Adapters.PlayerTrackPagerAdapter;
import com.example.himalayaproject.Bases.BaseActivity;
import com.example.himalayaproject.Interfaces.IPlayerViewCallback;
import com.example.himalayaproject.Presenters.PlayerPresenter;
import com.example.himalayaproject.R;
import com.example.himalayaproject.Utils.LogUtils;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerActivity extends BaseActivity implements IPlayerViewCallback {

    private ImageView mPlayControl;
    private ImageView mPlayPre;
    private ImageView mPlayNext;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mLeftTime;
    private TextView mCurTime;
    private SeekBar mSeekBar;
    private int mCurProgress;
    private boolean mIsSeekBarTouching;
    private TextView mTrackTitle;
    private String mTrackTitleText;
    private ViewPager mTrackPagerView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserTouchedPager;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeMap = new HashMap<>();
    private XmPlayListControl.PlayMode mCurMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    static {
        sPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST, XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
        sPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
        sPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
    }

    private ImageView mMPlayModeView;
    private ImageView mFavorBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //初始化控件对象
        initView();
        //获取presenter，实现回调逻辑并注册本Ac给presenter，便于在presenter的动作逻辑里通知调用本页回调修改UI
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registViewCallback(this);
        //获取播放列表，回调里会设置播放的List<Track>给VPager的适配器
        mPlayerPresenter.getPlayList();
        //给控件设置事件
        initEvent();
        //initAnimation();
    }
    /**
     * 设置动画，进入页面Ac的Window灰度值从暗到亮
     */
    /*
    private void initAnimation() {
        ValueAnimator enterBgAnimator = ValueAnimator.ofFloat(0.5f, 1.0f);
        enterBgAnimator.setDuration(800);
        enterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.alpha =(float) animation.getAnimatedValue();
                window.setAttributes(attributes);
            }
        });
        enterBgAnimator.start();
    }

     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            //取消注册，不取消会导致Ac的引用还被静态单例Presenter持有而无法回收
            mPlayerPresenter.unRegistViewCallback(this);
            mPlayerPresenter = null;
        }
    }

    /**
     * 初始化各控件
     */
    private void initView() {
        mPlayControl = this.findViewById(R.id.play_or_pause_img);
        mPlayPre = this.findViewById(R.id.play_pre_img);
        mPlayNext = this.findViewById(R.id.play_next_img);
        mLeftTime = this.findViewById(R.id.total_time);
        mCurTime = this.findViewById(R.id.current_postion);
        mSeekBar = this.findViewById(R.id.player_seek_bar);
        mTrackTitle = this.findViewById(R.id.player_title);
        //这里设置第一个音频的title，第一个音频的title变量在注册回调里已设置
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitle.setText(mTrackTitleText);
        }
        //播放器的ViewPager track图片内容
        mTrackPagerView = this.findViewById(R.id.track_pager_view);
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        mTrackPagerView.setAdapter(mTrackPagerAdapter);
        mMPlayModeView = this.findViewById(R.id.play_mode);
        mFavorBtn = this.findViewById(R.id.player_favor_btn);
    }

    /**
     * 给控件设置相关点击事件/进度条拖动事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果正在播放，调用播放器暂停
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                }else {
                    //如果是暂停的，调用播放器播放
                    mPlayerPresenter.play();
                }
            }
        });

        mPlayPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerPresenter.playPre();
            }
        });

        mPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerPresenter.playNext();
            }
        });
        //进度条相关事件
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsSeekBarTouching = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsSeekBarTouching = false;
                mPlayerPresenter.seekTo(mCurProgress);
                //手离开进度条更新进度
            }
        });

        //Vpager滑动事件
        mTrackPagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            /**
             * 当页面选中切换播放内容
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                if (mPlayerPresenter != null && mIsUserTouchedPager) {
                    mPlayerPresenter.playByIndex(position);
                }
                mIsUserTouchedPager = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //给ViewPager设置一个触摸事件，避免点击下一首切换图片与滑动图片切换图片冲突
        mTrackPagerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case  MotionEvent.ACTION_DOWN:
                        mIsUserTouchedPager = true;
                        break;
                }
                return false;
            }
        });

        /**
         * 设置播放模式点击事件
         */
        mMPlayModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmPlayListControl.PlayMode playMode = sPlayModeMap.get(mCurMode);
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.swichPlayMode(playMode);
                }
            }
        });

        mFavorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlayerActivity.this, "登录并充值成为大会员后开启此功能！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 根据当前状态更新播放模式图标
     */
    private void updatePlayModeImg() {
        int resId = R.mipmap.mode_list_icon;
        switch (mCurMode) {
            case PLAY_MODEL_LIST:
                resId = R.mipmap.mode_list_icon;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.mipmap.mode_single_list_icon;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.mipmap.mode_single_icon;
                break;
        }
        mMPlayModeView.setImageResource(resId);
    }



    /**
     * 播放开始的回调
     */
    @Override
    public void onPlayStart() {
        //修改播放按钮为暂停
        if (mPlayControl != null) {
            mPlayControl.setImageResource(R.mipmap.pause_icon);
        }
    }

    /**
     * 暂停的回调
     */
    @Override
    public void onPlayPause() {
        //修改播放按钮为播放
        if (mPlayControl != null) {
            mPlayControl.setImageResource(R.mipmap.play_icon);
        }
    }

    @Override
    public void onPlayStop() {
        if (mPlayControl != null) {
            mPlayControl.setImageResource(R.mipmap.play_icon);
        }
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

    /**
     * 播放进度改变回调更新UI 时间 + 进度条
     * @param currentProgress
     * @param total
     */
    @Override
    public void onProgressChange(long currentProgress, long total) {
        mSeekBar.setMax((int) total);
        //更新时间
        String leftTime;
        String currentTime;
        if (total > 1000 * 60 * 60) {
            leftTime = mHourFormat.format(total);
            currentTime = mHourFormat.format(currentProgress);
        } else {
            leftTime = mMinFormat.format(total);
            currentTime = mMinFormat.format(currentProgress);
        }
        if (mLeftTime != null) {
            mLeftTime.setText(leftTime);
        }
        if (mCurTime != null) {
            mCurTime.setText(currentTime);
        }
        //更新进度条
        if (!mIsSeekBarTouching) {
            mSeekBar.setProgress((int) currentProgress);
        }

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    /**
     * 播放音频改变，此处更新标题图片等
     */
    @Override
    public void onTrackUpdate(Track track, int position) {
        if (track == null) {
            exportLogInfo("TrackUpdate","null");
            return;
        }
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitle != null) {
            mTrackTitle.setText(mTrackTitleText);
        }
        //当音频改变，获取到当前节目索引位置
        //修改对应的Vpager图片
        if (mTrackPagerView != null) {
            mTrackPagerView.setCurrentItem(position, true);
        }
    }

    private void exportLogInfo(String tag, String s) {
        LogUtils.d(tag, s);
    }

    /**
     * 播放列表回来了，把所有音频(图片)设置进Pager适配器
     * @param playList
     */
    @Override
    public void onListLoaded(List<Track> playList) {
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(playList);
        }
    }

    /**
     * 播放模式切换后的回调
     * @param playMode
     */
    @Override
    public void onPlayModeChanged(XmPlayListControl.PlayMode playMode) {
        mCurMode = playMode;
        updatePlayModeImg();
    }
}
