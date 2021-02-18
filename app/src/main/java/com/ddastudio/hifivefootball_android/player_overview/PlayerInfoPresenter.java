package com.ddastudio.hifivefootball_android.player_overview;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.FootballDataManager;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.player_overview.PlayerInfoFragment;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 10. 29..
 */

public class PlayerInfoPresenter implements BaseContract.Presenter {

    int mDataType;
    int mPlayerId;
    String mPlayerName;
    PlayerInfoFragment mView;
    FootballDataManager mFootballManager;

    @NonNull
    CompositeDisposable mComposite;

    public PlayerInfoPresenter(int dataType, int playerId, String playerName) {
        this.mDataType = dataType;
        this.mPlayerId = playerId;
        this.mPlayerName = playerName;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (PlayerInfoFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mFootballManager = FootballDataManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null && !mComposite.isDisposed())
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadPlayer() {

        Flowable<PlayerModel> observable
                = mFootballManager.onLoadPlayer(mPlayerId);

        mComposite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(item -> {
                            if ( mDataType == PlayerInfoFragment.TYPE_BASIC_INFO) {
                                item.setItemType(MultipleItem.PLAYER_BASIC_INFO);
                            } else {
                                item.setItemType(MultipleItem.PLAYER_STATS);
                            }

                            return item;
                        })
                        .subscribe(
                                item -> {
                                    if ( mDataType == PlayerInfoFragment.TYPE_BASIC_INFO) {
                                        mView.onLoadFinished(item);
                                    } else {
//                                        mView.onLoadFinished2(item.getPlayerStatistics().getPlayerClubIntlStats());
//                                        mView.onLoadFinished2(item.getPlayerStatistics().getPlayerNatitionalStats());
//                                        mView.onLoadFinished2(item.getPlayerStatistics().getPlayerClubStats());
                                    }
                                },
                                e -> mView.showErrorMessage(e.getMessage()),
                                () -> {}
                        ));
    }
}
