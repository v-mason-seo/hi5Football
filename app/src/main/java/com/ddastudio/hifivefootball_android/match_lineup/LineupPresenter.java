package com.ddastudio.hifivefootball_android.match_lineup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

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
import com.ddastudio.hifivefootball_android.player_rating.PlayerRatingActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 10. 26..
 */

public class LineupPresenter implements BaseContract.Presenter {

    MatchModel mMatch;
    LineupFragment mView;
    RatingManager mRatingManager;
    MatchesManager mMatchesManager;

    List<PlayerModel> localTeamList;
    List<PlayerModel> visitorTeamList;

    @NonNull
    CompositeDisposable mComposite;

    public LineupPresenter(MatchModel match) {

        this.mMatch = match;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (LineupFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mRatingManager = new RatingManager(mView.getContext());
        this.mMatchesManager = MatchesManager.getInstance();

        localTeamList = new ArrayList<>();
        visitorTeamList = new ArrayList<>();

    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public MatchModel getmMatch() {
        return mMatch;
    }

    public List<PlayerModel> getLocalTeamList() {
        return localTeamList;
    }

    public List<PlayerModel> getVisitorTeamList() {
        return visitorTeamList;
    }

    /*---------------------------------------------------------------------------------------------*/

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
        intent.putExtra("ARGS_HOME_LINEUP", Parcels.wrap(localTeamList));
        intent.putExtra("ARGS_AWAY_LINEUP", Parcels.wrap(visitorTeamList));
//
//        intent.putExtra("ARGS_MATCH_ID", mMatch.getMatchId());
//        intent.putExtra("ARGS_MATCH_LOCAL_TEAM_ID", mMatch.getHomeTeamName());
//        intent.putExtra("ARGS_MATCH_LOCAL_TEAM_IMAGE_URL", mMatch.getHomeTeamEmblemUrl());
//        intent.putExtra("ARGS_MATCH_VISITOR_TEAM_ID", mMatch.getAwayTeamId());
//        intent.putExtra("ARGS_MATCH_VISITOR_TEAM_IMAGE_URL", mMatch.getAwayTeamEmblemUrl());

        mView.startActivity(intent);
    }

    public void openLoginActivity() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadLineup(boolean selectedHome) {

        mView.showLoading();

        Flowable<List<PlayerModel>> observable
                = mMatchesManager.onLoadLineup(mMatch.getMatchId());

        mComposite.add(
                observable.flatMap(items -> Flowable.fromIterable(items))
                        .map(item -> {
                            item.setItemType(ViewType.LINEUP);

                            if ( item.getTeamId() == mMatch.getHomeTeamId()) {
                                localTeamList.add(item);
                            } else {
                                visitorTeamList.add(item);
                            }

                            return item;
                        }).toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(err -> mView.showErrorMessage(err.getMessage()))
                        .subscribe(
                                items -> mView.onLoadFinished(selectedHome ? getLocalTeamList() : getVisitorTeamList()),
                                e -> mView.hideLoading(),
                                () -> {}
                        ));
    }

    public void onPostPlayerRating(Integer matchId,
                                   Integer playerId,
                                   Integer teamId,
                                   String timeSeq,
                                   Float rating,
                                   String updown,
                                   String comment) {

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
                                        //mView.onPostFinishedRating(position, rating > 0.0f, !TextUtils.isEmpty(comment));
                                        mView.showMessage("OK");
                                    } else {
                                        mView.showMessage("TT");
                                    }
                                },
                                e -> {
                                    mView.hideLoading();
                                    mView.showErrorMessage(e.toString());
                                },
                                () -> mView.hideLoading()
                        ));

    }
}
