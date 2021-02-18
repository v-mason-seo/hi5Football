package com.ddastudio.hifivefootball_android.match_arena;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.ArenaChatEvent;
import com.ddastudio.hifivefootball_android.data.model.arena_chat.ReactionModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.ui.reaction.b.HeartLayout2;
import com.ddastudio.hifivefootball_android.ui.widget.InkPageIndicator;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhihu.matisse.Matisse;

import org.parceler.Parcels;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Timed;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

import static com.ddastudio.hifivefootball_android.utils.CommonUtils.hideKeyboard;

public class ArenaActivity extends BaseActivity {

    @BindView(R.id.tv_status) TextView tvStatus;
    @BindView(R.id.tv_count_down) TextView tvCountDown;
    @BindView(R.id.ll_lineup_info) LinearLayout llLineupInfo;
    @BindView(R.id.ll_user_count) LinearLayout llUserCount;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_participation_user_count) TextView tvUserCount;
    @BindView(R.id.tv_home_team_name) TextView tvHomeName;
    @BindView(R.id.iv_home_emblem) ImageView ivHomeEmblem;
    @BindView(R.id.tv_home_team_score) TextView tvHomeScore;
    @BindView(R.id.tv_away_team_name) TextView tvAwayName;
    @BindView(R.id.iv_away_emblem) ImageView ivAwayEmblem;
    @BindView(R.id.tv_away_team_score) TextView tvAwayScore;
    @BindView(R.id.ink_pager_indicator) InkPageIndicator inkPageIndicator;
    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.ll_sticker_container) LinearLayout llStickerContainer;
    @BindView(R.id.layout_chatbox) LinearLayout llChatContainer;
    @BindView(R.id.et_chatbox) EditText etChatBox;
    @BindView(R.id.fl_progress) FrameLayout flProgress;
    @BindView(R.id.heart_layout2) HeartLayout2 heartLayout2;

    @BindArray(R.array.arena_tabs_array) String[] mVpTitles;
    public static final int REQUEST_CODE_CHOOSE = 23;

    /*---------------------------*/

    ArenaPresenter mPresenter;
    ArenaVpAdapter mVpAdapter;

    /*---------------------------*/

    private Subscription emoticonSubscription;
    private Subscriber subscriber;
    private final int MINIMUM_DURATION_BETWEEN_EMOTICONS = 100; // in milliseconds

    public FlowableEmitter mEmitter;

    /*---------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);
        ButterKnife.bind(this);

        MatchModel match = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_MATCH"));
        mPresenter = new ArenaPresenter(match);
        mPresenter.attachView(this);

        initToolbar();
        initData(match);
        initActionContainer();
        initViewPager(match);
        initCountDownTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
        createStickerObservable();
    }

    @Override
    protected void onStop() {
        mPresenter.onStop();
        cancleStickerObservable();
        super.onStop();
    }

    private void cancleStickerObservable() {
        if (emoticonSubscription != null) {
            emoticonSubscription.cancel();
        }
    }

    private void createStickerObservable() {

        //Create an instance of FlowableOnSubscribe which will convert click events to streams
        FlowableOnSubscribe flowableOnSubscribe = emitter -> {
            mEmitter = emitter;
            //convertClickEventToStream(emitter);
        };

        //Give the backpressure strategy as BUFFER, so that the click items do not drop.
        Flowable emoticonsFlowable = Flowable.create(flowableOnSubscribe, BackpressureStrategy.BUFFER);
        //Convert the stream to a timed stream, as we require the timestamp of each event
        Flowable<Timed> emoticonsTimedFlowable = emoticonsFlowable.timestamp();

        //Subscribe
        subscriber = getSubscriber();
        emoticonsTimedFlowable.subscribeWith(subscriber);
    }

    private Subscriber getSubscriber() {

        return new Subscriber<Timed<ReactionModel>>() {
            @Override
            public void onSubscribe(Subscription s) {
                emoticonSubscription = s;
                emoticonSubscription.request(1);
                // for lazy evaluation.(초기화 작업)
            }

            @Override
            public void onNext(final Timed<ReactionModel> timed) {

                addSticker(timed.value().getStickerResId(), timed.value().getSide());

                long currentTimeStamp = System.currentTimeMillis();
                long diffInMillis = currentTimeStamp - ((Timed) timed).time();
                if (diffInMillis > MINIMUM_DURATION_BETWEEN_EMOTICONS) {
                    emoticonSubscription.request(1);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> emoticonSubscription.request(1)
                            , MINIMUM_DURATION_BETWEEN_EMOTICONS - diffInMillis);
                }
            }

            @Override
            public void onError(Throwable t) {
                //Do nothing
            }

            @Override
            public void onComplete() {
                if (emoticonSubscription != null) {
                    emoticonSubscription.cancel();
                }
            }
        };
    }

    /*---------------------------*/

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            // 이미지 선택 -> 이미지 압축 -> 이미지 S3 업로드
            List<Uri> obtainResults = Matisse.obtainResult(data);
            mPresenter.imageCompressNUpload(obtainResults);
            showMessage("onActivityResult");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 홈팀 클릭
     * @param view
     */
    @OnClick({R.id.ll_home_team})
    public void onClickHome(View view) {

        if ( mPresenter.isHome() == false ) {
            mPresenter.setHome(true);
            App.getInstance().bus().send(new ArenaChatEvent.ArenaChoiceTeam(true));
            changeTeam(mPresenter.getMatchData());
        }
    }

    /**
     * 어웨이팀 클릭
     * @param view
     */
    @OnClick({R.id.ll_away_team})
    public void onClickAway(View view) {

        if ( mPresenter.isHome() == true ) {
            mPresenter.setHome(false);
            App.getInstance().bus().send(new ArenaChatEvent.ArenaChoiceTeam(false));
            changeTeam(mPresenter.getMatchData());
        }
    }

    /**
     * 현재 접속자 리스트
     */
    @OnClick(R.id.iv_participation_users)
    public void onClickShowParticipationUsers() {
        ArenaDialogFragment dialogFragment
                = ArenaDialogFragment.newInstance(ArenaDialogFragment.ARENA_MODE_PARTIFICATION_USERS, mPresenter.getMatchData());
        dialogFragment.show(getSupportFragmentManager(), "ArenaInfo");
    }

    /**
     * 텍스트 메시지 전송
     * @param view
     */
    @OnClick(R.id.button_chatbox_send)
    public void onClickSend(View view) {

        String message = etChatBox.getText().toString();
        etChatBox.setText("");

        if (!TextUtils.isEmpty(message)) {

            mPresenter.createMatchTalk(mPresenter.getMatchId(), message, 0, mPresenter.getMatchData().getStatus());
        }
    }

    /**
     * 이미지 삽입 타입 선택
     *  - 로컬이미지
     *  - 링크이미지
     */
    @OnClick(R.id.iv_attachment)
    public void onClickAttachment(View view) {
        new MaterialDialog.Builder(ArenaActivity.this)
                .title(R.string.insert_image_title)
                .items(R.array.insert_image_array)
                .itemsCallback((dialog, view1, which, text) -> {
                    if ( which == 0 ) {
                        mPresenter.openLocalImagePicker();
                    } else {
                        openLinkImageDialog();
                    }
                })
                .show();
    }

    /**
     * 이미지 링크 입력 다이얼로그
     */
    public void openLinkImageDialog() {

        new MaterialDialog.Builder(ArenaActivity.this)
                .title("이미지 링크")
                .content("이미지 링크 주소를 입력해주세요")
                .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                .input("https://", "", (dialog, input) -> {
                    //addArenaImage(input.toString());
                    // 이미지 url을 ArenaChatFragment로 보낸다.
                }).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if ( event instanceof ArenaChatEvent.ArenaSendMessage) {

            ReactionModel reaction = ((ArenaChatEvent.ArenaSendMessage) event).getReaction();

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String message = gson.toJson(reaction);
            mPresenter.publishMessage(message);
        }
    }

    @Override
    public void showLoading() {
        super.showLoading();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void showLoading2() {
        super.showLoading();
        flProgress.setVisibility(View.VISIBLE);
    }

    public void hideLoading2() {
        super.hideLoading();
        flProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
        Toasty.normal(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getApplicationContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /*---------------------------------------------------------------------------------------------*/


    @OnClick({R.id.ib_0, R.id.ib_1, R.id.ib_2, R.id.ib_3, R.id.ib_4
            , R.id.ib_5, R.id.ib_6, R.id.ib_7, R.id.ib_8, R.id.ib_9
            , R.id.ib_10, R.id.ib_11, R.id.ib_12,R.id.ib_13,R.id.ib_14
    })
    public void onClickButton(View view) {

        if ( mEmitter == null ) {
            return;
        }

        ReactionModel stickerReaction = new ReactionModel();
        stickerReaction.setSide(mPresenter.isHomeValue());
        switch (view.getId()) {
            case R.id.ib_0:
                stickerReaction.setSticker("1_0");
                break;
            case R.id.ib_1:
                stickerReaction.setSticker("1_1");
                break;
            case R.id.ib_2:
                stickerReaction.setSticker("1_2");
                break;
            case R.id.ib_3:
                stickerReaction.setSticker("1_3");
                break;
            case R.id.ib_4:
                stickerReaction.setSticker("1_4");
                break;
            case R.id.ib_5:
                stickerReaction.setSticker("1_5");
                break;
            case R.id.ib_6:
                stickerReaction.setSticker("1_6");
                break;
            case R.id.ib_7:
                stickerReaction.setSticker("1_7");
                break;
            case R.id.ib_8:
                stickerReaction.setSticker("1_8");
                break;
            case R.id.ib_9:
                stickerReaction.setSticker("1_9");
                break;
            case R.id.ib_10:
                stickerReaction.setSticker("1_10");
                break;
            case R.id.ib_11:
                stickerReaction.setSticker("1_11");
                break;
            case R.id.ib_12:
                stickerReaction.setSticker("1_12");
                break;
            case R.id.ib_13:
                stickerReaction.setSticker("1_13");
                break;
            case R.id.ib_14:
                stickerReaction.setSticker("1_14");
                break;
        }

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String message = gson.toJson(stickerReaction);
        mPresenter.publishMessage(message);
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initCountDownTimer() {

        Date matchDate = mPresenter.getMatchData().getMatchRawDate();
        matchDate.getTime();
        Calendar.getInstance().getTime().getTime();
        long diff = matchDate.getTime() - Calendar.getInstance().getTime().getTime();

        if ( diff > 0 ) {
            new CountDownTimer(diff, 1000) {

                public void onTick(long millisUntilFinished) {
                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
                    //tvCountDown.setText("seconds remaining: " + millisUntilFinished / 1000);
                    tvCountDown.setText(hms);
                }

                public void onFinish() {
                    tvCountDown.setText("done!");
                }
            }.start();
        } else {
            tvCountDown.setVisibility(View.GONE);
        }
    }

    /**
     * 툴바 초기화
     */
    private void initToolbar() {

        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(CommonUtils.getColor(getApplicationContext(), R.color.issue_status_bar));
        }
    }


    /**
     * 스티커와 입력창 초기화
     * 로그인 사용자한테만 스티커 이미지를 보여준다.
     *  - 스티커는 비로그인 사용자도 가능하도록 변경(예정)
     */
    private void initActionContainer() {

        if ( App.getAccountManager().isAuthorized()) {
            llStickerContainer.setVisibility(View.VISIBLE);
            llChatContainer.setVisibility(View.VISIBLE);
        } else {
            llStickerContainer.setVisibility(View.GONE);
            llChatContainer.setVisibility(View.GONE);
        }
    }


    /**
     * 뷰페이저 초기화
     * @param match
     */
    private void initViewPager(MatchModel match) {

        mVpAdapter = new ArenaVpAdapter(getSupportFragmentManager(), mVpTitles, match);
        mViewPager.setAdapter(mVpAdapter);
        inkPageIndicator.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hideKeyboard(ArenaActivity.this, mViewPager);
                if ( position == 0 ) {
                    llChatContainer.setVisibility(View.VISIBLE);
                    llStickerContainer.setVisibility(View.VISIBLE);
                    llLineupInfo.setVisibility(View.GONE);
                    //mAdView.setVisibility(View.GONE);
                    llUserCount.setVisibility(View.VISIBLE);
                } else {
                    llChatContainer.setVisibility(View.GONE);
                    llStickerContainer.setVisibility(View.GONE);
                    llLineupInfo.setVisibility(View.VISIBLE);
                    //mAdView.setVisibility(View.VISIBLE);
                    llUserCount.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 매치정보 바인딩
     * @param matchData
     */
    private void initData(MatchModel matchData) {

        if ( matchData == null)
            return;

        tvHomeName.setText(matchData.getHomeTeamName());
        tvHomeScore.setText(matchData.getHomeTeamScoreString());
        tvAwayName.setText(matchData.getAwayTeamName());
        tvAwayScore.setText(matchData.getAwayTeamScoreString());
        tvStatus.setText(matchData.getStatus());

        changeTeam(matchData);
    }

    public void updateData(MatchModel matchData) {

        if ( matchData == null)
            return;

        tvHomeScore.setText(matchData.getHomeTeamScoreString());
        tvAwayScore.setText(matchData.getAwayTeamScoreString());
        tvStatus.setText(matchData.getStatus());
    }


    /**
     * 팀변경 클릭
     * @param matchData
     */
    private void changeTeam(MatchModel matchData) {

        if ( mPresenter.isHome() ) {
            tvHomeName.setSelected(true);
            tvHomeScore.setSelected(true);
            tvAwayName.setSelected(false);
            tvAwayScore.setSelected(false);

            // --- Local team ---
            GlideApp.with(getApplicationContext())
                    .load(matchData.getHomeTeamEmblemUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .into(ivHomeEmblem);

            // -- Visitor team ---
            GlideApp.with(getApplicationContext())
                    .load(matchData.getAwayTeamEmblemUrl())
                    .centerCrop()
                    .transform(new GrayscaleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .into(ivAwayEmblem);
        } else {

            tvHomeName.setSelected(false);
            tvHomeScore.setSelected(false);
            tvAwayName.setSelected(true);
            tvAwayScore.setSelected(true);

            // --- Local team ---
            GlideApp.with(getApplicationContext())
                    .load(matchData.getHomeTeamEmblemUrl())
                    .centerCrop()
                    .transform(new GrayscaleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .into(ivHomeEmblem);

            // -- Visitor team ---
            GlideApp.with(getApplicationContext())
                    .load(matchData.getAwayTeamEmblemUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .into(ivAwayEmblem);
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    public void setUserCount(int count) {
        tvUserCount.setText(String.valueOf(count));
    }

    /**
     * 1. 스티커를 발행한다..
     * @param reaction
     */
    public void emitterSticker(ReactionModel reaction) {

        if ( mEmitter != null ) {
            mEmitter.onNext(reaction);
        }
    }

    /**
     * 2.애니메이션 레이아웃에 스티커뷰를 추가한다..
     * @param resId
     * @param t
     */
    private void addSticker(int resId, int t) {

        if ( heartLayout2 != null ) {
            heartLayout2.addHeart(resId, t == 0 ? true : false);
        }
    }
}
