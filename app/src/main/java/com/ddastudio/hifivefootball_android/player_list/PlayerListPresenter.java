package com.ddastudio.hifivefootball_android.player_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.manager.PlayersManager;
import com.ddastudio.hifivefootball_android.data.manager.TeamsManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.content_editor.EditorActivity;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * Created by hongmac on 2018. 1. 15..
 */

public class PlayerListPresenter implements BaseContract.Presenter {

    int type;
    int teamId;
    PlayerListFragment mView;
    TeamsManager mTeamsManager;
    PlayersManager mPlayersManager;

    @NonNull
    CompositeDisposable mComposite;

    public PlayerListPresenter(int type) {
        this.type = PlayerListFragment.TYPE_OVERALL_PLAYER_LIST;
    }

    public PlayerListPresenter(int type, int teamId) {
        this.type = PlayerListFragment.TYPE_TEAM_PLAYER_LIST;
        this.teamId = teamId;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (PlayerListFragment)view;
        this.mTeamsManager = TeamsManager.getInstance();
        this.mPlayersManager = PlayersManager.getInstance();
        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public int getType() {
        return type;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openLoginActivity() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    public void openPlayerActivity(int playerId, String playerName) {
        Intent intent = new Intent(mView.getContext(), PlayerActivity.class);
        intent.putExtra("ARGS_PLAYER_ID", playerId);
        intent.putExtra("ARGS_PLAYER_NAME", playerName);
        //intent.putExtra("ARGS_COMPETITION", Parcels.wrap(competition));

        mView.startActivity(intent);
    }

    /**
     * 글쓰기 액티비티 실행
     */
    public void openTextEditor(PlayerModel player) {

        if (!App.getAccountManager().isAuthorized()) {

            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Intent intent = new Intent(mView.getContext(), EditorActivity.class);
        // 신규 수정 모드
        intent.putExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        // 게시판
        intent.putExtra("ARGS_BOARD_TYPE", PostBoardType.FOOTBALL);
        // CellType
        intent.putExtra("ARGS_CELL_TYPE", PostCellType.PLAYER_TALK.value());
        // PlayerId, CellTyped : PostCellType.PLAYER_TALK 일경우 반드시 플레이어 아이디를 넘겨야 한다.
        intent.putExtra("ARGS_PLAYER", Parcels.wrap(player));

        mView.startActivityForResult(intent, mView.REQUEST_EDITOR);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadTeamPlayers() {

        mView.showLoading();

        Flowable<List<PlayerModel>> observable
                = mTeamsManager.onLoadPlayers(teamId);

        mComposite.add(
                observable
                        .flatMap(items -> Flowable.fromIterable(items))
                        .map(item -> {
                            item.setItemType(ViewType.PLAYER_INFO);
                            return item;
                        }).toList().toFlowable()
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> {Timber.i("player list error : %s", e.toString());})
                        .subscribe(item -> mView.onLoadFinishedPlayers(item),
                                e -> mView.hideLoading())
        );
    }

    public void onLoadPlayerList(int limit, int offset) {

        mView.showLoading();

        Flowable<List<PlayerModel>> observable
                = mPlayersManager.onLoadPlayerList(limit, offset);

        mComposite.add(
                observable
                        .flatMap(items -> Flowable.fromIterable(items))
                        .map(item -> {
                            item.setItemType(ViewType.PLAYER_INFO);
                            return item;
                        }).toList().toFlowable()
                        .delay(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> {})
                        .subscribe(item -> mView.onLoadFinishedPlayers(item),
                                e -> mView.hideLoading())
        );
    }

    public void requestChangePlayerName(int playerId,
                                        String beforeName,
                                        String afterName) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<DBResultResponse> observable
                = mPlayersManager.requestChangePlayerName(token, playerId, beforeName, afterName);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                },
                                e -> {},
                                () -> {}
                        ));
    }
}
