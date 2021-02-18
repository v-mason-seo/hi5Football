package com.ddastudio.hifivefootball_android.match_overview;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchOverviewModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchArenaInfoModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchPlayerRatingModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchPredictionsModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchRecentFormModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchOverviewFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.overview_recyclerview) RecyclerView rvOverviewList;

    MatchOverviewPresenter mPresenter;

    /**
     * RecyclerView Adapter
     */
    MatchOverviewRvAdapter mRvAdapter;

    /**
     * RecyclerView LinearLayoutManager
     */
    LinearLayoutManager linearLayoutManager;

    public MatchOverviewFragment() {
        // Required empty public constructor
    }

    public static MatchOverviewFragment newInstance(MatchModel matchData) {
        MatchOverviewFragment fragmentFirst = new MatchOverviewFragment();
        Bundle args = new Bundle();
        args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MatchModel matchData = Parcels.unwrap(getArguments().getParcelable("ARGS_MATCH"));
        // 아이템타입을 기본값으로 변경한다. MatchOverviewRvAdapter에서 사용함.
        matchData.setItemType(MultipleItem.ARENA_SCHEDULE);
        mPresenter = new MatchOverviewPresenter();
        mPresenter.setMatchData(matchData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_overview, container, false);
        _unbinder = ButterKnife.bind(this, view);
        mPresenter.attachView(this);

        initRefresh();
        initRecyclerView();

        //mPresenter.onLoadMatchPredictions();
        //mPresenter.onLoadMatchRecentForm();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.onLoadData();
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();

        if ( swipeRefreshLayout != null ) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
        Toasty.normal(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> refreshData());
    }

    private void refreshData() {
        mPresenter.onLoadData();
    }

    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();
        itemList.add(MatchOverviewRvAdapter.POSITION_ARENA_INFO, new MatchArenaInfoModel());
        itemList.add(MatchOverviewRvAdapter.POSITION_BASIC_INFO, mPresenter.getMatchData());
        itemList.add(MatchOverviewRvAdapter.POSITION_PLAYER_RATING, new MatchPlayerRatingModel());
        itemList.add(MatchOverviewRvAdapter.POSITION_PREDICTIOMS, new MatchPredictionsModel());
        itemList.add(MatchOverviewRvAdapter.POSITION_RECENT_FORM, new MatchRecentFormModel());

        mRvAdapter = new MatchOverviewRvAdapter(itemList);
        /*mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);*/

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvOverviewList.setLayoutManager(linearLayoutManager);
        rvOverviewList.setAdapter(mRvAdapter);

        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                if ( adapter.getItemViewType(position) == ViewType.MATCH_OVERVIEW_PREDICTIONS ) {
                    MatchPredictionsModel predic = (MatchPredictionsModel)adapter.getData().get(position);

                    switch (view.getId()) {
                        case R.id.home_team_container:
                            if ( !predic.getUserChoice().equals("")) {
                                Toast.makeText(getContext(), "이미 등록하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mPresenter.postMatchPredictions("H");
                            predic.setUserChoice("H");
                            predic.addUserCount();
                            predic.addHomeCount();
                            mRvAdapter.notifyItemChanged(MatchOverviewRvAdapter.POSITION_PREDICTIOMS);
                            break;
                        case R.id.draw_container:
                            if ( !predic.getUserChoice().equals("")) {
                                Toast.makeText(getContext(), "이미 등록하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mPresenter.postMatchPredictions("D");
                            predic.setUserChoice("D");
                            predic.addUserCount();
                            predic.addDrawCount();
                            mRvAdapter.notifyItemChanged(MatchOverviewRvAdapter.POSITION_PREDICTIOMS);
                            break;
                        case R.id.away_team_container:
                            if ( !predic.getUserChoice().equals("")) {
                                Toast.makeText(getContext(), "이미 등록하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mPresenter.postMatchPredictions("A");
                            predic.setUserChoice("A");
                            predic.addUserCount();
                            predic.addAwayCount();
                            mRvAdapter.notifyItemChanged(MatchOverviewRvAdapter.POSITION_PREDICTIOMS);
                            break;
                    }
                } else if ( adapter.getItemViewType(position) == ViewType.MATCH_OVERVIEW_PLAYER_RATING ) {

                    switch (view.getId()) {
                        case R.id.player_rating_button:
                            //Toast.makeText(getContext(), "선수평가", Toast.LENGTH_SHORT).show();
                            mPresenter.openPlayerRatingActivity();
                            break;
                    }
                } else if ( adapter.getItemViewType(position) == ViewType.MATCH_OVERVIEW_ARENA_INFO ) {
                    switch (view.getId()) {
                        case R.id.card_arena:

                            if (!App.getAccountManager().isAuthorized()) {
                                showMessage("로그인후 입장가능합니다.");
                                return;
                            }
                            mPresenter.openArenaActivity(mPresenter.getMatchData());
                            break;
                    }
                }
            }
        });
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadOverview(MatchOverviewModel items) {

        if ( items.getPlayers() != null ) {

             PlayerModel best = items.getPlayers().get(0);
             PlayerModel worst = items.getPlayers().get(1);

            MatchPlayerRatingModel overviewPlayer = (MatchPlayerRatingModel)mRvAdapter.getData().get(MatchOverviewRvAdapter.POSITION_PLAYER_RATING);

            if ( overviewPlayer != null ) {
                if ( best != null ) {
                    overviewPlayer.setBestPlayer(best);
                }

                if ( worst != null ) {
                    overviewPlayer.setWorstPlayer(worst);
                }
            }
        }

        if ( items.getMatch() != null ) {

            mRvAdapter.setData(MatchOverviewRvAdapter.POSITION_BASIC_INFO, items.getMatch());
        }
    }

    public void onLoadPredictions(MatchPredictionsModel predictions) {

        if ( mRvAdapter.getData().size() > 0 ) {
            mRvAdapter.setData(MatchOverviewRvAdapter.POSITION_PREDICTIOMS, predictions);
        } else {
            mRvAdapter.addData(predictions);
        }
    }

    public void onLoadRecentForm(MatchRecentFormModel recentForm) {

        if ( mRvAdapter.getData().size() > 0 ) {
            mRvAdapter.setData(MatchOverviewRvAdapter.POSITION_RECENT_FORM, recentForm);
        } else {
            mRvAdapter.addData(recentForm);
        }
    }

}
