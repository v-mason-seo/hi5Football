package com.ddastudio.hifivefootball_android.match_up;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.community.SimpleSectionHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.fragment.LeagueTableDialogFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchUpFragment extends BaseFragment {

    // 라이브 매치
    final public static int LIVE_MATCH = 0;
    // 최근 매치
    final public static int RECENT_MATCH = 1;
    // 인기 매치
    final public static int POPULAR_MATCH = 2;
    // 어제 매치
    final public static int YESTERDAY_MATCH = 3;
    // 내일 매치
    final public static int TOMORROW_MATCH = 4;
    // 오늘 매치
    final public static int TODAY_MATCH = 5;
    // 최근 종료된 인기 매치 ( ~3일 부터 오늘까지 )
    final public static int FINISH_RECENT_MATCH = 6;
    // 다가오는 인기 매치 ( 오늘부터 3일까지 )
    final public static int COME_MATCH = 7;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_match_up) RecyclerView rvMatchUp;

    MatchUpPresenter mPresenter;
    MatchUpRvAdapter mRvAdapter;
    // EmptyView
    View emptyView;
    // 전체경기일정보기 헤더뷰
    View headerView;

    public MatchUpFragment() {
        // Required empty public constructor
    }

    /**
     * 프래그먼트 객체 생성
     * @param matchType
     * @return
     */
    public static MatchUpFragment newInstance(int matchType) {
        MatchUpFragment fragmentFirst = new MatchUpFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ARGS_MATCH_TYPE", matchType);
        fragmentFirst.setArguments(bundle);
        return fragmentFirst;
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int matchType = getArguments().getInt("ARGS_MATCH_TYPE");
        mPresenter = new MatchUpPresenter(matchType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_up, container, false);
        _unbinder = ButterKnife.bind(this, view);

        mPresenter.attachView(this);

        initRefresh();
        initEmptyView();
        initRecyclerView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        if ( swipeRefreshLayout != null ) {
            swipeRefreshLayout.setRefreshing(true);
        }
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

    /**
     * SwipeRefreshLayout 초기화
     */
    public void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> mPresenter.onLoadData() );
    }

    /**
     * 데이터가 없을 때 보여줄 엠프티 뷰 생성
     */
    private void initEmptyView() {

        headerView = getActivity().getLayoutInflater().inflate(R.layout.row_match_up_all_show_match_header_item, (ViewGroup) rvMatchUp.getParent(), false);

        // 헤더뷰 클릭 리스너
        headerView.findViewById(R.id.btn_show_fixture_list).setOnClickListener(v ->
            mPresenter.openMatchDetailInfoActivity()
        );

        Random r = new Random();
        int randomIndex = r.nextInt(3);

        switch (randomIndex) {
            case 0:
                emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_no_schedule_view1, (ViewGroup) rvMatchUp.getParent(), false);
                break;
            case 1:
                emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_no_schedule_view2, (ViewGroup) rvMatchUp.getParent(), false);
                break;
            case 2:
                emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_no_schedule_view3, (ViewGroup) rvMatchUp.getParent(), false);
                break;
        }

        TextView tvEmptyTitle = emptyView.findViewById(R.id.empty_text);

        switch (mPresenter.getMatchType()) {
            case MatchUpFragment.LIVE_MATCH:
                tvEmptyTitle.setText("현재 진행중인 라이브 경기가 없습니다.");
                break;
            case MatchUpFragment.RECENT_MATCH:
                break;
            case MatchUpFragment.POPULAR_MATCH:
                break;
            default:
                break;
        }
    }

    /**
     * 매치업 리사이클러뷰 초기화
     */
    private void initRecyclerView() {

        mRvAdapter = new MatchUpRvAdapter(new ArrayList<>());

        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        rvMatchUp.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvMatchUp.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMatchUp.setAdapter(mRvAdapter);
        // 헤더뷰와 엠프티뷰 모두 사용하기 위해서는 아래 코드가 필요하다.
        mRvAdapter.setHeaderAndEmpty(true);
        // 헤더뷰 삽입
        mRvAdapter.addHeaderView(headerView);

        mRvAdapter.setOnItemClickListener(((adapter, view, position) -> {

            if ( adapter.getItem(position) instanceof  MatchModel) {

                MatchModel item = (MatchModel)mRvAdapter.getItem(position);
                mPresenter.openMatchActivity(item);

                // 아레나방 공사가 끝날때까지는 매치 상세보기로 이동하도록 함.
                /*if (!App.getAccountManager().isAuthorized()) {
                    showMessage("로그인후 입장가능합니다.");
                    return;
                }
                mPresenter.openArenaActivity(item);*/
            }

        }));

        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()) {

                    case R.id.iv_competition:
                        onRvClickCompetition();
                        break;

                    case R.id.iv_home_emblem:
                        MatchModel home = (MatchModel)mRvAdapter.getItem(position);
                        onRvClickTeam(home.getHomeTeamId());
                        break;

                    case R.id.iv_away_emblem:
                        MatchModel away = (MatchModel)mRvAdapter.getItem(position);
                        onRvClickTeam(away.getAwayTeamId());
                        break;

                    case R.id.iv_more_item:
                        onRcMoreClick(view, (MatchModel)mRvAdapter.getItem(position));
                        break;

                    case R.id.btn_rating:
                        onRvClickRating((MatchModel)mRvAdapter.getItem(position));
                        break;

                }
            }
        });
    }

    private void onRvClickCompetition() {

    }

    /*---------------------------------------------------------------------------------------------*/

    private void onRvClickRating(MatchModel match) {

        mPresenter.openPlayerRatingActivity(match);
    }

    private void onRvClickTeam(int teamId) {
        mPresenter.openTeamActivity(teamId);
    }


    public void onRcMoreClick(View view, MatchModel match) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.inflate(R.menu.menu_rv_matchup);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.menu_open_match_activity:
                        mPresenter.openMatchActivity(match);
                        return true;

                    case R.id.menu_open_match_chat_activity:
                        if (!App.getAccountManager().isAuthorized()) {
                            showMessage("로그인후 입장가능합니다.");
                            return false;
                        }
                        mPresenter.openArenaActivity(match);
                        return true;

                    case R.id.menu_show_league_table:
                        LeagueTableDialogFragment dialogFragment
                                = LeagueTableDialogFragment.newInstance(MultipleItem.BOTTOM_STANDING, match.getCompetitionId());
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "LeagueTable");
                        return true;

                    case R.id.menu_show_match_id:
                        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                                .title("매치아이디")
                                .content(String.valueOf(match.getMatchId()) + " / " + String.valueOf(match.getMatchFaId()))
                                .positiveText("확인")
                                .show();
                        return true;

                }
                return false;
            }
        });

        popup.show();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinishMatchList(List<MatchModel> items) {

        mRvAdapter.getData().clear();
        if ( items == null || items.size() == 0 ) {
            mRvAdapter.setEmptyView(emptyView);
            hideLoading();
            return;
        }

        if ( mPresenter.getMatchType() == MatchUpFragment.FINISH_RECENT_MATCH) {

            String compName = "";

            for ( int i = 0 ; i < items.size(); i++ ) {

                MatchModel match = items.get(i);

                if ( i == 0 ) {
                    compName = match.getCompetitionName();
                    if ( !compName.equals("")) {
                        mRvAdapter.addData(new SimpleSectionHeaderModel(compName + "  " + match.getSeason() + "   " + match.getWeek()));
                    }
                }

                if ( !compName.equals(match.getCompetitionName())) {
                    compName = match.getCompetitionName();
                    mRvAdapter.addData(new SimpleSectionHeaderModel(compName + "  " + match.getSeason() + "   " + match.getWeek()));
                }

                mRvAdapter.addData(match);
            }

        } else {
            mRvAdapter.addData(items);
        }


        hideLoading();
    }
}
