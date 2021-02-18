package com.ddastudio.hifivefootball_android.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.data.manager.AuthManager;
import com.ddastudio.hifivefootball_android.data.manager.SettingsManager;
import com.ddastudio.hifivefootball_android.data.manager.UserManager;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.NotificationTypeMasterModel;
import com.ddastudio.hifivefootball_android.signin.SnsInfoModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.HttpException;
import timber.log.Timber;

/**
 * Created by hongmac on 2017. 9. 23..
 */

public class SplashPresenter implements BaseContract.Presenter {

    Context mContext;
    SplashActivity mView;
    AuthManager mAuthManager;
    UserManager mUserManager;
    SettingsManager mSettingsManager;

    private Handler mHandler;
    private Runnable mRunnable;


    @NonNull
    CompositeDisposable _composite;

    public SplashPresenter() {
        _composite = new CompositeDisposable();
    }


    @Override
    public void attachView(BaseContract.View view) {
        mView = (SplashActivity)view;
        mContext = mView.getApplicationContext();
        mAuthManager = AuthManager.getInstance();
        mUserManager = UserManager.getInstance();
        mSettingsManager = SettingsManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( _composite != null )
            _composite.clear();

        if ( mHandler != null && mRunnable != null ) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    public static final int LOGIN_REQUEST = 5413;

    public void openLoginActivity() {
        Intent intent = new Intent(mView, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, LOGIN_REQUEST);
        //mView.finish();
    }


    public void openMainActivity() {

        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mView, MainActivity.class);
                mView.startActivity(intent);
                mView.finish();
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 500);


//        Intent intent = new Intent(mView, MainActivity.class);
//        mView.startActivity(intent);
//        mView.finish();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void refreshHifiveAccessToken() {

        String refreshToken = App.getAccountManager().getSharedHifiveRefreshToken();

        Flowable<DBResultResponse> observable
                = mAuthManager.accessTokenRefresh(refreshToken);

