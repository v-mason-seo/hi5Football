package com.ddastudio.hifivefootball_android.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.data.manager.AuthManager;
import com.ddastudio.hifivefootball_android.data.manager.UserManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.signin.AccountUser;
import com.ddastudio.hifivefootball_android.signin.SnsInfoModel;
import com.ddastudio.hifivefootball_android.signin.SocialLoginManager;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;

import org.parceler.Parcels;

import java.lang.ref.WeakReference;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static com.ddastudio.hifivefootball_android.common.EntityType.SOCIAL_KAKAO;
import static com.ddastudio.hifivefootball_android.common.EntityType.SOCIAL_NAVER;
import static com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler;

/**
 * Created by hongmac on 2017. 9. 6..
 */

public class LoginPresenter implements BaseContract.Presenter {

    Context mContext;
    LoginActivity mView;
    /**
     * 하아파이브 로그인 관련 API
     */
    AuthManager mAuthManager;
    /**
     * 하이파이브 사용자 API
     */
    UserManager mUserManager;
    /**
     * 네이버 API
     */
    SocialLoginManager mSocialManager;


    // *** Naver ***
    OAuthLogin mOAuthLoginModule;


    // *** Kakao ***
    private SessionCallback callback;

    @NonNull
    CompositeDisposable _composite;

    public LoginPresenter() {

        _composite = new CompositeDisposable();
    }

    @Override
    public void attachView(BaseContract.View view) {

        mView = (LoginActivity)view;
        mContext = mView.getApplicationContext();
        mAuthManager = AuthManager.getInstance();
        mUserManager = UserManager.getInstance();
        this.mSocialManager = new SocialLoginManager(mContext);
    }

    @Override
    public void detachView() {

        if ( _composite != null )
            _composite.clear();

        // 네이버
        if ( mOAuthLoginHandler != null )
            mOAuthLoginHandler.removeCallbacksAndMessages(null);

        // 카카오
        Session.getCurrentSession().removeCallback(callback);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openSignupActivity(SnsInfoModel snsInfoModel) {
        Intent intent = new Intent(mView.getApplicationContext(), SignupActivity.class);
        intent.putExtra("ARGS_AUTH_MODEL", Parcels.wrap(snsInfoModel));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivity(intent);
        mView.finish();
    }

    public void openSignupActivity() {
        Intent intent = new Intent(mView.getApplicationContext(), SignupActivity.class);
        //intent.putExtra("ARGS_AUTH_MODEL", Parcels.wrap(snsInfoModel));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivity(intent);
        mView.finish();
    }


    /*---------------------------------------------------------------------------------------------*/

    public void initKaKaoOAuth() {
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    protected void requestMe() {

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    //KakaoToast.makeToast(getApplicationContext(), getString(R.string.error_message_for_service_unavailable), Toast.LENGTH_SHORT).show();
                    //finish();
                } else {
                    //redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onSuccess(UserProfile userProfile) {

//                Timber.d("[requestMe] onSuccess, user profile : " + userProfile);
//                Timber.d("[requestMe] kakao access token : %s", Session.getCurrentSession().getTokenInfo().getAccessToken());
//                Timber.d("[requestMe] kakao refresh token : %s", Session.getCurrentSession().getTokenInfo().getRefreshToken());

                mView.showLoading();

                AccountUser user = new AccountUser();
                user.setAccessToken(Session.getCurrentSession().getTokenInfo().getAccessToken());
                user.setRefreshToken(Session.getCurrentSession().getTokenInfo().getRefreshToken());
                user.setAvatar(userProfile.getProfileImagePath());
                user.setEmail(userProfile.getEmail());
                user.setNickName(userProfile.getNickname());
                user.setUserName(userProfile.getNickname());
                user.setProvider(SOCIAL_KAKAO);
                user.setProviderUserId(String.valueOf(userProfile.getId()));
                App.getAccountManager().registerAccountUser(user);

                //Timber.d(user.toString());

                hifiveLoginObservable(user)
                        .filter(hifiveToken -> hifiveToken != null
                                && hifiveToken.isValidToken())
                        .map(hifiveToken -> {
                            App.getAccountManager().setHifiveToken(hifiveToken.getAuthToken());
                            return hifiveToken;
                        })
                        .flatMap(hifiveToken -> hifiveUserObservable(hifiveToken.getAccessToken()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(hifiveUser -> {
                                    //Timber.d("call registerHifiveUser!!");
                                    App.getAccountManager().registerHifiveUser(hifiveUser);
                                    mView.hideLoading();
                                    mView.finish();
                                },
                                err -> {
                                    //Timber.d("[KaKao] Hifive login error : %s", err.getMessage());
                                    // TODO: 2017. 11. 25. 에러코드를 확인하고 회원가입 창으로 이동해야한다. 확인 후 프로그램 오류일 경우 아래 주석처럼 이벤트를 전송하자.
//                            App.getInstance().bus().send(new UserAccountEvent.SignError("로그인에 실패했습니다.\r\n" + t.getMessage()));
//                            mView.finish();
                                    mView.hideLoading();
                                    openSignupActivity();
                                    mView.finish();
                                },
                                () -> {}
                        );
            }

            @Override
            public void onNotSignedUp() {
                //showSignup();
            }
        });
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            //mView.showErrorMessage("[onSessionOpenFailed]");
            if(exception != null) {
                //mView.showErrorMessage("[onSessionOpenFailed] error messate : " +  exception.getMessage());
                Logger.e(exception);
                //Timber.i("[onSessionOpenFailed] exception");
            }
        }
    }


