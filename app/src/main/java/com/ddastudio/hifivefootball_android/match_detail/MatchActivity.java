package com.ddastudio.hifivefootball_android.match_detail;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.kakao.adfit.ads.ba.BannerAdView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MatchActivity extends BaseActivity {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.match_toolbar) Toolbar mToolbar;
    @BindView(R.id.match_tabs) SmartTabLayout mTabLayout;
    @BindView(R.id.match_viewpager) ViewPager mViewPager;
    @BindView(R.id.match_score) TextView tvScore;
    @BindView(R.id.localteam_emblem) ImageView ivLocalTeamEmblem;
    @BindView(R.id.localteam_name) TextView tvLocalTeamName;
    @BindView(R.id.visitorteam_emblem) ImageView ivVisitorTeamEmblem;
    @BindView(R.id.visitorteam_name) TextView tvVisitorTeamName;
    @BindView(R.id.match_date) TextView tvMatchDate;
    @BindView(R.id.tv_comp) TextView tvCompetition;
    @BindArray(R.array.match_tabs_array) String[] mVpTitles;

    Menu mMenu;
    MatchVpAdapter mVpAdapter;
    int mPagerPosition;
    int mPagerOffsetPixels;
    MatchModel matchData;
    MatchPresenter mPresenter;

    // 매치 글쓰기 요청 ID
    public static final int REQUEST_EDITOR = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MatchTheme);
        super.onCreate(savedInstanceState);

        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_match)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener((v, dx, x, y) -> {
                    if (v == mViewPager) {
                        return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
                                || dx < 0;
                    }

                    return false;
                });
        ButterKnife.bind(this);

        // TODO: 2017. 10. 27.  Class not found when unmarshalling 오류가 발생함
        // TODO: 2017. 10. 27. 1. 안드로이드 스튜디오 업데이트 후 에러 발생
        // TODO: 2017. 10. 27. 2. parcel 로직을 다시 보자

        mPresenter = new MatchPresenter();
        mPresenter.attachView(this);

        initToolbar();

        matchData = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_MATCH"));

        if ( matchData == null ) {
            // 1.매치정보가 없으면 ID로 매치 정보를 조회한다.
            int matchid = getIntent().getIntExtra("ARGS_MATCH_ID", 0);
            mPresenter.onLoadMatch(matchid);
        } else if (matchData.getCompetition() == null) {
            // 2.매치정보는 있지만 리그정보가 없다면 매치정보를 다시 조회한다.
            int matchid = matchData.getMatchId();
            mPresenter.onLoadMatch(matchid);
        } else {
            // 3.넘어온 매치정보로 데이터 바인딩
            mPresenter.setMatch(matchData);
            initData(matchData);
            initViewPager(matchData);
            hideLoading();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
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
        mPresenter.detachView();
        super.onDestroy();
    }

    /*───-----------------------------------────────────────────────────────*/

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
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

    @OnClick(R.id.visitorteam_emblem)
    public void onClickAwayTeam(View view) {
        openTeamActivity(matchData.getAwayTeamId());
    }

    @OnClick(R.id.localteam_emblem)
    public void onClickHomeTeam(View view) {
        openTeamActivity(matchData.getHomeTeamId());
    }

    public void openTeamActivity(int teamId) {
        Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
        intent.putExtra("ARGS_TEAM_ID", teamId);

        startActivity(intent);
    }

    /*───-----------------------------------────────────────────────────────*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
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

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
    }

    /*───-----------------------------------────────────────────────────────*/

    @OnClick(R.id.fab_post_content)
    public void onPostContentFabClick() {
        mPresenter.openTextEditor();
    }

    /*───-----------------------------------────────────────────────────────*/

    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
    }

    private void initData(MatchModel matchData) {

        String score = matchData.getHomeTeamScoreString() + "  :  " + matchData.getAwayTeamScoreString();
        tvScore.setText(score);
        tvMatchDate.setText(matchData.getStatus());
        tvLocalTeamName.setText(matchData.getHomeTeamName());
        tvVisitorTeamName.setText(matchData.getAwayTeamName());
        tvCompetition.setText(matchData.getCompetitionName() + "  " + matchData.getSeason() +  "  " + matchData.getWeek());

        // HomeTeam
        Picasso.get()
                .load(matchData.getHomeTeamLargeEmblemUrl())
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .fit()
                .centerInside()
                .into(ivLocalTeamEmblem);

        // AwayTema
        Picasso.get()
                .load(matchData.getAwayTeamLargeEmblemUrl())
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .fit()
                .centerInside()
                .into(ivVisitorTeamEmblem);
    }

    private void initViewPager(MatchModel matchData) {

        mVpAdapter = new MatchVpAdapter(getSupportFragmentManager(), mVpTitles, matchData);
        mViewPager.setAdapter(mVpAdapter);
        mTabLayout.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) { }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /*───-----------------------------------────────────────────────────────*/

    public void onLoadFinishedMatch(MatchModel match) {

        if ( match != null ) {
            initData(match);
            initViewPager(match);
        }

        hideLoading();
    }
}
