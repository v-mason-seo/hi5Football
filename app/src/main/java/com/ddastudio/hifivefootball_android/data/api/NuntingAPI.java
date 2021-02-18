package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.nunting_bset.model.BoardInfoModel;
import com.ddastudio.hifivefootball_android.nunting_bset.model.PostsItemModel;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NuntingAPI {

    @GET("best/siteboard")
    Flowable<List<SiteBoardModel>> onLoadSiteList();

    /**
     * 사이트의 게시판 정보를 가져온다.
     * @param siteId
     * @return
     */
    @GET("best/{siteId}/boards")
    Observable<BoardInfoModel[]> loadBoardInfoList(@Path("siteId") String siteId);


    /**
     * 게시글 리스트를 가져온다.
     * @param siteId
     * @param boardid
     * @param yyyyMMddHH
     * @return
     */
    @GET("best/{siteId}/board/{boardid}")
    Flowable<PostsItemModel> loadPostsList(@Path("siteId") String siteId,
                                           @Path("boardid") String boardid,
                                           @Query("q") String yyyyMMddHH);

}
