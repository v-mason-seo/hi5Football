package com.ddastudio.hifivefootball_android.content_list;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.data.manager.CompetitionsManager;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostType;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.manager.BestManager;
import com.ddastudio.hifivefootball_android.data.manager.BoardsManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.manager.PlayersManager;
import com.ddastudio.hifivefootball_android.data.manager.TeamsManager;
import com.ddastudio.hifivefootball_android.content_list.model.BestContentsHeaderModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.content_viewer.ContentViewerActivity;
import com.ddastudio.hifivefootball_android.match_detail.MatchActivity;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.user_profile.UserProfileActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.content_viewer.CommentInputFragment;
import com.ddastudio.hifivefootball_android.ui.fragment.ItemListDialogFragment;

import org.parceler.Parcels;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 9. 4..
 */

public class ContentsListPresenter implements BaseContract.Presenter {

    int excludeMatchReport = 0;
    int excludeFootballNews = 0;

    ContentListFragment mView;
    BoardsManager mBoardManager;
    BestManager mBestManager;
    MatchesManager mMatchManager;
    PlayersManager mPlayerManager;
    TeamsManager mTeamManager;
    CompetitionsManager mCompetitionManager;

    @NonNull
    CompositeDisposable mComposite;

    BoardMasterModel mSelectedBoard;
    LiveData<BoardMasterModel> mLiveSelectedBoard;

    public ContentsListPresenter() {
        excludeMatchReport = 0;
        excludeFootballNews = 0;
    }

    @Override
    public void attachView(BaseContract.View view) {

        this.mView = (ContentListFragment)view;
        mComposite = new CompositeDisposable();
        mBoardManager = BoardsManager.getInstance();
        mBestManager = BestManager.getInstance();
        mMatchManager = MatchesManager.getInstance();
        mPlayerManager = PlayersManager.getInstance();
        mTeamManager = TeamsManager.getInstance();
        mCompetitionManager = CompetitionsManager.getInstance();
    }

    @Override
    public void detachView() {

        if ( mComposite != null )
            mComposite.clear();

    }

    public void onStop() {
        if ( mComposite != null )
            mComposite.clear();
    }
    /*---------------------------------------------------------------------------------------------*/

    public LiveData<BoardMasterModel> getLiveSelectedBoard() {

        mLiveSelectedBoard = AppDatabase.getInstance(mView.getActivity()).boardsDao().getLiveSelectedBoard();

        return mLiveSelectedBoard;
    }

    public BoardMasterModel getSelectedBoard() {
        return mSelectedBoard;
    }

