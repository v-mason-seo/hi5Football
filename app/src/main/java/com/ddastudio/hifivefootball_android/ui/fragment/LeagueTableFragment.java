package com.ddastudio.hifivefootball_android.ui.fragment;


import android.os.Bundle;
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
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.community.SimpleSectionHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.presenter.LeagueTablePresenter;
import com.ddastudio.hifivefootball_android.ui.rvadapter.LeagueTableRvAdapter;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeagueTableFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_league_table) RecyclerView rvLeagueTable;
    View emptyView;

    LeagueTablePresenter mPresenter;
    LeagueTableRvAdapter mRvAdapter;

    public LeagueTableFragment() {
        // Required empty public constructor
    }

    public static LeagueTableFragment newInstance(int competitionId, int teamId) {

        return newInstance(competitionId, teamId, teamId);
    }

    public static LeagueTableFragment newInstance(int competitionId, int teamId, int teamId2) {
        LeagueTableFragment fragmentFirst = new LeagueTableFragment();
        Bundle args = new Bundle();
        //args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        args.putInt("ARGS_COMP_ID", competitionId);
        args.putInt("ARGS_TEAM_ID", teamId);
        args.putInt("ARGS_TEAM_ID2", teamId2);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_league_table, container, false);
        _unbinder = ButterKnife.bind(this, view);

        int competitionId = getArguments().getInt("ARGS_COMP_ID");
        int teamId = getArguments().getInt("ARGS_TEAM_ID");
        int teamId2 = getArguments().getInt("ARGS_TEAM_ID2");

        mPresenter = new LeagueTablePresenter(competitionId, teamId, teamId2);
        mPresenter.attachView(this);

        initRefresh();
        initRecyclerView();

        mPresenter.onLoadLeagueTable();

        return view;
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
    }

    /**
     * 메시지를 보여준다.
     * @param message
     */
    @Override
    public void showMessage(String message) {
        Toasty.normal(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 에러 메시지를 보여준다.
     * @param errMessage
     */
    @Override
    public void showErrorMessage(String errMessage ) {
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * 로딩화면 보이기
     */
    @Override
    public void showLoading() {
        if ( swipeRefreshLayout != null ) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    /**
     * 로딩화면 감추기
     */
    @Override
    public void hideLoading() {

        if ( swipeRefreshLayout != null ) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * SwipeRefreshLayout 초기화
     */
    public void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> {

            if ( mRvAdapter != null && mRvAdapter.getData().size() > 0) {
                mRvAdapter.getData().clear();
            }

            mPresenter.onLoadLeagueTable();
        });
    }

    /**
     * 리사이클뷰 초기화
     */
    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();
        emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvLeagueTable.getParent(), false);
        mRvAdapter = new LeagueTableRvAdapter(itemList);
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
//        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
//        mRvAdapter.isFirstOnly(true);
//        mRvAdapter.setNotDoAnimationCount(3);

        rvLeagueTable.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLeagueTable.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvLeagueTable.setAdapter(mRvAdapter);

        mRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                onRvClickItem((StandingModel)mRvAdapter.getData().get(position));
            }
        });
    }

    private void onRvClickItem(StandingModel standing) {

        if ( standing.getTeamId() != mPresenter.getTeamId()) {

            mPresenter.openTeamActivity(standing.getTeamId());
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinishedLeagueTable(List<StandingModel> items) {

        mRvAdapter.getData().clear();
        if ( items == null && items.size() == 0 ) {
            mRvAdapter.setEmptyView(emptyView);
            mRvAdapter.notifyDataSetChanged();
            hideLoading();
            return;
        }

//        StandingModel headerStanding = new StandingModel();
//        headerStanding.setItemType(MultipleItem.BOTTOM_STANDING_HEADER);
//        mRvAdapter.addData(headerStanding);
//        mRvAdapter.addData(items);

        String grooupName = "";
        for ( int i =0; i < items.size(); i++ ) {

            StandingModel standing = items.get(i);
            if ( i == 0 ) {
                grooupName = standing.getCompGroup();
                if ( !grooupName.equals("")) {
                    mRvAdapter.addData(new SimpleSectionHeaderModel(standing.getCompGroup()));
                }
            }

            if ( !grooupName.equals(standing.getCompGroup())) {
                grooupName = standing.getCompGroup();
                mRvAdapter.addData(new SimpleSectionHeaderModel(standing.getCompGroup()));
            }

            mRvAdapter.addData(standing);
        }

        hideLoading();
    }

}
