package com.ddastudio.hifivefootball_android.content_list;


import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.content_list.model.BestContentsHeaderModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.content_report.ContentReportFragment;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.data.event.UserAccountEvent;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.fragment.LeagueTableDialogFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.utils.EndlessRecyclerViewScrollListener;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import im.ene.toro.widget.Container;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 *
 * Created      -       onAttatch()
 *              -       oncreate()
 *              -       onCreateView()
 *              -       onActivityCreated()
 * Started      -       onStart()
 * Resumed      -       onResume()
 * Paused       -       onPause()
 * Stopped      -       onStop()
 * Destroyed    -       onDestroyView()
 *              -       onDestroy()
 *              -       onDetach()
 */
public class ContentListFragment extends BaseFragment {

    public static final int REQUEST_CONTENT_VIEWER = 10001;
    private static final int LIMIT = 15;

    @BindView(R.id.fl_main) FrameLayout flMain;
    @BindView(R.id.contents_list) Container rvContentsList;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    View emptyView;
    View emptyFooterView;

    long mLastClickTime = 0;

    ContentsListPresenter mPresenter;
    ContentListRvAdapter3 mRvAdapter3;

    LinearLayoutManager mLayoutManager;
    EndlessRecyclerViewScrollListener scrollListener;

    // 현재 선택된 아이템
    ChatAndAttributeModel selectedItem;

    /**
     * 생성자
     */
    public ContentListFragment() {
        // Required empty public constructor
    }

    public static ContentListFragment newInstance(/*BoardMasterModel board*/) {

        ContentListFragment fragment = new ContentListFragment();
        return fragment;
    }