    public void setSelectedBoard(BoardMasterModel board) {
        this.mSelectedBoard = board;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openContentViewActivity(ContentHeaderModel contentHeaderModel/*, PostType postType*/, int position) {

        Intent intent;
//        if ( contentHeaderModel.getBoardId() == 420 || contentHeaderModel.getBoardId() == 350 ) {
//
//            if ( contentHeaderModel.getLink() == null  ) {
//                Toasty.normal(mView.getContext(), "널입니다", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentHeaderModel.getLink()));
//            mView.startActivity(intent);
//        } else {
//            intent = new Intent(mView.getContext(), ContentViewerActivity.class);
//            intent.putExtra("ARGS_CONTENTS_HEADER_MODEL", Parcels.wrap(contentHeaderModel));
//            intent.putExtra("ARGS_POSTS_TYPE", postType);
//            intent.putExtra("ARGS_POSITION", position);
//            intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK| */Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            //mView.startActivity(intent);
//            mView.startActivityForResult(intent, ContentListFragment.REQUEST_CONTENT_VIEWER);
//        }

        intent = new Intent(mView.getContext(), ContentViewerActivity.class);
        intent.putExtra("ARGS_CONTENTS_HEADER_MODEL", Parcels.wrap(contentHeaderModel));
        //intent.putExtra("ARGS_POSTS_TYPE", postType.value());
        intent.putExtra("ARGS_POSITION", position);
        intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK| */Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //mView.startActivity(intent);
        mView.startActivityForResult(intent, ContentListFragment.REQUEST_CONTENT_VIEWER);

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

    public void openMatchActivity(int matchid) {
        Intent intent = new Intent(mView.getContext(), MatchActivity.class);
        //intent.putExtra("ARGS_MATCH", Parcels.wrap(match));
        intent.putExtra("ARGS_MATCH_ID", matchid);

        mView.startActivity(intent);
    }

    public void openUserProfile(UserModel user) {
        Intent intent = new Intent(mView.getContext(), UserProfileActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("ARGS_USER", Parcels.wrap(user));
        mView.startActivity(intent);
    }

    public void openCommentDialogFragment(int contentId) {
        CommentInputFragment commentInputFragment
                = CommentInputFragment.newInstance(CommentInputFragment.COMMENT_TYPE_BASIC, contentId);
        commentInputFragment.show(mView.getFragmentManager(), "CommentInput");
    }

    public void openHifiveDialogFragment(int contentId) {
        ItemListDialogFragment dialogFragment;
        dialogFragment = ItemListDialogFragment.newInstance(contentId, EntityType.TYPE_LIKE);
        dialogFragment.show(mView.getFragmentManager(), "Like");
    }

    public void openScrapDialogFragment(int contentId) {
        ItemListDialogFragment dialogFragment;
        dialogFragment = ItemListDialogFragment.newInstance(contentId, EntityType.TYPE_LIKE);
        dialogFragment.show(mView.getFragmentManager(), "Like");
    }

    /**
     * 팀 액티비티 열기
     * @param teamId
     */
    public void openTeamActivity(int teamId) {
        Intent intent = new Intent(mView.getContext(), TeamActivity.class);
        intent.putExtra("ARGS_TEAM_ID", teamId);

        mView.startActivity(intent);
    }

    /***
     * 플레이어 액티비티 열기
     * @param playerId
     * @param playerName
     */
    public void openPlayerActivity(int playerId, String playerName) {
        Intent intent = new Intent(mView.getContext(), PlayerActivity.class);
        intent.putExtra("ARGS_PLAYER_ID", playerId);
        intent.putExtra("ARGS_PLAYER_NAME", playerName);
        //intent.putExtra("ARGS_COMPETITION", Parcels.wrap(competition));

        mView.startActivity(intent);
    }


    /**
     * 로그인창 열기
     */
    public void openLoginActivity() {
        Intent intent = new Intent(mView.getActivity(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 데이터 로드
     * @param type
     * @param id
     * @param limit
     * @param offset
     */
    public void onLoadData(String type, int id, int limit, int offset) {
        switch (type) {
            case "P":
                // 플레이어
                onLoadPlayerContentList(id, limit, offset);
                break;
            case "M":
                // 매치
                onLoadMatchContentList(id, limit, offset);
                break;
            case "T":
                // 팀
                onLoadTeamContentList(id, limit, offset);
                break;
            case "C":
                onLoadCompetitionContentList(id, limit, offset);
                break;
            case "B":
                // 게시판
                onLoadContentList(id, limit, offset);
                break;
        }
    }

    public void onLoadContentList(int boardId, int limit, int offset) {

        String token = "";
        Flowable<List<ContentHeaderModel>> observable
                = mBoardManager.getBoardContentList(token, boardId, limit, offset)
                //.flatMap(items -> Flowable.fromIterable(items))
                //.doOnError(e -> mView.showErrorMessage("게시글 로딩중 오류가 발생했습니다\n" + e.getMessage()))
                ;

        mComposite.add(
                observable
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished3(items, offset > 0);
                                },
                                e -> {
                                    //mView.showErrorMessage("베스트 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
                                    //mView.hideLoading();
                                },
                                () -> {}
                        ));
    }

    public void onLoadMatchContentList(int matchId, int limit, int offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mMatchManager.onLoadMatchContentList(matchId, offset, limit);

        mComposite.add(
                observable
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished3(items, offset > 0);
                                },
                                e -> {
                                    //mView.showErrorMessage("매치정보를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
                                    //mView.hideLoading();
                                },
                                () -> { }
                        ));
    }

    /**
     * 선수 관련글 가져오기
     * @param playerId
     * @param limit
     * @param offset
     */
    public void onLoadPlayerContentList(int playerId, int limit, int offset) {

        String token = "";

        Flowable<List<ContentHeaderModel>> observable
                = mPlayerManager.onLoadPlayerContentList(token, playerId, null, limit, offset);

        mComposite.add(
                observable
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> mView.onLoadFinished3(items, offset > 0),
                                e -> {
                                    //mView.showErrorMessage("플레이어 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
                                    //mView.hideLoading();
                                }
                        ));
    }

    /**
     *
     * @param teamId

     * @param limit
     * @param offset
     */
    public void onLoadTeamContentList(int teamId, int limit, int offset) {

        String token = "";

        Flowable<List<ContentHeaderModel>> observable
                = mTeamManager.onLoadTeamContentList(token, teamId, null, limit, offset);

        mComposite.add(
                observable
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> mView.onLoadFinished3(items, offset > 0),
                                e -> {
                                    //mView.showErrorMessage("팀 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
                                    //mView.hideLoading();
                                }
                        ));
    }


