package com.ddastudio.hifivefootball_android.board;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoardListFragment extends BaseFragment {

    @BindView(R.id.contents_list)
    RecyclerView rvBoardList;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    View emptyFooterView;

    BoardListPresenter mPresenter;
    BoardListRvAdapter mRvAdapter;



    public BoardListFragment() {
        // Required empty public constructor
    }

    public static BoardListFragment newInstance() {

        return new BoardListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        // 프리젠터 생성
        mPresenter = new BoardListPresenter();
        mPresenter.attachView(this);

        initEmptyView();
        // 리사이클러뷰 초기화
        initRecyclerView();
        // 스와이프 리프레쉬 초기화
        initSwipeRefresh();

        // 게시판 목록 상배 변화를 감지한다.
        mPresenter.getDBBoardList().observe(this, boards -> {
            if ( boards != null && boards.size() > 0) {
                mRvAdapter.onNewData(boards);
            } else {
                mPresenter.getBoardList(true);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

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

    /**
     * 스와이프 리프레쉬 초기화
     */
    public void initSwipeRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () ->
            // 새로고침을 하면 네트워크로 데이터를 가져와 룸 디비에 데이터를 넣는다.
            mPresenter.getBoardList(true)
        );
    }


    /*----------------------------------------
     * 엠프티뷰 생성
     *----------------------------------------*/
    private void initEmptyView() {

        // 라사이클뷰 하단에 빈공간을 삽입한다. - fab 중복을 피하기 위해서
        emptyFooterView = getActivity().getLayoutInflater().inflate(R.layout.row_empty_footer_view, (ViewGroup)rvBoardList.getParent(), false);
    }

    /**
     * RecyclerView 초기화
     */
    private void initRecyclerView() {

        mRvAdapter = new BoardListRvAdapter(new ArrayList<>());
        mRvAdapter.addFooterView(emptyFooterView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBoardList.setLayoutManager(linearLayoutManager);
        rvBoardList.setAdapter(mRvAdapter);

        // 디바이더
        rvBoardList.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 0));

        // --- 로우 클릭 리스너 ---
        mRvAdapter.setOnItemClickListener((adapter, view, position) -> {

            BoardMasterModel board = mRvAdapter.getData().get(position);

            if ( board.getBoardId() == 450) {
                // 눈팅 베스트 선택시 눈팅베스트 액티비티를 실행한다.
                mPresenter.openNuntingActivity();
            } else {
                // 1. ContentListContainerFragment에서 뷰페이저 게시글 탭으로 이동시키기 위한 이벤트 발생
                App.getInstance().bus().send(new ContentListEvent.SelectedBoardEvent(board));
                // 2. 디비에 현재 선택한 게시판으로 업데이트
                AppDatabase.getInstance(getActivity()).boardsDao().clearAndUpdateSelectedBoard(board.getBoardId(), 0);
            }
        });

        // --- 차일드 뷰 클릭 리스너 ---
        mRvAdapter.setOnItemChildClickListener(((adapter, view, position) -> {

            if ( view.getId() == R.id.btn_best_content) {

                // 베스트 버튼 클릭시 베스트 글을 보여준다.
                BoardMasterModel board = mRvAdapter.getData().get(position);

                // 1. ContentListContainerFragment에서 뷰페이저 게시글 탭으로 이동시키기 위한 이벤트 발생
                App.getInstance().bus().send(new ContentListEvent.SelectedBoardEvent(board));
                // 2. 디비에 현재 선택한 게시판으로 업데이트
                AppDatabase.getInstance(getActivity()).boardsDao().clearAndUpdateSelectedBoard(board.getBoardId(), 1);
            }
        }));

    }

}
