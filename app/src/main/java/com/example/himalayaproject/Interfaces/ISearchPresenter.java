package com.example.himalayaproject.Interfaces;

import com.example.himalayaproject.Bases.IBasePresenter;

public interface ISearchPresenter extends IBasePresenter<ISearchViewCallback> {
    /**
     * 进行搜索
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重新尝试搜索
     */
    void reSearch();

    /**
     * 加载更多
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWord();

    /**
     * 获取联想词推荐
     * @param keyword
     */
    void getRecommendKeyWord(String keyword) ;

}