    /*----------------------------------------
     * [onCreate]
     *  - 전달된 파라미터 처리를 한다.
     * @param savedInstanceState
     *----------------------------------------*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 프래그먼트에서 옵션 메뉴 사용하기
        //setHasOptionsMenu(true);
        mPresenter = new ContentsListPresenter();
    }


    /*----------------------------------------
     * [onCreateView]
     *  - 뷰 생성
     *  - 뷰 초기화
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     *----------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content_list, container, false);
        _unbinder = ButterKnife.bind(this, view);
        mPresenter.attachView(this);

        initEmptyView();
        initRecyclerView3();
        initRefresh();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 챗리스트 데이터 변화를 관촬한다.
        LiveData<ChatAndAttributeModel> footballChat = AppDatabase.getInstance(getActivity()).chatDao().loadSelectedChatAndAttr();
        footballChat.observe(this, item -> {
            if ( item != null ) {
                selectedItem = item;
                mPresenter.onLoadData(item.getMentionType(), item.getMentionId(), 15, 0);
            }
        });
    }

    /*----------------------------------------
     * [onDestroyView]
     *  - 프리젠터 정리작업 호출
     *----------------------------------------*/
    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }


    /*---------------------------------------------------------------------------------------------*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case REQUEST_CONTENT_VIEWER:

                ContentListEvent.UpdateContentEvent event
                        = Parcels.unwrap(data.getParcelableExtra("RETURN_CONTENT_EVENT"));

                if ( mRvAdapter3 != null ) {

                    if ( event.isDelete()) {
                        mRvAdapter3.removeItem(event.getPosition());
                    } else {
                        ContentHeaderModel contentHeader = mRvAdapter3.getItem(event.getPosition());
                        contentHeader.setDeleted(event.isDelete());
                        contentHeader.setLiked(event.isLike());
                        contentHeader.setLikers(event.getLikeCount());
                        contentHeader.setScraped(event.isScrap());
                        contentHeader.setScraps(event.getScrapCount());
                        contentHeader.setComments(event.getCommentCount());
                        mRvAdapter3.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
        //Log.i("hong", "onEvent");

        if (event instanceof UserAccountEvent.SignIn) {
            refreshData();
        } else if (event instanceof UserAccountEvent.SignOut) {
            refreshData();
        } else if ( event instanceof ContentListEvent.RefreshListEvent) {

            //Log.i("hong", "RefreshListEvent");
            ContentListEvent.RefreshListEvent eventData = (ContentListEvent.RefreshListEvent)event;
            // 건의/개선 관련 글 작성시에는 새로고침 하지 않는다.
            // 건의/개선창은 IssueActvity이기 대문에 여기서는 새로기침할 필요가 없다.
            if ( eventData.getBoardid() == PostBoardType.ISSUE) {
                return;
            }

            refreshData();
        }
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

    /*----------------------------------------
     * 새로고침
     *----------------------------------------*/
    public void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> refreshData());
    }


    /*----------------------------------------
     * 엠프티뷰 생성
     *----------------------------------------*/
    private void initEmptyView() {

        // 라사이클뷰 하단에 빈공간을 삽입한다. - fab 중복을 피하기 위해서
        emptyFooterView = getActivity().getLayoutInflater().inflate(R.layout.row_empty_footer_view, (ViewGroup)rvContentsList.getParent(), false);
        emptyView = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.recycler_empty_view, (ViewGroup) rvContentsList.getParent(), false);
    }


    private void initRecyclerView3() {

        mRvAdapter3 = new ContentListRvAdapter3(getActivity());
        mLayoutManager = new LinearLayoutManager(getContext());
        rvContentsList.setLayoutManager(mLayoutManager);
        rvContentsList.setAdapter(mRvAdapter3);

        // 성능향상
        rvContentsList.setHasFixedSize(true);

        //-----------------------------------------
        // EndlessScroll
        //-----------------------------------------
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if ( selectedItem != null ) {
                    mPresenter.onLoadData(selectedItem.getMentionType(), selectedItem.getMentionId(), LIMIT, totalItemsCount);
                }
            }
        };

        // Adds the scroll listener to RecyclerView
        rvContentsList.addOnScrollListener(scrollListener);

        //-----------------------------------------
        // 클릭 리스너
        //-----------------------------------------
        // Setup the click listener
        mRvAdapter3.setOnItemClickListener(new ContentListRvAdapter3.OnItemClickListener() {

            // 아이템 클릭
            @Override
            public void onItemClick(View itemView, int position) {
                ContentHeaderModel item = mRvAdapter3.getItem(position);
                onRvClickPostList(item, position);
            }

            // 아바타 클릭
            @Override
            public void onAvatarClick(View itemView, int position) {
                ContentHeaderModel item = mRvAdapter3.getItem(position);
                onRvClickAvatar(item.getUser());
            }

            // 댓글버튼 클릭
            @Override
            public void onCommentClick(View itemView, int position) {
                ContentHeaderModel item = mRvAdapter3.getItem(position);
                onRvClickShowComment(item);
            }

            // 스크랩 버튼 클릭
            @Override
            public void onScrapClick(View itemView, int position) {
                ContentHeaderModel item = mRvAdapter3.getItem(position);
            }

            // 하이파이브 버튼 클릭
            @Override
            public void onHifiveClick(View itemView, int position) {
                ContentHeaderModel item = mRvAdapter3.getItem(position);
                onRvClickShowHifive(item);
            }

            @Override
            public void onMoreClick(View itemView, int position) {
                ContentHeaderModel item = mRvAdapter3.getItem(position);
                onRvClickMore(itemView, item);
            }
        });
    }


    /*---------------------------------------------------------------------------------------------*/


    /*----------------------------------------
     * 스크롤 맨 위로 올리기 버튼 클릭
     *----------------------------------------*/
    @OnClick(R.id.fab_up)
    public void onScrollTopClick() {

        if ( mRvAdapter3 != null && mRvAdapter3.getItemCount() > 0 ) {
            // 맨 위로 올린다.
            //rvContentsList.smoothScrollToPosition(0);
            rvContentsList.scrollToPosition(0);
        }
    }


    /*----------------------------------------
     * 게시글 클릭
     * @param item
     * @param position
     *----------------------------------------*/
    private void onRvClickPostList(ContentHeaderModel item, int position) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        int bodyType = item.getBodytype();

        if (PostBodyType.toEnum(bodyType) == PostBodyType.LINK) {
            mPresenter.openLinkActivity(item.getLink());
        } else {
            mPresenter.openContentViewActivity(item/*, mPostType*/, position);
        }
    }

    /**
     * 아바타 클릭
     * @param user
     */
    private void onRvClickAvatar(UserModel user) {
        mPresenter.openUserProfile(user);
    }


    /*----------------------------------------
     * 댓글보기 아이콘 클릭
     * @param item
     *----------------------------------------*/
    private void onRvClickShowComment(ContentHeaderModel item) {
        if ( item.getComments() > 0) {
            mPresenter.openCommentDialogFragment(item.getContentId());
        } else {
            showMessage("등록된 댓글이 없습니다");
        }
    }


    /*----------------------------------------
     * 하이파이브 텍스트뷰 클릭
     * @param item
     *----------------------------------------*/
    private void onRvClickShowHifive(ContentHeaderModel item) {
        if ( item.getLikers() > 0) {
            mPresenter.openHifiveDialogFragment(item.getContentId());
        } else {
            Toasty.info(getContext(), "좋아요는 0이네요", Toast.LENGTH_LONG).show();
        }
    }


    /*----------------------------------------
     * 더보기 메뉴 클릭
     * @param view
     * @param content
     *----------------------------------------*/
    private void onRvClickMore(View view, ContentHeaderModel content) {

        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.inflate(R.menu.menu_rv_content_list);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.menu_report_content:
                        openContentReportBottomSheetFragment(content);
                        return true;

                    case R.id.menu_show_content_id:
                        new MaterialDialog.Builder(getContext())
                                .title("매치아이디")
                                .content(String.format("content_id : %d\nboard_id : %d", content.getContentId(), content.getBoardId()))
                                .positiveText("확인")
                                .show();
                        break;
                }

                return false;
            }
        });

        popup.show();
    }

    private void openContentReportBottomSheetFragment(ContentHeaderModel content) {

        if ( !App.getAccountManager().isAuthorized() ) {
            showLoginSnackBar();
            return;
        }

        ContentReportFragment fragment = ContentReportFragment.newInstance(content);
        fragment.show(getActivity().getSupportFragmentManager(), "ContentReport");
    }

    public void showLoginSnackBar() {


        Snackbar snackbar = Snackbar.make(((MainActivity)getActivity()).getContainerView(), getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG);
        View v = snackbar.getView();
//        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
//        params.setMargins(params.leftMargin,
//                params.topMargin,
//                params.rightMargin,
//                params.bottomMargin + CommonUtils.dp(54));

        snackbar.setAction("이동", va -> mPresenter.openLoginActivity())
                .show();
    }


    /**
     * 스크랩 아이콘 클릭
     * @param item
     */
    private void onRvClickShowScrap(ContentHeaderModel item) {
        if ( item.getLikers() > 0) {
            mPresenter.openHifiveDialogFragment(item.getContentId());
        } else {
            Toasty.info(getContext(), "좋아요는 0이네요", Toast.LENGTH_LONG).show();
        }
    }

    /*---------------------------------------------------------------------------------------------*/


    /**
     * 새로운 데이터 가져오기
     */
    public void refreshData() {

        if ( selectedItem != null ) {
            mPresenter.onLoadData(selectedItem.getMentionType(), selectedItem.getMentionId(), LIMIT, 0);
        }

    }


    /*----------------------------------------
     * 게시글 로링 완료
     * @param items
     * @param isLoadMore
     *----------------------------------------*/
    public void onLoadFinished3(List<ContentHeaderModel> items, boolean isLoadMore) {

        if ( isLoadMore == false ) {
            mRvAdapter3.onNewData(items);
            rvContentsList.scrollToPosition(0);
        } else {
            mRvAdapter3.addItems(items);
        }

        hideLoading();
    }

}
