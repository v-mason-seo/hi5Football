package com.ddastudio.hifivefootball_android.team;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.data.manager.FootballDataManager;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.content_editor.EditorActivity;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 10. 10..
 */

public class TeamPresenter implements BaseContract.Presenter {

    int mTeamId;
    TeamModel mTeamData;
    TeamActivity mView;
    FootballDataManager mFootballManager;

    @NonNull
    CompositeDisposable mComposite;

    public TeamPresenter(int teamId) {
        this.mTeamId = teamId;
    }

    @Override
    public void attachView(BaseContract.View view) {
        mView = (TeamActivity)view;
        this.mComposite = new CompositeDisposable();
        this.mFootballManager = FootballDataManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null && !mComposite.isDisposed())
            mComposite.clear();
    }

    public TeamModel getTeamData() {
        return mTeamData;
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

        if ( getTeamData() == null ) {
            mView.showErrorMessage("팀 데이터가 없습니다.");
            return;
        }

        Intent intent = new Intent(mView, EditorActivity.class);
        // 신규 수정 모드
        intent.putExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        // 게시판
        intent.putExtra("ARGS_BOARD_TYPE", PostBoardType.FOOTBALL);
        // CellType
        intent.putExtra("ARGS_CELL_TYPE", PostCellType.TEAM_TALK.value());
        // PlayerId, CellTypedl : PostCellType.PLAYER_TALK 일경우 반드시 플레이어 아이디를 넘겨야 한다.
        intent.putExtra("ARGS_TEAM", Parcels.wrap(getTeamData()));

        mView.startActivityForResult(intent, mView.REQUEST_EDITOR);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadTeam() {

        Flowable<TeamModel> observable
                = mFootballManager.onLoadTeam(mTeamId);

        mComposite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item -> {
                                    mTeamData = item;
                                    mView.onLoadFinished(item);
                                },
                                e -> mView.showErrorMessage("팀정보를 불러오는중 오류가 발생했습니다.\n" + e.getMessage()),
                                () -> {}
                        ));
    }


}
