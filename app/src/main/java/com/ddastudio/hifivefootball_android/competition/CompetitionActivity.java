package com.ddastudio.hifivefootball_android.competition;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.parceler.Parcels;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CompetitionActivity extends AppCompatActivity {

    @BindView(R.id.comp_toolbar) Toolbar mToolbar;
    @BindView(R.id.comp_tabs) SmartTabLayout mTabLayout;
    @BindView(R.id.comp_viewpager) ViewPager mViewPager;
    @BindView(R.id.competition_emblem) ImageView ivEmblem;
    @BindView(R.id.competition_name) TextView tvCompetitionName;
    @BindArray(R.array.comp_tabs_array) String[] mVpTitles;


    int mPagerPosition;
    int mPagerOffsetPixels;
    CompetitionVpAdapter mVpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CompetitionTheme);
        super.onCreate(savedInstanceState);

        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_competition)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener((v, dx, x, y) -> {
                    if (v == mViewPager) {
                        return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
                                || dx < 0;
                    }

                    return false;
                });
        ButterKnife.bind(this);

        CompetitionModel competitionData = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_COMPETITION"));

        initToolbar();
        initData(competitionData);
        initViewPager(competitionData);
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

    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
    }

    private void initData(CompetitionModel competitionData) {

        tvCompetitionName.setText(competitionData.getCompetitionName());
        GlideApp.with(getApplicationContext())
                .load(competitionData.getCompetitionImageUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into(ivEmblem);
    }

    private void initViewPager(CompetitionModel competitionData) {

        mVpAdapter = new CompetitionVpAdapter(getSupportFragmentManager(), mVpTitles, competitionData);
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
                // 글쓰기 버튼은 게시판에서만 활성화
                if ( position == 0 ) {

                } else {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
