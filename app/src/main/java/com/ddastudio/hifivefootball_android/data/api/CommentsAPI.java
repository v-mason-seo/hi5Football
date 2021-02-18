package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 9. 6..
 */

public interface CommentsAPI {

    // *** Comment ***

    /**
     * 댓글 정보를 가져온다.
     * @param token
     * @param contentid
     * @return
     */
    @GET("/v1/comments")
    Flowable<List<CommentModel>> getComments(@Header("authorization") String token,
                                             @Query("contentid") int contentid);


    @GET("/v1/comments/{groupid}/group")
    Flowable<List<CommentModel>> getGroupComments(@Header("authorization") String token,
                                                  @Path("groupid") int groupId);


    /**
     * 하나의 댓글 정보를 가져온다.
     * @param commentid
     * @param userid
     * @return
     */
    @GET("/v1/comments/{id}")
    Flowable<CommentModel> getComment(@Path("id") int commentid,
                                      @Query("userid") @Nullable int userid);


    /**
     * 댓글 입력
     * @param token
     * @param contentId
     * @param parentId
     * @param groupId
     * @param depth
     * @param content
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/comments")
    Flowable<DBResultResponse> createComment(@Header("authorization") String token,
                                             @Field("contentid") int contentId,
                                             @Field("parentid") int parentId,
                                             @Field("groupid") int groupId,
                                             @Field("depth") int depth,
                                             @Field("content") String content);


    /**
     * 댓글 삭제
     * @param token
     * @param commentId
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @HTTP(method = "DELETE", path = "/v1/comments/{id}", hasBody = true)
    Flowable<DBResultResponse> deleteComment(@Header("authorization") String token,
                                             @Path("id") int commentId,
                                             @Field("contentid") int contentid);


    /**
     * 댓글 수정
     * @param id
     * @param content
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("/v1/comments/{id}")
    Flowable<DBResultResponse> updateComment(@Header("authorization") String token,
                                             @Path("id") Integer id,
                                             @Field("content") String content);


    /**
     * 좋아요
     * @param id
     * @param userid
     * @return
     */
    //@FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("/v1/comments/{id}/hifive")
    Flowable<DBResultResponse> setLike(@Header("authorization") String token,
                                       @Path("id") int comment_id);

    //@FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("/v1/comments/{id}/unhifive")
    Flowable<DBResultResponse> setUnlike(@Header("authorization") String token,
                                         @Path("id") int comment_id);

    /**
     * 좋아버튼을 누른 유저 리스트
     * @param id
     * @return
     */
    @GET("/v1/comments/{id}/hifiveduser")
    Flowable<List<UserModel>> getLikers(@Path("id") int id);




    // *** Report ***

    @PUT("/v1/comments/{id}/reported")
    Flowable<DBResultResponse> setReported(@Header("authorization") String token, @Path("id") int id);




}
