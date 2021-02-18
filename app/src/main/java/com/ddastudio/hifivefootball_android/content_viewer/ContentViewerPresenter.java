package com.ddastudio.hifivefootball_android.content_viewer;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.manager.CommentsManager;
import com.ddastudio.hifivefootball_android.data.manager.ContentManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchTalkManager;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.community.MatchTalkModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.content_editor.EditorActivity;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.match_detail.MatchActivity;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.user_profile.UserProfileActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.content_list.ContentListFragment;

import org.parceler.Parcels;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * Created by hongmac on 2017. 9. 8..
 */

public class ContentViewerPresenter implements BaseContract.Presenter {

    public static final int COMMENT_SCROLL_POSITION_TOP = 0;
    public static final int COMMENT_SCROLL_POSITION_BOTTOM = Integer.MAX_VALUE;

    ContentViewerActivity mView;
    ContentManager mContentManager;
    CommentsManager mCommentsManager;
    MatchTalkManager mMatchTalkManager;
    CompositeDisposable composite;

    //ContentHeaderModel mHeaderData;
    ContentHeaderModel mContentData;

    public ContentViewerPresenter(/*ContentHeaderModel headerData, */ContentHeaderModel contentModel) {
        //this.mHeaderData = headerData;
        this.mContentData = contentModel;
    }
    @Override
    public void attachView(BaseContract.View view) {
        mView = (ContentViewerActivity)view;
        composite = new CompositeDisposable();
        mContentManager = ContentManager.getInstance();
        mMatchTalkManager = MatchTalkManager.getInstance();
        mCommentsManager = CommentsManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( composite != null ) {
            composite.clear();
        }
    }

    /*----------------------------------------------------------------------------------------------*/

    public ContentHeaderModel getContentData() {
        return mContentData;
    }

    public void setContentData(ContentHeaderModel contentData) {
        this.mContentData = contentData;
    }

