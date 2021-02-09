package com.example.himalayaproject.Interfaces;

import com.example.himalayaproject.Bases.IBasePresenter;

public interface IDetailPresenter extends IBasePresenter<IDetailViewCallback>{
    /**
     * 获取专辑详情数据
     * @param albumId
     * @param page
     */
    void getDetailData(long albumId, int page);
    //注：此处注册取消注册的方法已抽取到IBasePresenter，可以删去
    void registViewCallback(IDetailViewCallback implAc);
    void unRegistViewCallback(IDetailViewCallback implAc);
    void pull2Rresh();
    void loadMore();
}
