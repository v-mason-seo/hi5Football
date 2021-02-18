package com.ddastudio.hifivefootball_android.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.content_list.ContentListContainerFragment;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.data.event.UserAccountEvent;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.main.model.DisplayBoardModel;
import com.ddastudio.hifivefootball_android.match_chat.MatchChatFragment;
import com.ddastudio.hifivefootball_android.match_chat.MatchChatModel;
import com.ddastudio.hifivefootball_android.match_chat.utils.KeyboardLayout;
import com.ddastudio.hifivefootball_android.match_summery.MainMatchFragment;
import com.ddastudio.hifivefootball_android.match_up.MatchUpContainerFragment;
import com.ddastudio.hifivefootball_android.my_news.NotificationContainer;
import com.ddastudio.hifivefootball_android.settings.SettingsActivity;
import com.ddastudio.hifivefootball_android.signin.AccountManager;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.ui.utils.BackPressCloseHandler;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hanks.htextview.typer.TyperTextView;
import com.kakao.util.helper.SharedPreferencesCache;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    public static final int REQUEST_SETTINGS = 10000;
    public static final int REQUEST_LOGIN = 10001;
    public static final int REQUEST_EDITOR = 20000;

    @BindView(R.id.main_content) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.main_toolbar) Toolbar mToolbar;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.flContainer) FrameLayout flContainer;
    @BindView(R.id.ll_mini_dp_board_container) LinearLayout llMiniDpBoardContainer;
    @BindView(R.id.tv_mini_dp_board) TyperTextView tvMiniDpBoardView;
    @BindView(R.id.btn_go) Button btnGo; // 미니전광판 이동 버
    @BindView(R.id.fab_post_content) FloatingActionButton mFab;
    @BindView(R.id.bottom_navigation) AHBottomNavigation mBottomNavigationView;
    @BindArray(R.array.main_tabs_array) String[] mVpTitles;
    @BindString(R.string.naver_client_name) String mNaverClientName;
    @BindView(R.id.keyboardLayout)
    KeyboardLayout keyboardLayout;

    EditText etMessage;
    View btnSendMessage;

    Menu mMenu;
    ActionBarDrawerToggle mDrawerToggle;
    MainVpAdapter mVpAdapter;
    BackPressCloseHandler backPressCloseHandler;
    MainPresenter mPresenter;

    // 현재 게시판 위치
    //int mCurrentBoardPosition = 0;

    //BoardMasterModel mCurrentBoard;
    //boolean mIsCurrentBest;

    //------------------------------------------------
    // Mqtt 관련 변수
    //------------------------------------------------
    MqttAndroidClient mqttAndroidClient;
    //String subscriptionTopic = "match/chat/#";
    //String notificationTopic = "dpboard/match/#";
    String[] topics = {"match/chat/#", "dpboard/match/#"};
    LinkedHashMap<String, String> dpBoardData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter = new MainPresenter(PostBoardType.ALL_BOARD);
        mPresenter.attachView(this);
        backPressCloseHandler = new BackPressCloseHandler(this); // 뒤로가기 버튼

        // 메인화면에서 광고는 주석처리함.
        // 하단에 bottomNavigationView와 함께 있으니 보기 않좋다.
        // 게시글 중간에 삽입하는 방법으로 변경하자.(작업안함)
        //initAdmob();
        initToolbar();
        initNavigationView();
        initBottomNavigationView();

        updateUserInfo();
        initCheckVersion();

        initKeyboard();

        dpBoardData = new LinkedHashMap<>();

        if ( savedInstanceState == null ) {
            Timber.i("savedInstanceState is null!");
            loadFragment(ContentListContainerFragment.newInstance());
            mFab.setVisibility(View.VISIBLE);
            keyboardLayout.setVisibility(View.GONE);
        } else {
            Timber.i("savedInstanceState is not null!");
        }

        // 현재 선택된 게시판 정보의 변화를 관찰한다.
        mPresenter.getSelectedFootballChat().observe(this, selectedItem -> {

            if ( selectedItem != null ) {
                setTitle(selectedItem.getBoardName());
            }
        });


        // 푸시테스트
        String token = FirebaseInstanceId.getInstance().getToken();
        String id = FirebaseInstanceId.getInstance().getId();

