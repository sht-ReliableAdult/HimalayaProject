package com.example.himalayaproject.Interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchViewCallback {
    /**
     * 搜索结果回调
     * @param searchResultList
     */
    void onSearchResult(List<Album> searchResultList);

    /**
     * 获取热词结果回调
     * @param hotWords
     */
    void onHotWordLoaded(List<HotWord> hotWords);

    /**
     * 加载更多结果回调
     * @param result 结果
     * @param isOk 加载更多是否成功，f没有更多
     */
    void onLoadMoreResult(List<Album> result, boolean isOk);

    /**
     * 获取联想热词结果回调
     * @param keywordList
     */
    void onRecommendWordLoaded(List<QueryResult> keywordList);

    /**
     * 错误通知回调
     * @param errorCode
     * @param msg
     */
    void onError(int errorCode, String msg);

}
