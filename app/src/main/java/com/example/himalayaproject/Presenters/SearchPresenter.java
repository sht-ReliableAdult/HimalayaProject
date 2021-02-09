package com.example.himalayaproject.Presenters;

import com.example.himalayaproject.DetailActivity;
import com.example.himalayaproject.Interfaces.ISearchPresenter;
import com.example.himalayaproject.Interfaces.ISearchViewCallback;
import com.example.himalayaproject.api.AiTingBuTingApi;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.AlbumResult;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.ArrayDeque;

public class SearchPresenter implements ISearchPresenter {

    private List<ISearchViewCallback> mCallBack = new ArrayList<>();
    private String mCurKeyword;
    private AiTingBuTingApi mAiTingBuTingApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurPage = DEFAULT_PAGE;

    private SearchPresenter(){
        mAiTingBuTingApi = AiTingBuTingApi.getInstance();
    }

    private static SearchPresenter sSearchPresenter;

    public static SearchPresenter getInstance() {
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    /**
     * 搜索keyword
     * @param keyword
     */
    @Override
    public void doSearch(String keyword) {
        mCurKeyword = keyword;
        search(keyword);
    }

    @Override
    public void reSearch() {
        search(mCurKeyword);
    }

    /**
     * 通用搜索动作逻辑
     * @param keyword
     */
    private void search(String keyword){
        mAiTingBuTingApi.searchByKeyword(keyword, mCurPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    for (ISearchViewCallback callback : mCallBack) {
                        callback.onSearchResult(albums);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                for (ISearchViewCallback callback : mCallBack) {
                    callback.onError(i, s);
                }
            }
        });
    }

    @Override
    public void loadMore() {

    }

    /**
     * 请求获取热词
     */
    @Override
    public void getHotWord() {
        mAiTingBuTingApi.getHotWord(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    for (ISearchViewCallback callback : mCallBack) {
                        callback.onHotWordLoaded(hotWords);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 根据关键词获取联想词汇
     * @param keyword
     */
    @Override
    public void getRecommendKeyWord(String keyword) {
        mAiTingBuTingApi.getRecommendWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    for (ISearchViewCallback callback : mCallBack) {
                        callback.onRecommendWordLoaded(keyWordList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    @Override
    public void registViewCallback(ISearchViewCallback iSearchViewCallback) {
        if (!mCallBack.contains(iSearchViewCallback)) {
            mCallBack.add(iSearchViewCallback);
        }
    }

    @Override
    public void unRegistViewCallback(ISearchViewCallback iSearchViewCallback) {
        mCallBack.remove(iSearchViewCallback);
    }
}
