package com.ddastudio.hifivefootball_android.signin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.data.event.UserAccountEvent;
import com.ddastudio.hifivefootball_android.data.model.AuthToken;
import com.ddastudio.hifivefootball_android.data.model.UserModel;

import java.util.Calendar;
import java.util.Observable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import timber.log.Timber;

/**
 * Created by hongmac on 2017. 11. 16..
 */

public class AccountManager {

    private Context context;
    private AccountUser accountUser;
    private UserModel hifiveUser;
    AuthToken hifiveToken;

    private Preferences prefs;

    /**
     * 현재 사용자. 로그인하지 않은 사용자는 UseDevice만 있고, User는 없음
     */
    private UserDevice device;

    public AccountManager(Context context) {
        this.context = context;

        //EventBus.getDefault().register(this);

        loadDevice();
    }

    public void loadDevice() {
        /*UserDevice loaded = prefs.loadDevice();
        if (loaded == null) {
            //Log.i(TAG, "Device doesn't exists. Creating new device");
            this.device = createUserDevice();

            //App.getApiProxy().registerDevice(device, this);
        } else {
            //Log.i(TAG, "Device restored from prefs: " + loaded);
            this.device = loaded;

            if (device.getGcmToken() == null) {
                requestGcmToken();
            }
        }*/

        this.device = createUserDevice();
    }

    /**
     * 서버에 등록할 사용자 기기정보를 생성
     */
    private UserDevice createUserDevice() {
        UserDevice device = new UserDevice();
        device.setId(UUID.randomUUID().toString());
        device.setAndroidId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        //device.setIid(InstanceID.getInstance(context).getId());

        return device;
    }

    public void registerAccountUser(AccountUser user) {
        this.accountUser = user;

        // 소셜계정으로 로그인 하면 소셜타입과 토큰을 환경변수에 저장한다
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit()
                .putString("sns_type", accountUser.getProvider())
                .putString("sns_refresh_token", accountUser.getSnsRefreshToken())
                .putString("sns_access_token", accountUser.getSnsAccessToken())
                .apply();
    }

    public void setHifiveToken(AuthToken token) {

//        Timber.d("[setHifiveToken]" + token.toString());
        this.hifiveToken = token;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit()
                .putString("hifive_access_token", token.getAccessToken())
                .putString("hifive_refresh_token", token.getRefreshToken())
                .putInt("hifive_token_expires_in", token.getExpiresIn())
                .putLong("hifive_saved_time", Calendar.getInstance().getTimeInMillis())
            .apply();
    }

    /**
     * Access 토큰을 갱신했을 때는 Refresh 토큰이 넘어오지 않는다.
     * @param token
     */
    public void setHifiveAccessToken(AuthToken token) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String refreshToken = pref.getString("hifive_refresh_token", "");
        token.setRefreshToken(refreshToken);
        this.hifiveToken = token;

