package com.ddastudio.hifivefootball_android.team;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeamActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.team_tabs) SmartTabLayout mTabLayout;
    @BindView(R.id.team_viewpager) ViewPager mViewPager;
    @BindView(R.id.team_emblem) ImageView ivTeamEmblem;
    @BindView(R.id.team_name) TextView tvTeamName;
    @BindArray(R.array.team_tabs_array) String[] mVpTitles;

    // 팀(클럽) 글쓰기 요청 ID
    public static final int REQUEST_EDITOR = 20000;

    int mPagerPosition;
    int mPagerOffsetPixels;
    TeamPresenter mPresenter;
    TeamVpAdapter mVpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.TeamTheme);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_team);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_team)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener((v, dx, x, y) -> {
                    if (v == mViewPager) {
                        return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
                                || dx < 0;
                    }

                    return false;
                });
        ButterKnife.bind(this);

        int teamId = getIntent().getIntExtra("ARGS_TEAM_ID", 0);
        mPresenter = new TeamPresenter(teamId);
        mPresenter.attachView(this);

        initToolbar();

        mPresenter.onLoadTeam();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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

    private void initViewPager(TeamModel teamData) {

        mVpAdapter = new TeamVpAdapter(getSupportFragmentManager(), mVpTitles, teamData);
        mViewPager.setAdapter(mVpAdapter);
        mTabLayout.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /*───-----------------------------------────────────────────────────────*/

    public void onLoadFinished(TeamModel teamData) {

        setTeamData(teamData);
        initViewPager(teamData);
    }

    private void setTeamData(TeamModel teamData) {

        tvTeamName.setText(teamData.getTeamName());

        Picasso.get()
                .load(teamData.getLargeEmblemUrl())
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .fit()
                .centerInside()
                .into(ivTeamEmblem);
    }
}
