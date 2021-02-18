package com.ddastudio.hifivefootball_android.signin;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.data.event.UserAccountEvent;
import com.ddastudio.hifivefootball_android.data.manager.AuthManager;
import com.ddastudio.hifivefootball_android.data.manager.UserManager;
import com.ddastudio.hifivefootball_android.data.model.AuthToken;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.nhn.android.naverlogin.OAuthLogin;

import java.lang.ref.WeakReference;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;
import timber.log.Timber;

import static com.ddastudio.hifivefootball_android.common.EntityType.SOCIAL_NAVER;

/**
 * Created by hongmac on 2017. 11. 16..
 */

class NaverSignInProvider extends SignInProvider<String> {

    NaverSignInProvider myInstance;
    @NonNull
    CompositeDisposable _composite;

    /**
     * 사용자 정보를 제공하는 네이버 API의 URL
     */
    private static final String NAVER_PROFILE_API_URL = "https://openapi.naver.com/v1/nid/getUserProfile.xml";

    /**
     * 네이버에서 제공하는 helper. Access token 등의 제어를 좀더 쉽게 할 수 있게 해줌.
     */
    private OAuthLogin oAuthLogin;

    /**
     * 네이버 API
     */
    SocialLoginManager mSocialManager;

    /**
     * 하아파이브 로그인 관련 API
     */
    AuthManager mAuthManager;

    /**
     * 하이파이브 사용자 API
     */
    UserManager mUserManager;

    /**
     * 생성자
     * @param context
     */
    protected  NaverSignInProvider(Context context) {
        super(context);

        this.myInstance = this;
        this.mSocialManager = new SocialLoginManager(context);
        this.mAuthManager = AuthManager.getInstance();
        this.mUserManager = UserManager.getInstance();
        this._composite = new CompositeDisposable();

    }

    @Override
    public void requestSignIn(Activity activity, Secret secret) {

        // 네이버 로그인 초기화
        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(context,
                secret.getNaverClientId(),
                secret.getNaverClientSecret(),
                secret.getNaverClientName());

        // 네이버 로그인
        oAuthLogin.startOauthLoginActivity(activity, new OAuthLoginHandler(this, activity));
    }

    @Override
    public void requestSignOut(Activity activity, Secret secret) {

        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(context,
                secret.getNaverClientId(),
                secret.getNaverClientSecret(),
                secret.getNaverClientName());

        new DeleteTokenTask().execute(oAuthLogin);
    }

    @Override
    protected void onSignInComplete(String result) {
        if ( result.equals("success")) {
            App.getInstance().bus().send(new UserAccountEvent.SignIn());
        }
    }

    @Override
    protected void onSignOutComplete(String result) {

        if ( result.equals("success")) {
            App.getInstance().bus().send(new UserAccountEvent.SignOut());
        }
    }

    /*------------------------------------------------------*/


    /**
     * 네이버 로그인 완료 후 호출됨
     * 네이버, 네이버 ID, 네이버 Access token을 통해 하이파이브 로그인
     * 로그인 실패시 회원가입창으로 이동함.
     */
    private static class OAuthLoginHandler extends com.nhn.android.naverlogin.OAuthLoginHandler {
        WeakReference<NaverSignInProvider> providerRef;
        WeakReference<Activity> activityRef;

        private OAuthLoginHandler(NaverSignInProvider provider, Activity activity) {
            this.providerRef = new WeakReference<>(provider);
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void run(boolean success) {
            NaverSignInProvider provider = providerRef.get();
            if ( provider == null ) {
                //Log.d("hong", "[Naver] providerRef.get() return null");
                return;
            }
            OAuthLogin naverLogin = provider.oAuthLogin;
            Activity activity = activityRef.get();

            if (success) {
                String accessToken = naverLogin.getAccessToken(activity);
                String refreshToken = naverLogin.getRefreshToken(activity);
//                Timber.i("Naver token {\n" +
//                        "\t- access token : %s\n" +
//                        "\t- refresh token : %s }", accessToken, refreshToken);

                //하이파이브 로그인
                provider.hifiveLogin(naverLogin);

            } else {
                //Log.w(TAG, "Naver SignIn failed:" + naverLogin.getLastErrorDesc(activity));
                // 로그인 실패 이벤트를 보내야한다.
            }
        }
    }


    /**
     * 네이버 연동해제
     * todo 나중에 RxJava2로 변경하자
     *
     */
    private class DeleteTokenTask extends AsyncTask<OAuthLogin, Void, String> {
        @Override
        protected String doInBackground(OAuthLogin... params) {
            OAuthLogin naverOAuth = params[0];
            boolean isSuccessDeleteToken = naverOAuth.logoutAndDeleteToken(context);

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
//                Log.i("hong", "----------------------------------------------");
//                Log.i("hong", "[Naver] errorCode : " + naverOAuth.getLastErrorCode(context));
//                Log.i("hong", "[Naver] errorDesc : " + naverOAuth.getLastErrorDesc(context));
//                Log.i("hong", "----------------------------------------------");
                return "error";
            }

            return "compete";
        }
        protected void onPostExecute(String v) {
            super.onPostExecute(v);
            onSignOutComplete(v);
        }
    }

    /*------------------------------------------------------*/

    /**
     * 하이파이브 로그인 메인
     * @param naverOAuth
     */
    private void hifiveLogin(OAuthLogin naverOAuth) {

        Flowable<AccountUser> naverProfileObservable = naverProfileObservable(naverOAuth);

        naverProfileObservable
                .filter(accountUser -> accountUser != null)
                .flatMap(accountUser -> hifiveLoginObservable(accountUser))
                .filter(hifiveToken -> hifiveToken != null && hifiveToken.isValidToken())
                .map(hifiveToken -> {
                    App.getAccountManager().setHifiveToken(hifiveToken.getAuthToken());
                    return hifiveToken.getAuthToken();
                })
                .flatMap(hifiveToken -> hifiveUserObservable(hifiveToken.getAccessToken()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hifiveUser -> {
                            App.getAccountManager().registerHifiveUser(hifiveUser);
                            onSignInComplete("success");
                        },
                        err -> {
                            //Timber.i("[Naver] Hifive login error : %s", err.getMessage());
                            onSignInComplete("failure");
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
                = mSocialManager.onLoadNaverUserProfile("Bearer " + naverOAuth.getAccessToken(context))
                .flatMap(item -> {

                    AccountUser user = null;
                    if ( item.getResultcode().equals("00")) {
                        user = new AccountUser();
                        user.setAccessToken(naverOAuth.getAccessToken(context));
                        user.setRefreshToken(naverOAuth.getRefreshToken(context));
                        user.setAvatar(item.getAvatar());
                        user.setEmail(item.getEmail());
                        user.setNickName(item.getUserName());
                        user.setUserName(item.getUserName());
                        user.setProvider(SOCIAL_NAVER);
                        user.setProviderUserId(item.getProviderId());

                        App.getAccountManager().registerAccountUser(user);
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

    /*------------------------------------------------------*/
}
