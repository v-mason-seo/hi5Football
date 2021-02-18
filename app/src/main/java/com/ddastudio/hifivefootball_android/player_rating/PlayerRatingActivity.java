package com.ddastudio.hifivefootball_android.player_rating;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.PlayerRatingEvent;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.match_lineup.LineupFragment;
import com.ddastudio.hifivefootball_android.match_lineup.LineupGridFragment;
import com.ddastudio.hifivefootball_android.ui.widget.InkPageIndicator;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.kakao.adfit.ads.ba.BannerAdView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

public class PlayerRatingActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.fl_container) FrameLayout flContainer;
    @BindView(R.id.tv_home_team_name) TextView tvHomeName;
    @BindView(R.id.iv_home_emblem) ImageView ivHomeEmblem;
    @BindView(R.id.tv_home_team_score) TextView tvHomeScore;
    @BindView(R.id.tv_away_team_name) TextView tvAwayName;
    @BindView(R.id.iv_away_emblem) ImageView ivAwayEmblem;
    @BindView(R.id.tv_away_team_score) TextView tvAwayScore;
    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.ink_pager_indicator) InkPageIndicator inkPageIndicator;
    //@BindView(R.id.adView) AdView mAdView;
    @BindView(R.id.adview) BannerAdView adView;

    int mPagerPosition;
    int mPagerOffsetPixels;
    MatchModel matchData;
    boolean isHomeSelection = true;
    PlayerRatingVpAdapter mVpAdapter;

    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_player_rating);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_player_rating)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener((v, dx, x, y) -> {
                    if (v == mViewPager) {
                        return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
                                || dx < 0;
                    }

                    return false;
                });
        ButterKnife.bind(this);

        matchData = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_MATCH_DATA"));
        int selectedTeamId = getIntent().getIntExtra("ARGS_SELECTED_TEAM_ID", 0);
        int selectedPlayerId = getIntent().getIntExtra("ARGS_SELECTED_PLAYER_ID", 0);

        if ( selectedTeamId == 0 || matchData.getHomeTeamId() == selectedTeamId) {
            isHomeSelection = true;
        } else {
            isHomeSelection = false;
        }

        initToolbar();
        initAdmob();
        initData(matchData);
        //initViewPager(matchData, selectedTeamId, selectedPlayerId, homeLienupList, awayLienupList);
        //initViewPager(matchData, selectedTeamId, selectedPlayerId);
        initKeyboardStateObserver();
        initEditorFragment(matchData, TYPE_GRID);
    }

    private int TYPE_GRID = 0;
    private int TYPE_LIST = 1;

    private void initEditorFragment(MatchModel matchData, int type) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if ( type == TYPE_GRID) {
            LineupGridFragment fragment = LineupGridFragment.newInstance(matchData);
            fragmentTransaction.replace(R.id.fl_container, fragment);
        } else {
            LineupFragment fragment = LineupFragment.newInstance(matchData);
            fragmentTransaction.replace(R.id.fl_container, fragment);
        }

        //fragmentTransaction.addToBackStack()
        fragmentTransaction.commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }

    /*----------------------------------------------------------------------------------*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_rating_activity, menu);
        this.mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_view_type_grid:
                initEditorFragment(matchData, TYPE_GRID);
                break;
            case R.id.action_view_type_list:
                initEditorFragment(matchData, TYPE_LIST);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*----------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if ( event instanceof PlayerRatingEvent.ShowPlayerComment ) {
            if ( mViewPager != null ) {
                mViewPager.setCurrentItem(1, true);
            }
        }
    }

    /**
     * 광고 보이기
     */
    private void showAdView() {

        //mAdView.setVisibility(View.VISIBLE);
        adView.setVisibility(View.VISIBLE);
    }

    /**
     * 광고 숨기기
     */
    private void hideAdView() {

        //mAdView.setVisibility(View.GONE);
        adView.setVisibility(View.GONE);
    }

    /*----------------------------------------------------------------------------------*/

    @OnClick({R.id.ll_home_team})
    public void onClickHome(View view) {

        if ( isHomeSelection == false ) {
            isHomeSelection = true;
            changeTeam(matchData);
            mViewPager.setCurrentItem(0);
            //App.getInstance().bus().send(new PlayerRatingEvent.ChoiceTeam(true));
        }
    }

    @OnClick({R.id.ll_away_team})
    public void onClickAway(View view) {

        if ( isHomeSelection == true ) {
            isHomeSelection = false;
            changeTeam(matchData);
            mViewPager.setCurrentItem(1);
            //App.getInstance().bus().send(new PlayerRatingEvent.ChoiceTeam(false));
        }
    }

    /*----------------------------------------------------------------------------------*/

    /**
     * 툴바 초기화
     */
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_vector);
    }

    private void changeTeam(MatchModel matchData) {

        if ( isHomeSelection == true ) {
            tvHomeName.setSelected(true);
            tvHomeScore.setSelected(true);
            tvAwayName.setSelected(false);
            tvAwayScore.setSelected(false);

            // --- Local team ---
            GlideApp.with(getApplicationContext())
                    .load(matchData.getHomeTeamEmblemUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .placeholder(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_empty_emblem_vector_1))
                    .into(ivHomeEmblem);

            // -- Visitor team ---
            GlideApp.with(getApplicationContext())
                    .load(matchData.getAwayTeamEmblemUrl())
                    .centerCrop()
                    .transform(new GrayscaleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .placeholder(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_empty_emblem_vector_1))
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
                    //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .placeholder(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_empty_emblem_vector_1))
                    .into(ivHomeEmblem);

            // -- Visitor team ---
            GlideApp.with(getApplicationContext())
                    .load(matchData.getAwayTeamEmblemUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .placeholder(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_empty_emblem_vector_1))
                    .into(ivAwayEmblem);
        }
    }

    private void initData(MatchModel matchData) {

        tvHomeName.setText(matchData.getHomeTeamName());
        tvHomeScore.setText(matchData.getHomeTeamScoreString());
        tvAwayName.setText(matchData.getAwayTeamName());
        tvAwayScore.setText(matchData.getAwayTeamScoreString());

        changeTeam(matchData);

    }

    /**
     * 뷰페이저/아답터 초기화
     */
    private void initViewPager(MatchModel matchData, int selectedTeamId, int selectedPlayerId) {

        mVpAdapter = new PlayerRatingVpAdapter(getSupportFragmentManager(),
                matchData,
                selectedTeamId,
                selectedPlayerId);

        mViewPager.setAdapter(mVpAdapter);
        mViewPager.setOffscreenPageLimit(1);
        inkPageIndicator.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {
                if ( position == 0 ) {
                    isHomeSelection = true;
                    changeTeam(matchData);
                } else {
                    isHomeSelection = false;
                    changeTeam(matchData);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 광고 초기화
     */
    private void initAdmob() {

//        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);
//        AdRequest adReuest = new AdRequest.Builder()
//                .addTestDevice("C688FB9C0634F2E6FF40ADFF71711FFF")
//                .addTestDevice("E4B200C7136AD0708A2BF1FA76A2E94B").addTestDevice("70D53A4C46EFEAAA47D76A37CA7499DA")
//                .build();
//        mAdView.loadAd(adReuest);

        adView.loadAd();
    }

    private void initKeyboardStateObserver() {

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if ( isOpen ) {
                            hideAdView();
                        } else {
                            showAdView();
                        }
                    }
                });
    }


    /*------------------------------------------------------------------*/
}
