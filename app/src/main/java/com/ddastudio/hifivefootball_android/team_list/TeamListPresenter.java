package com.ddastudio.hifivefootball_android.team_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.data.manager.TeamsManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.content_editor.EditorActivity;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2018. 1. 26..
 */

public class TeamListPresenter implements BaseContract.Presenter {

    int type;

    TeamListFragment mView;

    @NonNull
    CompositeDisposable mComposite;

    public TeamListPresenter(int type) {
        this.type = type;
    }

    @Override
    public void attachView(BaseContract.View view) {
        mView = (TeamListFragment)view;
        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public int getType() {
        return type;
    }


    /*---------------------------------------------------------------------------------------------*/

    /**
     * 로그인 액티비티 실행
     */
    public void openLoginActivity() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    /**
     * 플레이어 액티비티 실행
     */
    public void openPlayerActivity(int playerId, String playerName) {
        Intent intent = new Intent(mView.getContext(), PlayerActivity.class);
        intent.putExtra("ARGS_PLAYER_ID", playerId);
        intent.putExtra("ARGS_PLAYER_NAME", playerName);
        //intent.putExtra("ARGS_COMPETITION", Parcels.wrap(competition));

        mView.startActivity(intent);
    }

    /**
     * 팀 액티비티 실행
     */
    public void openTeamActivity(int teamId) {
        Intent intent = new Intent(mView.getContext(), TeamActivity.class);
        intent.putExtra("ARGS_TEAM_ID", teamId);

        mView.startActivity(intent);
    }

    /**
     * 팀관련 글쓰기 액티비티 실행
     */
    public void openTextEditor(TeamModel team) {

        //
        // 로그인 상태 확인
        //
        if (!App.getAccountManager().isAuthorized()) {

            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        // 인텐트 생성
        Intent intent = new Intent(mView.getContext(), EditorActivity.class);
        // 신규 수정 모드
        intent.putExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        // 게시판
        intent.putExtra("ARGS_BOARD_TYPE", PostBoardType.FOOTBALL);
        // CellType
        intent.putExtra("ARGS_CELL_TYPE", PostCellType.TEAM_TALK.value());
        // PlayerId, CellTypedl : PostCellType.PLAYER_TALK 일경우 반드시 플레이어 아이디를 넘겨야 한다.
        intent.putExtra("ARGS_TEAM", Parcels.wrap(team));

        mView.startActivityForResult(intent, mView.REQUEST_EDITOR);
    }


    /*---------------------------------------------------------------------------------------------*/


    /**
     * 팀 정보 불러오기
     */
    public void onLoadTeamList(int limit, int offset) {

        mView.showLoading();

        Flowable<List<TeamModel>> observable
                = TeamsManager.getInstance().onLoadTeamList(limit, offset);

        mComposite.add(
                observable
                        /*.flatMap(items -> Flowable.fromIterable(items))
                        .map(item -> {
                            item.setItemType(ViewType.PLAYER_INFO);
                            return item;
                        }).toList().toFlowable()*/
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .subscribe(item -> mView.onLoadFinishedTeams(item),
                                e -> mView.hideLoading())
        );
    }


    /**
     * 팀명 변경 요청
     */
    public void requestChangeTeamName(int teamId,
                                      String beforeName,
                                      String afterName) {

        String token;

        //
        // 로그인 여부 확인
        //
        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<DBResultResponse> observable
                = TeamsManager.getInstance().requestChangeTeamName(token, teamId, beforeName, afterName);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> mView.showMessage("변경 요청이 완료되었습니다."),
                                e -> mView.showErrorMessage(e.getMessage()),
                                () -> {}
                        ));
    }
}