    public void openLoginActivity() {
        Intent intent = new Intent(mView, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    public void openUserProfile(UserModel user) {
        Intent intent = new Intent(mView.getApplicationContext(), UserProfileActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("ARGS_USER", Parcels.wrap(user));
        mView.startActivity(intent);
    }


    /**
     * 매치정보액티비티 열기
     * @param match
     */
    public void openMatchActivity(MatchModel match) {
        Intent intent = new Intent(mView.getApplicationContext(), MatchActivity.class);
        intent.putExtra("ARGS_MATCH", Parcels.wrap(match));

        mView.startActivity(intent);
    }


    /**
     * 팀 액티비티 열기
     * @param teamId
     */
    public void openTeamActivity(int teamId) {
        Intent intent = new Intent(mView.getApplicationContext(), TeamActivity.class);
        intent.putExtra("ARGS_TEAM_ID", teamId);

        mView.startActivity(intent);
    }

    /***
     * 플레이어 액티비티 열기
     * @param playerId
     * @param playerName
     */
    public void openPlayerActivity(int playerId, String playerName) {
        Intent intent = new Intent(mView.getApplicationContext(), PlayerActivity.class);
        intent.putExtra("ARGS_PLAYER_ID", playerId);
        intent.putExtra("ARGS_PLAYER_NAME", playerName);
        //intent.putExtra("ARGS_COMPETITION", Parcels.wrap(competition));

        mView.startActivity(intent);
    }

    public void openLinkActivity(String url) {
        try {
            Uri link = Uri.parse(url);

            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            mView.startActivity(intent);
        } catch (Exception ex ) {
            mView.showErrorMessage("올바르지 않은 링크주소입니다\n" + ex.getMessage());
        }
    }

    public void openContentViewActivity(ContentHeaderModel contentHeaderModel) {

        Intent intent;
        intent = new Intent(mView.getApplicationContext(), ContentViewerActivity.class);
        intent.putExtra("ARGS_CONTENTS_HEADER_MODEL", Parcels.wrap(contentHeaderModel));
        intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK| */Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, ContentListFragment.REQUEST_CONTENT_VIEWER);

    }

    public void openCommentDialogFragment(int contentId) {
        CommentInputFragment commentInputFragment
                = CommentInputFragment.newInstance(CommentInputFragment.COMMENT_TYPE_BASIC, contentId);
        commentInputFragment.show(mView.getSupportFragmentManager(), "CommentInput");
    }

    /**
     * 글쓰기 액티비티 실행
     */
    public void openTextEditor() {

        if (!App.getAccountManager().isAuthorized()) {
            mView.showLoginSnackBar();
            return;
        }

        if ( !App.getAccountManager().isSameUser(mContentData.getUserName())) {
            mView.showMessage("본인의 글만 수정할 수 있습니다.");
            return;
        }

        Intent intent = new Intent(mView, EditorActivity.class);
        // 신규 수정 모드
        intent.putExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.EDIT.value());
        intent.putExtra("ARGS_BOARD_TYPE", mContentData.getBoardId());
        intent.putExtra("ARGS_CONTENT", Parcels.wrap(getContentData()));
        // CellType
        intent.putExtra("ARGS_CELL_TYPE", getContentData().getCellType());
        // PlayerId, CellTyped : PostCellType.PLAYER_TALK 일경우 반드시 플레이어 아이디를 넘겨야 한다.
        intent.putExtra("ARGS_PLAYER", Parcels.wrap(getContentData().getPlayer()));
        // PlayerId, CellTyped : PostCellType.Team_TALK 일경우 반드시 플레이어 아이디를 넘겨야 한다.
        intent.putExtra("ARGS_TEAM", Parcels.wrap(getContentData().getTeam()));

        mView.startActivityForResult(intent, 100);
    }

    /*----------------------------------------------------------------------------------------------*/

    public void onLoadData(Integer contentId) {

        mView.showLoading();
        String token = App.getAccountManager().getHifiveAccessToken();

        composite.add(
            Flowable.timer(10, TimeUnit.MILLISECONDS)
                .concatMap(i -> getContentObservable(token, contentId))
                .concatMap(val -> {
                    if ( val.getMatch() != null && val.getMatch().getMatchId() > 0) {
                        return getPlayerRatingsObservalbe(token, contentId)
                                .flatMap(val2 -> getMatchTalks(token, mContentData.getMatch().getMatchId(), mContentData.getUserName(), 5, 0));
                    }

                    return Flowable.just(mContentData);
                })
                .concatMap(val -> getRelationContentObservable(token, contentId))
                .concatMap(val -> getRelationMatchObservable(contentId))
                .concatMap(val -> {
                    Flowable<List<CommentModel>> commentObservable = getCommentObservable(token, contentId);
                    return commentObservable;
                })
                .concatMap(val -> Flowable.just(true))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ret -> {},
                        e -> {
                            mView.hideLoading();
                        },
                        () -> mView.hideLoading()
            )
        );
    }

    /**
     * [1] 게시글 내용을 가져온다.
     * @param token
     * @param contentId
     * @return
     */
    private Flowable<ContentHeaderModel> getContentObservable(String token, Integer contentId) {

        Flowable<ContentHeaderModel> observable1
                = mContentManager.getContent(token, contentId)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(content -> {
                                    if ( content != null ) {
                                        setContentData(content);
                                    } else {
                                        mContentData.setContent(content.getContent());
                                    }
                                    mView.onLoadFinishedBody(content);
                                })
                                .doOnError(err -> mView.showMessage("[1]-게시글 상세정보 로드 실패"));

        return observable1;
    }