        _composite.add(
            observable.map(authToken -> {
                //Timber.d("[refreshHifiveAccessToken] %s", authToken.toString());
                App.getAccountManager().setHifiveAccessToken(authToken.getAuthToken());
                return authToken;
            })
                    .flatMap(hifiveToken -> hifiveUserObservable(hifiveToken.getAccessToken()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(hifiveUser -> {
                                //Timber.d("call registerHifiveUser!!");
                                App.getAccountManager().registerHifiveUser(hifiveUser);
                                openMainActivity();
                            },
                            e -> {
                                // TODO: 2017. 11. 25. 에러코드를 확인하고 회원가입 창으로 이동해야한다. 확인 후 프로그램 오류일 경우 아래 주석처럼 이벤트를 전송하자.
                                if ( e instanceof ConnectException) {
                                    mView.showErrorMessage("서버가 응답이 없습니다. 잠시후 다시 실행해주세요. 죄송합니다.");
                                } else if ( e instanceof retrofit2.HttpException) {
                                    if ( ((HttpException)e).code() == 401 ) {
                                        mView.showErrorMessage(mView.getResources().getString(R.string.expire_token_info));
                                        //openLoginActivity();
                                        openMainActivity();
                                    } else {
                                        mView.showErrorMessage(mView.getResources().getString(R.string.fail_load_user_info) + "\n" + e.getMessage());
                                        openMainActivity();
                                    }
                                }

                            },
                            () -> {})
        );
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
            Snackbar.make(mView.getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    //.setAction("이동", v -> openLoginActivity())
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
                                    openMainActivity();
                                },
                                e -> {
                                    mView.showErrorMessage(mView.getResources().getString(R.string.fail_load_user_info) + e.getMessage());
                                    openMainActivity();
                                },
                                () -> { }
                        ));
    }


    /*---------------------------------------------------------------------------------------------*/

    public void onLoadHifiveSettings() {

        // 게시판
        Flowable<List<BoardMasterModel>> boardFlowable
                = mSettingsManager.getBoardList(true);

        // 알람 타입
        Flowable<List<NotificationTypeMasterModel>> notificationTypeFlowable
                = mSettingsManager.getNotificationType();

        _composite.add(
            boardFlowable
                .subscribeOn(Schedulers.io())
                .concatMap( board_mater -> {
                    Timber.i("1");
                    AppDatabase.getInstance(mView).boardsDao().insertAll(board_mater);
                    return notificationTypeFlowable;
            }).concatMap(notification_type_master -> {
                Timber.i("2");
                App.getHifiveSettingsManager().setNotificationTypeMasterList(notification_type_master);
                return Flowable.just("complete");
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                        //Timber.d("[onLoadHifiveSettings] onNext : %s", s);
                        Timber.i("3");
                        mView.onLoadFinished(null);
                    },
                    e -> {
                        mView.onLoadFinished(e.getMessage());
                    }
                )
        );
    }

    /**
     * 게시판 정보를 가져와서 HifiveSettingsManager에 등록한다.
     * @param isUse
     */
    public void getBoardList(boolean isUse) {

        Flowable<List<BoardMasterModel>> observable
                = mSettingsManager.getBoardList(isUse);

        _composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    //Timber.d("board master data size : %s", items.size());
                                    //App.getHifiveSettingsManager().setBoardMasterList(items);
                                    },
                                e -> {},
                                () -> {}
                        ));
    }

    public void getNotificationType() {

        Flowable<List<NotificationTypeMasterModel>> observable
                = mSettingsManager.getNotificationType();

        _composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    //Timber.d("board master data size : %s", items.size());
                                    App.getHifiveSettingsManager().setNotificationTypeMasterList(items);
                                },
                                e -> {},
                                () -> {}
                        ));
    }

    /*---------------------------------------------------------------------------------------------*/

    // *** Naver ***
    OAuthLogin mOAuthLoginModule;

    public void initNaverOAuth(String clientId, String clientSecret, String clientName) {
        OAuthLoginDefine.DEVELOPER_VERSION = false;

        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(mView.getApplicationContext(),
                clientId,
                clientSecret,
                clientName);
    }

    public void executeAutoLogin() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mView);
        String sns_type = pref.getString("sns_type", "");
        String sns_token = pref.getString("sns_token", "");

        if ( sns_type.equals(EntityType.SOCIAL_NAVER) ) {

            if (!TextUtils.isEmpty(sns_token)) {
                OAuthLogin.getInstance().startOauthLoginActivity(mView, new OAuthLoginHandler(this, mView));
            }
        }
    }

    private static class OAuthLoginHandler extends com.nhn.android.naverlogin.OAuthLoginHandler {
        WeakReference<SplashPresenter> providerRef;
        WeakReference<Activity> activityRef;

        private OAuthLoginHandler(SplashPresenter provider, Activity activity) {
            this.providerRef = new WeakReference<>(provider);
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void run(boolean success) {
            SplashPresenter provider = providerRef.get();
            OAuthLogin naverLogin = provider.mOAuthLoginModule;

            if ( success ) {
                // 로그인 성공시 사용자 정보를 가져온다
                provider.executeNaverRequestApiTask();
            } else {
                String errorCode = naverLogin.getLastErrorCode(provider.mContext).getCode();
                String errorDesc = naverLogin.getLastErrorDesc(provider.mContext);
            }
        }
    }

    public void executeNaverRequestApiTask() {
        new RequestApiTask().execute();
    }

    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            //String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginModule.getAccessToken(mView.getApplicationContext());
            String rt = mOAuthLoginModule.getRefreshToken(mView.getApplicationContext());

            //Timber.d("naver access token : %s", at);
            //Timber.d("naver access refresh : %s", rt);
            return mOAuthLoginModule.requestApi(mView.getApplicationContext(), at, url);
        }
        protected void onPostExecute(String content) {
            // xml 정보를 파싱한다
            getParseNaverProfile(content);
        }
    }

    private void getParseNaverProfile(String xmlData) {

        try {
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactory.newPullParser();
            parser.setInput(new StringReader(xmlData));

            String lastName ="";
            String resultCode ="";
            String email ="";
            String nickName ="";
            String avatarUrl ="";
            String id = "";
            String gender = "";
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        lastName = parser.getName();
                        break;
                    case XmlPullParser.TEXT:

                        switch (lastName) {
                            case "nickname":
                                nickName = parser.getText();
                                break;
                            case "profile_image":
                                avatarUrl = parser.getText();
                                break;
                            case "email":
                                email = parser.getText();
                                break;
                            case "resultcode":
                                resultCode = parser.getText();
                                break;
                            case "id":
                                id = parser.getText();
                                break;
                            case "gender":
                                gender = parser.getText();
                                break;
                        }
                        break;
                }
                try {
                    eventType = parser.next();
                } catch (IOException ex) {
                    eventType = parser.END_DOCUMENT;
                }
            }

            if ( resultCode.equals("00")) {

                SnsInfoModel snsInfoModel = new SnsInfoModel();
                snsInfoModel.setAccessToken(mOAuthLoginModule.getAccessToken(mView.getApplicationContext()));
                snsInfoModel.setRefreshToken(mOAuthLoginModule.getRefreshToken(mView.getApplicationContext()));
                snsInfoModel.setEmail(email);
                snsInfoModel.setUsername(nickName);
                snsInfoModel.setProvider(EntityType.SOCIAL_NAVER);
                snsInfoModel.setGender(gender);
                snsInfoModel.setProviderUserId(id);
                snsInfoModel.setAvatar(avatarUrl);
                // 하이파이브 로그인
                //socialLogin(snsInfoModel);
            }
        } catch (XmlPullParserException ex) {
            mView.showErrorMessage("Naver xml parsing - " + ex.getMessage());
            openMainActivity();
        }
    }

    /**
     * 하이파이브 로그인
     * @param snsInfoModel
     */
//    public void socialLogin(SnsInfoModel snsInfoModel) {
//
//        Flowable<AuthToken> observable
//                = mAuthManager.socialLogin(snsInfoModel.getProvider(),
//                                snsInfoModel.getProviderUserId(),
//                                "bearer " + snsInfoModel.getAccessToken());
//
//        _composite.add(observable.observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        authToken -> {
//                            Log.i("hong", String.format("access token : %s, refresh token : %s", authToken.getAccessToken(), authToken.getRefreshToken()));
//                            App.getInstance().setAuthToken(authToken);
//                            App.getInstance().setSnsInfo(snsInfoModel);
//                            getLoginUser();
//                        },
//                        e -> {
//                            mView.showErrorMessage("socialLogin - " + e.getMessage());
//                            openMainActivity();
//                        },
//                        () -> {}
//                ));
//    }
}
