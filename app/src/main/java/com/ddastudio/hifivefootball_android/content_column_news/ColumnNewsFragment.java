package com.ddastudio.hifivefootball_android.content_column_news;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.content_viewer.CommentInputFragment;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;
import com.ddastudio.hifivefootball_android.ui.widget.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColumnNewsFragment extends BaseFragment {

    @BindView(R.id.container) FrameLayout flContainer;
    @BindView(R.id.humor_list) CustomRecyclerView rvHumorList;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    long mLastClickTime = 0;
    ColumnNewsPresenter mPresenter;
    ColumnNewsRvAdapter mRvAdapter;

    public ColumnNewsFragment() {
        // Required empty public constructor
    }

    public static ColumnNewsFragment newInstance(int type) {

        ColumnNewsFragment fragment = new ColumnNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ARGS_FRAGMENT_TYPE", type);
        fragment.setArguments(bundle);

        return fragment;
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int type = getArguments().getInt("ARGS_FRAGMENT_TYPE");
        mPresenter = new ColumnNewsPresenter(type);
        mPresenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_column_news, container, false);
        _unbinder = ButterKnife.bind(this, view);

        if ( mPresenter.getBoardType() == PostBoardType.COLUMN) {
            flContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_grey_200));
        }

        initRecyclerView();
        initRefresh();

        mPresenter.getContentList(30, 0);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        mPresenter.onStart();
//
//        if ( mRvAdapter == null || mRvAdapter.getData().isEmpty()) {
//            mPresenter.getContentList(30, 0);
//        }

    }

    @Override
    public void onStop() {
        super.onStop();
        //mPresenter.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if ( mPresenter != null ) {
            mPresenter.detachView();
        }
    }

    @OnClick(R.id.fab_up)
    public void onScrollTopClick() {

        if ( mRvAdapter != null && mRvAdapter.getItemCount() > 0 ) {
            // 맨 위로 올린다.
            rvHumorList.smoothScrollToPosition(0);
        }
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
        Toasty.normal(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initRecyclerView() {

        List<ContentHeaderModel> itemList = new ArrayList<>();
        mRvAdapter = new ColumnNewsRvAdapter(itemList);
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);
        mRvAdapter.setEnableLoadMore(true);
        mRvAdapter.setOnLoadMoreListener( () ->
                mPresenter.getContentList(30, mRvAdapter.getLoadMoreViewPosition())
        );

        rvHumorList.setLayoutManager(new LinearLayoutManager(getContext()));
        if ( mPresenter.getBoardType() != PostBoardType.COLUMN) {
            rvHumorList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        }
        rvHumorList.setAdapter(mRvAdapter);

        /*---------------------------------------------*/

        mRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ContentHeaderModel content = (ContentHeaderModel)adapter.getItem(position);
                onRvClickContent(content);
            }
        });

        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.humor_comment_container:
                        //ContentHeaderModel content2 = (ContentHeaderModel)adapter.getItem(position);
                        //onRvClickHifive(content2, position);
                        break;
                    case R.id.iv_comment:
                        ContentHeaderModel content = (ContentHeaderModel)adapter.getItem(position);
                        onRvClickShowComment(content);
                        break;
                }
            }
        });

//        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//
//                ContentHeaderModel content = (ContentHeaderModel)adapter.getItem(position);
//
//                switch (view.getId()) {
//                    case R.id.humor_container:
//                    case R.id.column_container:
//                        //Timber.d("[onItemChildClick] position : %d", position);
//                        onRvClickContent(content);
//                    break;
//
//                    case R.id.hifive_container:
//                        //Timber.d("[onItemChildClick] column hifive click");
//                        onRvClickHifive(content, position);
//                        break;
//
//                    case R.id.comment_container:
//                    case R.id.humor_comment_container:
//                        //Timber.d("[onItemChildClick] column comment click");
//                        onRvClickShowComment(content);
//                        break;
//                }
//            }
//        });
    }

    /**
     * 게시글 클릭
     * @param item
     */
    private void onRvClickContent(ContentHeaderModel item) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        try {
            Uri link = Uri.parse(item.getLink());

            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        } catch (Exception ex ) {
            showErrorMessage("올바르지 않은 링크주소입니다\n" + ex.getMessage());
        }
    }

    /**
     * 하아파이브
     * @param item
     */
    private void onRvClickHifive(ContentHeaderModel item, int position) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        mPresenter.setContentLike(item, 1, position);
    }

    /**
     * 댓글창 열기
     * @param item
     */
    private void onRvClickShowComment(ContentHeaderModel item) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        CommentInputFragment commentInputFragment
                = CommentInputFragment.newInstance(CommentInputFragment.COMMENT_TYPE_BASIC, item.getContentId());
        commentInputFragment.show(getFragmentManager(), "CommentInput");
    }

    public void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> refreshData());
    }

    /*---------------------------------------------------------------------------------------------*/

    public void refreshData() {
        mRvAdapter.getData().clear();
        mPresenter.getContentList(30, 0);
    }

    public void onLoadFinished(List<? extends ContentHeaderModel> items) {

        if ( items.size() > 0 ) {
            mRvAdapter.addData(items);
            mRvAdapter.loadMoreComplete();
        } else {
            //mRvAdapter.loadMoreEnd();
            mRvAdapter.loadMoreEnd(false);
            mRvAdapter.enableLoadMoreEndClick(true);
            //mRvAdapter.setLoadMoreView();
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    public void updateHifive(ContentHeaderModel content, int hifive, int position) {

        if ( mRvAdapter != null && mRvAdapter.getData().size() > 0 ) {

            content.setLiked(true);
            content.addLikes();
            mRvAdapter.setData(position, content);
            mRvAdapter.notifyItemChanged(position);
        }
    }
}