    /**
     * [2] 선수평점 데이터 가져오기
     * @param token
     * @param contentId
     * @return
     */
    private Flowable<List<PlayerRatingInfoModel>> getPlayerRatingsObservalbe(String token, Integer contentId) {

        Flowable<List<PlayerRatingInfoModel>> observable
                = mContentManager.getRelationPlayerRatings(token, contentId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(rating -> {
                    mView.onLoadFinishedPlayerRatings(rating);
                })
                .doOnError(err -> mView.showMessage("[2]-선수평가 로드 실패"));

        return observable;
    }

    private Flowable<List<MatchTalkModel>> getMatchTalks(String token,
                                                         Integer matchId,
                                                         String userName,
                                                         Integer limit,
                                                         Integer offset) {

        Flowable<List<MatchTalkModel>> observable
                = mMatchTalkManager.onLoadMatchTalks(token, matchId, userName, limit, offset)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(talk -> {
                    mView.onLoadFinishedMatchTalks(talk);
                });

        return observable;
    }

    /**
     * [3] 관련글 가져오기
     * @param token
     * @param contentId
     * @return
     */
    private Flowable<List<ContentHeaderModel>> getRelationContentObservable(String token, Integer contentId) {

        Flowable<List<ContentHeaderModel>> observable
                = mContentManager.getRelationContent(token, contentId)
                .flatMap(items -> Flowable.fromIterable(items))
                .map(item -> {
                    item.setItemType(ViewType.CONTENT_GENERAL);
                    return item;
                }).toList().toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(relation -> {
                    mView.onLoadFinishedRelationContent(relation);
                })
                .doOnError(err -> mView.showMessage("[3]-관련글 로드 실패"));

        return observable;
    }

    /**
     * [4] 관련 경기정보 가져오기
     * @param contentId
     * @return
     */
    private Flowable<List<MatchModel>> getRelationMatchObservable(Integer contentId) {

        Flowable<List<MatchModel>> observable
                = mContentManager.getRelationMatches(contentId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(match -> {
                    mView.onLoadFinishedRelationMatches(match);
                })
                .doOnError(err -> mView.showMessage("[4]-관련경기정보 로드 실패"));

        return observable;
    }

    /**
     * [5] 댓글 가져오기
     * @param token
     * @param contentId
     * @return
     */
    private Flowable<List<CommentModel>> getCommentObservable(String token, Integer contentId) {

        Flowable<List<CommentModel>> observable
                = mCommentsManager.getComments(token, contentId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext( comments -> {
                    mView.onLoadFinishedComments(comments, 0);
                })
                .doOnError(err -> mView.showMessage("[5]-댓글 로드 실패"));

        return observable;
    }

    /*----------------------------------------------------------------------------------------------*/

    //-------------------------------------------------------
    // *** Content ***
    //-------------------------------------------------------

    public void getContent() {

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<ContentHeaderModel> observable
                = mContentManager.getContent(token, mContentData.getContentId());

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            item -> {
                                mContentData.setContent(item.getContent());
                                mView.onLoadFinishedBody(item);
                        },
                        e -> mView.showErrorMessage("데이터 로드중 오류가 발생했습니다.\n" + e.getMessage()),
                        () -> {}
                ));
    }

    public void getRelationMatches() {

        Flowable<List<MatchModel>> observable
                = mContentManager.getRelationMatches(mContentData.getContentId());

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedRelationMatches(items);
                                },
                                e -> {
                                    mView.showErrorMessage(e.getMessage());
                                },
                                () -> {}
                        ));
    }

    public void getRelationContent() {

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<ContentHeaderModel>> observable
                = mContentManager.getRelationContent(token, mContentData.getContentId())
                .flatMap(items -> Flowable.fromIterable(items))
                .map(item -> {
                    item.setItemType(ViewType.CONTENT_GENERAL);
                    return item;
                }).toList().toFlowable();

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedRelationContent(items);
                                },
                                e -> {
                                    mView.showErrorMessage(e.getMessage());
                                },
                                () -> {}
                        ));
    }

    /**
     * 게시글 삭제
     */
    public void deleteContent() {

        String token;

        if ( App.getAccountManager().isAuthorized() ) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showLoginSnackBar();
            return;
        }

        if ( !App.getAccountManager().isSameUser(mContentData.getUserName())) {
            mView.showMessage("본인의 글만 삭제할 수 있습니다.");
            return;
        }

        Flowable<DBResultResponse> observable
                = mContentManager.deleteContent(token, mContentData.getContentId());

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            result -> {
                                if ( result.getResult() > 0 ) {
                                    getContentData().setDeleted(true);
                                    mView.showMessage(mView.getResources().getString(R.string.delete_content_success));
                                    mView.finish();
                                }
                        },
                        e -> {
                            mView.showErrorMessage(mView.getResources().getString(R.string.delete_content_fail) + e.getMessage());
                        },
                        () -> { }
                ));
    }

    //-------------------------------------------------------
    // *** 게시글 스크랩 ***
    //-------------------------------------------------------

    public void updateScrap() {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showLoginSnackBar();
            return;
        }

        if ( mContentData != null) {
            if(mContentData.isScraped())  {
                deleteScrap(token);
            } else {
                postScrap(token);
            }
        }
    }

    /**
     * 게시글 스크랩
     * @param token
     */
    public void postScrap(String token) {

        mView.updateScrap(true);
        Flowable<DBResultResponse> observable
                = mContentManager.postScrap(token, mContentData.getContentId());

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            result -> {
                                if ( result.getResult() > 0) {
                                    mContentData.setScraped(true);
                                    mContentData.addScrap();
                                    mView.updateScrap(true);
                                    mView.bindHifiveNScrap(mContentData);
                                } else {
                                    // TODO: 2017. 8. 18. 정상적으로 처리가 되지 않으면 메시지 또는 다시 시도 같은 처리로직이 필요함
                                }
                        },
                        e -> {
                            mContentData.setScraped(false);
                            mView.updateScrap(false);
                            mView.showErrorMessage("스크랩 오류가 발생했습니다\n"+e.getMessage());
                        },
                        () -> { }
                ));
    }

    /**
     * 게시글 스크랩 취소
     * @param token
     */
    public void deleteScrap(String token) {

        mView.updateScrap(false);

        Flowable<DBResultResponse> observable
                = mContentManager.deleteScrap(token, mContentData.getContentId());

        composite.add(
                observable.subscribe(
                        result -> {
                            if ( result.getResult() > 0 ) {
                                mContentData.setScraped(false);
                                mContentData.minScrap();
                                mView.updateScrap(false);
                                mView.bindHifiveNScrap(mContentData);
                            } else {
                                //mView.showMessage(result.getInfo());
                            }
                       },
                        e -> {
                            mView.showErrorMessage("스크랩 해제 오류가 발생했습니다.\n"+e.getMessage());
                            mContentData.setScraped(true);
                            mView.updateScrap(true);
                        },
                        () -> { }
                ));
    }


    //-------------------------------------------------------
    // *** 게시글 하이파이브 (좋아요) ***
    //-------------------------------------------------------

    public void setContentLike(int hifive_count) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {

            mView.showLoginSnackBar();
            return;
        }

        if ( App.getAccountManager().isSameUser(mContentData.getUserName())) {
            mView.showMessage("본인의 글에는 하이파이브를 할 수 없습니다");
            return;
        }

        mView.updateLike(true);

        Flowable<DBResultResponse> observable
                = mContentManager.setLike(token, mContentData.getContentId(), hifive_count);

        composite.add(
                observable.subscribe(
                        result -> {
                            if ( result.getResult() > 0) {
                                mContentData.setLiked(1);
                                mContentData.addLikes();
                                mView.bindHifiveNScrap(mContentData);
                                mView.showMessage("하이파이브 +1");
                            } else {
                                // TODO: 2017. 8. 18. 정상적으로 처리가 되지 않으면 메시지 또는 다시 시도 같은 처리로직이 필요함
                            }
                        },
                        e -> {
                            mView.showErrorMessage("하이파이브 오류가 발생했습니다.\n"+e.getMessage());
                            mView.updateLike(mContentData.isLiked());
                        },
                        () -> { }
                ));
    }

    public void setContentUnLike() {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showLoginSnackBar();
            return;
        }

        mView.updateLike(false);

        Flowable<DBResultResponse> observable
                = mContentManager.setUnlike(token, mContentData.getContentId());

        composite.add(
                observable.subscribe(
                        result -> {

                            if ( result.getResult() > 0 ) {
                                mContentData.setLiked(0);
                                mContentData.setLikers(0);
                                mView.bindHifiveNScrap(mContentData);

                            } else {
                                //mView.showMessage(result.getInfo());
                            }
                            mView.showMessage("하이파이브를 취소했습니다.");
                        },
                        e -> {
                            mView.updateLike(mContentData.isLiked());
                            mView.showErrorMessage("하이파이브 취소 오류가 발생했습니다.\n"+e.getMessage());
                        },
                        () -> { }
                ));
    }

    //-------------------------------------------------------
    // *** Comment ***
    //-------------------------------------------------------

    public void createComment(int parentId,
                              int groupId,
                              int depth,
                              String content,
                              int scrollPosition) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showLoginSnackBar();
            return;
        }

        Flowable<DBResultResponse> observable
                = mCommentsManager.createComment(token, mContentData.getContentId(), parentId, groupId, depth, content);

        composite.add(
                observable.subscribe(
                        message ->  getComments(scrollPosition),
                        e -> mView.showErrorMessage("댓글 입력중 오류가 발생했습니다.\n" + e.getMessage()),
                        () -> {}
                ));
    }

    /**
     * 댓글 정보를 가져온다.
     */
    public void getComments(int scrollToPosition) {

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<CommentModel>> observable
                = mCommentsManager.getComments(token, mContentData.getContentId());

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> mView.onLoadFinishedComments(items, scrollToPosition),
                                e -> mView.showErrorMessage("댓글을 가져오는중 오류가 발생했습니다.\n" + e.getMessage()),
                                () -> {}
                ));
    }

    /**
     * 댓글삭제
     */
    public void deleteComment(CommentModel comment, Integer position) {

        if ( comment.isDeleted() ) {
            mView.showMessage("삭제된 댓글입니다");
            return;
        }

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showLoginSnackBar();
            return;
        }

        Flowable<DBResultResponse> observable
                = mCommentsManager.deleteComment(token, comment.getCommentId(), mContentData.getContentId());

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item -> {
                                    getComments(position);
                                    mView.showMessage("댓글이 삭제되었습니다");
                                },
                                // TODO: 2017. 8. 18. 삭제를 취소할 수 있는 기능이 필요함
                                e -> mView.showErrorMessage("댓글삭제 오류\n" + e.getMessage()),
                                () -> { }
                        ));
    }


    /**
     * 댓글수
     * @param commentId
     * @param body
     * @param position
     */
    public void editComment(Integer commentId, String body, int position) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showLoginSnackBar();
            return;
        }

        Flowable<DBResultResponse> observable
                = mCommentsManager.updateComment(token, commentId, body);

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item -> {
                                    getComments(position);
                                    mView.showMessage("댓글수정 완료");
                                },
                                // TODO: 2017. 8. 18. 삭제를 취소할 수 있는 기능이 필요함
                                e -> {
                                    mView.showErrorMessage("댓글수정 오류\n" + e.getMessage());
                                },
                                () -> { }
                        ));
    }

    /**
     * 댓글 좋아요
     * @param commentModel
     */
    public void setCommentLike(CommentModel commentModel, int position) {

        if ( commentModel.isDeleted() ) {
            mView.showMessage("삭제된 댓글에는 하이파이브를 할 수 없습니다");
            return;
        }

        if ( App.getAccountManager().isSameUser(commentModel.getUser().getUsername())) {
            mView.showMessage("본인의 댓글은 하이파이브를 할 수 없습니다");
            return;
        }

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showLoginSnackBar();
            return;
        }

        Flowable<DBResultResponse> observable = mCommentsManager.setLike(token, commentModel.getCommentId());

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                        message -> { },
                        e -> mView.showErrorMessage("댓글 하이파이브 오류가 발생했습니다.\n" + e.getMessage()),
                        () -> {
                            commentModel.setLiked(1);
                            commentModel.addLikers();
                            mView.notifyCommentItemChanged(position);
                        }
                ));
    }


    /**
     * 댓글 좋아요 취소
     * @param commentModel
     */
    public void setCommentUnLike(CommentModel commentModel, int position) {

        if ( commentModel.isDeleted() ) {
            mView.showMessage("삭제된 댓글에는 하이파이브 취소를 할 수 없습니다");
            return;
        }

        if ( App.getAccountManager().isSameUser(commentModel.getUser().getUsername())) {
            mView.showMessage("본인의 댓글은 하이파이브를 할 수 없습니다");
            return;
        }

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showLoginSnackBar();
            return;
        }

        Flowable<DBResultResponse> observable = mCommentsManager.setUnlike(token, commentModel.getCommentId());

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                message -> {},
                                e -> mView.showErrorMessage("댓글 하이파이브 취소 오류가 발생했습니다.\n" + e.getMessage()),
                                () -> {
                                    commentModel.setLiked(0);
                                    commentModel.minLikers();
                                    mView.notifyCommentItemChanged(position);

                                }
                        ));
    }

    public void setCommentReport(CommentModel commentModel, int position) {

        if ( commentModel.isDeleted() ) {
            mView.showMessage("삭제된 댓글은 취소할 수 없습니다");
            return;
        }

        if ( App.getAccountManager().isSameUser(commentModel.getUser().getUsername())) {
            mView.showMessage("본인의 댓글은 신고할 수 없습니다");
            return;
        }

        mView.showLoading();
        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showLoginSnackBar();
            return;
        }

        Flowable<DBResultResponse> observable
                = mCommentsManager.setReported(token, commentModel.getCommentId());

        composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                message -> {
                                    if ( message.getResult() > 0 ) {
                                        commentModel.setReported(1);
                                        mView.notifyCommentItemChanged(position);
                                        mView.showMessage("신고 처리되었습니다.");
                                    }
                                },
                                e -> {
                                    mView.showErrorMessage("신고 처리중 오류가 발생했습니다\n" + e.getMessage());
                                    mView.hideLoading();
                                },
                                () -> mView.hideLoading()
                        ));
    }


    //-------------------------------------------------------
    // *** 매치리포트 ***
    //-------------------------------------------------------

    public void onLoadRelationPlayerRatings(int contentId) {

        mView.showLoading();

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<PlayerRatingInfoModel>> observable
                = mContentManager.getRelationPlayerRatings(token, contentId);

        composite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(items -> {})
                        .subscribe(item -> {
                                    mView.onLoadFinishedPlayerRatings(item);
                                },
                                e -> mView.hideLoading(),
                                () ->{} )
        );
    }


    //-------------------------------------------------------
    // *** 매치토크 ***
    //-------------------------------------------------------

    public void onLoadMatchTalks(Integer matchId,
                                 String userName,
                                 Integer limit,
                                 Integer offset) {

        mView.showLoading();

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<MatchTalkModel>> observable
                = mMatchTalkManager.onLoadMatchTalks(token, matchId, userName, limit, offset);

        composite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(items -> {})
                        .subscribe(item -> mView.onLoadFinishedMatchTalks(item),
                                e -> mView.hideLoading(),
                                () ->{} )
        );
    }

    //-------------------------------------------------------

}
