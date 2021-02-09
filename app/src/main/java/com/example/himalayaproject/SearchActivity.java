package com.example.himalayaproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalayaproject.Adapters.RecommendRVAdapter;
import com.example.himalayaproject.Adapters.SearchRecommendRVAdapter;
import com.example.himalayaproject.Bases.BaseActivity;
import com.example.himalayaproject.Interfaces.ISearchViewCallback;
import com.example.himalayaproject.Presenters.DetailPresenter;
import com.example.himalayaproject.Presenters.SearchPresenter;
import com.example.himalayaproject.Utils.LogUtils;
import com.example.himalayaproject.Views.FlowTextLayout;
import com.example.himalayaproject.Views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchViewCallback, RecommendRVAdapter.IItemClick {

    private TextView mBackBtn;
    private TextView mSearchBtn;
    private EditText mInputBox;
    private FrameLayout mSearchContainer;
    private SearchPresenter mSearchPresenter;
    private UILoader mUILoader;
    private RecyclerView mResultRV;
    private RecommendRVAdapter mAdapter;
    private FlowTextLayout mFlowTextLayout;
    private RecyclerView mSearchRecommendList;
    private SearchRecommendRVAdapter mSearchRecommendRVAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mSearchPresenter = SearchPresenter.getInstance();
        mSearchPresenter.registViewCallback(this);
        //请求热词数据
        mSearchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegistViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initView() {
        mBackBtn = this.findViewById(R.id.back_btn);
        mSearchBtn = this.findViewById(R.id.search_btn);
        mInputBox = this.findViewById(R.id.edit_text);
        mSearchContainer = this.findViewById(R.id.search_container);
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
            if (mUILoader.getParent() instanceof ViewGroup) {
                //先把自己去掉，因为不可以重复添加
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            mSearchContainer.addView(mUILoader);
        }
    }

    /**
     * 给UILoader创建数据请求成功的页面
     * @return
     */
    private View createSuccessView() {
        //请求热词成功页面，包含热词页和搜索结果页，根据搜索结果是否成功选择性显示热词页或者搜索结果页
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        mFlowTextLayout = resultView.findViewById(R.id.flow_text_layout);
        mResultRV = resultView.findViewById(R.id.search_result_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mResultRV.setLayoutManager(layoutManager);
        //搜索结果界面是专辑界面，跟推荐页一样，可以复用推荐页的Adapter,设置跟推荐页一样的点击事件就行
        mAdapter = new RecommendRVAdapter();
        mResultRV.setAdapter(mAdapter);
        mAdapter.setIItemClick(this);
        //给每个Item加装饰，这里加Item间距
        mResultRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 3);//用工具(这是magicindicator的一个类)把dp转像素做屏幕适配
                outRect.bottom = UIUtil.dip2px(view.getContext(), 3);//这个工具类可以copy出来以后自用
                outRect.left = UIUtil.dip2px(view.getContext(), 6);
                outRect.right = UIUtil.dip2px(view.getContext(), 6);
            }
        });
        //搜索联想View
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(linearLayoutManager);
        mSearchRecommendRVAdapter = new SearchRecommendRVAdapter();
        mSearchRecommendList.setAdapter(mSearchRecommendRVAdapter);
        return resultView;
    }

    private void initEvent() {
        //后退按钮
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //搜索按钮
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = mInputBox.getText().toString().trim();
                if (TextUtils.isEmpty(inputText)) {
                    Toast.makeText(SearchActivity.this, "搜索内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(inputText);
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        //输入框
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            /**
             * 删除完搜索框文字回到热词界面
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //文本为空，就获取热词，回到热词界面
                if (TextUtils.isEmpty(s)) {
                    mSearchPresenter.getHotWord();
                }else {
                    //文本不为空，触发联想
                    getSuggestWord(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //热词推荐条目
        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
            //点击热词丢进输入框，执行搜索，更新光标到输入框末尾
                mInputBox.setText(text);
                mInputBox.setSelection(text.length());
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(text);
                }
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        //搜索联想条目
        if (mSearchRecommendRVAdapter != null) {
            mSearchRecommendRVAdapter.setItemClickListener(new SearchRecommendRVAdapter.ItemClickListener() {
                @Override
                public void onItemClick(String keyword) {
                    mInputBox.setText(keyword);
                    mInputBox.setSelection(keyword.length());
                    mSearchPresenter.doSearch(keyword);
                }
            });
        }

    }

    /**
     * 获取联想推荐词汇
     * @param keyword
     */
    private void getSuggestWord(String keyword) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendKeyWord(keyword);
        }
    }

    /**
     * 搜索结果的回调
     * @param searchResultList
     */
    @Override
    public void onSearchResult(List<Album> searchResultList) {
        hideAllSuccessView();
        mResultRV.setVisibility(View.VISIBLE);
        //隐藏键盘
        InputMethodManager inputService = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //windowToken是个Binder，用于跟键盘服务通信，同时功能是Token令牌，用于证明自己身份
        inputService.hideSoftInputFromWindow(mInputBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (searchResultList != null) {
            if (searchResultList.size() == 0) {
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                }
            }else{
                mAdapter.setData(searchResultList);
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }

    }

    /**
     * 获取到热词的回调
     * @param hotWords
     */
    @Override
    public void onHotWordLoaded(List<HotWord> hotWords) {
        hideAllSuccessView();
        //热词回来显示热词页面
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        List<String> hotWordList = new ArrayList<>();
        for (HotWord hotWord : hotWords) {
            String searchword = hotWord.getSearchword();
            hotWordList.add(searchword);
        }
        Collections.sort(hotWordList);
        mFlowTextLayout.setTextContents(hotWordList);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOk) {

    }

    /**
     * 获取完联想词汇通知的回调
     * @param keywordList
     */
    @Override
    public void onRecommendWordLoaded(List<QueryResult> keywordList) {
        if (mSearchRecommendRVAdapter != null) {
            mSearchRecommendRVAdapter.setData(keywordList);
        }
        //控制联想View显示和隐藏
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        hideAllSuccessView();
        mSearchRecommendList.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏所有子页面
     */
    private void hideAllSuccessView(){
        mSearchRecommendList.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
        mResultRV.setVisibility(View.GONE);
    }

    /**
     * 搜索出错的回调
     * @param errorCode
     * @param msg
     */
    @Override
    public void onError(int errorCode, String msg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORKE_RROR);
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        DetailPresenter.getInstance().setAlbum(album);
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}