    public void onLoadCompetitionContentList(int compId, int limit, int offset) {

        String token = "";

        Flowable<List<ContentHeaderModel>> observable
                = mCompetitionManager.onLoadCompetitionContentList(token, compId, limit, offset);

        mComposite.add(
                observable
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> mView.onLoadFinished3(items, offset > 0),
                                e -> {
                                    //mView.showErrorMessage("팀 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
                                    //mView.hideLoading();
                                }
                        ));
    }




    // --------------------------------------------------
    // [ 아래 코드는 사용안함 ]
    // --------------------------------------------------

//    public void onLoadNewData(int limit, int offset) {
//
//        // 현재 선택된 보드 정보가 없으면 세로 세팅해준다.
//        if ( mSelectedBoard == null ) {
//
//            // 디비에서 선택한 게시판 정보를 가져온다.
//            BoardMasterModel selectedBoard = AppDatabase.getInstance(mView.getActivity()).boardsDao().getSelectedBoard();
//
//            // 선택한 게시판 정보가 없으면 축구게시판(200)을 세팅해준다.
//            if ( selectedBoard != null ) {
//                mSelectedBoard = selectedBoard;
//            } else {
//                AppDatabase.getInstance(mView.getActivity()).boardsDao().clearAndUpdateSelectedBoard(200, 0);
//                return;
//            }
//        }
//
//        if ( mSelectedBoard.isSelectedBest() == 0) {
//            getBoardContentList(mSelectedBoard.getBoardId(), limit, offset);
//        } else {
//            getBestContentList2(mSelectedBoard.getBoardId(), "C");
//        }
//
//    }

