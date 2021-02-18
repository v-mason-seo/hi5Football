package com.ddastudio.hifivefootball_android.signin;

import android.app.Activity;
import android.content.Context;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

/**
 * Created by hongmac on 2017. 11. 20..
 */

public class KakaoSignInProvider extends SignInProvider<String> {

    private SessionCallback callback;

    protected KakaoSignInProvider(Context context) {
        super(context);

        Session.getCurrentSession().clearCallbacks();
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    @Override
    public void requestSignIn(Activity activity, Secret secret) {
//        Timber.d("[requestSignIn] kakao");
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Override
    protected void onSignInComplete(String result) {
        Session.getCurrentSession().removeCallback(callback);
    }

    @Override
    public void requestSignOut(Activity activity, Secret secret) {

    }

    @Override
    protected void onSignOutComplete(String result) {
        Session.getCurrentSession().removeCallback(callback);
    }

    /*------------------------------------------------------*/

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
//            Timber.d("[onSessionOpened]");
//            Timber.d("[onSessionOpened] kakao access token : %s", Session.getCurrentSession().getTokenInfo().getAccessToken());
//            Timber.d("[onSessionOpened] kakao refresh token : %s", Session.getCurrentSession().getTokenInfo().getRefreshToken());
            //onKakaoSignup();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
//                Timber.d(exception);
            }
        }
    }
}
