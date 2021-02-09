package com.example.himalayaproject.api;

import com.example.himalayaproject.Utils.Constant;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

/**
 * 这里是Model层，负责请求数据
 */
public class AiTingBuTingApi {

    private AiTingBuTingApi(){

    }

    private static AiTingBuTingApi sAiTingBuTingApi;

    public static AiTingBuTingApi getInstance(){
        if (sAiTingBuTingApi == null) {
            synchronized (AiTingBuTingApi.class) {
                if (sAiTingBuTingApi == null) {
                    sAiTingBuTingApi = new AiTingBuTingApi();
                }
            }
        }
        return sAiTingBuTingApi;
    }

    /**
     * 获取推荐页专辑数据
     * @param callBack 请求结果回调接口
     */
    public void getRecommendData(IDataCallBack<GussLikeAlbumList> callBack){
        Map<String, String> map = new HashMap<>();
        //参数表示一页返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constant.RECOMMEND_ITEM_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, callBack);
    }

    /**
     * 根据专辑ID获取详情页音频数据
     * @param callBack 获取结果回调
     * @param albumId 专辑ID
     * @param page 专辑页码
     */
    public void getDetailData(IDataCallBack<TrackList> callBack, long albumId, int page) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, 50 + "");
        CommonRequest.getTracks(map, callBack);
    }

    /**
     * 根据关键字请求搜索数据
     * @param keyword
     */
    public void searchByKeyword(String keyword, int page, IDataCallBack<SearchAlbumList> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, 50 + "");
        CommonRequest.getSearchedAlbums(map, callBack);
    }

    /**
     * 获取搜索热词
     * @param callBack
     */
    public void getHotWord(IDataCallBack<HotWordList> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, 20 + "");
        CommonRequest.getHotWords(map, callBack);
    }

    /**
     * 根据关键字获取联想词
     * @param keyword
     * @param callBack
     */
    public void getRecommendWord(String keyword, IDataCallBack<SuggestWords> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map, callBack);
    }
}
