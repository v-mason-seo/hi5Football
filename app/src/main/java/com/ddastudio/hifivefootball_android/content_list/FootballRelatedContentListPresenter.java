package com.ddastudio.hifivefootball_android.content_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostType;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.content_viewer.ContentViewerActivity;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.manager.PlayersManager;
import com.ddastudio.hifivefootball_android.data.manager.TeamsManager;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class FootballRelatedContentListPresenter implements BaseContract.Presenter {

    int mRelatedId;
    int mId;
    FootballRelatedContentListFragment mView;

    MatchesManager mMatchesManager;
    PlayersManager mPlayersManager;
    TeamsManager mTeamsManager;

    @NonNull
    CompositeDisposable mComposite;

    public FootballRelatedContentListPresenter(int relatedId, int id) {
        this.mRelatedId = relatedId;
        this.mId = id;
    }

    @Override
    public void attachView(BaseContract.View view) {

        mView = (FootballRelatedContentListFragment)view;
        mComposite = new CompositeDisposable();
        mMatchesManager = MatchesManager.getInstance();
        mPlayersManager = PlayersManager.getInstance();
        mTeamsManager = TeamsManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openContentViewActivity(ContentHeaderModel contentHeaderModel, int position) {

        Intent intent;

        intent = new Intent(mView.getContext(), ContentViewerActivity.class);
        intent.putExtra("ARGS_CONTENTS_HEADER_MODEL", Parcels.wrap(contentHeaderModel));
        intent.putExtra("ARGS_POSITION", position);
        intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK| */Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, ContentListFragment.REQUEST_CONTENT_VIEWER);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadData(int limit, int offset) {

        if (mRelatedId == FootballRelatedContentListFragment.MATCH_RELATED_CONTENT) {
            onLoadMatchRelatedContent(mId, limit, offset);
        } else if ( mRelatedId == FootballRelatedContentListFragment.TEAM_RELATED_CONTENT) {
            onLoadTeamRelatedContent(mId, limit, offset);
        } else if ( mRelatedId == FootballRelatedContentListFragment.PLAYER_RELATED_CONTENT) {
            onLoadPlayerRelatedContent(mId, limit, offset);
        }
    }

    public void onLoadMatchRelatedContent(int matchId, int limit, int offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mMatchesManager.onLoadMatchContentList(matchId, offset, limit);

        mComposite.add(
                observable
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished(items);
                                },
                                e -> {
                                    mView.showErrorMessage("매치정보를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
                                    mView.hideLoading();
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
    public void onLoadPlayerRelatedContent(int playerId, int limit, int offset) {

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<ContentHeaderModel>> observable
                = mPlayersManager.onLoadPlayerContentList(token, playerId, null, limit, offset)
                .flatMap(items -> Flowable.fromIterable(items))
                .map(item -> {

                    item.setPostType(PostType.PLAYER_RELATION);
                    return item;

                }).toList().toFlowable();

        mComposite.add(
                observable
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> mView.onLoadFinished(items),
                                e -> {
                                    mView.showErrorMessage("플레이어 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
                                    mView.hideLoading();
                                }
                        ));
    }


    /**
     *
     * @param teamId

     * @param limit
     * @param offset
     */
    public void onLoadTeamRelatedContent(int teamId, int limit, int offset) {

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<ContentHeaderModel>> observable
                = mTeamsManager.onLoadTeamContentList(token, teamId, null, limit, offset)
                .flatMap(items -> Flowable.fromIterable(items))
                .map(item -> {

                    item.setPostType(PostType.TEAM_RELATION);
                    return item;

                }).toList().toFlowable();

        mComposite.add(
                observable
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> mView.onLoadFinished(items),
                                e -> {
                                    mView.showErrorMessage("팀 데이터를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
                                    mView.hideLoading();
                                }
                        ));
    }
}
