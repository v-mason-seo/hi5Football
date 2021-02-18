package com.ddastudio.hifivefootball_android.player_list;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.player_comment.PlayerCommentListDialogFragment;
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
public class PlayerListFragment extends BaseFragment {

    public static int TYPE_OVERALL_PLAYER_LIST = 1200;
    public static int TYPE_TEAM_PLAYER_LIST = 1210;

    // 플레이어 글쓰기 요청 ID
    public static final int REQUEST_EDITOR = 20000;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_player_list) RecyclerView rvPlayerList;

    int mOffset = 0;
    View notDataView;
    PlayerListPresenter mPresenter;
    PlayerListRvAdapter mRvAdapter;

    public PlayerListFragment() {
        // Required empty public constructor
    }

    public static PlayerListFragment newInstance(int type) {
        PlayerListFragment fragmentFirst = new PlayerListFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_DATA_TYPE", type);
        args.putInt("ARGS_TEAM_ID", -1);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public static PlayerListFragment newInstance(int type, int teamId) {
        PlayerListFragment fragmentFirst = new PlayerListFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_DATA_TYPE", type);
        args.putInt("ARGS_TEAM_ID", teamId);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_player_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        mOffset = 0;
        int type = getArguments().getInt("ARGS_DATA_TYPE");
        int teamId = getArguments().getInt("ARGS_TEAM_ID", -1);
        if ( teamId != -1) {
            mPresenter = new PlayerListPresenter(type, teamId);
        } else {
            mPresenter = new PlayerListPresenter(type);
        }

        mPresenter.attachView(this);


        // 프래그먼트에서 옵션 메뉴 사용하기
        //setHasOptionsMenu(true);
        initRefresh();
        initRecyclerView();

        if ( mPresenter.getType() == TYPE_TEAM_PLAYER_LIST ) {
            mPresenter.onLoadTeamPlayers();
        } else {
            mPresenter.onLoadPlayerList(15, mOffset);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // destroy all menu and re-call onCreateOptionsMenu
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_player_list_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_sort:
                break;
            case R.id.action_filter:
                break;
        }

        return super.onOptionsItemSelected(item);
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
            if ( mPresenter.getType() == TYPE_TEAM_PLAYER_LIST ) {
                mPresenter.onLoadTeamPlayers();
            } else {
                mPresenter.onLoadPlayerList(15, mOffset);
            }
        });
    }


    /**
     * 리사이클뷰 초기화
     */
    private void initRecyclerView() {

        notDataView = getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvPlayerList.getParent(), false);

        List<PlayerModel> itemList = new ArrayList<>();
        mRvAdapter = new PlayerListRvAdapter(itemList);
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);

        rvPlayerList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlayerList.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 54));
        rvPlayerList.setAdapter(mRvAdapter);

        if ( mPresenter.getType() != TYPE_TEAM_PLAYER_LIST ) {
            mRvAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    if ( mPresenter.getType() == TYPE_TEAM_PLAYER_LIST ) {
                        mPresenter.onLoadTeamPlayers();
                    } else {
                        mPresenter.onLoadPlayerList(15, mOffset);
                    }
                }
            });
        }

        mRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                onRvClickItem((PlayerModel)mRvAdapter.getData().get(position));
            }
        });

        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.comment_box:
                    case R.id.iv_comment_icon:
                        onRvClickComment((PlayerModel)mRvAdapter.getData().get(position));
                        break;
                    case R.id.iv_more_item:
                        onRvClickMoreItem(view, (PlayerModel)mRvAdapter.getData().get(position));
                        break;
                }
            }
        });
    }

    EditText etAfterName;

    /**
     * 더보게 메뉴 클릭
     * @param view
     * @param player
     */
    private void onRvClickMoreItem(View view, PlayerModel player) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.inflate(R.menu.menu_rv_player);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_request_change_name:
                        //-----------------------------------------
                        // 플레이어 이름 변경 요청
                        //-----------------------------------------
                        Toast.makeText(getContext(), "menu_request_change_name", Toast.LENGTH_LONG).show();

                        MaterialDialog dialog =
                                new MaterialDialog.Builder(getContext())
                                        .title(R.string.title_change_player_name)
                                        .customView(R.layout.dialog_custome_change_name_item, true)
                                        .positiveText(R.string.positive)
                                        .negativeText(R.string.negative)
                                        .onPositive(
                                                (dialog1, which) -> {
                                                    mPresenter.requestChangePlayerName(player.getPlayerId(), player.getPlayerName(), etAfterName.getText().toString());
                                                }
                                        )
                                        .onNegative(
                                                (dialog1, which) -> {}
                                        )
                                        .build();

                        TextView tvBeforeName = dialog.getCustomView().findViewById(R.id.tv_before_name);
                        etAfterName = dialog.getCustomView().findViewById(R.id.et_after_name);
                        tvBeforeName.setText(player.getPlayerName());
                        dialog.show();

                        break;
                    case R.id.menu_request_update_info:
                        //-----------------------------------------
                        // 플레이어 최신정보 업데이트 요청
                        //-----------------------------------------
                        Toast.makeText(getContext(), "menu_request_update_info", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.menu_player_post_content:
                        //-----------------------------------------
                        // 플레이어 글쓰기
                        //-----------------------------------------
                        Toast.makeText(getContext(), "menu_player_post_content", Toast.LENGTH_LONG).show();
                        mPresenter.openTextEditor(player);
                        break;
                }
                return false;
            }
        });

        popup.show();
    }

    private void onRvClickComment(PlayerModel player) {

        if ( player.getCommentCount() == 0 ) {
            showMessage("한줄평 데이터가 없습니다.");
            return;
        }

        PlayerCommentListDialogFragment
                .newInstance(player.getPlayerId())
                .show(getFragmentManager(), "player_comment_list_dialog");
    }

    private void onRvClickItem(PlayerModel player) {

        mPresenter.openPlayerActivity(player.getPlayerId(), player.getPlayerName());
    }

    /*---------------------------------------------------------------------------------------------*/


    public void onLoadFinishedPlayers(List<PlayerModel> players) {

        // --------------------------------------------------------------
        // 1. mOffset 값이 0일 경우는 diffUtil를 이용해서 변경된 값만 업데이트 한다.
        //    - 새로고침
        //    - 처음으로 데이터를 로드할 경우
        // --------------------------------------------------------------
        if ( mOffset == 0 ) {
            mOffset += players.size();
            mRvAdapter.onNewData(players);
            hideLoading();
            return;
        }

        // --------------------------------------------------------------
        // 2. 널이거나 데이터가 없으면 EmptyView를 보여주고 리턴
        // --------------------------------------------------------------
        if ( players == null || players.size() == 0) {
            mRvAdapter.setEmptyView(notDataView);
            mRvAdapter.loadMoreEnd(true);
            hideLoading();
            return;
        }

        // --------------------------------------------------------------
        // 3. 데이터 바인딩
        // --------------------------------------------------------------
        mOffset += players.size();
        mRvAdapter.addData(players);
        mRvAdapter.loadMoreComplete();
        hideLoading();
    }

    /*---------------------------------------------------------------------------------------------*/
}
