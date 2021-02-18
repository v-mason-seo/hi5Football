package com.ddastudio.hifivefootball_android.main;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.data.manager.AuthManager;
import com.ddastudio.hifivefootball_android.data.manager.UserManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.match_arena.ArenaActivity;
import com.ddastudio.hifivefootball_android.content_column_news.ColumnNewsActivity;
import com.ddastudio.hifivefootball_android.content_editor.EditorActivity;
import com.ddastudio.hifivefootball_android.content_issue.IssueActivity;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.nunting_bset.NuntingActivity;
import com.ddastudio.hifivefootball_android.player_list.PlayerListActivity;
import com.ddastudio.hifivefootball_android.team_list.TeamListActivity;
import com.ddastudio.hifivefootball_android.user_profile.UserProfileActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by hongmac on 2017. 9. 8..
 */

public class MainPresenter implements BaseContract.Presenter {

    Context mContext;
    MainActivity mView;
    AuthManager mAuthManager;
    UserManager mUserManager;


    //int mSelectedBoard;
    PostBoardType mBoardType;

    // 사용안함
    LiveData<BoardMasterModel> mLiveSelectedBoard;

    BoardMasterModel mSelectedBoard;

    public static final int REQUEST_EDITOR = 20001;

    @NonNull
    CompositeDisposable _composite;

    @NonNull
    CompositeDisposable _refreshComposite;

    int dpBoardCurrentIndex = 0;

    public MainPresenter(PostBoardType boardType) {

        this.mBoardType = boardType;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (MainActivity)view;
        this.mContext = mView.getApplicationContext();
        this.mAuthManager = AuthManager.getInstance();
        this.mUserManager = UserManager.getInstance();

        this._composite = new CompositeDisposable();

        dpBoardCurrentIndex = 0;
    }

    public void onStart() {
        this._refreshComposite = new CompositeDisposable();
        refreshHifiveAccessToken();

        // 미니 전광판 갱신 작업 ( 작업중 )
        refreshMiniDisplayBoard();
    }

    public void onStop() {

        if ( _refreshComposite != null && !_refreshComposite.isDisposed() )
            _refreshComposite.clear();
    }

    @Override
    public void detachView() {

        if ( _refreshComposite != null && !_refreshComposite.isDisposed() )
            _refreshComposite.clear();

        if ( _composite != null )
            _composite.clear();
    }

//    public int getSelectedBoard() {
//        return mSelectedBoard;
//    }
//
//    public void setSelectedBoard(int selectedBoard) {
//        this.mSelectedBoard = selectedBoard;
//    }

    public PostBoardType getBoardType() {
        return mBoardType;
    }

    public void setBoardType(PostBoardType mBoardType) {
        this.mBoardType = mBoardType;
    }


    /**
     * 현재 선택한 게시판 정보 ( 게시판 방식으로 현재 사용안함 )
     * @return
     */
    public LiveData<BoardMasterModel> getLiveSelectedBoard() {

        mLiveSelectedBoard = AppDatabase.getInstance(mView).boardsDao().getLiveSelectedBoard();
        return mLiveSelectedBoard;
    }

    public LiveData<ChatAndAttributeModel> getSelectedFootballChat() {

        return AppDatabase.getInstance(mView).chatDao().loadSelectedChatAndAttr();
    }

    public BoardMasterModel getSelectedBoard() {
        return mSelectedBoard;
    }