    /*---------------------------------------------------------------------------------------------*/

    public void initNaverOAuth(/*String clientId, String clientSecret, */String clientName) {
        // TODO: 2017. 11. 26. 릴리즈시 아래 코드 주석처리해야함.
        //OAuthLoginDefine.DEVELOPER_VERSION = true;

        mOAuthLoginModule = OAuthLogin.getInstance();
//        mOAuthLoginModule.init(mView.getApplicationContext(),
//                clientId,
//                clientSecret,
//                clientName);

        mOAuthLoginModule.init(mView.getApplicationContext(),
                BuildConfig.NAVER_CLIENT_ID,
                BuildConfig.NAVER_CLIENT_SECRET,
                clientName);

        mView.addNaverHandler(new OAuthLoginHandler(this, mView));
    }

    public void executeAutoLogin() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mView);
        String sns_type = pref.getString("sns_type", "");
        String sns_token = pref.getString("sns_token", "");

        if ( sns_type.equals(EntityType.SOCIAL_NAVER) ) {

            if (!TextUtils.isEmpty(sns_token)) {
                OAuthLogin.getInstance().startOauthLoginActivity(mView, new OAuthLoginHandler(this, mView));
            } else {
                Snackbar.make(mView.getWindow().getDecorView(), "ㅇㄴㄹㄴㅇㄹ", Snackbar.LENGTH_LONG)
                        .setAction("종료", v -> mView.finish())
                        .show();
            }
        }
    }

    private static class OAuthLoginHandler extends com.nhn.android.naverlogin.OAuthLoginHandler {
        WeakReference<LoginPresenter> providerRef;
        WeakReference<Activity> activityRef;

        private OAuthLoginHandler(LoginPresenter provider, Activity activity) {
            this.providerRef = new WeakReference<>(provider);
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void run(boolean success) {
            LoginPresenter provider = providerRef.get();
            OAuthLogin naverLogin = provider.mOAuthLoginModule;

            if ( success ) {
                //Timber.d("[Naver] sign success");

                if ( provider == null ) {
                    //Timber.d("[Naver] provider is null!");
                    return;
                }

                String at = naverLogin.getAccessToken(provider.mContext);
                String rt = naverLogin.getRefreshToken(provider.mContext);
                //Timber.d("**** naver access token : " + at);
                //Timber.d("**** naver refresh token : " + rt);

                // 하이파이브 로그인
                provider.hifiveLogin(naverLogin);
            } else {
                String errorCode = naverLogin.getLastErrorCode(provider.mContext).getCode();
                String errorDesc = naverLogin.getLastErrorDesc(provider.mContext);
                //Timber.d("[Naver] sign fail, errorCode : %s, errorDesc : %s", errorCode, errorDesc);
            }
        }
    }

    /**
     * 하이파이브 로그인 메인
     * @param naverOAuth
     */
    private void hifiveLogin(OAuthLogin naverOAuth) {

        mView.showLoading();
        Flowable<AccountUser> naverProfileObservable = naverProfileObservable(naverOAuth);

        naverProfileObservable
                .filter(accountUser -> accountUser != null)
                .flatMap(accountUser -> hifiveLoginObservable(accountUser))
                .filter(hifiveToken -> hifiveToken != null
                        && hifiveToken.isValidToken())
                .map(hifiveToken -> {
                    App.getAccountManager().setHifiveToken(hifiveToken.getAuthToken());
                    return hifiveToken.getAuthToken();
                })
                //.flatMap(hifiveToken -> hifiveUserObservable(hifiveToken.getHifiveToken()))
                .flatMap(hifiveToken -> hifiveUserObservable(hifiveToken.getAccessToken()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hifiveUser -> {
                    //Timber.d("call registerHifiveUser!!");
                    //Timber.d("[hifiveLogin] hifive access token : %s", App.getAccountManager().getHifiveAccessToken());
                            App.getAccountManager().registerHifiveUser(hifiveUser);
                            mView.hideLoading();
                            mView.finish();
                        },
                        err -> {
                            //Timber.d("[Naver] Hifive login error : %s", err.getMessage());
                            // TODO: 2017. 11. 25. 에러코드를 확인하고 회원가입 창으로 이동해야한다. 확인 후 프로그램 오류일 경우 아래 주석처럼 이벤트를 전송하자.
//                            App.getInstance().bus().send(new UserAccountEvent.SignError("로그인에 실패했습니다.\r\n" + t.getMessage()));
//                            mView.finish();
                            mView.hideLoading();
                            openSignupActivity();
                            mView.finish();
                        },
                        () -> {}
                    );
    }

    /**
     * 1. 네이버 회원정보를 가져온다.
     * @param naverOAuth
     * @return
     */
    private Flowable<AccountUser> naverProfileObservable(OAuthLogin naverOAuth) {

        Flowable<AccountUser> observable
                = mSocialManager.onLoadNaverUserProfile("Bearer " + naverOAuth.getAccessToken(mContext))
                .flatMap(item -> {

                    AccountUser user = null;
                    if ( item.getResultcode().equals("00")) {
                        user = new AccountUser();
                        user.setAccessToken(naverOAuth.getAccessToken(mContext));
                        user.setRefreshToken(naverOAuth.getRefreshToken(mContext));
                        user.setAvatar(item.getAvatar());
                        user.setEmail(item.getEmail());
                        user.setNickName(item.getUserName());
                        user.setUserName(item.getUserName());
                        user.setProvider(SOCIAL_NAVER);
                        user.setProviderUserId(String.valueOf(item.getProviderId()));
                        App.getAccountManager().registerAccountUser(user);

                        //Timber.d(user.toString());
                    }

                    return Flowable.just(user);
                });

        return observable;
    }

    /**
     * 2. 하이파이브 로그인
     *    소셜 아이디와 토큰으로 로그인
     *    실패시 회원가입 창으로 이동
     * @param accountUser
     * @return
     */
    private Flowable<DBResultResponse> hifiveLoginObservable(AccountUser accountUser) {

        //Timber.d("[hifiveLoginObservable] access token : %s", accountUser.getSnsAccessToken());
        Flowable<DBResultResponse> observable
                = mAuthManager.socialLogin(accountUser.getProvider(),
                accountUser.getProviderUserId(),
                accountUser.getSnsAccessToken());

        // 하이파이브 인증 실패하도록함 -> 회원가입 테스트를 위해서..
//        Flowable<AuthToken> observable
//                = mAuthManager.socialLogin(accountUser.getProvider(),
//                accountUser.getProviderUserId(),
//                "bearer ");

        return observable;
    }




    private Flowable<UserModel> hifiveUserObservable(String token) {

        Flowable<UserModel> observable
                = mUserManager.getLoginUser(token);

        return observable;
    }

    /**
     * 네이버 API 호출 - 사용자 기본정보 가져오기
     */
//    public void executeNaverRequestApiTask() {
//        new RequestApiTask().execute();
//    }

    /**
     * 네이버 - 하이파이브 연동 해제
     */
    public void executeNaverDeleteTokenTask() {
        new DeleteTokenTask().execute();
    }

    /**
     * 네이버 로그아웃
     */
    public void executeNaverLogout() {
        mOAuthLoginModule.logout(mView.getApplicationContext());
    }

    /**
     * 네이버 API 호출 - 사용자 기본정보 가져오기
     */
//    private class RequestApiTask extends AsyncTask<Void, Void, String> {
//        @Override
//        protected void onPreExecute() {
//        }
//        @Override
//        protected String doInBackground(Void... params) {
//            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
//            //String url = "https://openapi.naver.com/v1/nid/me";
//            String at = mOAuthLoginModule.getAccessToken(mView.getApplicationContext());
//            String rt = mOAuthLoginModule.getRefreshToken(mView.getApplicationContext());
//
//            Log.i("hong", "naver access token : " + at);
//            Log.i("hong", "naver refresh token : " + rt);
//            return mOAuthLoginModule.requestApi(mView.getApplicationContext(), at, url);
//        }
//        protected void onPostExecute(String content) {
//            Log.i("hong", content);
//            // xml 정보를 파싱한다
//            getParseNaverProfile(content);
//        }
//    }

    /**
     * 네이버 - 하이파이브 연동 해제
     */
    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(mView.getApplicationContext());

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                //Log.i("hong", "errorCode : " + mOAuthLoginModule.getLastErrorCode(mView.getApplicationContext()));
                //Log.i("hong", "errorDesc : " + mOAuthLoginModule.getLastErrorDesc(mView.getApplicationContext()));
            }

            return null;
        }
        protected void onPostExecute(Void v) {
            //updateView();
        }
    }

    /**
     *  네이버 토큰을 갱신하다 - 하이파이브는 사용 안함
     */
    private class RefreshTokenTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return mOAuthLoginModule.refreshAccessToken(mView.getApplicationContext());
        }
        protected void onPostExecute(String res) {
            //updateView();
        }
    }

    /**
     * 네이버 사용자 정보 파싱
     * @param xmlData
     */
