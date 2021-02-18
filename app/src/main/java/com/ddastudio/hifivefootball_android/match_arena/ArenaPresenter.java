package com.ddastudio.hifivefootball_android.match_arena;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.common.Constants;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.event.ArenaChatEvent;
import com.ddastudio.hifivefootball_android.data.manager.AuthManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchTalkManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.arena_chat.ReactionModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.Glide4Engine;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.utils.AWSUtil;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.ddastudio.hifivefootball_android.utils.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.google.android.gms.internal.zzahn.runOnUiThread;


/**
 * Created by hongmac on 2018. 1. 26..
 */

public class ArenaPresenter implements BaseContract.Presenter {

    ArenaActivity mView;
    @NonNull
    CompositeDisposable _composite;
    @NonNull
    CompositeDisposable _refreshComposite;
    AuthManager mAuthManager;
    MatchesManager mMatchesManager;
    MatchTalkManager mMatchTalkManager;

    Compressor imageCompressor;
    TransferUtility transferUtility;

    MatchModel matchData;
    boolean isHome = true;
    final String serverUri;
    final String subscriptionTopic;
    final String publishTopic;
    final String clientId;
    boolean isSubscribeTopic = false;
    MqttAsyncClient mqttClient;

    public ArenaPresenter(MatchModel match) {

        this.isSubscribeTopic = false;
        this.matchData = match;

        //1. url
        serverUri = BuildConfig.MQTT_SERVER_URI;
        //2. 토픽
        subscriptionTopic = String.valueOf(match.getMatchId());
        publishTopic = String.valueOf(match.getMatchId());
        // 3.아이디 설정
        if ( App.getAccountManager().isAuthorized() ) {
            clientId = App.getAccountManager().getUserName();
        } else {
            clientId = "Guest-" + subscriptionTopic + System.currentTimeMillis();
        }
    }


    /**
     * MqttAndroidClient 객체 생성
     * @param view
     */
    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (ArenaActivity)view;
        this.mAuthManager = AuthManager.getInstance();
        this.mMatchesManager = MatchesManager.getInstance();
        this.mMatchTalkManager = MatchTalkManager.getInstance();
        this._composite = new CompositeDisposable();
        this.imageCompressor = new Compressor(mView).setQuality(60)
                .setCompressFormat(Bitmap.CompressFormat.JPEG);
        this.transferUtility = AWSUtil.getTransferUtility(mView);
        //
        // Mqtt 클라이언트 생성
        //
        createMqttClient();
    }

    public void onStart() {
        this._refreshComposite = new CompositeDisposable();
        refreshHifiveAccessToken();
        onLoadParticipationUserCount(matchData.getMatchId());
    }

    public void onStop() {

        if ( _refreshComposite != null && !_refreshComposite.isDisposed() )
            _refreshComposite.clear();
    }

    public void onResume() {
        //
        // Mqtt 서버 접속 및 토픽 구독
        //
        if ( mqttClient.isConnected()) {
            subscribeToTopic();
        } else {
            connectMqttServer();
        }

    }

    public void onPause() {
        //
        // 토픽 구독 취소
        //
        unsubscribeTopic();
    }

    /**
     * 아레나 채팅(MQTT) 접속을 종료한다.
     */
    @Override
    public void detachView() {
        //
        // Mqtt 접속을 종료하고 자원을 정리한다.
        //
        closeMqtt();

        if ( _refreshComposite != null && !_refreshComposite.isDisposed() )
            _refreshComposite.clear();

        if ( _composite != null )
            _composite.clear();
    }

