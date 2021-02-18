package com.ddastudio.hifivefootball_android.match_detail;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.data.manager.AuthManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.content_editor.EditorActivity;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.match_detail.MatchActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by hongmac on 2018. 2. 1..
 */

public class MatchPresenter implements BaseContract.Presenter {

     //int mMatchId;

    MatchActivity mView;
    MatchesManager mMatchesManager;
    AuthManager mAuthManager;

    MatchModel match;

    public MatchModel getMatch() {
        return match;
    }

    public void setMatch(MatchModel match) {
        this.match = match;
    }

    @NonNull
    CompositeDisposable mComposite;

    @NonNull
    CompositeDisposable _refreshComposite;

    public void onStart() {
        this._refreshComposite = new CompositeDisposable();
        refreshHifiveAccessToken();
    }

    public void onStop() {

        if ( _refreshComposite != null && !_refreshComposite.isDisposed() )
            _refreshComposite.clear();
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (MatchActivity) view;
        this.mMatchesManager = MatchesManager.getInstance();
        this.mAuthManager = AuthManager.getInstance();

        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( _refreshComposite != null && !_refreshComposite.isDisposed() )
            _refreshComposite.clear();

        if ( mComposite != null )
            mComposite.clear();
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

        if ( getMatch() == null ) {
            mView.showErrorMessage("매치 데이터가 없습니다.");
            return;
        }

        Intent intent = new Intent(mView, EditorActivity.class);
        // 신규 수정 모드
        intent.putExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        // 게시판
        intent.putExtra("ARGS_BOARD_TYPE", PostBoardType.FOOTBALL);
        // CellType
        intent.putExtra("ARGS_CELL_TYPE", PostCellType.MATCH_CHAT.value());
        // PlayerId, CellTypedl : PostCellType.PLAYER_TALK 일경우 반드시 플레이어 아이디를 넘겨야 한다.
        intent.putExtra("ARGS_MATCH", Parcels.wrap(getMatch()));

        mView.startActivityForResult(intent, mView.REQUEST_EDITOR);
    }


    /*---------------------------------------------------------------------------------------------*/

    public void refreshHifiveAccessToken() {

        if ( !App.getAccountManager().isAuthorized() ) {
            return;
        }

        String refreshToken = App.getAccountManager().getSharedHifiveRefreshToken();

        Flowable<DBResultResponse> refreshObservable =
                Flowable.interval(0, 5, TimeUnit.MINUTES)
                        .map(i -> {
                            long nowMillis = Calendar.getInstance().getTimeInMillis();
                            long sharedSaveTime = App.getAccountManager().getSharedSaveTime();
                            long diff = ( nowMillis - sharedSaveTime ) / 1000;
                            //Timber.i("nowMillis : %d, sharedSaveTime : %d, sec : %d", nowMillis, sharedSaveTime,  diff / 1000);
                            return diff;
                        })
                        .filter( diff -> diff > 60 * 60) // 360 <- 한시간마다 토큰 갱신
                        .flatMap(val -> mAuthManager.accessTokenRefresh(refreshToken))
                        .filter(val -> val.getResult() > 0);

        Disposable disposable = refreshObservable.subscribe(res -> {
                    //Timber.i("[onStart] onNext : %s", res.getAuthToken().toString());
                    App.getAccountManager().setHifiveAccessToken(res.getAuthToken());
                }
                , e -> mView.showErrorMessage("토큰 갱신 오류\n" + e.getMessage())
                , () -> { }
        );

        _refreshComposite.add(disposable);
    }

    public void onLoadMatch(int matchid) {

        mView.showLoading();

        Flowable<MatchModel> observable
                = mMatchesManager.onLoadMatche(matchid);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    setMatch(items);
                                    mView.onLoadFinishedMatch(items);
                                },
                                e -> {
                                    mView.showErrorMessage("매치정보를 불러오는중 오류가 발생했습니다.\n" + e.getMessage());
                                    mView.hideLoading();
                                },
                                () -> {}
                        ));
    }
}