    public void setSelectedBoard(BoardMasterModel board) {
        this.mSelectedBoard = board;
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 글쓰기 액티비티 실행
     */
    public void openTextEditor() {

        if (!App.getAccountManager().isAuthorized()) {

            Snackbar.make(mView.getContainerView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Intent intent = new Intent(mView, EditorActivity.class);
        // 신규 수정 모드
        intent.putExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        // 게시판
        intent.putExtra("ARGS_BOARD_TYPE", mBoardType.value());
        //intent.putExtra("ARGS_TEST_MODE", 0);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //mView.startActivity(intent);
        mView.startActivityForResult(intent, mView.REQUEST_EDITOR);
    }

    public void openTestTextEditor() {

        if (!App.getAccountManager().isAuthorized()) {

            Snackbar.make(mView.getContainerView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Intent intent = new Intent(mView, EditorActivity.class);
        // 신규 수정 모드
        intent.putExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        // 게시판
        intent.putExtra("ARGS_BOARD_TYPE", mBoardType.value());
        intent.putExtra("ARGS_TEST_MODE", 1);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //mView.startActivity(intent);
        mView.startActivityForResult(intent, mView.REQUEST_EDITOR);
    }

    /**
     * 사용자 프로필 액티비티 실행
     * @param user
     */
    public void openUserProfile(UserModel user) {
        Intent intent = new Intent(mView.getApplicationContext(), UserProfileActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("ARGS_USER", Parcels.wrap(user));
        mView.startActivity(intent);
    }

    public void openTeamListActivity() {
        Intent intent = new Intent(mView.getApplicationContext(), TeamListActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.putExtra("ARGS_USER", Parcels.wrap(user));
        mView.startActivity(intent);
    }

    public void openPlayerListActivity() {
        Intent intent = new Intent(mView.getApplicationContext(), PlayerListActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.putExtra("ARGS_USER", Parcels.wrap(user));
        mView.startActivity(intent);
    }

    public void openIssueActivity() {
        Intent intent = new Intent(mView.getApplicationContext(), IssueActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.putExtra("ARGS_USER", Parcels.wrap(user));
        mView.startActivity(intent);
    }

    public void openLoginActivity() {
        Intent intent = new Intent(mView, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    public void openHumorActivity(int fragmentType) {
        Intent intent = new Intent(mView, ColumnNewsActivity.class);
        intent.putExtra("ARGS_FRAGMENT_TYPE", fragmentType);
        mView.startActivity(intent);
    }

    public void openNuntingActivity(int fragmentType) {
        Intent intent = new Intent(mView, NuntingActivity.class);
        intent.putExtra("ARGS_FRAGMENT_TYPE", fragmentType);
        mView.startActivity(intent);
    }

    public void openArenaActivity() {
        Intent intent = new Intent(mView, ArenaActivity.class);
        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void refreshMiniDisplayBoard() {


        _refreshComposite.add(
            io.reactivex.Observable.interval(0, 5, TimeUnit.SECONDS)
                    .map(v -> {
                        LinkedHashMap list = mView.getDisplayBoardData();
                        if ( list == null || list.size() == 0 ) {
                            return -1;
                        }

                        if ( dpBoardCurrentIndex >= list.size()) {
                            dpBoardCurrentIndex = 0;
                        }

                        return dpBoardCurrentIndex;
                    })
            .doOnNext(v -> {
                //Timber.i("val : " + v);
            })
                    .observeOn(AndroidSchedulers.mainThread())
            .subscribe(v -> {
                if ( dpBoardCurrentIndex != -1) {
                    LinkedHashMap list = mView.getDisplayBoardData();
                    if ( list != null && list.size() != 0 ) {
                        String info = (String)list.values().toArray()[dpBoardCurrentIndex];
                        mView.showDisplayBoard(info);
                        dpBoardCurrentIndex++;
                    }
                }
                    },
                    e -> Timber.i("[onError] " + e.getLocalizedMessage()))
        );
    }

    /**
     * 5분 마다 토큰 만료시간을 체크해서
     * 토큰 갱신시간과 현재시간 차가 한시간 이상나면 토큰을 갱신한다.
     */
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

        Disposable disposable = refreshObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    //Timber.i("[onStart] onNext : %s", res.getAuthToken().toString());
                    App.getAccountManager().setHifiveAccessToken(res.getAuthToken());
                }
                , e -> mView.showErrorMessage("토큰 갱신 오류\n" + e.getMessage())
                , () -> { }
        );

        _refreshComposite.add(disposable);
    }

    private Flowable<UserModel> hifiveUserObservable(String token) {

        Flowable<UserModel> observable
                = mUserManager.getLoginUser(token);

        return observable;
    }

    /**
     * 하이파이브 로그인 사용자 정보 불러오기
     */
    public void getLoginUser() {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getContainerView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<UserModel> observable
                = mUserManager.getLoginUser(token);

        _composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item -> {
                                    Toasty.success(mView.getApplicationContext(), mView.getResources().getString(R.string.complete_load_user_info), Toast.LENGTH_LONG, true).show();
                                    //App.getInstance().setUser(item);
                                    App.getAccountManager().registerHifiveUser(item);
                                    mView.updateUserInfo();
                                },
                                e -> {
                                    mView.showErrorMessage(mView.getResources().getString(R.string.fail_load_user_info) + e.getMessage());
                                },
                                () -> { }
                        ));
    }
}
