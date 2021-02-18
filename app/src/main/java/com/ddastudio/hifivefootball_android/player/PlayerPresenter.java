package com.ddastudio.hifivefootball_android.player;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.data.manager.PlayersManager;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.content_editor.EditorActivity;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class PlayerPresenter implements BaseContract.Presenter {

    int playerId;
    PlayerModel playerData;
    PlayerActivity mView;
    PlayersManager mPlayersManager;
    @NonNull
    CompositeDisposable mComposite;

    public PlayerPresenter(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (PlayerActivity)view;
        this.mPlayersManager = PlayersManager.getInstance();
        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public PlayerModel getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerModel playerData) {
        this.playerData = playerData;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openLoginActivity() {
        Intent intent = new Intent(mView, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    /**
     * 글쓰기 액티비티 실행
     */
    public void openTextEditor() {

        if (!App.getAccountManager().isAuthorized()) {

            Snackbar.make(mView.getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        if ( getPlayerData() == null ) {
            mView.showErrorMessage("선수 데이터가 없습니다.");
            return;
        }

        Intent intent = new Intent(mView, EditorActivity.class);
        // 신규 수정 모드
        intent.putExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        // 게시판
        intent.putExtra("ARGS_BOARD_TYPE", PostBoardType.FOOTBALL);
        // CellType
        intent.putExtra("ARGS_CELL_TYPE", PostCellType.PLAYER_TALK.value());
        // PlayerId, CellTypedl : PostCellType.PLAYER_TALK 일경우 반드시 플레이어 아이디를 넘겨야 한다.
        intent.putExtra("ARGS_PLAYER", Parcels.wrap(getPlayerData()));

        mView.startActivityForResult(intent, mView.REQUEST_EDITOR);
    }


    /*---------------------------------------------------------------------------------------------*/

    public void onLoadPlayer() {

        Flowable<PlayerModel> observable
                = mPlayersManager.onLoadPlayer(playerId);

        mComposite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item -> {
                                    if ( item != null ) {
                                        setPlayerData(item);
                                        mView.setData(item);
                                    }
                                },
                                e -> mView.showErrorMessage(e.getMessage()),
                                () -> {}
                        ));
    }
}
