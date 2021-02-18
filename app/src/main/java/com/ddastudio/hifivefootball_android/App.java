package com.ddastudio.hifivefootball_android;

import android.app.Application;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.data.model.arena.ArenaConnection;
import com.ddastudio.hifivefootball_android.signin.AccountManager;
import com.ddastudio.hifivefootball_android.signin.KakaoSDKAdapter;
import com.ddastudio.hifivefootball_android.ui.widget.DiscreteScrollViewOptions;
import com.ddastudio.hifivefootball_android.utils.RxBus;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.kakao.auth.KakaoSDK;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * 작업내역
 * 1. 선수프로필 빈화면으로 나오는 문제 수정
 */

/**
 * Created by hongmac on 2017. 9. 4..
 * todo 릴리즈 버전 배포시 체크사항 확인할 것.
 * todo 1. 네이버 로그인
 * todo 2. 카카오 크해시
 * todo 3. LeakCanary 주석처리
 * todo 4. 렘을 지우던지 구현하던지 선택할 것.
 *
 */

public class App extends Application {

    //public static final String AUTH_URL = "http://" + "172.17.0.59" + ":9080/auth";
    //public static final String REALM_URL = "realm://" + "172.17.0.59" + ":9080/~/realmtasks";

    /*
     * 테스트 - MQTT_Connection
     */
    private ArenaConnection arenaConnection;

    public ArenaConnection getArenaConnection() {
        return arenaConnection;
    }

    public void setArenaConnection(ArenaConnection arenaConnection) {
        this.arenaConnection = arenaConnection;
    }

    /**
     * 싱글턴 인스턴스
     */
    static App mAppInstance;

    /**
     * 사용자정보 저장/관리
     */
    private AccountManager accountManager;

    /**
     * 하이파이브 설정 및 마스터 데이 관리
     */
    HifiveSettingsManager settingsManager;

    /**
     * 이벤트 버스
     */
    RxBus bus;

    /**
     * Room Database
     */
    AppDatabase hifiveDB;


    public static App getInstance() {
        return mAppInstance;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        mAppInstance = this;

        KakaoSDK.init(new KakaoSDKAdapter());
        accountManager = new AccountManager(this);
        settingsManager = new HifiveSettingsManager(this);

//        if (BuildConfig.DEBUG) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
//        }

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        DiscreteScrollViewOptions.init(this);
        // 오류보고
//        if (!BuildConfig.DEBUG) {
//            Fabric.with(this, new Crashlytics());
//        }

        Fabric.with(this, new Crashlytics());
        Fresco.initialize(this);
    }



    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    public RxBus bus() {
        if ( bus == null ) {
            bus = new RxBus();
        }

        return bus;
    }

    public static AccountManager getAccountManager() {
        return mAppInstance.accountManager;
    }

    public static HifiveSettingsManager getHifiveSettingsManager() {
        return mAppInstance.settingsManager;
    }
}