        pref.edit()
                .putString("hifive_access_token", token.getAccessToken())
                .putInt("hifive_token_expires_in", token.getExpiresIn())
                .putLong("hifive_saved_time", Calendar.getInstance().getTimeInMillis())
            .apply();
    }

    public String getHifiveAccessToken() {

        if ( hifiveToken != null) {
            return hifiveToken.getAccessToken();
        }

        return "";
    }

    public String getHifiveRefreshToken() {

        if ( hifiveToken != null) {
            return hifiveToken.getRefreshToken();
        }

        return "";
    }

    public int getHifiveExpiresIn() {
        if ( hifiveToken != null ) {
            return hifiveToken.getExpiresIn();
        }

        return -1;
    }


    /**
     * 하이파이브 로그인 완료시 실행
     * @param hifiveUser
     */
    public void registerHifiveUser(UserModel hifiveUser) {

        registerHifiveUser(hifiveUser, true);

    }

    /**
     * 하이파이브 로그인 완료시 실행
     * @param hifiveUser
     * @param sendEvent
     */
    public void registerHifiveUser(UserModel hifiveUser, boolean sendEvent) {

        this.hifiveUser = hifiveUser;

        // TODO: 2017. 11. 19. 로그인 이벤트 전달
        if ( sendEvent == true ) {
            App.getInstance().bus().send(new UserAccountEvent.SignIn());
        }
    }

    /**
     * 하이파이브 로그아웃시 실행
     */
    public void unregisterHifiveUser() {
        this.accountUser = null;
        this.hifiveUser = null;
        this.hifiveToken = null;

        // TODO: 2017. 11. 19. 로그아웃 이벤트 전달
        App.getInstance().bus().send(new UserAccountEvent.SignOut());
    }

    public AccountUser getAccountUser() {
        return accountUser;
    }

    public boolean hasAccountUser() {

        return accountUser != null && !TextUtils.isEmpty(accountUser.getProviderUserId()) && !accountUser.getSnsAccessToken().replace("bearer ", "").equals("");
    }

    public String getSocialAccessToken() {

        return accountUser != null ? accountUser.getSnsAccessToken() : null;
    }

    public String getProvider() {

        return accountUser != null ? accountUser.getProvider() : null;
    }

    public String getProviderId() {

        return accountUser != null ? accountUser.getProviderUserId() : "";
    }

    public boolean isHifiveMember() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String sns_type = pref.getString("sns_type", "");
        String hifive_access_token = pref.getString("hifive_access_token", "");
        String hifive_refresh_token = pref.getString("hifive_refresh_token", "");
        int hifive_token_expires_in = pref.getInt("hifive_token_expires_in", 0);

        return  !TextUtils.isEmpty(sns_type) &&
                !TextUtils.isEmpty(hifive_refresh_token);
    }

    public Long getSharedSaveTime() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Long hifive_save_time = pref.getLong("hifive_saved_time", -1);

        return hifive_save_time;
    }

    public String getSharedHifiveRefreshToken() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String hifive_refresh_token = pref.getString("hifive_refresh_token", "");

        return hifive_refresh_token;
    }

    public int getSharedHifiveExpires() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int hifive_token_expires_in = pref.getInt("hifive_token_expires_in", 0);

        return hifive_token_expires_in;
    }

    /**
     * 앱 최초 실행시 하이파이브 멤버인 경우 자동으로 토큰을 갱신할 때 유저정보와 소셜계정정보는 안들어 가있기 때문에
     * 이 함수로 체크해야 함.
     * 단, 만료시간 로직을 꼭 추가하자.
     * @return
     */
    public boolean isAuthorized() {

        if ( hifiveToken == null ) {
            return false;
        }

        long nowMillis = Calendar.getInstance().getTimeInMillis();
        long sharedSaveTime = App.getAccountManager().getSharedSaveTime();
        long diff = ( nowMillis - sharedSaveTime ) / 1000; // 초 단위

        return hifiveToken != null &&
                !TextUtils.isEmpty(hifiveToken.getAccessToken()) &&
                !TextUtils.isEmpty(hifiveToken.getRefreshToken()) &&
                diff < 21600; // 21600 = 6시간을 초로 변환 ( 6 * 60 * 60 )
    }

    /**
     * 기존 멤버는 토큰 갱신만 하기때문에  소셜 회원정보와 토큰 정보가 없다.
     * 아래 함수처럼 체크하면 안되고 하이파이브 토큰 정보와 만료시간 정보로 로그인 유무를 판단해야 한다.
     */
//    public boolean isAuthorized() {
//
//        // TODO: 2017. 11. 19. 만료시간도 추가해줘야 한다.
//        return accountUser != null &&
//                !TextUtils.isEmpty(accountUser.getSnsAccessToken().replace("bearer ", "")) &&
//                !TextUtils.isEmpty(accountUser.getSnsRefreshToken().replace("bearer ", "")) &&
//                hifiveUser != null &&
//                hifiveToken != null &&
//                !TextUtils.isEmpty(hifiveToken.getAccessToken()) &&
//                !TextUtils.isEmpty(hifiveToken.getRefreshToken());
//    }

    public boolean isSameUser(String userName) {

        return isAuthorized() &&
                hifiveUser.getUsername().equals(userName);
    }

    public UserModel getHifiveUser() {
        return hifiveUser;
    }

    public String getNickName() {

        return hifiveUser != null ? hifiveUser.getNickname() : "";
    }

    public String getUserName() {
        return hifiveUser != null ? hifiveUser.getUsername() : "";
    }

    public String getUserNameAndNickName() {

        return getUserName() + " " + getNickName();
    }

    public String getNickNameAndUserName() {

        return String.format("%s (%s)", getNickName(), getUserName());
    }

    public void setUserAvatar(String avatarUrl) {

        if ( hifiveUser != null ) {
            hifiveUser.setAvatarUrl(avatarUrl);
        }
    }

    public String getUserAvatar(boolean isCdn) {

        if ( hifiveUser == null ) {
            return "";
        }

        if ( isCdn ) {
            return hifiveUser.getAvatarUrl();
        } else {
            return hifiveUser.getAvatarOriginUrl();
        }
    }

    public String getUserProfile() {
        return hifiveUser != null && !TextUtils.isEmpty(hifiveUser.getProfile()) ? hifiveUser.getProfile() : "간단한 프로필을 작성해보세요";
    }
}
