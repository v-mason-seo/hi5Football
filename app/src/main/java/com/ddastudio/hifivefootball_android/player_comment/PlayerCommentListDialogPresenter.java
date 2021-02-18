package com.ddastudio.hifivefootball_android.player_comment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.manager.ArenaManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.manager.PlayersManager;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.PlayerCommentModel;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.user_profile.UserProfileActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.player_comment.PlayerCommentListDialogFragment;

import org.parceler.Parcels;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2018. 1. 10..
 */

public class PlayerCommentListDialogPresenter implements BaseContract.Presenter {

    /**
     * MODE_MATCH_PLAYER(0)   : 하나의 매치에 대한 선수 한줄평 리스트
     * MODE_OVERALL_PLAYER(1) : 선수에 대한 모든 한줄평 리스트
     */
    int mode;
    int mMatchId;
    int mPlayerId;

    PlayerCommentListDialogFragment mView;
    MatchesManager mMatchesManager;
    PlayersManager mPlayerManager;
    ArenaManager mArenaManager;

    @NonNull
    CompositeDisposable mComposite;

    public PlayerCommentListDialogPresenter(int mode, int matchid, int playerId) {
        this.mode = mode;
        this.mMatchId = matchid;
        this.mPlayerId = playerId;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (PlayerCommentListDialogFragment)view;
        this.mMatchesManager = MatchesManager.getInstance();
        this.mPlayerManager = PlayersManager.getInstance();
        this.mArenaManager = ArenaManager.getInstance();
        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMatchId() {
        return mMatchId;
    }

    public int getPlayerId() {
        return mPlayerId;
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 로그인 프로필 액티비티 샐행
     */
    public void openLoginActivity() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    /**
     * 사용자 프로필 액티비티 실행
     * @param user
     */
    public void openUserProfile(UserModel user) {
        Intent intent = new Intent(mView.getContext(), UserProfileActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("ARGS_USER", Parcels.wrap(user));
        mView.startActivity(intent);
    }

    /**
     * 선수 프로필 액티비티 실행
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

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadPlayerComment() {

//        mView.showLoading();
//
//        Flowable<List<ContentHeaderModel>> observable
//                = mArenaManager.onLoadPlayerComment(mMatchId, mPlayerId);
//
//        mComposite.add(
//                observable.doOnError(e -> Timber.d("[onLoadPlayerComment] error : %s", e.getMessage()))
//                        .doOnNext(items -> Timber.d("[onLoadPlayerComment] items size : %d", items.size()))
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(item -> {
//                                    //mView.onLoadFinishedPlayerComment(item);
//                                },
//                                e -> mView.hideLoading(),
//                                () ->{} )
//        );
    }

    public void onLoadPlayerComment(Integer matchId, Integer playerId) {

        mView.showLoading();

        Flowable<List<PlayerRatingInfoModel>> observable
                = mPlayerManager.onLoadPlayerComments(playerId, matchId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(items -> {})
                        .subscribe(items -> {
                                    mView.onLoadFinishedPlayerComment(items);
                                    //Toast.makeText(mView.getContext(), "[onLoadPlayerComment] item size : " + items.size(), Toast.LENGTH_LONG).show();
                                },
                                e -> mView.hideLoading(),
                                () ->{} )
        );
    }

    public void onLoadPlayerOverallComment(int limit, int offset) {

//        mView.showLoading();
//
//        Flowable<List<PlayerCommentModel>> observable
//                = mPlayerManager.onLoadPlayerComments(mPlayerId, null);
//
//        mComposite.add(
//                observable.doOnError(e -> Timber.d("[onLoadPlayerComment] error : %s", e.getMessage()))
//                        .doOnNext(items -> Timber.d("[onLoadPlayerComment] items size : %d", items.size()))
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(item -> {
//                                    //mView.onLoadFinishedPlayerComment(item);
//                                },
//                                e -> mView.hideLoading(),
//                                () ->{} )
//        );
    }

    /**
     *
     * @param position 리사이클뷰에서 위치
     * @param playerComment
     * @param hifiveCount
     */
    public void setPlayerCommentHifive(int position,
                                       PlayerCommentModel playerComment,
                                       int hifiveCount) {

        String token;
        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

//        mView.showLoading();
//        // 서버 업데이트 하기 전 화면먼저 갱신한다.
//        mView.updateHifive(position, playerComment, hifiveCount);
//
//        Flowable<DBResultResponse> observable
//                = mMatchesManager.setPlayerCommentHifive(token, playerComment.getPlayerCommentId(), hifiveCount);
//
//        mComposite.add(
//                observable.observeOn(AndroidSchedulers.mainThread())
//                        .doOnError(err -> mView.showErrorMessage(err.getMessage()))
//                        .subscribe(
//                                items -> {
//                                    if ( items.getResult() == 1 ) {
//                                        mView.showMessage(items.getMessage());
//                                    } else {
//                                        // 업데이트에 실패하면 원래대로 되돌린다.
//                                        mView.updateHifive(position, playerComment, hifiveCount*-1);
//                                        mView.showErrorMessage(items.getMessage());
//                                    }
//                                },
//                                e -> {
//                                    mView.updateHifive(position, playerComment, hifiveCount*-1);
//                                    mView.hideLoading();
//                                },
//                                () -> mView.hideLoading()
//                        ));
    }

}