//    public void onLoadData(PostBoardType boardType, String best_id, PostType postType, int relationId, int limit, int offset) {
//        mView.showLoading();
//
//        switch (postType) {
//
//            case BOARD_ALL:
//            case BOARD_SINGLE:
//                if ( boardType == PostBoardType.ALL_BOARD) {
//                    getBoardALLContentList(limit, offset);
//                } else {
//                    getBoardContentList(boardType.value(), limit, offset);
//                }
//                break;
//            case BEST_HIFIVE_ALL:
//            case BEST_HIFIVE_SINGLE:
//                if ( boardType == PostBoardType.ALL_BOARD) {
//                    //getAllBestContentList(best_id, "L", limit, offset);
//                    getAllBestContentList2("L");
//                } else {
//                    //getBestContentList(boardType.value(), best_id, "L", limit, offset);
//                    getBestContentList2(boardType.value(), "L");
//                }
//                break;
//            case BEST_COMMENT_ALL:
//            case BEST_COMMENT_SINGLE:
//                if ( boardType == PostBoardType.ALL_BOARD) {
//                    //getAllBestContentList(best_id, "C", limit, offset);
//                    getAllBestContentList2("C");
//                } else {
//                    //getBestContentList(boardType.value(), best_id, "C", limit, offset);
//                    getBestContentList2(boardType.value(), "C");
//                }
//                break;
//            case MATCH_RELATION:
//                onLoadMatchRelatedContent(relationId, limit, offset);
//                break;
//            case TEAM_RELATION:
//                onLoadTeamRelatedContent(relationId, boardType, limit, offset);
//                break;
//            case PLAYER_RELATION:
//                onLoadPlayerRelatedContent(relationId, boardType, limit, offset);
//                break;
//        }
//
//        /*// 게시글 데이터 조회
//        if ( postType == EntityType.POSTS_GENERAL) {
//
//            if ( boardType == PostBoardType.ALL_BOARD) {
//                getBoardALLContentList(limit, offset);
//            } else {
//                getBoardContentList(boardType.value(), limit, offset);
//            }
//            // 베스트 게시글 조회
//        } else if ( postType == EntityType.POSTS_COMMETS_BEST) {
//            if ( boardType == PostBoardType.ALL_BOARD) {
//                getAllBestContentList(best_id, "C", limit, offset);
//            } else {
//                getBestContentList(boardType.value(), best_id, "C", limit, offset);
//            }
//        } else if ( postType == EntityType.POSTS_LIKERS_BEST ) {
//            if ( boardType == PostBoardType.ALL_BOARD) {
//                getAllBestContentList(best_id, "L", limit, offset);
//            } else {
//                getBestContentList(boardType.value(), best_id, "C", limit, offset);
//            }
//        } else if ( postType == EntityType.POSTS_RELATION) {
//            onLoadMatchRelatedContent(relationId, limit, offset);
//        }*/
//    }
//
//
//    public void getBoardALLContentList(int limit, int offset) {
//
//        String token = App.getAccountManager().getHifiveAccessToken();
//        Flowable<List<ContentHeaderModel>> observable
//                = mBoardManager.getBoardALLContentList(token, getExcludeMatchReport(), getExcludeFootballNews(), limit, offset)
//                        .flatMap(items -> Flowable.fromIterable(items))
//                        .map(item -> {
//
//                            item.setPostType(PostType.BOARD_ALL);
//                            if ( item.getBoardId() == EntityType.BOARD_MATCH_REPORT ) {
//                                item.setItemType(ViewType.CONTENT_MATCH_REPORT_SMALL);
//                            }
//                            return item;
//
//                        }).toList().toFlowable()
//                        ;
//
//        mComposite.add(
//                observable
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .doOnError(e -> mView.showErrorMessage("게시글 로딩중 오류가 발생했습니다\n" + e.getMessage()))
//                        .subscribe(
//                                items -> mView.onLoadFinished(items),
//                                e -> mView.hideLoading(),
//                                () -> { }
//                        ));
//    }
//
//    public void getBoardContentList(int boardType, int limit, int offset/*, boolean loadMore*/) {
//
//        String token = App.getAccountManager().getHifiveAccessToken();
//        Flowable<List<ContentHeaderModel>> observable
//                = mBoardManager.getBoardContentList(token, boardType, limit, offset)
//                        .flatMap(items -> Flowable.fromIterable(items))
//                        .map(item -> {
//
//                            item.setPostType(PostType.BOARD_SINGLE);
//                            return item;
//
//                        }).toList().toFlowable()
//                        .doOnError(e -> mView.showErrorMessage("게시글 로딩중 오류가 발생했습니다\n" + e.getMessage()));
//
//        mComposite.add(
//                observable
//                        .delay(10, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                        items -> {
//                            mView.onLoadFinished3(items, offset > 0);
//                        },
//                        e -> {
//                            mView.showErrorMessage("베스트 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
//                            mView.hideLoading();
//                        },
//                        () -> {}
//                ));
//    }
//
//
//    public void getAllBestContentList(String best_id, String bestRoll, int limit, int offset/*, boolean loadMore*/) {
//
//        Flowable<List<BestContentsHeaderModel>> observable
//                = mBestManager.getALLBestContentList(best_id, bestRoll, limit, offset)
//                                .flatMap(items -> Flowable.fromIterable(items))
//                                .map(item -> {
//
//                                    if ( bestRoll.equals("C")) {
//                                        item.setItemType(ViewType.CONTENT_BEST_COMMENT);
//                                    } else {
//                                        item.setItemType(ViewType.CONTENT_BEST_HIFIVE);
//                                    }
//                                    return item;
//                                }).toList().toFlowable();
//
//        mComposite.add(
//                observable
//                        .delay(10, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .doOnError(e -> mView.showErrorMessage("베스트 게시글 로딩중 오류가 발생했습니다\n" + e.getMessage()))
//                        .subscribe(
//                        items -> mView.onLoadBestListFinished(items),
//                        e -> {
//                            mView.showErrorMessage("베스트 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
//                            mView.hideLoading();
//                        },
//                        () -> { }
//                ));
//    }
//
//    public void getBestContentList(int boardid, String best_id, String bestRoll, int limit, int offset) {
//
//
//        Flowable<List<BestContentsHeaderModel>> observable
//                = mBestManager.getBestContentList(boardid, best_id, bestRoll, limit, offset)
//                                .flatMap(items -> Flowable.fromIterable(items))
//                                .map(item -> {
//
//                                    if ( bestRoll.equals("C")) {
//                                        item.setItemType(ViewType.CONTENT_BEST_COMMENT);
//                                    } else {
//                                        item.setItemType(ViewType.CONTENT_BEST_HIFIVE);
//                                    }
//                                    return item;
//                                }).toList().toFlowable()
//                                .doOnError(e -> mView.showErrorMessage("베스트 게시글 로딩중 오류가 발생했습니다\n" + e.getMessage()));;
//
//        mComposite.add(
//                observable
//                        .delay(10, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                items -> mView.onLoadBestListFinished(items),
//                                e -> mView.hideLoading(),
//                                () -> { }
//                        ));
//    }
//
//
//    public void getAllBestContentList2(String bestRoll) {
//
//        String token = App.getAccountManager().getHifiveAccessToken();
//        Flowable<List<ContentHeaderModel>> observable
//                = mBestManager.getALLBestContentList2(token, bestRoll)
//                .flatMap(items -> Flowable.fromIterable(items))
//                .map(item -> {
//
//                    if ( bestRoll.equals("C")) {
//                        item.setItemType(ViewType.CONTENT_BEST_COMMENT);
//                    } else {
//                        item.setItemType(ViewType.CONTENT_BEST_HIFIVE);
//                    }
//                    return item;
//                }).toList().toFlowable();
//
//        mComposite.add(
//                observable
//                        .delay(10, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .doOnError(e -> mView.showErrorMessage("베스트 게시글 로딩중 오류가 발생했습니다\n" + e.getMessage()))
//                        .subscribe(
//                                items -> mView.onLoadBestListFinished2(items),
//                                e -> {
//                                    mView.showErrorMessage("베스트 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
//                                    mView.hideLoading();
//                                },
//                                () -> { }
//                        ));
//    }
//
//    public void getBestContentList2(int boardid, String bestRoll) {
//
//        String token = App.getAccountManager().getHifiveAccessToken();
//        Flowable<List<ContentHeaderModel>> observable
//                = mBestManager.getBestContentList2(token, boardid, bestRoll)
//                .flatMap(items -> Flowable.fromIterable(items))
//                .map(item -> {
//
//                    if ( bestRoll.equals("C")) {
//                        item.setItemType(ViewType.CONTENT_BEST_COMMENT);
//                    } else {
//                        item.setItemType(ViewType.CONTENT_BEST_HIFIVE);
//                    }
//                    return item;
//                }).toList().toFlowable()
//                .doOnError(e -> mView.showErrorMessage("베스트 게시글 로딩중 오류가 발생했습니다\n" + e.getMessage()));;
//
//        mComposite.add(
//                observable
//                        .delay(10, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                items -> mView.onLoadBestListFinished2(items),
//                                e -> mView.hideLoading(),
//                                () -> { }
//                        ));
//    }
//
//    /*---------------------------------------------------------------------------------------------*/
//
//    public void onLoadMatchRelatedContent(int matchId, int limit, int offset/*, boolean loadMore*/) {
//
//        Flowable<List<ContentHeaderModel>> observable
//                = mMatchManager.onLoadMatchContentList(matchId, offset, limit);
//
//        mComposite.add(
//                observable
//                        .delay(10, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                items -> {
//                                    mView.onLoadFinished(items);
//                                },
//                                e -> {
//                                    mView.showErrorMessage("매치정보를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
//                                    mView.hideLoading();
//                                },
//                                () -> { }
//                        ));
//    }
//
//    /**
//     * 선수 관련글 가져오기
//     * @param playerId
//     * @param boardType
//     * @param limit
//     * @param offset
//     */
//    public void onLoadPlayerRelatedContent(int playerId, PostBoardType boardType, int limit, int offset) {
//
//        String token = App.getAccountManager().getHifiveAccessToken();
//
//        Flowable<List<ContentHeaderModel>> observable
//                = mPlayerManager.onLoadPlayerContentList(token, playerId, boardType == PostBoardType.ALL_BOARD ? null : boardType.value(), limit, offset)
//                                .flatMap(items -> Flowable.fromIterable(items))
//                                .map(item -> {
//
//                                    item.setPostType(PostType.PLAYER_RELATION);
//                                    return item;
//
//                                }).toList().toFlowable();
//
//        mComposite.add(
//                observable
//                        .delay(10, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                items -> mView.onLoadFinished(items),
//                                e -> {
//                                    mView.showErrorMessage("플레이어 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
//                                    mView.hideLoading();
//                                }
//                        ));
//    }
//
//
//    /**
//     *
//     * @param teamId
//     * @param boardType
//     * @param limit
//     * @param offset
//     */
//    public void onLoadTeamRelatedContent(int teamId, PostBoardType boardType, int limit, int offset) {
//
//        String token = App.getAccountManager().getHifiveAccessToken();
//
//        Flowable<List<ContentHeaderModel>> observable
//                = mTeamManager.onLoadTeamContentList(token, teamId, boardType == PostBoardType.ALL_BOARD ? null : boardType.value(), limit, offset)
//                                .flatMap(items -> Flowable.fromIterable(items))
//                                .map(item -> {
//
//                                    item.setPostType(PostType.TEAM_RELATION);
//                                    return item;
//
//                                }).toList().toFlowable();
//
//        mComposite.add(
//                observable
//                        .delay(10, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                items -> mView.onLoadFinished(items),
//                                e -> {
//                                    mView.showErrorMessage("팀 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
//                                    mView.hideLoading();
//                                }
//                        ));
//    }
}
