package com.example.himalayaproject.Fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalayaproject.Adapters.RecommendRVAdapter;
import com.example.himalayaproject.Bases.BaseApplication;
import com.example.himalayaproject.Bases.BaseFragment;
import com.example.himalayaproject.DetailActivity;
import com.example.himalayaproject.Interfaces.IRecommendViewCallback;
import com.example.himalayaproject.MainActivity;
import com.example.himalayaproject.Presenters.DetailPresenter;
import com.example.himalayaproject.Presenters.RecommendPresenter;
import com.example.himalayaproject.R;
import com.example.himalayaproject.Utils.Constant;
import com.example.himalayaproject.Utils.LogUtils;
import com.example.himalayaproject.Views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这里是推荐页Fragment-MVP里的主角，与自己要做的回调动作组装实现
 * 安排逻辑执行者干活，干完自己做回调
 *
 */
public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, RecommendRVAdapter.IItemClick {
    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRV;
    private RecommendRVAdapter mRecommendRVAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUILoader;
    private TwinklingRefreshLayout mRefreshLayout;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        /**
         * UILoader是一个FrameLayout，根据状态不同选择显示哪一个页面到本Fragment
         */
        mUILoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }
        };

        //让逻辑执行者获取数据
        mRecommendPresenter = RecommendPresenter.getInstance();
        //让fragment来实现回调接口，在对应的onDestroyView取消注册
        mRecommendPresenter.registViewCallback(this);
        mRecommendPresenter.getRecommendData();
        //同一个View不能多次绑定，所以绑定前要判断是否已绑定，已绑定需要解绑
        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }
        return mUILoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //如果是主布局，一般要把Decor传进来包外面,attachToRoot F表示不包,这里传了一个包装，却不允许包装是因为涉及到测量，不传的话可能引发问题
        //这里解析布局生成了View树骨架
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        //1.这里从骨架里通过findViewById找到rv和fresh控件
        mRecommendRV = mRootView.findViewById(R.id.recommend_list);
        mRefreshLayout = mRootView.findViewById(R.id.main_refresh_layout);
        initRefreshEvent();
        //2.给RV准备layoutManager
        LinearLayoutManager loutManager = new LinearLayoutManager(getContext());
        loutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRV.setLayoutManager(loutManager);
        //给每个Item加装饰，这里加Item间距
        mRecommendRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 3);//用工具(这是magicindicator的一个类)把dp转像素做屏幕适配
                outRect.bottom = UIUtil.dip2px(view.getContext(), 3);//这个工具类可以copy出来以后自用
                outRect.left = UIUtil.dip2px(view.getContext(), 6);
                outRect.right = UIUtil.dip2px(view.getContext(), 6);
            }
        });
        //3.给RV设置适配器
        mRecommendRVAdapter = new RecommendRVAdapter();
        mRecommendRV.setAdapter(mRecommendRVAdapter);
        //给适配器设置item的点击事件
        mRecommendRVAdapter.setIItemClick(this);

        return mRootView;
    }

    /**
     * 伪实现下拉刷新效果，喜马拉雅免费api没有刷新推荐页的功能，假装刷新成功
     */
    private void initRefreshEvent() {
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BaseApplication.getAppContext(), "刷新成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(BaseApplication.getAppContext(), "暂无更多", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishLoadmore();
                    }
                }, 1200);
            }
        });
    }

    /**
     * Item被点击触发跳转详情页的事件
     * @param position
     */
    @Override
    public void onItemClick(int position, Album album) {
        DetailPresenter.getInstance().setAlbum(album);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消注册，避免内存泄漏,因为传this进来持有了这个fragment的引用，RPresenter又是单例
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegistViewCallback(this);
        }
    }


    private void upDateUI(List<Album> albumList) {
        //给适配器设置数据并更新UI
        mRecommendRVAdapter.setData(albumList);

    }

    @Override
    public void onRecommendDataLoaded(List<Album> result) {
        //获取数据成功，触发这个回调来给适配器装载数据更新UI
        upDateUI(result);
        //发消息给主线程更新状态，当然目前本来就在主线程
        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);

    }

    @Override
    public void onNetworkError() {
        mUILoader.updateStatus(UILoader.UIStatus.NETWORKE_RROR);

    }

    @Override
    public void onEmpty() {
        mUILoader.updateStatus(UILoader.UIStatus.EMPTY);

    }

    @Override
    public void onLoading() {
        mUILoader.updateStatus(UILoader.UIStatus.LOADING);

    }

    @Override
    public void onRefresh(List<Album> result) {

    }

    @Override
    public void onLoadedMore(List<Album> result) {

    }
    /*
    @Override
    public void registViewCallback(IRecommendViewCallback callback) {

    }

    @Override
    public void unRegistViewCallback(IRecommendViewCallback callback) {

    }

     */


}