//    private void getParseNaverProfile(String xmlData) {
//
//        try {
//            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
//            XmlPullParser parser = xmlFactory.newPullParser();
//            parser.setInput(new StringReader(xmlData));
//
//            String lastName ="";
//            String resultCode ="";
//            String email ="";
//            String nickName ="";
//            String avatarUrl ="";
//            String id = "";
//            String gender = "";
//            int eventType = parser.getEventType();
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//
//                switch (eventType) {
//                    case XmlPullParser.START_TAG:
//                        Log.i("oauth", "getPlayerName() - " + parser.getName());
//                        lastName = parser.getName();
//                        break;
//                    case XmlPullParser.TEXT:
//
//                        switch (lastName) {
//                            case "nickname":
//                                nickName = parser.getText();
//                                break;
//                            case "profile_image":
//                                avatarUrl = parser.getText();
//                                break;
//                            case "email":
//                                email = parser.getText();
//                                break;
//                            case "resultcode":
//                                resultCode = parser.getText();
//                                break;
//                            case "id":
//                                id = parser.getText();
//                                break;
//                            case "gender":
//                                gender = parser.getText();
//                                break;
//                        }
//                        break;
//                }
//                try {
//                    eventType = parser.next();
//                } catch (IOException ex) {
//                    eventType = parser.END_DOCUMENT;
//                }
//            }
//
//            if ( resultCode.equals("00")) {
//                Log.i("hong", String.format("email : %s, nickname : %s, avatar : %s, id : %s", email, nickName, avatarUrl, id));
//                Log.i("hong", "naver id : " + id);
//
//                SnsInfoModel snsInfoModel = new SnsInfoModel();
//                snsInfoModel.setAccessToken(mOAuthLoginModule.getAccessToken(mView.getApplicationContext()));
//                snsInfoModel.setRefreshToken(mOAuthLoginModule.getRefreshToken(mView.getApplicationContext()));
//                snsInfoModel.setEmail(email);
//                snsInfoModel.setUsername(nickName);
//                snsInfoModel.setProvider(EntityType.SOCIAL_NAVER);
//                snsInfoModel.setGender(gender);
//                snsInfoModel.setProviderUserId(id);
//                snsInfoModel.setAvatar(avatarUrl);
//                // 하이파이브 로그인
//                socialLogin(snsInfoModel);
//            }
//        } catch (XmlPullParserException ex) {
//            Log.i("hong", "xmp parsing error : " + ex.getMessage());
//        }
//
//        //return null;
//    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 하이파이브 로그인
     *  - 성공(200) : 회원 가입 완료
     *  - 실패(401) : 회원가입창으로 이동
     * @param snsInfoModel
     */
