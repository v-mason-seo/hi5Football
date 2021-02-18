package com.ddastudio.hifivefootball_android.player_rating;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.ArenaManager;
import com.ddastudio.hifivefootball_android.data.manager.FootballDataManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingRecordModel;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.player_rating.PlayerRatingFragment;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 11. 10..
 */

public class PlayerRatingPresenter implements BaseContract.Presenter {

    int mMatchId;
    String mLocalTeamImageUrl;
    String mVisitorTeamImageUrl;

    PlayerRatingFragment mView;
    FootballDataManager mFootballManager;
    MatchesManager mMatchesManager;
    ArenaManager mArenaManager;

    List<PlayerRatingRecordModel> ratingRecodList;
    List<PlayerRatingInfoModel> localPlayerList;
    List<PlayerRatingInfoModel> visitorPlayerList;

    @NonNull
    CompositeDisposable mComposite;

    public PlayerRatingPresenter(int matchId, String localTeamImageUrl, String visitorTeamImageUrl) {
        this.mMatchId = matchId;
        this.mLocalTeamImageUrl = localTeamImageUrl;
        this.mVisitorTeamImageUrl = visitorTeamImageUrl;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (PlayerRatingFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mFootballManager = FootballDataManager.getInstance();
        this.mMatchesManager = MatchesManager.getInstance();
        this.mArenaManager = ArenaManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public List<PlayerRatingRecordModel> getRatingRecodList() {
        return ratingRecodList;
    }

    public void setRatingRecodList(List<PlayerRatingRecordModel> ratingRecodList) {
        this.ratingRecodList = ratingRecodList;
    }

    public String getmLocalTeamImageUrl() {
        return mLocalTeamImageUrl;
    }

    public String getmVisitorTeamImageUrl() {
        return mVisitorTeamImageUrl;
    }

    public List<PlayerRatingInfoModel> getLocalPlayerList() {
        return localPlayerList;
    }

    public void setLocalPlayerList(List<PlayerRatingInfoModel> localPlayerList) {
        this.localPlayerList = localPlayerList;
    }

    public List<PlayerRatingInfoModel> getVisitorPlayerList() {
        return visitorPlayerList;
    }

    public void setVisitorPlayerList(List<PlayerRatingInfoModel> visitorPlayerList) {
        this.visitorPlayerList = visitorPlayerList;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openLoginActivity() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadPlayerRating() {

        Flowable.zip(
                mArenaManager.getPlayerRatingInfo("", mMatchId, null, null),
                mMatchesManager.getPlayerRatingRecord(mMatchId, "180"),
                (list1, list2) -> {
                    return Flowable.fromIterable(list1)
                            .filter(info -> info != null)
                            .map(info -> {
                                return info;
                            }).map(item -> {
                                return item;
                            }).toList().blockingGet();

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> mView.onLoadFinished(getLocalPlayerList()),
                err -> {},
                () -> {});
    }
}
