package com.ddastudio.hifivefootball_android.player_comment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.data.manager.ArenaManager;
import com.ddastudio.hifivefootball_android.data.manager.ContentManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.manager.PlayersManager;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.player_rating.PlayerRatingActivity;
import com.ddastudio.hifivefootball_android.user_profile.UserProfileActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.player_comment.PlayerCommentFragment;

import org.parceler.Parcels;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2018. 1. 7..
 */

public class PlayerCommentPresenter implements BaseContract.Presenter {

    int dataType;
    int contentId;
    MatchModel mMatch;
    PlayerCommentFragment mView;
    MatchesManager mMatchesManager;
    ArenaManager mArenaManager;
    ContentManager mContentManager;
    PlayersManager mPlayersManager;

    @NonNull
    CompositeDisposable mComposite;

    public PlayerCommentPresenter(MatchModel match) {

        this.mMatch = match;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (PlayerCommentFragment)view;
        this.mMatchesManager = MatchesManager.getInstance();
        this.mArenaManager = ArenaManager.getInstance();
        this.mContentManager = ContentManager.getInstance();
        this.mPlayersManager = PlayersManager.getInstance();

        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }
    /*---------------------------------------------------------------------------------------------*/

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public MatchModel getMatch() {
        return mMatch;
    }

    public void setMatch(MatchModel match) {
        this.mMatch = match;
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

    public void openPlayerRatingActivity(int selectedTeamId, int selectedPlayerId) {
        Intent intent = new Intent(mView.getContext(), PlayerRatingActivity.class);

        intent.putExtra("ARGS_MATCH_DATA", Parcels.wrap(mMatch));
        intent.putExtra("ARGS_SELECTED_TEAM_ID", selectedTeamId);
        intent.putExtra("ARGS_SELECTED_PLAYER_ID", selectedPlayerId);
        //intent.putExtra("ARGS_HOME_LINEUP", Parcels.wrap(localTeamList));
        //intent.putExtra("ARGS_AWAY_LINEUP", Parcels.wrap(visitorTeamList));

        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/


    public void onLoadRelationPlayerRatings(int contentId) {

        mView.showLoading();

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<PlayerRatingInfoModel>> observable
                = mContentManager.getRelationPlayerRatings(token, contentId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(items -> {})
                        .subscribe(item -> {
                                    mView.onLoadFinishedPlayerRatings(item);
                                },

                                e -> mView.hideLoading(),
                                () ->{} )
        );
    }


    public void onLoadPlayerComment(Integer matchId) {

        mView.showLoading();

        Flowable<List<PlayerRatingInfoModel>> observable
                = mMatchesManager.onLoadPlayerComments(matchId);

        mComposite.add(
                observable
                        .flatMap(items -> Flowable.fromIterable(items))
                        .filter(item -> !TextUtils.isEmpty(item.getPlayerComment()))
                        .toList()
                        .toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(items -> {})
                        .subscribe(item -> {
                                    mView.onLoadFinished(item);
                                },
                                e -> mView.hideLoading(),
                                () ->{} )
        );
    }

    public void onLoadPlayerComment(Integer playerId, Integer matchId) {

        mView.showLoading();

        Flowable<List<PlayerRatingInfoModel>> observable
                = mPlayersManager.onLoadPlayerComments(playerId, matchId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(items -> {})
                        .subscribe(item -> {
                                    mView.onLoadFinished(item);
                                },
                                e -> mView.hideLoading(),
                                () ->{} )
        );
    }



    public void onLoadPlayerRatingNComment() {

        mView.showLoading();

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<PlayerRatingInfoModel>> observable
                = mArenaManager.getPlayerRatingInfo(token, mMatch.getMatchId(), null, null);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(items -> {})
                        .subscribe(item -> {
                                    //mView.onLoadFinished(item);
                                },
                                e -> mView.hideLoading(),
                                () ->{} )
        );
    }



    /**
     *
     * @param position 리사이클뷰에서 위치
     * @param ratingInfo
     * @param hifiveCount
     */
    public void setPlayerCommentHifive(int position,
                                       PlayerRatingInfoModel ratingInfo,
                                       int hifiveCount) {

//        String token;
//        if (App.getAccountManager().isAuthorized()) {
//            token = App.getAccountManager().getHifiveAccessToken();
//        } else {
//            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
//                    .setAction("이동", v -> openLoginActivity())
//                    .show();
//
//            return;
//        }
//
//        mView.showLoading();
//        // 서버 업데이트 하기 전 화면먼저 갱신한다.
//        mView.updateHifive(position, ratingInfo, hifiveCount);
//
//        Flowable<DBResultResponse> observable
//                = mMatchesManager.setPlayerCommentHifive(token, ratingInfo.getMatchId(), ratingInfo.getPlayerId(), ratingInfo.getUserId(), ratingInfo.getTimeSeq(), hifiveCount);
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
//                                        mView.updateHifive(position, ratingInfo, hifiveCount*-1);
//                                        mView.showErrorMessage(items.getMessage());
//                                    }
//                                },
//                                e -> {
//                                    mView.updateHifive(position, ratingInfo, hifiveCount*-1);
//                                    mView.hideLoading();
//                                    },
//                                () -> mView.hideLoading()
//                        ));
    }
}