//    Flowable<String> getMqttCallbackObservable() {
//        return Flowable.create(new FlowableOnSubscribe<String>() {
//            @Override
//            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
//                MqttCallbackExtended callbackExtended = new MqttCallbackExtended() {
//                    @Override
//                    public void connectComplete(boolean reconnect, String serverURI) {
//                        emitter.onNext("connectComplete");
//                    }
//
//                    @Override
//                    public void connectionLost(Throwable cause) {
//                        emitter.onNext("connectionLost");
//                    }
//
//                    @Override
//                    public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//                        Gson gson = new Gson();
//                        ReactionModel reaction = gson.fromJson(message.toString(), ReactionModel.class);
//                        emitter.onNext("messageArrived");
//
//                    }
//
//                    @Override
//                    public void deliveryComplete(IMqttDeliveryToken token) {
//                        emitter.onNext("deliveryComplete");
//                    }
//                };
//
//                mqttClient.setCallback(callbackExtended);
//
//                emitter.setCancellable(new Cancellable() {
//                    @Override
//                    public void cancel() throws Exception {
//                        Timber.i("cancel");
//                    }
//                });
//            }
//        }, BackpressureStrategy.BUFFER);
//    }

    /*---------------------------------------------------------------------------------------------*/

    public boolean isSubscribeTopic() {
        return isSubscribeTopic;
    }

    public void setSubscribeTopic(boolean subscribeTopic) {
        isSubscribeTopic = subscribeTopic;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    public int isHomeValue() {

        return isHome ? 0 : 1;
    }

    public MatchModel getMatchData() {
        return matchData;
    }

    public void setMatchData(MatchModel matchData) {
        this.matchData = matchData;
    }

    public int getMatchId() {
        if ( matchData != null ) {
            return matchData.getMatchId();
        }

        return -1;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openLoginActivity() {
        Intent intent = new Intent(mView, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 하이파이브 액세스 토큰 갱신
     *  - 5분 단위로 실행됨
     *  - 토근만료시간과 현재시간을 비교하여 한시간 이상 차이가 날때 업데이트
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

        Disposable disposable = refreshObservable.subscribe(res -> {
                    //Timber.i("[onStart] onNext : %s", res.getAuthToken().toString());
                    App.getAccountManager().setHifiveAccessToken(res.getAuthToken());
                }
                , e -> mView.showErrorMessage("토큰 갱신 오류\n" + e.getMessage())
                , () -> { }
        );

        _refreshComposite.add(disposable);
    }

    /**
     * 접속자 현황 조회
     * @param matchId
     */
    public void onLoadParticipationUserCount(Integer matchId) {

        Flowable<DBResultResponse> observable
                = mMatchesManager.onLoadParticipationUserCount(matchId);

        _refreshComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(count -> {})
                        .subscribe(count -> {
                                    mView.setUserCount(count.getResult());
                                },
                                e -> mView.showErrorMessage(e.getMessage()),
                                () ->{} )
        );
    }

    /**
     * 매치토크 생성
     * @param matchid
     * @param content
     * @param cellType
     * @param status
     */
    public void createMatchTalk(Integer matchid,
                                String content,
                                Integer cellType,
                                String status) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<DBResultResponse> observable
                = mMatchTalkManager.onCreateMatchTalk(token, matchid, content, cellType, status);

        Disposable disposable
                = observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(dbResultModel -> {
                            if ( dbResultModel.getResult() > 0 ) {
                                if ( dbResultModel.getData().containsKey("id") ) {
                                    int talkId = ((Double)dbResultModel.getData().get("id")).intValue();

                                    ReactionModel reaction = new ReactionModel(isHome() == true
                                            ? ViewType.REACTION_HOME_MESSAGE
                                            : ViewType.REACTION_AWAY_MESSAGE
                                            , isHome ? ReactionModel.HOME : ReactionModel.AWAY
                                            , content
                                            , App.getAccountManager().getHifiveUser());
                                    reaction.setTalkId(talkId);
                                    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                                    String message = gson.toJson(reaction);
                                    publishMessage(message);
                                }
                            }
                        },
                        e -> {
                            mView.showErrorMessage(e.getMessage());
                            mView.hideLoading();
                        },
                        () -> {
                            mView.hideLoading();
                        });

        _composite.add(disposable);

    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 1. Mqtt 클라이언트 객체 생성
     */
    private void createMqttClient() {
        try {
            mqttClient = new MqttAsyncClient(serverUri, clientId, new MemoryPersistence());
        } catch (MqttException ex) {
            mView.showErrorMessage("Mqtt클라이언트 생성 오류\n" + ex.getLocalizedMessage());
        }
    }

    /**
     * 2. Mqtt 서버에 접속
     */
    private void connectMqttServer() {

        try {
            if ( mqttClient == null ) {
                mView.showErrorMessage("Mqtt클라이언트가 생성되지 않았습니다. 다시 입장해주세요. 죄송합니다.");
                return;
            }

            if ( mqttClient.isConnected()) {
                subscribeToTopic();
            } else {
                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setAutomaticReconnect(true);
                mqttConnectOptions.setCleanSession(true);
                mqttClient.setCallback(mqttCallbackExtended);
                //
                // 서버 접속이 정상적으로 이루어지면 토픽을 구독한다.(mqttConnectionActionListener)
                //
                mqttClient.connect(mqttConnectOptions, mqttConnectionActionListener);
            }

        } catch (MqttException ex) {
            ex.printStackTrace();
            //mView.showErrorMessage("서버 접속중 오류가 발생했습니다.\n" + ex.getLocalizedMessage());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mView.showErrorMessage("서버 접속중 오류가 발생했습니다.\n" + ex.getLocalizedMessage());
                        }
                    });
                }
            }).start();

        }
    }

    /**
     * 3. 토픽 구독하기
     */
    public void subscribeToTopic() {

        try {

            if ( mqttClient == null || !mqttClient.isConnected() ) {
                mView.showMessage("아레나 접속이 정상적으로 이루어지지 않았습니다.\n아레나 창 종료후 다시 입장해주세요");
                return;
            }

            mqttClient.subscribe(subscriptionTopic, 0, null, mqttSubscriptionActionListener);

        } catch (MqttException ex) {
            //ex.printStackTrace();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mView.showErrorMessage(ex.getMessage());
                        }
                    });
                }
            }).start();
        }
    }

    /**
     * 4. 토픽 구독을 취소한다.
     */
    private void unsubscribeTopic() {

        try {
            if ( mqttClient == null ) {
                return;
            }

            if ( mqttClient.isConnected()) {
                mqttClient.unsubscribe(subscriptionTopic, null, mqttUnSubscriptionActionListener);
            }

        } catch (MqttException ex) {
            //mView.showErrorMessage(ex.getMessage());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mView.showErrorMessage(ex.getMessage());
                        }
                    });
                }
            }).start();
        }
    }

    /**
     * 5. Mqtt 접속을 종료하고 자원을 정리한다.
     */
    private void closeMqtt() {

        try {
            if ( mqttClient.isConnected()) {
                mqttClient.disconnect();
            }

            //mqttClient.unregisterResources();
            mqttClient.close();
            mqttConnectionActionListener = null;
            mqttSubscriptionActionListener = null;
            mqttCallbackExtended = null;
            mqttClient = null;
        } catch (MqttException ex) {
            //Timber.i("[MQTT] disconnect error : %s", ex.getMessage());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mView.showErrorMessage(ex.getMessage());
                        }
                    });
                }
            }).start();
        }
    }

    /**
     * 메시지 전송
     * @param publishMessage
     */
    public void publishMessage(String publishMessage) {

        try {
            if ( mqttClient == null ) {
                //Timber.i("[publishMessage] mqttAndroidClient is null");
                return;
            }

            if ( !mqttClient.isConnected()) {
                //Timber.i("[publishMessage] 아레나 서버접속이 끊겼습니다");
                return;
            }

            /*if ( !isSubscribeTopic) {
                mView.showMessage("아레나에 접속이 완료되지 않았습니다. 잠시만 기다려주세요. ");
                return;
            }*/

            MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            mqttClient.publish(publishTopic, message);

        } catch (MqttException ex) {
            //Timber.i("[publishMessage] exception : %s", ex.getCause().toString());
            //mView.showErrorMessage("메시지 전송중 오류가 발생했습니다.\n->" + ex.getLocalizedMessage());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mView.showErrorMessage("메시지 전송중 오류가 발생했습니다.\n->" + ex.getLocalizedMessage());
                        }
                    });
                }
            }).start();
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * MQTT 접속 리스너
     */
    IMqttActionListener mqttConnectionActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            //Timber.i("[mqttConnectionActionListener] onSeccess");
            DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
            disconnectedBufferOptions.setBufferEnabled(true);
            disconnectedBufferOptions.setBufferSize(100);
            disconnectedBufferOptions.setPersistBuffer(false);
            disconnectedBufferOptions.setDeleteOldestMessages(false);
            mqttClient.setBufferOpts(disconnectedBufferOptions);
            subscribeToTopic();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            mView.showErrorMessage("서버접속 실패\n->" + exception.getLocalizedMessage());
                        }
                    });
                }
            }).start();
        }
    };

    /**
     * 토픽 구독 리스너
     */
    IMqttActionListener mqttSubscriptionActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            setSubscribeTopic(true);
                            // 토픽을 구독했을 때 접속 사용자 수를 다시한번 조회한다.
                            onLoadParticipationUserCount(matchData.getMatchId());
                            mView.hideLoading2();
                        }
                    });
                }
            }).start();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            setSubscribeTopic(false);
                            mView.showErrorMessage("토픽구독 실패\n-> " + exception.getLocalizedMessage());
                            mView.hideLoading2();
                        }
                    });
                }
            }).start();
        }
    };

    /**
     * 토필 구독 해제 리스너
     */
    IMqttActionListener mqttUnSubscriptionActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            //setSubscribeTopic(false);
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            //mView.showErrorMessage("[onFailure] " + exception.getMessage());
        }
    };

    /**
     * 메시지 리스너
     */
    MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {
        @Override
        public void connectionLost(Throwable cause) {
            if ( cause != null ) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                // 해당 작업을 처리함
                                mView.showErrorMessage("1. 서버 접속이 끊겼습니다.\n-> " + cause.toString());
                                connectMqttServer();
                            }
                        });
                    }
                }).start();
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

            Gson gson = new Gson();
            ReactionModel reaction = gson.fromJson(message.toString(), ReactionModel.class);
            //
            // 메시지 전송
            //
            if ( reaction.getItemType() == ViewType.REACTION_HOME_MESSAGE
                    || reaction.getItemType() == ViewType.REACTION_AWAY_MESSAGE
                    || reaction.getItemType() == ViewType.REACTION_HOME_IMAGE
                    || reaction.getItemType() == ViewType.REACTION_AWAY_IMAGE
                    || reaction.getItemType() == ViewType.REACTION_BOT_MESSAGE) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                // 해당 작업을 처리함
                                App.getInstance().bus().send(new ArenaChatEvent.ArenaMessageArrived(reaction));
                            }
                        });
                    }
                }).start();
                return;
            }
            //
            // 스트커
            //
            if ( reaction.getItemType() == ViewType.REACTION_HOME_STICKER
                || reaction.getItemType() == ViewType.REACTION_AWAY_STICKER ) {

                reaction.findStickerResId(mView.getApplicationContext());
                int drawableid = reaction.getStickerResId();
                if ( drawableid > 0 ) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    // 해당 작업을 처리함
                                    mView.emitterSticker(reaction);
                                }
                            });
                        }
                    }).start();
                }
                return;

            }
            //
            // 매치정보 업데이트 - 화면도 업데이트 해줘야 한다.
            //
            if ( reaction.getItemType() == ViewType.REACTION_MATCH_DATA) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                // 해당 작업을 처리함
                                setMatchData(reaction.getMatch());
                                mView.updateData(getMatchData());
                            }
                        });
                    }
                }).start();

                return;
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {

        }
    };


    /*---------------------------------------------------------------------------------------------*/



