package com.ddastudio.hifivefootball_android.team_list;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.manager.TeamRepository;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.utils.GridLineDividerDecoration;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamListFragment extends BaseFragment {

    //
    //
    //
    public static int TYPE_OVERALL_TEAM_LIST = 1300;
    //
    // 팀(클럽) 글쓰기 요청 ID
    //
    public static final int REQUEST_EDITOR = 20000;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_team_list) RecyclerView rvTeamList;

    int mOffset = 0;
    View notDataView;
    EditText etAfterName;
    TeamListPresenter mPresenter;
    TeamListRvAdapter mRvAdapter;

    //
    // 뷰모델 ( 테스트중 )
    //
    TeamListViewModel teamListViewModel;

    public TeamListFragment() {
        // Required empty public constructor
    }

    /**
     * TeamListFragment 프래그먼트 객체 생성
     * @param type
     * @return
     */
    public static TeamListFragment newInstance(int type) {
        TeamListFragment fragmentFirst = new TeamListFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_DATA_TYPE", type);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_team_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        int type = getArguments().getInt("ARGS_DATA_TYPE");
        mPresenter = new TeamListPresenter(type);
        mPresenter.attachView(this);

        // 프래그먼트에서 옵션 메뉴 사용하기
        setHasOptionsMenu(true);

        // 리프레쉬 초기화
        initRefresh();

        // 라사이클뷰 초기화
        initRecyclerView();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //initViewModel();
        mPresenter.onLoadTeamList(15, mOffset);
    }


    @Override
    public void onResume() {
        super.onResume();
        // destroy all menu and re-call onCreateOptionsMenu
        //getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 메뉴를 인플레이트한다.
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_player_list_fragment, menu);
    }

    /**
     * 메뉴를 선택했을때 이벤트
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            //
            // 1. 정렬
            //
            case R.id.action_sort:
                break;
            //
            // 2. 필터
            //
            case R.id.action_filter:
                break;
            //
            // 3. 리스트 전환
            //
            case R.id.action_view_type_basic:
                showMessage("R.id.action_view_type_basic");
                if ( mRvAdapter != null ) {
                    rvTeamList.setLayoutManager(new LinearLayoutManager(getContext()));
                    removeItemDecoration();
                    rvTeamList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
                    mRvAdapter.setViewMode(TeamListRvAdapter.BASIC);
                    rvTeamList.setAdapter(mRvAdapter);
                    //mRvAdapter.notifyDataSetChanged();
                }
                break;
            //
            // 4. 그리드 전환
            //
            case R.id.action_view_type_grid:
                showMessage("R.id.action_view_type_grid");
                if ( mRvAdapter != null ) {
                    final GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
                    removeItemDecoration();
                    rvTeamList.addItemDecoration(new GridLineDividerDecoration(getContext()));
                    rvTeamList.setLayoutManager(manager);
                    mRvAdapter.setViewMode(TeamListRvAdapter.GRID);
                    rvTeamList.setAdapter(mRvAdapter);
                    //mRvAdapter.notifyDataSetChanged();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 이벤트
     * @param event
     */
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
        swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * 로딩화면 감추기
     */
    @Override
    public void hideLoading() {

        swipeRefreshLayout.setRefreshing(false);
    }

    /*---------------------------------------------------------------------------------------------*/


    private void initViewModel() {

        TeamRepository dataSource = TeamRepository.getInstance();
        TeamListViewModelFactory factory = new TeamListViewModelFactory(dataSource);

        teamListViewModel = ViewModelProviders.of(getActivity(), factory).get(TeamListViewModel.class);
        teamListViewModel.getTeams().observe(this, temas -> {

            if ( temas == null ) {
                return;
            }

            //mRvAdapter.getData().clear();
            mRvAdapter.addData(temas);
            mRvAdapter.loadMoreComplete();
        });


    }

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

            mOffset = 0;
            mPresenter.onLoadTeamList(15, mOffset);
        });
    }

    /**
     * 리사이클뷰 초기화
     */
    private void initRecyclerView() {

        // 데이터가 없을 때 화면에 표시할 뷰 생성
        notDataView = getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvTeamList.getParent(), false);

        mRvAdapter = new TeamListRvAdapter(new ArrayList<>());
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);

        rvTeamList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTeamList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvTeamList.setAdapter(mRvAdapter);

        //
        // 추가 데이터 불러오기
        //
        mRvAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.onLoadTeamList(15, mOffset);
            }
        });

        //
        // 로우 클릭 리스너
        //
        mRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                onRvClickItem((TeamModel)mRvAdapter.getData().get(position));
            }
        });

        //
        // 로우안에 있는 뷰 클릭 리스너
        //
        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()) {

                    case R.id.iv_more_item:
                        onRvClickMoreItem(view, (TeamModel)mRvAdapter.getData().get(position));
                        break;
                }
            }
        });
    }


    /**
     * 로우 클릭
     * @param team 팀모델
     */
    private void onRvClickItem(TeamModel team) {

        mPresenter.openTeamActivity(team.getTeamId());
    }


    /**
     * 더보기 메뉴 클릭 ( 리사이클뷰 내부에 있는 메뉴 )
     * @param view
     * @param team
     */
    private void onRvClickMoreItem(View view, TeamModel team) {

        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.inflate(R.menu.menu_rv_team);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    //
                    // 1. 팀명 변경 요청
                    //
                    case R.id.menu_request_change_name:
                        //handle menu1 click
                        //Toast.makeText(getContext(), "menu 1", Toast.LENGTH_LONG).show();

                        MaterialDialog dialog =
                                new MaterialDialog.Builder(getContext())
                                        .title(R.string.title_change_team_name)
                                        .customView(R.layout.dialog_custome_change_name_item, true)
                                        .positiveText(R.string.positive)
                                        .negativeText(R.string.negative)
                                        .onPositive(
                                                (dialog1, which) -> {
                                                    mPresenter.requestChangeTeamName(team.getTeamId(), team.getTeamName(), etAfterName.getText().toString());
                                                }
                                        )
                                        .onNegative(
                                                (dialog1, which) -> {}
                                        )
                                        .build();

                        TextView tvBeforeName = dialog.getCustomView().findViewById(R.id.tv_before_name);
                        etAfterName = dialog.getCustomView().findViewById(R.id.et_after_name);
                        tvBeforeName.setText(team.getTeamName());
                        dialog.show();

                        break;

                    //
                    // 2. 팀정보 최신데이터 가져오기
                    //
                    case R.id.menu_request_update_info:
                        //handle menu2 click
                        break;

                    //
                    // 3. 팀관련 글쓰기
                    //
                    case R.id.menu_team_post_content:
                        //-----------------------------------------
                        // 플레이어 글쓰기
                        //-----------------------------------------
                        mPresenter.openTextEditor(team);
                        break;
                }

                return false;
            }
        });

        popup.show();
    }


    /*---------------------------------------------------------------------------------------------*/

    /**
     * 팀리스트 로드 완료
     * @param teams
     */
    public void onLoadFinishedTeams(List<TeamModel> teams) {

        // --------------------------------------------------------------
        // 1. mOffset 값이 0일 경우는 diffUtil를 이용해서 변경된 값만 업데이트 한다.
        //    - 새로고침
        //    - 처음으로 데이터를 로드할 경우
        // --------------------------------------------------------------
        if ( mOffset == 0 ) {
            mOffset += teams.size();
            mRvAdapter.onNewData(teams);
            hideLoading();
            return;
        }


        // --------------------------------------------------------------
        // 2. 널이거나 데이터가 없으면 EmptyView를 보여주고 리턴
        // --------------------------------------------------------------
        if ( teams == null || teams.size() == 0) {
            mRvAdapter.setEmptyView(notDataView);
            mRvAdapter.loadMoreEnd(true);
            hideLoading();
            return;
        }

        // --------------------------------------------------------------
        // 3. 데이터 바인딩
        // --------------------------------------------------------------
        mOffset += teams.size();
        mRvAdapter.addData(teams);
        mRvAdapter.loadMoreComplete();
        hideLoading();
    }

    /*---------------------------------------------------------------------------------------------*/


    /**
     * RecyclerView의 ItemDecoration을 모두 제거한다.
     * - 옵션메뉴에서 리스트, 그리드 전환시 새로운 아이템 데코레이션을 삽입하기 전
     * - 기존의 ItemDecoration을 제거해야한다.
     */
    private void removeItemDecoration() {

        if ( rvTeamList != null ) {
            int count = rvTeamList.getItemDecorationCount();
            for ( int i = 0; i < count; i++ ) {
                rvTeamList.removeItemDecorationAt(i);
            }
        }
    }

}
