package com.ddastudio.hifivefootball_android.player_rating;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.manager.RatingManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.player_rating.PlayerRatingListFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2018. 1. 5..
 */

public class PlayerRatingListPresenter implements BaseContract.Presenter {

    MatchModel mMatch;
    PlayerRatingListFragment mView;
    RatingManager mRatingManager;
    MatchesManager mMatchesManager;

    @NonNull
    CompositeDisposable mComposite;

    //List<PlayerModel> homeLineupList;
    //List<PlayerModel> awayLineupList;

    List<PlayerModel> lineupList;

    boolean isHome;
    int selectedTeamId;
    int selectedPlayerId;

    public PlayerRatingListPresenter(MatchModel match, boolean isHome) {

        this.mMatch = match;
        this.isHome = isHome;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (PlayerRatingListFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mRatingManager = new RatingManager(mView.getContext());
        this.mMatchesManager = MatchesManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public int getSelectedTeamId() {
        return selectedTeamId;
    }

    public void setSelectedTeamId(int selectedTeamId) {
        this.selectedTeamId = selectedTeamId;
    }

    public int getSelectedPlayerId() {
        return selectedPlayerId;
    }

    public void setSelectedPlayerId(int selectedPlayerId) {
        this.selectedPlayerId = selectedPlayerId;
    }

    public boolean isHome() {
        return isHome;
    }

    //    public boolean isHomeTeam(int teamId) {
//
//        return (getmMatch().getHomeTeamId() == teamId);
//    }

    public int getCurrentTeamId() {

        if ( isHome ) {
            return getmMatch().getHomeTeamId();
        }

        return getmMatch().getAwayTeamId();
    }

    /*---------------------------------------------------------------------------------------------*/

    public MatchModel getmMatch() {
        return mMatch;
    }

//    public List<PlayerModel> getHomeLineupList() {
//        return homeLineupList;
//    }
//
//    public void setHomeLineupList(List<PlayerModel> homeLineupList) {
//        this.homeLineupList = homeLineupList;
//    }
//
//    public List<PlayerModel> getAwayLineupList() {
//        return awayLineupList;
//    }
//
//    public void setAwayLineupList(List<PlayerModel> awayLineupList) {
//        this.awayLineupList = awayLineupList;
//    }

    public List<PlayerModel> getLineupList() {
        return lineupList;
    }

    public void setLineupList(List<PlayerModel> lineupList) {
        this.lineupList = lineupList;
    }



    /*---------------------------------------------------------------------------------------------*/

    public void openPlayerActivity(int playerId, String playerName) {
        Intent intent = new Intent(mView.getContext(), PlayerActivity.class);
        intent.putExtra("ARGS_PLAYER_ID", playerId);
        intent.putExtra("ARGS_PLAYER_NAME", playerName);
        //intent.putExtra("ARGS_COMPETITION", Parcels.wrap(competition));

        mView.startActivity(intent);
    }

    public void openLoginActivity() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onPostPlayerRating(Integer matchId,
                                   Integer playerId,
                                   Integer teamId,
                                   String timeSeq,
                                   Float rating,
                                   String updown,
                                   String comment,
                                   //--------------
                                   Integer position) {

        String token;
        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        mView.showLoading();

        Flowable<DBResultResponse> observable
                = mRatingManager.onPostPlayerRating(token, matchId, playerId, teamId, timeSeq, rating, updown, comment);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(err -> mView.showErrorMessage(err.getMessage()))
                        .subscribe(
                                items -> {
                                    if ( items.getResult() > 0 ) {
                                        mView.onPostFinishedRating(position, rating > 0.0f, !TextUtils.isEmpty(comment));
                                    }
                                },
                                e -> mView.hideLoading(),
                                () -> mView.hideLoading()
                        ));

    }

    public void onLoadLineup() {

        mView.showLoading();

        Flowable<List<PlayerModel>> observable
                = mRatingManager.onLoadLineup(mMatch.getMatchId());

        mComposite.add(
                observable.flatMap(items -> Flowable.fromIterable(items))
                        .filter(item -> getCurrentTeamId() == item.getTeamId())
                        .map(item -> {
                            item.setItemType(ViewType.LINEUP);

                            if ( lineupList == null ) {
                                lineupList = new ArrayList<>();
                            }
                            lineupList.add(item);

//                            if ( item.getTeamId() == mMatch.getHomeTeamId()) {
//                                if ( homeLineupList == null ) {
//                                    homeLineupList = new ArrayList<>();
//                                }
//                                homeLineupList.add(item);
//                            } else {
//                                if ( awayLineupList == null ) {
//                                    awayLineupList = new ArrayList<>();
//                                }
//                                awayLineupList.add(item);
//                            }

                            return item;
                        }).toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(err -> mView.showErrorMessage(err.getMessage()))
                        .subscribe(
                                items -> mView.onLoadFinished(items),
                                e -> mView.hideLoading(),
                                () -> {}
                        ));
    }

    /*---------------------------------------------------------------------------------------------*/

}