//        if ( TextUtils.isEmpty(token)) {
//            Log.i("hong", "token is null");
//        } else {
//            Log.i("hong", "token is " + token);
//        }
//
//        if ( TextUtils.isEmpty(id)) {
//            Log.i("hong", "id is null");
//        } else {
//            Log.i("hong", "id is " + id);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
        // Mqtt 시작
        //initMqtt();
    }

    @Override
    protected void onStop() {
        mPresenter.onStop();
        // Mqtt 종료
        //stopMqtt();
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if ( backPressCloseHandler != null ) {
            backPressCloseHandler = null;
        }

        if ( mBackListener != null ) {
            mBackListener = null;
        }
    }

    /*---------------------------------------------------------*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        this.mMenu = menu;

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = mMenu.findItem(R.id.action_login);

        if (App.getAccountManager().isAuthorized()) {
            menuItem.setIcon(R.drawable.ic_person_amber_vector);
        } else {
            menuItem.setIcon(R.drawable.ic_person_grey_vector);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                return true;
            case R.id.action_login:
                Timber.d("action_login menu click!");
                if ( App.getAccountManager().isAuthorized()) {
                    // 로그인 상태에서는 로그아웃 메시지 보여주기
                    new MaterialDialog.Builder(this)
                            .content("로그아웃 하시겠습니까?")
                            .positiveText("예")
                            .onPositive(((dialog, which) -> {
                                App.getAccountManager().unregisterHifiveUser();
                            }))
                            .negativeText("아니오")
                            .show();
                } else {
                    //로그인 창으로 이동
                    startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN);
                    return true;
                }
                return true;
            case R.id.home:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // 뒤로가기 버튼 입력시간이 담길 long 객체
    private long pressedTime = 0;

    // 리스너 생성
    public interface OnBackPressedListener {
        public boolean onBack();
    }

    // 리스너 객체 생성
    private OnBackPressedListener mBackListener;

    // 리스너 설정 메소드
    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListener = listener;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        //--------------------------------------------------

//        // 다른 Fragment 에서 리스너를 설정했을 때 처리됩니다.
//        if(mBackListener != null) {
//            mBackListener.onBack();
//            Log.e("!!!", "Listener is not null");
//            // 리스너가 설정되지 않은 상태(예를들어 메인Fragment)라면
//            // 뒤로가기 버튼을 연속적으로 두번 눌렀을 때 앱이 종료됩니다.
//        } else {
//            Log.e("!!!", "Listener is null");
//            if ( pressedTime == 0 ) {
//                Snackbar.make(findViewById(R.id.flContainer),
//                        " 한 번 더 누르면 종료됩니다." , Snackbar.LENGTH_LONG).show();
//                pressedTime = System.currentTimeMillis();
//            }
//            else {
//                int seconds = (int) (System.currentTimeMillis() - pressedTime);
//
//                if ( seconds > 2000 ) {
//                    Snackbar.make(findViewById(R.id.flContainer),
//                            " 한 번 더 누르면 종료됩니다." , Snackbar.LENGTH_LONG).show();
//                    pressedTime = 0 ;
//                }
//                else {
//                    super.onBackPressed();
//                    Log.e("!!!", "onBackPressed : finish, killProcess");
//                    finish();
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                }
//            }
//        }

        //--------------------------------------------------

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(mBottomNavigationView.getCurrentItem() == 0 && mBackListener != null) {

                if ( mBackListener.onBack()) {
                    return;
                }
            }

            if (keyboardLayout.onBackPressed()) {
                return;
            }

            backPressCloseHandler.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case REQUEST_EDITOR:

                // 어떤 게시판에 글을 작성했는지 -> 해당 게시판으로 이동해서 새로고침을 한다.
                if ( data != null ) {
                    int returnBoardId = data.getIntExtra("RETURN_SELECTED_BOARD_ID", EntityType.BOARD_GENERAL);
                    mPresenter.setBoardType(PostBoardType.toEnum(returnBoardId));
                    _rxBus.send(new ContentListEvent.RefreshListEvent(mPresenter.getBoardType()));
                }

                break;
        }
    }

    /*---------------------------------------------------------*/


    @OnClick(R.id.fab_post_content)
    public void onPostContentFabClick() {
        mPresenter.openTextEditor();
    }

    @OnLongClick(R.id.fab_post_content)
    public boolean onPostContentFabLongClick() {
        mPresenter.openTestTextEditor();
        return true;
    }

    /*───-----------------------------------────────────────────────────────*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if (event instanceof UserAccountEvent.SignIn) {
            // 로그인
            showMessage("로그인 되었습니다");
            updateUserInfo(false);
        } else if ( event instanceof UserAccountEvent.SignOut) {
            // 로그아웃
            showMessage("로그아웃 되었습니다");
            updateUserInfo(false);
        } else if ( event instanceof UserAccountEvent.SignError) {
            // 회원가입 또는 로그인 오류
            Snackbar.make(mCoordinatorLayout, ((UserAccountEvent.SignError) event).getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
        Toasty.normal(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getApplicationContext(), errMessage, Toast.LENGTH_SHORT).show();
    }

    /*───-----------------------------------────────────────────────────────*/

    /**
     * 네비게이션뷰 초기화
     */
    private void initNavigationView() {

        mDrawerToggle = new ActionBarDrawerToggle(this
                , mDrawerLayout
                , mToolbar
                , R.string.drawer_open
                , R.string.drawer_close);

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) { }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) { }
        });

        mNavigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.nav_board_club:
                    mPresenter.openTeamListActivity();
                    break;
                case R.id.nav_board_player:
                    mPresenter.openPlayerListActivity();
                    break;
                case R.id.nav_settings:
                    startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), REQUEST_SETTINGS);
                    break;
                case R.id.nav_board_issue:
                    mPresenter.openIssueActivity();
                    break;
                case R.id.nav_board_report:
                    mPresenter.openIssueActivity();
                    break;
                case R.id.nav_arena:
                    mPresenter.openArenaActivity();
                    break;
                case R.id.nav_board_instruction:
                    //changeBoard(PostBoardType.INSTRUCTION);
                    break;
                case R.id.nav_board_notification:
                    //changeBoard(PostBoardType.NOTIFICATION_A);
                    break;
            }

            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        View navHeader = mNavigationView.getHeaderView(0);
        if ( navHeader != null ) {
            ImageView ivNavAvatar = navHeader.findViewById(R.id.nav_header_avatar);
            Button logoutButton = navHeader.findViewById(R.id.nav_header_logout);

            ivNavAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 로그인 안한 상태에서는 로그인창으로
                    mDrawerLayout.closeDrawer(GravityCompat.START);

                    mDrawerLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if ( App.getAccountManager().isAuthorized()) {
                                // 로그인 상태에서는 유저 정보창으로
                                mPresenter.openUserProfile(App.getAccountManager().getHifiveUser());
                            } else {
                                //로그인 창으로 이동
                                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN);
                            }
                        }
                    }, 200);
                }
            });

            logoutButton.setOnClickListener(view -> {
                App.getAccountManager().unregisterHifiveUser();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            });
        }
    }

    /**
     * 네비게이션 드로워에서 게시판 변경
     *  - 앞으로 사용안함
     *  - 게시판은 옆으로 밀어서 하나씩 보는 방법으로 변경
     * @param boardType
     */