//    public void publishReactionMessage(Integer talkId, String message) {
//
//        ReactionModel reaction = new ReactionModel(mPresenter.isChoiceHomeTeam() == true
//                ? ViewType.REACTION_HOME_MESSAGE
//                : ViewType.REACTION_AWAY_MESSAGE
//                , mPresenter.isChoiceHomeTeam() ? ReactionModel.HOME : ReactionModel.AWAY
//                , message
//                , App.getAccountManager().getHifiveUser());
//
//        reaction.setTalkId(talkId);
//        App.getInstance().bus().send(new ArenaChatEvent.ArenaSendMessage(reaction));
//    }

    /*---------------------------------------------------------------------------------------------*/

    public void openLocalImagePicker() {

        RxPermissions rxPermissions = new RxPermissions(mView);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {

                            Matisse.from(mView)
                                    .choose(MimeType.ofAll(), false)
                                    .countable(true)
                                    .maxSelectable(1)
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new Glide4Engine())
                                    .forResult(ArenaActivity.REQUEST_CODE_CHOOSE);
                        } else {
                            mView.showErrorMessage(mView.getString(R.string.permission_request_denied));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * 이미지 압축 및 업로드
     * @param obtainResults
     */
    public void imageCompressNUpload(List<Uri> obtainResults) {

        mView.showLoading();

        Disposable disposable
                = Flowable.fromArray(obtainResults)
                .flatMap(uris -> Flowable.fromIterable(uris))
                .subscribeOn(Schedulers.io())
                .map(uri -> FileUtil.getFile(mView, uri))
                .flatMap(file -> {
                    if ( FileUtil.getExtension(file.getName()).equals(".gif")) {
                        return Flowable.just(file);
                    } else {
                        return imageCompressor.compressToFileAsFlowable(file);
                    }
                })
                //.delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        file -> uploadS3(file),
                        e -> {  mView.hideLoading();
                            //Log.i("hong", "imageCompressNUpload error : " + e.getMessage());
                        },
                        () -> mView.hideLoading()
                );

        _composite.add(disposable);

    }

    private void uploadS3(File file) {

        mView.showMessage("uploadS3");
        mView.showLoading();

        String fileName = getImageFileName(App.getAccountManager().getUserName(), FileUtil.getExtension(file.getName()));

        try {
            TransferObserver observer = transferUtility.upload(Constants.UPLOAD_ARENA_ORIGINAL_BUCKET_NAME, fileName, file);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    if ( state == TransferState.COMPLETED) {
                        // TODO: 2017. 9. 11. 베이스 url 변수로 저장할 것.
                        String url = "https://s3.ap-northeast-2.amazonaws.com/hifivesoccer/origin/arena/" + fileName;
                        //App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_image, url, PostBodyType.PLAIN));
                        observer.cleanTransferListener();
                        //mView.addArenaImage(url);
                        // ArenaChatFragment로 이미지 url을 보낸다.

                        ReactionModel stickerReaction = new ReactionModel();
                        stickerReaction.setSide(isHomeValue());
                        stickerReaction.setImage(url);
                        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                        String message = gson.toJson(stickerReaction);
                        publishMessage(message);

                        mView.hideLoading();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    observer.cleanTransferListener();
                    mView.hideLoading();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getImageFileName(String userName, String extension) {

        String dateString = DateUtils.convertDateToString(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss_SSS");
        return dateString + "_" + userName + extension;
    }
}
