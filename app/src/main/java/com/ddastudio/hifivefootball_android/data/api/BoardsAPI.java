package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.IssueModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 9. 4..
 */

public interface BoardsAPI {

    /**
     * 게시글 조회
     * http://45.32.254.69:5200/board?board_type=1&limit=15&offset=0
     * @param id 게시판 종류
     * @param limit
     * @param offset
     * @return
     */
    @GET("v1/boards/{id}")
    Flowable<List<ContentHeaderModel>> getBoardContentList(@Header("authorization") String token,
                                                           @Path("id") int boardid,
                                                           @Query("limit") int limit,
                                                           @Query("offset") int offset);


    /**
     * 전체 게시글 조회
     * @param limit
     * @param offset
     * @return
     */
    @GET("v1/boards")
    Flowable<List<ContentHeaderModel>> getBoardALLContentList(@Header("authorization") String token,
                                                              @Query("exmatchreport") int excludeMatchReport,
                                                              @Query("exnews") int excludeFootballNews,
                                                              @Query("limit") int limit,
                                                              @Query("offset") int offset);



    @GET("v1/boards/{id}/issue")
    Flowable<List<IssueModel>> getIssueBoardContentList(@Header("authorization") String token,
                                                        @Path("id") int boardid,
                                                        @Query("limit") int limit,
                                                        @Query("offset") int offset,
                                                        @Query("closed") int closed);
}