//    public void socialLogin(SnsInfoModel snsInfoModel) {
//
//        Flowable<AuthToken> observable
//                = mAuthManager.socialLogin(snsInfoModel.getProvider(),
//                                            snsInfoModel.getProviderUserId(),
//                                            "bearer " + snsInfoModel.getAccessToken());
//
//        _composite.add(observable.observeOn(AndroidSchedulers.mainThread())
//        .subscribe(
//                authToken -> {
//                    Log.i("hong", String.format("access token : %s, refresh token : %s", authToken.getAccessToken(), authToken.getRefreshToken(), "bearer " + authToken.getAccessToken()));
//                    App.getInstance().setAuthToken(authToken);
//                    App.getInstance().setSnsInfo(snsInfoModel);
//                    getLoginUser();
//                },
//                e -> {
//                    if ( e instanceof HttpException ) {
//                        if ( ((HttpException) e).code() == 401) {
//                            // 회원가입 액티비티 호출
//                            Log.i("hong", e.getMessage());
//                            openSignupActivity(snsInfoModel);
//                        } else {
//                            Log.i("hong", "token error : " + e.getMessage());
//                            mView.finish();
//                        }
//                    } else {
//                        mView.finish();
//                    }
//                },
//                () -> Log.i("hong", "[socialLogin] complete")
//            ));
//    }

    /**
     * 하이파이브 로그인 사용자 정보 불러오기
     */
//    public void getLoginUser() {
//
//        String token;
//
//        if (App.getInstance().isAuthorized()) {
//            token = App.getInstance().getHifiveToken();
//        } else {
//            Snackbar.make(mView.getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
//                    //.setAction("이동", v -> openLoginActivity())
//                    .show();
//
//            return;
//        }
//
//        Flowable<UserModel> observable
//                = mUserManager.getLoginUser(token);
//
//        _composite.add(
//                observable.observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                item -> {
//                                    Toasty.success(mView.getApplicationContext(), mView.getResources().getString(R.string.complete_load_user_info), Toast.LENGTH_LONG, true).show();
//                                    App.getInstance().setUser(item);
//                                    App.getInstance().bus().send(new LoginEvent.LoginStatusEvent(true, mView.getClass().getSimpleName()));
//                                    mView.finish();
//
//                                },
//                                e -> {
//                                    mView.showErrorMessage(mView.getResources().getString(R.string.fail_load_user_info) + e.getMessage());
//                                    mView.finish();
//                                },
//                                () -> { }
//                        ));
//    }
}