//    private void changeBoard(PostBoardType boardType) {
//
//        mPresenter.setBoardType(boardType);
//        setTitle(boardType.toString());
//        // 게시판을 변경하면 첫 화면을 게시판으로 이동시킨다.
//        if ( boardType == PostBoardType.FOOTBALL
//                || boardType == PostBoardType.PLAYER_SCOUT
//                || boardType == PostBoardType.MATCH_REPORT
//                || boardType == PostBoardType.FREE) {
//
//            if ( mBottomNavigationView.getCurrentItem() != 0 ) {
//                mBottomNavigationView.setCurrentItem(0);
//            }
//        }


        //mViewPager.setCurrentItem(0, true);
//        int bottomNavId = bottomNavigationView.getSelectedItemId();
//        if ( bottomNavId != R.id.action_content) {
//            bottomNavigationView.setSelectedItemId(R.id.action_content);
//        }
//        _rxBus.send(new ContentListEvent.RefreshListEvent(boardType));
//    }

    /**
     * 뷰페이저/아답터 초기화
     */
//    private void initViewPager() {
//
//        mTabLayout.setCustomTabView((container, position, adapter) -> {
//
//            LayoutInflater inflater = LayoutInflater.from(container.getContext());
//            View grpView = inflater.inflate(R.layout.tab_custom_icon_and_text_item, container, false);
//            AppCompatImageView icon = (AppCompatImageView)grpView.findViewById(R.id.custom_tab_icon);
//            TextView tv = grpView.findViewById(R.id.custom_tab_text);
//            tv.setText(mVpTitles[position]);
//
//            switch (position) {
//                case 0:
//                    icon.setImageResource(R.drawable.ic_list_black_vector);
//                    break;
//                case 1:
//                    icon.setImageResource(R.drawable.ic_football_goal_post_vector);
//                    break;
//                case 2:
//                    icon.setImageResource(R.drawable.ic_football_shoe_vector);
//                    break;
//                case 3:
//                    icon.setImageResource(R.drawable.ic_schedule_vector);
//                    break;
//                case 4:
//                    icon.setImageResource(R.drawable.ic_notifications);
//                    break;
//            }
//
//            return grpView;
//        });
//
//        mVpAdapter = new MainVpAdapter(getSupportFragmentManager(), mVpTitles, mPresenter.getBoardType());
//        mViewPager.setAdapter(mVpAdapter);
//        mViewPager.setOffscreenPageLimit(1);
//        mTabLayout.setViewPager(mViewPager);
//
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                // 글쓰기 버튼은 게시판에서만 활성화
//                if ( position == 0 ) {
//                    mFab.setVisibility(View.VISIBLE);
//                } else {
//                    mFab.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//    }

    /**
     * 매치챗 텍스트 입력기 초기화
     */
    private void initKeyboard() {
        keyboardLayout.setup(this, null, mBottomNavigationView);
        etMessage = keyboardLayout.getEditText();
        btnSendMessage = keyboardLayout.getSendButton();

        // 메시지 전송버튼 클릭
        btnSendMessage.setOnClickListener(v -> {
            Timber.i("send message...");
            String sendMessage = etMessage.getText().toString();
            if (TextUtils.isEmpty(sendMessage)) {
                return;
            }

            // topics[0] 에는 와일드카드 문자도 들어가 있기 때문에 현재는 하드코딩을 한다.
            publishMessage("match/chat", sendMessage);
            etMessage.setText("");
        });
    }

    public void showDisplayBoard(String info) {
        tvMiniDpBoardView.animateText(info);
    }

    public LinkedHashMap<String, String> getDisplayBoardData() {
        return dpBoardData;
    }

    /**
     * Mqtt 초기화
     */
    private void initMqtt() {

        String serverUri = BuildConfig.MQTT_SERVER_URI;
        String clientId;
        if ( App.getAccountManager().isAuthorized()) {
            clientId = App.getAccountManager().getUserName();
        } else {
            clientId = "Guest-" + UUID.randomUUID().toString();
        }

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {

            @Override
            public void connectionLost(Throwable cause) {

                // onDestroy 에서 연결이 종료될 때 또는 접속이 다른 이유로 끊겼을 때 호출되는데
                // cause 널 값으로 들어올 수 있기 때문에 cause 널 체크가 필요하다.
                if ( cause != null ) {
                    App.getInstance().bus().send(new MatchChatModel("[connectionLost] " + cause.getLocalizedMessage()));
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                switch (topic) {
                    case "dpboard/match/result":
                    case "dpboard/match/schedule":
                        //-------------------------
                        // Display board ( 전광판 )
                        //-------------------------
                        Gson gson = new Gson();
                        List<DisplayBoardModel> dpBoard = gson.fromJson(message.toString(), new TypeToken<List<DisplayBoardModel>>(){}.getType());
                        if ( dpBoardData != null && dpBoard != null ) {
                            for ( int i =0; i < dpBoard.size(); i++) {
                                String dpKey = dpBoard.get(i).getCode() + dpBoard.get(i).getInfo();
                                String dpValue = dpBoard.get(i).getInfo();
                                dpBoardData.put(dpKey, dpValue);
                            }
                        }
                        break;

                    case "match/chat":
                        //-------------------------
                        // 채팅
                        //-------------------------
                        Gson gson2 = new Gson();
                        MatchChatModel chat = gson2.fromJson(message.toString(), MatchChatModel.class);
                        App.getInstance().bus().send(chat);
                        break;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // 메시지 전송 완료
                //String matchChatMessate = String.format("[deliveryComplete] token: %s", token.toString());
                //App.getInstance().bus().send(new MatchChatModel(matchChatMessate));
            }

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                // 접속 완료
                String matchChatMessate = String.format("[connectComplete] reconnect: %s, serverURI: %s", reconnect, serverURI);
                Timber.i(matchChatMessate);
                App.getInstance().bus().send(new MatchChatModel(matchChatMessate));
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    // 접속에 성공했다면 토픽을 구독한다.
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //addToHistory("Failed to connect to: " + serverUri);
                    App.getInstance().bus().send(new MatchChatModel("1Failed to connect to: " + serverUri));
                }
            });
        } catch (MqttException ex) {
            App.getInstance().bus().send("[initMqtt] " + ex.getLocalizedMessage());
        }
    }

    /**
     * Mqtt 종료
     */
    private void stopMqtt() {

        // Mqtt 종료
        if ( mqttAndroidClient != null ) {

            if ( mqttAndroidClient.isConnected() ) {

                // 토픽 구독을 취소한다.
                try {
                    mqttAndroidClient.unsubscribe(topics);
                } catch (MqttException ex) {
                    Timber.i("[unsubscribe] topic error : " + ex.getMessage());
                }

                // Mqtt 서비스 클리어 및 접속 종료
                try {
                    // unregisterResources(), close() 함수를 호출해야 메모리릭이 발생하지 않는다.
                    // unregisterResources(), close(), disconnect() 순서를 변경하면 오류 발생.
                    mqttAndroidClient.unregisterResources();
                    mqttAndroidClient.close();
                    mqttAndroidClient.disconnect();
                    mqttAndroidClient = null;
                } catch (MqttException ex) {
                    Timber.i("[Disconnect] error : " + ex.getMessage());
                }
            }
        }
    }

    /**
     * 토픽을 구독한다.
     */
    private void subscribeToTopic() {

        try {
            int[] qos = {0, 0};
            mqttAndroidClient.subscribe(topics, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    App.getInstance().bus().send(new MatchChatModel("Subscribed! - topic length : " + asyncActionToken.getTopics().length));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    App.getInstance().bus().send(new MatchChatModel("Failed to subscribe"));
                }
            });

        } catch (MqttException ex) {
            App.getInstance().bus().send("[subscribeToTopic] " + ex.getLocalizedMessage());
        }
    }

    /**
     * 메시지 전송
     * @param topic
     * @param sendMessage
     */
    private void publishMessage(String topic, String sendMessage) {

        try {

            // 1.입력된 문자열을 MatchChatModel로 변환
            MatchChatModel chat = new MatchChatModel(sendMessage, App.getAccountManager().getHifiveUser());
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String jsonMessage = gson.toJson(chat);

            // 2.Mqtt 메시지 생성
            MqttMessage message = new MqttMessage();
            message.setPayload(jsonMessage.getBytes());

            // 3.메시지 전송
            if ( mqttAndroidClient !=null && mqttAndroidClient.isConnected()) {
                mqttAndroidClient.publish(topic, message);
            }

        } catch (MqttException ex) {
            App.getInstance().bus().send(new MatchChatModel("[publishMessage] Error Publishing : " + ex.getMessage()));
        }
    }

    private void initBottomNavigationView() {

        AHBottomNavigationItem item0 = new AHBottomNavigationItem(getResources().getString(R.string.bottomnav_title_0), R.drawable.ic_list_black_vector);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getResources().getString(R.string.bottomnav_title_1), R.drawable.ic_football_goal_post_vector);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getResources().getString(R.string.bottomnav_title_2), R.drawable.ic_chat3_vector);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getResources().getString(R.string.bottomnav_title_3), R.drawable.ic_event_note_black_vector);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getResources().getString(R.string.bottomnav_title_4), R.drawable.ic_notifications);

        mBottomNavigationView.addItem(item0);
        mBottomNavigationView.addItem(item1);
        //mBottomNavigationView.addItem(item2);
        mBottomNavigationView.addItem(item3);
        mBottomNavigationView.addItem(item4);

        // Disable the translation inside the CoordinatorLayout
        mBottomNavigationView.setBehaviorTranslationEnabled(false);

        // Enable the translation of the FloatingActionButton
        mBottomNavigationView.manageFloatingActionButtonBehavior(mFab);

        // Change colors
        //bottomNavigationView.setAccentColor(Color.parseColor("#8E24AA"));
        //bottomNavigationView.setInactiveColor(R.color.md_green_500);

        // Force to tint the drawable (useful for font with icon for example)
        //bottomNavigationView.setForceTint(true);

        // Display color under navigation bar (API 21+)
        // Don't forget these lines in your style-v21
        // <item name="android:windowTranslucentNavigation">true</item>
        // <item name="android:fitsSystemWindows">true</item>
        //bottomNavigationView.setTranslucentNavigationEnabled(false);

        // Manage titles
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        // Set background color
        mBottomNavigationView.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.md_white_1000));

        // Change colors
        mBottomNavigationView.setAccentColor(ContextCompat.getColor(this, R.color.md_blue_500));
        mBottomNavigationView.setInactiveColor(ContextCompat.getColor(this, R.color.md_grey_500));

        // Use colored navigation with circle reveal effect
        //mBottomNavigationView.setColored(true);

        // Set listeners
        mBottomNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                Fragment fragment = null;

                switch (position) {
                    case 0:
                        mFab.setVisibility(View.VISIBLE);
                        keyboardLayout.setVisibility(View.GONE);
                        llMiniDpBoardContainer.setVisibility(View.VISIBLE);
                        fragment = ContentListContainerFragment.newInstance();

                        setTitle("게시판 리스트");
                        break;
                    case 1:
                        mFab.setVisibility(View.GONE);
                        keyboardLayout.setVisibility(View.GONE);
                        llMiniDpBoardContainer.setVisibility(View.VISIBLE);
                        fragment = MatchUpContainerFragment.newInstance();

                        setTitle(getResources().getString(R.string.bottomnav_title_1));
                        break;
//                    case 2:
//                        mFab.setVisibility(View.GONE);
//                        keyboardLayout.setVisibility(View.VISIBLE);
//                        llMiniDpBoardContainer.setVisibility(View.GONE);
//                        fragment = MatchChatFragment.newInstance();
//
//                        setTitle(getResources().getString(R.string.bottomnav_title_2));
//                        break;
                    case 2:
                        mFab.setVisibility(View.GONE);
                        keyboardLayout.setVisibility(View.GONE);
                        llMiniDpBoardContainer.setVisibility(View.VISIBLE);
                        fragment = MainMatchFragment.newInstance();

                        setTitle(getResources().getString(R.string.bottomnav_title_3));
                        break;
                    case 3:
                        mFab.setVisibility(View.GONE);
                        keyboardLayout.setVisibility(View.GONE);
                        llMiniDpBoardContainer.setVisibility(View.VISIBLE);
                        fragment = NotificationContainer.newInstance();

                        setTitle(getResources().getString(R.string.bottomnav_title_4));
                        break;
                }
                return loadFragment(fragment);
            }
        });

    }

