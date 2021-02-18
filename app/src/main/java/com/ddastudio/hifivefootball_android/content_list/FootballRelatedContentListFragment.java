package com.ddastudio.hifivefootball_android.content_list;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class FootballRelatedContentListFragment extends BaseFragment {

    // 0. 매치 관련 글
    public static final int MATCH_RELATED_CONTENT = 0;
    // 1. 팀 관련 글
    public static final int TEAM_RELATED_CONTENT = 1;
    // 2. 선수 관련 글
    public static final int PLAYER_RELATED_CONTENT = 2;

    @BindView(R.id.contents_list)
    RecyclerView rvContentsList;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    View emptyView;
    View emptyFooterView;

    FootballRelatedContentListPresenter mPresenter;
    FootballRelatedContentListRvAdapter mRvAdapter;

    int mLimit = 10;
    int mOffset = 0;
    long mLastClickTime = 0;

    public FootballRelatedContentListFragment() {
        // Required empty public constructor
    }

    public static FootballRelatedContentListFragment newInstance(int relatedId, int id) {

        FootballRelatedContentListFragment fragment = new FootballRelatedContentListFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        bundle.putInt("ARGS_RELATED_ID", relatedId);
        bundle.putInt("ARGS_ID", id);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int relatedId = getArguments().getInt("ARGS_RELATED_ID");
        int id = getArguments().getInt("ARGS_ID");
        mPresenter = new FootballRelatedContentListPresenter(relatedId, id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_football_related_content_list, container, false);
        _unbinder = ButterKnife.bind(this, view);
        mPresenter.attachView(this);

        initEmptyView();
        initRecyclerView();
        initSwipeRefresh();

        mLimit = 10;
        mOffset = 0;
        mPresenter.onLoadData(mLimit, mOffset);

        return view;
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
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void initSwipeRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> {
            if ( rvContentsList != null && mRvAdapter != null ) {
                mRvAdapter.getData().clear();
                mLimit = 10;
                mOffset = 0;
                mPresenter.onLoadData(mLimit, mOffset);
            }
        });
    }

    private void initEmptyView() {

        // 라사이클뷰 하단에 빈공간을 삽입한다. - fab 중복을 피하기 위해서
        emptyFooterView = getActivity().getLayoutInflater().inflate(R.layout.row_empty_footer_view, (ViewGroup)rvContentsList.getParent(), false);
        emptyView = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.recycler_empty_view, (ViewGroup) rvContentsList.getParent(), false);
    }


    /**
     * RecyclerView 초기화
     */
    private void initRecyclerView() {

        mRvAdapter = new FootballRelatedContentListRvAdapter(new ArrayList<>());
        mRvAdapter.setHeaderFooterEmpty(true, true);
        mRvAdapter.setEmptyView(emptyView);
        mRvAdapter.addFooterView(emptyFooterView);
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        mRvAdapter.setEnableLoadMore(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvContentsList.setLayoutManager(linearLayoutManager);
        rvContentsList.setAdapter(mRvAdapter);


        mRvAdapter.setOnLoadMoreListener( () -> {
                    mOffset = mRvAdapter.getLoadMoreViewPosition();
                    mPresenter.onLoadData(mLimit, mOffset);
                }
        );

        // --- ItemClick ---
        mRvAdapter.setOnItemClickListener((adapter, view, position) -> {
            if ( mRvAdapter.getItem(position) instanceof ContentHeaderModel) {
                //onRvClickPostList((ContentHeaderModel)mRvAdapter.getItem(position), position);
            }
        });

        // --- ChildItemClick ---
        mRvAdapter.setOnItemChildClickListener( (adapter, view, position) -> {

            if ( mRvAdapter.getItem(position) instanceof ContentHeaderModel) {
                ContentHeaderModel item = (ContentHeaderModel)mRvAdapter.getItem(position);

                switch (view.getId()) {
                    case R.id.iv_avatar:
                        //onRvClickAvatar(item.getUser());
                        break;
                    case R.id.comment_icon:
                    case R.id.comment_count:
                    case R.id.comment_box:
                    case R.id.iv_comment:
                        //onRvClickShowComment(item);
                        break;
                    case R.id.liked:
                    case R.id.like_count:
                        //onRvClickShowHifive(item);
                        break;
                    case R.id.scraped:
                        //onRvClickShowScrap(item);
                        break;
                    case R.id.fl_profile_box:
                        //onRvClickProfile(item);
                        break;
                    case R.id.include_match:
                        try {
                            if ( item.getMatch() != null ) {
                                int matchId = item.getMatch().getMatchId();
                                //mPresenter.openMatchActivity(matchId);
                            } else {
                                showMessage("매치정보가 없습니다.");
                            }

                        } catch (Exception ex) {
                            showErrorMessage("매치아이디가 없습니다.");
                        }
                        break;
                    case R.id.include_player:
                        Timber.i("플레이어클릭");
                        try {
                            int playerId = item.getPlayerId();
                            String playerName = item.getPlayerName();
                            //mPresenter.openPlayerActivity(playerId, playerName);
                        } catch (Exception ex) {
                            showErrorMessage("플레이어 아이디가 없습니다.");
                        }
                        break;
                    case R.id.include_team:
                        Timber.i("팀클릭");
                        try {
                            int teamId = item.getTeam().getTeamId();
                            //mPresenter.openTeamActivity(teamId);
                        } catch (Exception ex) {
                            showErrorMessage("팀 아이디가 없습니다.");
                        }
                        break;
                    case R.id.iv_home_emblem:

                        if ( item.getMatch() != null ) {

                            if ( item.getMatch().getHomeTeamId() != 0 ) {
                                int teamId = item.getMatch().getHomeTeamId();
                                //mPresenter.openTeamActivity(teamId);
                            } else {
                                showMessage("팀 아이디가 없습니다.");
                            }
                        } else {
                            showMessage("팀 아이디가 없습니다.");
                        }
                        break;
                    case R.id.iv_away_emblem:
                        if ( item.getMatch() != null ) {

                            if ( item.getMatch().getAwayTeamId() != 0 ) {
                                int teamId = item.getMatch().getAwayTeamId();
                                //mPresenter.openTeamActivity(teamId);
                            } else {
                                showMessage("팀 아이디가 없습니다.");
                            }
                        } else {
                            showMessage("팀 아이디가 없습니다.");
                        }
                        break;
                }
            }
        });
    }



    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinished(List<ContentHeaderModel> items) {

        if ( items.size() > 0 ) {
            mRvAdapter.addData(items);
            mRvAdapter.loadMoreComplete();
        } else {
            if ( emptyView != null ) {
                mRvAdapter.setEmptyView(emptyView);
            }
            mRvAdapter.loadMoreEnd(false);
            mRvAdapter.enableLoadMoreEndClick(true);
        }

        hideLoading();
    }

}
