package com.ddastudio.hifivefootball_android.signin;

import android.app.Activity;
import android.content.Context;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;

import timber.log.Timber;

import static com.ddastudio.hifivefootball_android.signin.Constants.ACCOUNT_NAVER;

/**
 * Created by hongmac on 2017. 11. 16..
 */

public class SignInManager implements Constants {

    private Context context;

    /**
     * 페이스북, 네이버의 OAuth 인증을 위해 필요한 ID, secret 등이 저장된 객체
     * SplashActivity.onCreate() 에서 loadSecret() 함수를 호출한 후 자동로그인을 진행한다.
     */
    private Secret secret;

    public SignInManager(Context context) {
        this.context = context;
    }

    public void loadSecret() {

        // TODO: 2017. 11. 16. 1. Secret 정보는 앱에 있으면 안되고 서버에서 받아올 수 있도록 변경하자.
        // TODO: 2017. 11. 16. 2. 아니면 그래들에서 암호화하여 보관할 수 있도록 하자
        secret = new Secret();
//        secret.setNaverClientId(context.getResources().getString(R.string.naver_client_id));
//        secret.setNaverClientSecret(context.getResources().getString(R.string.naver_client_secret));
        secret.setNaverClientId(BuildConfig.NAVER_CLIENT_ID);
        secret.setNaverClientSecret(BuildConfig.NAVER_CLIENT_SECRET);
        secret.setNaverClientName(context.getResources().getString(R.string.naver_client_name));
    }

    /**
     * 약식 팩토리.
     * @param accountType
     * @return
     */
    public SignInProvider getSignInProvider(String accountType) {
        switch (accountType) {
            case ACCOUNT_GOOGLE:
                //return new GoogleSignInProvider(context);
                throw new RuntimeException("Unknown provider: " + accountType);
            case ACCOUNT_NAVER:
                return new NaverSignInProvider(context);
            case ACCOUNT_KAKAO:
                return new KakaoSignInProvider(context);
        }

        throw new RuntimeException("Unknown provider: " + accountType);
    }

    public void SignIn(BaseActivity activity, String accountType) {

        SignInProvider provider = getSignInProvider(accountType);
        provider.requestSignIn(activity, secret);
    }

    public void SignOut(BaseActivity activity, String accountType) {

        SignInProvider provider = getSignInProvider(accountType);
        provider.requestSignOut(activity, secret);
    }

}