//    private void initBottomNavigationView() {
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                Fragment fragment = null;
//
//                switch (item.getItemId()) {
//                    case R.id.action_content:
//                        mFab.setVisibility(View.VISIBLE);
//                        fragment = ContentListContainerFragment.newInstance(mPresenter.getBoardType().value());
//                        break;
//                    case R.id.action_matchup:
//                        mFab.setVisibility(View.GONE);
//                        fragment = MatchUpContainerFragment.newInstance();
//                        break;
//                    case R.id.action_data_center:
//                        mFab.setVisibility(View.GONE);
//                        fragment = MainMatchFragment.newInstance();
//                        break;
//                    case R.id.action_my_news:
//                        mFab.setVisibility(View.GONE);
//                        fragment = NotificationContainer.newInstance();
//                        break;
//                }
//                return loadFragment(fragment);
//            }
//        });
//    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /**
     * 툴바 초기화
     */
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        setTitle("게시글 리스트");
    }

    /**
     * 광고 초기화
     */
    private void initAdmob() {

        // 애드몹
//        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);
//        AdRequest adReuest = new AdRequest.Builder()
//                .addTestDevice("C688FB9C0634F2E6FF40ADFF71711FFF")
//                .addTestDevice("E4B200C7136AD0708A2BF1FA76A2E94B")
//        .addTestDevice("70D53A4C46EFEAAA47D76A37CA7499DA")
//                .build();
//        mAdView.loadAd(adReuest);

        //애드핏
        //adView.loadAd();

    }

    private void initCheckVersion() {
        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setTitleOnUpdateAvailable("새로운 버전이 출시되었습니다!")
                .setButtonDoNotShowAgain("다시보지않기")
                .setButtonUpdate("업데이트")
                .setButtonDismiss("취소")
                .setIcon(R.mipmap.ic_launcher_round)
        ;
        appUpdater.start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    public void updateUserInfo() {
        updateUserInfo(true);
    }


    /**
     * 사용자 정보 업데이트 ( 네비게이션 드로워 )
     */
    public void updateUserInfo(boolean isCdn) {

        View navHeader = mNavigationView.getHeaderView(0);

        if ( navHeader != null ) {

            ImageView ivNavAvatar = navHeader.findViewById(R.id.nav_header_avatar);
            TextView tvUsername = navHeader.findViewById(R.id.nav_header_username);
            TextView tvProfile = navHeader.findViewById(R.id.nav_header_profile_text);
            Button logoutButton = navHeader.findViewById(R.id.nav_header_logout);

            // 메뉴도 새로고침 해준다.
            invalidateOptionsMenu();
            if ( App.getAccountManager().isAuthorized()) {

                AccountManager accountManager = App.getAccountManager();
                logoutButton.setVisibility(View.VISIBLE);
                tvUsername.setText(accountManager.getNickNameAndUserName());
                tvProfile.setText(accountManager.getUserProfile());


                // *** 사용자 프로필 ***
                GlideApp.with(getApplicationContext())
                        .load(accountManager.getUserAvatar(isCdn))
                        .centerCrop()
                        .transform(new CropCircleTransformation())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_face)
                        .into(ivNavAvatar);

                llMiniDpBoardContainer.setEnabled(true);
            } else {
                ivNavAvatar.setImageResource(R.drawable.ic_person);
                tvUsername.setText("하이파이브 풋볼");
                tvProfile.setText("새로운 축구문화의 시작");
                logoutButton.setVisibility(View.GONE);
                llMiniDpBoardContainer.setEnabled(false);
            }
        }
    }

    public View getContainerView() {
        return mCoordinatorLayout;
    }
}
