package com.ddastudio.hifivefootball_android.content_viewer;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.content_report.ContentReportFragment;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.data.event.ContentViewerEvent;
import com.ddastudio.hifivefootball_android.data.event.UserAccountEvent;
import com.ddastudio.hifivefootball_android.data.model.CommentInputModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.community.MatchTalkModel;
import com.ddastudio.hifivefootball_android.data.model.community.SimpleSectionHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.content.ContentRelationModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.player_comment.PlayerCommentRvAdapter;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.ui.utils.ContentViewerDividerItemDecoration;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;
import com.ddastudio.hifivefootball_android.ui.widget.CustomRecyclerView;
import com.ddastudio.hifivefootball_android.ui.widget.ObservableWebView;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import timber.log.Timber;

// TODO: 2017. 11. 29. 1. 스크롤 탑으로 올리는 기능 구현
// TODO: 2017. 11. 29. 2. 하이파이브 +30 까지 증가하는 기능 구현
// TODO: 2017. 11. 29. 3. 웹뷰 이미지 저장 기능
// TODO: 2017. 11. 29. 4. 광고 보이니 감추기 마진 설정
// TODO: 2017. 11. 29. 5. 유튜브 체크
public class ContentViewerActivity extends BaseActivity {

    // TODO: 2017. 9. 18.  1. 스크롤 하기전 fab 클릭시 아래로 이동하는 문제
    // TODO: 2017. 9. 18. 2. fab behavior 문제

    @BindView(R.id.container) RelativeLayout relativeLayout;
    @BindView(R.id.content_viewer_coordinator) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.post_content_view_appbar) AppBarLayout mAppBarLayout;
    @BindView(R.id.content_view_toolbar) Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.content_viewer_scroll) NestedScrollView mNestedScrollView;
    @BindView(R.id.ll_main) LinearLayout llMain;
    @BindView(R.id.content_view_title) TextView mTitle;
    //
    // 매치정보
    //
    @BindView(R.id.cv_match) CardView cvMatch;
    @BindView(R.id.tv_match_header) TextView tvMatchHeader;
    @BindView(R.id.tv_home_team_name) TextView tvHomeTeamName;
    @BindView(R.id.tv_away_team_name) TextView tvAwayTeamName;
    @BindView(R.id.tv_home_team_score) TextView tvHomeTeamScore;
    @BindView(R.id.tv_away_team_score) TextView tvAwayTeamScore;
    @BindView(R.id.iv_home_emblem) ImageView ivHomeEmblem;
    @BindView(R.id.iv_away_emblem) ImageView ivAwayEmblem;
    //
    // 하이파이브 스크랩
    //
    @BindView(R.id.hifive_count) TextView tvHifiveCount;
    @BindView(R.id.btn_undo_hifive) Button btn_undo_hifive;
    //
    // 태그정보
    //
    @BindView(R.id.fbx_tag) FlexboxLayout fbxTags;
    //
    // 글작성자 정보
    //
    @BindView(R.id.user_name) TextView tvUserName;
    @BindView(R.id.iv_avatar) ImageView ivAvatar;
    //
    // 리싸이클뷰
    //
    @BindView(R.id.rv_content) CustomRecyclerView rvContent;
    @BindView(R.id.rv_comment) CustomRecyclerView rvComment;
    @BindView(R.id.rv_ratings) CustomRecyclerView rvRatings;

    @BindView(R.id.card_content_container) CardView cvContentContainer;

    @BindView(R.id.fab_hifive) FloatingActionButton mFabHifive;
    @BindView(R.id.fab_scroll_up_content) FloatingActionButton mFabScrollUp;
    @BindView(R.id.content_webview) ObservableWebView mContentWebview;
    @BindView(R.id.content_textview) TextView mContentTextView;

    @BindString(R.string.permission_request_denied) String mPermissionDenied;
    @BindString(R.string.download_success_image) String mSuccessImage;
    @BindString(R.string.download_fail_image) String mFailImage;

    View noCommentsView;
    TextView tvCommentsCount;
    Button footer_login_button;
    LinearLayout footerContainer;

    Menu mMenu;
    ContentViewerPresenter mPresenter;
    int mWebviewLeftPositionOffset = 0;
    long mLastClickTime = 0;
    int mPosition;
    PlayerCommentRvAdapter mRvMatchReport;
    RelationContentRvAdapter mRelationContentRvAdapter;
    CommentRvAdapter mCommentRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_content_viewer)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener(new SwipeBack.OnInterceptMoveEventListener() {
                    @Override
                    public boolean isViewDraggable(View v, int delta, int x, int y) {
                        return v == mContentWebview && (mWebviewLeftPositionOffset > 0);
                    }
                });
        ButterKnife.bind(this);

        // 넘어온 파라미터
        mPosition =  getIntent().getIntExtra("ARGS_POSITION", -1);
        ContentHeaderModel contentHeader = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_CONTENTS_HEADER_MODEL"));

        if ( contentHeader == null ) {
            showErrorMessage("데이터가 올바르게 넘어오지 않았습니다 액티비티를 종료합니다.");
            finish();
        }

        mPresenter = new ContentViewerPresenter(contentHeader);
        mPresenter.attachView(this);

        initToolbar();
        initRefresh();
        initWebView();
        initRelationContentRecyclerView();
        initCommentRecyclerView();
        initRatingRecyclerView();
        initKeyboardStateObserver();

        bindHifiveNScrap(contentHeader);
        bindTags(contentHeader.getTags());
        bindUser(contentHeader.getUser(), contentHeader.getCreatedString());
        updateUI(contentHeader);
        //
        // 데이터 조회
        //
        mPresenter.onLoadData(contentHeader.getContentId());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ( mContentWebview != null )
            mContentWebview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if ( mContentWebview != null )
            mContentWebview.onPause();
    }

    @Override
    protected void onDestroy() {

        if ( mContentWebview != null ) {
            mContentWebview.removeAllViews();
            mContentWebview.destroy();
        }

        mPresenter.detachView();
        super.onDestroy();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 액티비티 종료 - 게시글 리스트에서 변경된 사항을 적용하기 위해서 변경된 정보를 전달한다.
     */
    @Override
    public void finish() {

        Intent intent = new Intent();

        if ( mPosition != -1 ) {

            ContentListEvent.UpdateContentEvent event
                    = new ContentListEvent.UpdateContentEvent(mPresenter.getContentData().getContentId(),
                    ContentListEvent.UpdateContentEvent.SUCCESS,
                    mPosition,
                    mPresenter.getContentData().isDeleted(),
                    mPresenter.getContentData().isLiked(),
                    mPresenter.getContentData().getLikers(),
                    mPresenter.getContentData().isScraped(),
                    mPresenter.getContentData().getScraps(),
                    mPresenter.getContentData().getComments());

            intent.putExtra("RETURN_CONTENT_EVENT", Parcels.wrap(event));
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(Activity.RESULT_CANCELED, intent);
        }

        super.finish();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // *** 내글관련 메뉴 ***
        if ( App.getAccountManager().isAuthorized() ) {
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_report).setVisible(true);
        }

        // 스크랩여부 업데이트
        if ( mPresenter != null
                && mPresenter.getContentData() != null
                && mPresenter.getContentData().isScraped()) {
            updateScrap(true);
        } else {
            updateScrap(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content_viewer_activity, menu);
        this.mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        int id = item.getItemId();

        switch (id) {
            case R.id.action_scrap:
                mPresenter.updateScrap();
                break;
            case R.id.action_edit:
                mPresenter.openTextEditor();
                break;
            case R.id.action_delete:
                // 게시글 삭제
                mPresenter.deleteContent();
                break;
            case R.id.action_report:
                openContentReportedDialog();
                break;
            case R.id.home:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 스크롤 올리기 버튼 ( FAB )
     */
    @OnClick(R.id.fab_scroll_up_content)
    public void onScrollUpClick() {

        // 연속으로 버튼 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        mNestedScrollView.scrollTo(0,0);
    }

    /**
     * 하이파이브 ( 좋아요 )
     * @param view
     */
    @OnClick(R.id.fab_hifive)
    public void onClickHifive(View view) {

        // 연속으로 버튼 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        mPresenter.setContentLike(1);
    }

    /**
     * 하아피이브 취소
     * todo
     *  - 하이파이브를 3번 하고 취소를 눌렀을 때 0으로 됨, 내가 몇번을 했는지 저장을 하고 있어야 함.
     * @param view
     */
    @OnClick(R.id.btn_undo_hifive)
    public void onClickUndoHifive(View view) {

        // 연속으로 버튼 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        mPresenter.setContentUnLike();
    }

    @OnClick(R.id.iv_avatar)
    public void onClickAvatar() {
        onRvClickAvatar(mPresenter.getContentData().getUser());
    }
    /*---------------------------------------------------------------------------------------------*/

    /**
     * 이벤트 처리
     * @param event 이벤트 타입 정보
     */
    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if ( event instanceof ContentViewerEvent.PostCommentEvent) {
            // --- 댓글쓰기 이벤트 ( CommentInputFragment 에서 전달 대댓글 관련 ) ---
            CommentInputModel commentInputData
                    = ((ContentViewerEvent.PostCommentEvent) event).getCommentModel();

            mPresenter.createComment(commentInputData.getParentCommentId(),
                    commentInputData.getParentGroupId(),
                    commentInputData.getDepth(),
                    commentInputData.getContent(),
                    ((ContentViewerEvent.PostCommentEvent) event).getScrollPosition());

        } else if (event instanceof UserAccountEvent.SignIn) {
            // --- 로그인 ---
            updateFooterView();
            showMessage("로그인 되었습니다.");

        } else if ( event instanceof UserAccountEvent.SignError) {
            // --- 회원가입 또는 로그인 오류 ---
            UserAccountEvent.SignError signError = (UserAccountEvent.SignError)event;
            showErrorMessage("로그인을 실패했습니다.\n" + signError.getMessage());
        }
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

        Toasty.normal(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getApplicationContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    public void showLoginSnackBar() {

        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG);
        View v = snackbar.getView();
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
        params.setMargins(params.leftMargin,
                params.topMargin,
                params.rightMargin,
                params.bottomMargin + CommonUtils.dp(54));

        snackbar.setAction("이동", va -> mPresenter.openLoginActivity())
                .show();
    }

    /**
     * 게시글 신고하기 메뉴 클릭
     */
    public void openContentReportedDialog() {

        if ( !App.getAccountManager().isAuthorized() ) {
            showLoginSnackBar();
            return;
        }

        ContentReportFragment fragment = ContentReportFragment.newInstance(mPresenter.getContentData());
        fragment.show(getSupportFragmentManager(), "ContentReport");

    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 광고 초기화
     */
    private void initAdmob() {

        //adView.loadAd();
    }

    /**
     * 툴바 초기화
     */
    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
    }

    public void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> {

            rvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshData();
                }
            }, 200);
        });
    }

    /**
     * 새로고침
     */
    public void refreshData() {

        if ( mRelationContentRvAdapter != null
                && mRelationContentRvAdapter.getData() != null
                && mRelationContentRvAdapter.getData().size() > 0 ) {
            mRelationContentRvAdapter.getData().clear();
            mRelationContentRvAdapter.notifyDataSetChanged();
        }

        if ( mRvMatchReport != null
                && mRvMatchReport.getData() != null
                && mRvMatchReport.getData().size() > 0 ) {
            mRvMatchReport.getData().clear();
            mRvMatchReport.notifyDataSetChanged();
        }

        if ( mCommentRvAdapter != null
                && mCommentRvAdapter.getData() != null
                && mCommentRvAdapter.getData().size() > 0 ) {

            mCommentRvAdapter.getData().clear();
            mCommentRvAdapter.notifyDataSetChanged();
        }

        tvMatchHeader.setText("매치리포트");
        tvHomeTeamName.setText("");
        tvHomeTeamScore.setText("");
        tvAwayTeamName.setText("");
        tvAwayTeamScore.setText("");

        tvHifiveCount.setText("0 하이파이브");
        //tvScrpCount.setText("0 스크랩");
        if ( fbxTags != null ) {
            fbxTags.setVisibility(View.VISIBLE);
            fbxTags.removeAllViews();
        } else {
            fbxTags.setVisibility(View.GONE);
        }

        tvUserName.setText("");
        //tvUserProfile.setText("");
        //tvContentCreated.setText("");


        updateFooterView();

        // 데이터 조회
        mPresenter.onLoadData(mPresenter.getContentData().getContentId());
//        mPresenter.getContent();
//        mPresenter.getComments(0);
//        mPresenter.getRelationContent();
//        mPresenter.getRelationMatches();
//
//        if ( mPresenter.getContentData().getArenaId() != 0 ) {
//            mPresenter.onLoadRelationPlayerRatings(mPresenter.getContentData().getContentId());
//            mPresenter.onLoadMatchTalks(mPresenter.getContentData().getArenaId(), mPresenter.getContentData().getUserName(), 5, 0);
//        }
    }

    /**
     * 웹뷰 초기화
     */
    private void initWebView() {

        mContentWebview.setWebChromeClient(new MyWebChromeClient());
        mContentWebview.setWebViewClient(new MyWebViewClient());

        WebSettings settings = mContentWebview.getSettings();
        settings.setJavaScriptEnabled(true);
        //settings.setBuiltInZoomControls(true);
        //settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        //settings.setDisplayZoomControls(false);
        //settings.setSupportZoom(false);
        settings.setDefaultTextEncodingName("UTF-8");

        // Setting Local Storage
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);

        // No Cache
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        mContentWebview.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                final WebView.HitTestResult result = ((WebView)v).getHitTestResult();

                if ( result.getType() == WebView.HitTestResult.IMAGE_TYPE
                        || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

                    downloadImage(result.getExtra());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 매치리포트 리사이클뷰 초기화
     */
    private void initRatingRecyclerView() {

        mRvMatchReport = new PlayerCommentRvAdapter(new ArrayList<>());

        rvRatings.setNestedScrollingEnabled(false);
        rvRatings.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvRatings.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        rvRatings.setAdapter(mRvMatchReport);

        mRvMatchReport.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });

        //
        // 매치리포트 클릭 리스너
        //
        mRvMatchReport.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (adapter.getItemViewType(position)) {

                    //
                    // 매치리포트 선수 아바타
                    //
                    case ViewType.PLAYER_RATING_INFO:
                        PlayerRatingInfoModel rating = (PlayerRatingInfoModel)adapter.getItem(position);
                        switch (view.getId()) {
                            case R.id.iv_avatar:
                                mPresenter.openPlayerActivity(rating.getPlayerId(), rating.getPlayerName());
                                break;
                        }

                        break;

                    case ViewType.MATCH_TALK:
                        MatchTalkModel talk = (MatchTalkModel)adapter.getItem(position);
                        switch (view.getId()) {
                            case R.id.iv_avatar:
                                onRvClickAvatar(talk.getUser());
                                break;
                        }

                        break;
                    //
                    // 매치리포트 더보기 ( 정렬순서 메뉴 )
                    //
                    case ViewType.SIMPLE_SECTION_HEADER:
                        onRvMatchReportMoreClick(view);
                        break;
                }
            }
        });
    }

    private void onRvMatchReportMoreClick(View view) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        popup.inflate(R.menu.menu_rv_match_report);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_match_report_rating_order:

                        Collections.sort(mRvMatchReport.getData(), new Comparator<MultiItemEntity>() {
                            @Override
                            public int compare(MultiItemEntity o1, MultiItemEntity o2) {

                                if ( o1 instanceof PlayerRatingInfoModel
                                        && o2 instanceof PlayerRatingInfoModel ) {

                                    PlayerRatingInfoModel src = (PlayerRatingInfoModel)o1;
                                    PlayerRatingInfoModel target = (PlayerRatingInfoModel) o2;

                                    return Float.compare(target.getPlayerRating(), src.getPlayerRating());
                                }

                                return 0;
                            }
                        });

                        mRvMatchReport.notifyDataSetChanged();
                        showMessage("평점순으로 정렬");
                        break;
                    case R.id.menu_match_report_team_order:

                        Collections.sort(mRvMatchReport.getData(), new Comparator<MultiItemEntity>() {
                            @Override
                            public int compare(MultiItemEntity o1, MultiItemEntity o2) {

                                if ( o1 instanceof PlayerRatingInfoModel
                                        && o2 instanceof PlayerRatingInfoModel ) {

                                    PlayerRatingInfoModel src = (PlayerRatingInfoModel)o1;
                                    PlayerRatingInfoModel target = (PlayerRatingInfoModel) o2;

                                    return Integer.compare(src.getPlayer().getTeamId(), target.getPlayer().getTeamId());
                                }

                                return 0;
                            }
                        });

                        mRvMatchReport.notifyDataSetChanged();
                        showMessage("팀순으로 정렬");
                        break;
                    case R.id.menu_match_report_player_name_order:
                        Collections.sort(mRvMatchReport.getData(), new Comparator<MultiItemEntity>() {
                            @Override
                            public int compare(MultiItemEntity o1, MultiItemEntity o2) {

                                if ( o1 instanceof PlayerRatingInfoModel
                                        && o2 instanceof PlayerRatingInfoModel ) {

                                    PlayerRatingInfoModel src = (PlayerRatingInfoModel)o1;
                                    PlayerRatingInfoModel target = (PlayerRatingInfoModel) o2;

                                    return src.getPlayerName().compareTo(target.getPlayerName());
                                }

                                return 0;
                            }
                        });

                        mRvMatchReport.notifyDataSetChanged();
                        showMessage("선수이름순으로 정렬");
                        break;
                }

                return false;
            }
        });

        popup.show();
    }

    /**
     * 댓글 리사이클뷰 초기화
     */
    private void initCommentRecyclerView() {

        View footer = getFooterView();
        View commentsHeaderView = getCommentsHeaderView();
        noCommentsView = getLayoutInflater().inflate(R.layout.recyclerview_simple_no_data_item, (ViewGroup) rvComment.getParent(), false);

        mCommentRvAdapter = new CommentRvAdapter(new ArrayList<>());
        mCommentRvAdapter.addHeaderView(commentsHeaderView);
        mCommentRvAdapter.addFooterView(footer);
        mCommentRvAdapter.setHeaderFooterEmpty(true, true);

        rvComment.setNestedScrollingEnabled(false);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.addItemDecoration(new ContentViewerDividerItemDecoration(getApplicationContext(), 0));
        rvComment.setAdapter(mCommentRvAdapter);

        // *** ChildItemClickListener ***
        mCommentRvAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.comment_like:
                case R.id.comment_like_count:
                    // 하이파이브
                    CommentModel commentModel = (CommentModel) adapter.getItem(position);
                    //((SwitchIconView)view).setIconEnabled(!commentModel.isLiked());
                    if ( commentModel.isLiked()) {
                        mPresenter.setCommentUnLike(commentModel, position);
                    } else {
                        mPresenter.setCommentLike(commentModel, position);
                    }
                    break;
                case R.id.btn_reply_comment:
                    //대댓글 달기 버튼
                    if ( !App.getAccountManager().isAuthorized() ) {
                        showLoginSnackBar();
                        return;
                    }
                    CommentInputFragment commentInputFragment
                            = CommentInputFragment.newInstance(CommentInputFragment.COMMENT_TYPE_GROUP,
                            (CommentModel) adapter.getItem(position),
                            position);
                    commentInputFragment.show(getSupportFragmentManager(), "CommentInput");
                    break;
                case R.id.btn_delete_comment:
                    int deletePosition = position + mCommentRvAdapter.getHeaderLayoutCount();
                    onRvClickDeleteComment((CommentModel) mCommentRvAdapter.getItem(position), deletePosition);

                case R.id.btn_edit_comment:
                    //
                    // 댓글수정
                    //
                    int editPosition = position + mCommentRvAdapter.getHeaderLayoutCount();
                    onRvClickEditComment((CommentModel) adapter.getItem(position), editPosition);
                    break;
                case R.id.btn_report_comment:
                    //
                    // 댓글신고
                    //
                    onRvClickReportComment((CommentModel) adapter.getItem(position), position);
                    break;
                case R.id.avatar:
                    CommentModel item = (CommentModel)adapter.getItem(position);
                    if ( item != null && item.getUser() != null ) {
                        onRvClickAvatar(item.getUser());
                    }
                    break;
            }
        });
    }

    private void onRvClickDeleteComment(CommentModel comment, int position) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        mPresenter.deleteComment(comment, position);
    }

    private void onRvClickReportComment(CommentModel comment, int position) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if ( comment == null ) {
            showMessage("수정할 데이터가 없습니다.");
            return;
        }

//

        mPresenter.setCommentReport(comment, position);
    }

    private void onRvClickEditComment(CommentModel comment, int position) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if ( comment == null ) {
            showMessage("수정할 데이터가 없습니다.");
        }

        if ( comment.isDeleted() ) {
            showMessage("삭제된 댓글을 수정할 수 없습니다.");
            return;
        }

        //hideAdView();

        new MaterialDialog.Builder(this)
                .title("댓글수정")
                .content("수정할 댓글을 입력해주세요")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .input("댓글을 입력해주세요", comment.getContent(), (dialog, input) -> {
                    mPresenter.editComment(comment.getCommentId(), input.toString(), position);
                }).show();
    }

    private View getCommentsHeaderView() {

        View view = getLayoutInflater().inflate(R.layout.row_content_viewer_comment_info_item, (ViewGroup) rvComment.getParent(), false);
        tvCommentsCount = view.findViewById(R.id.comment_count_info);

        return view;
    }

    /**
     * 댓글쓰기 버튼
     * @return
     */
    private View getFooterView() {

        View view = getLayoutInflater().inflate(R.layout.row_content_viewer_footer, (ViewGroup) rvComment.getParent(), false);
        footer_login_button = view.findViewById(R.id.content_viewer_login_button);
        footerContainer = view.findViewById(R.id.content_viewer_footer);
        EditText etInput = view.findViewById(R.id.comment_input_text);
        ImageButton ibSend = view.findViewById(R.id.comment_send);

        updateFooterView();

        ibSend.setOnClickListener(v -> {

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            // TODO: 2017. 9. 23. 글자 입력 상태에 따라 enable, disable 상태값을 변경하고 색깔을 변경해주자
            if ( etInput.getText().length() > 0 ) {
                mPresenter.createComment(0, 0, 1, etInput.getText().toString(), mPresenter.COMMENT_SCROLL_POSITION_BOTTOM);
                etInput.setText("");
                // 전송 버튼 클릭 후 키보드 내리기
                hideKeyboard();
            }
        });

        footer_login_button.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });

        return view;
    }

    /*-----------------------------------------------------------------------*/

    //View noRelationView;

    /**
     * RecyclerView 초기화
     */
    private void initRelationContentRecyclerView() {

        //noRelationView = getLayoutInflater().inflate(R.layout.recyclerview_simple_no_data_item, (ViewGroup) rvContent.getParent(), false);
        //TextView tv = noRelationView.findViewById(R.id.tv_title);
        //tv.setText("관련글 및 매치정보가 없습니다.");

        mRelationContentRvAdapter = new RelationContentRvAdapter(new ArrayList<>());
        mRelationContentRvAdapter.setHeaderFooterEmpty(true, true);


        rvContent.setNestedScrollingEnabled(false);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext(), 0));
        rvContent.setAdapter(mRelationContentRvAdapter);

        mRelationContentRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                int viewType = adapter.getItemViewType(position);

                switch (viewType) {

                    case MultipleItem.ARENA_SCHEDULE:
                        onRvClickMatchRow((MultiItemEntity)adapter.getItem(position));
                        break;

                    case ViewType.CONTENT_GENERAL:
                        onRvClickContentRow((MultiItemEntity)adapter.getItem(position));
                        break;
                }
            }
        });

        mRelationContentRvAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.hometeam_image:
                    MatchModel homeTeam = (MatchModel)adapter.getItem(position);
                    onRvClickTeam(homeTeam.getHomeTeamId());
                    break;
                case R.id.awayteam_image:
                    MatchModel awayTeam = (MatchModel)adapter.getItem(position);
                    onRvClickTeam(awayTeam.getAwayTeamId());
                    break;
                case R.id.comment_icon:
                case R.id.comment_count:
                case R.id.comment_box:
                    onRvClickShowComments((ContentHeaderModel)adapter.getItem(position));
                    break;
//                case R.id.undo_hifive:
//                    //하이파이브 취소
//                    mPresenter.setContentUnLike();
//                    break;
                case R.id.content_report:
                    //신고
                    //mPresenter.openContentReportedDialog();
                    break;
//                case R.id.comment_like:
//                case R.id.comment_like_count:
//                    // 하이파이브
//                    CommentModel commentModel = (CommentModel) mRelationContentRvAdapter.getItem(position);
//                    ((SwitchIconView)view).setIconEnabled(!commentModel.isLiked());
//                    if ( commentModel.isLiked()) {
//                        mPresenter.setCommentUnLike(commentModel, position);
//                    } else {
//                        mPresenter.setCommentLike(commentModel, position);
//                    }
//                    break;
//                case R.id.comment_reply:
//                    //대댓글 달기 버튼
//                    if ( !App.getAccountManager().isAuthorized() ) {
//                        Snackbar.make(getWindow().getDecorView(), getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
//                                .setAction("이동", v -> mPresenter.openLoginActivity())
//                                .show();
//                        return;
//                    }
//                    CommentInputFragment commentInputFragment
//                            = CommentInputFragment.newInstance(CommentInputFragment.COMMENT_TYPE_GROUP,
//                                                               (CommentModel) mRelationContentRvAdapter.getItem(position),
//                                                                position);
//                    commentInputFragment.show(getSupportFragmentManager(), "CommentInput");
//                    break;
//                case R.id.comment_delete:
//                    CommentModel deleteComment = (CommentModel) mRelationContentRvAdapter.getItem(position);
//                    mPresenter.deleteComment(deleteComment.getCommentId());
//                    break;
//                case R.id.comment_edit:
//                    break;
//                case R.id.iv_avatar:
//                    onRvClickAvatar(((ContentWriterModel)mRelationContentRvAdapter.getItem(position)).getUser());
//                    break;
                case R.id.relation_container:

                    ContentRelationModel relationModel = (ContentRelationModel) mRelationContentRvAdapter.getItem(position);

                    if ( relationModel != null ) {
                        ContentHeaderModel contentModel = new ContentHeaderModel();
                        contentModel.setTitle(relationModel.getTitle());
                        contentModel.setContent_id(relationModel.getContentId());
                        contentModel.setUser(relationModel.getUser());
                        contentModel.setCreated(relationModel.getCreated());

                        mPresenter.setContentData(contentModel);

//                        mRelationContentRvAdapter.setRelationItemCount(0);
//                        mRelationContentRvAdapter.getData().clear();
//
//                        setBaseData();
//                        mRelationContentRvAdapter.notifyDataSetChanged();
//
//                        mPresenter.getContent();
//                        mPresenter.getComments(0);
//                        mPresenter.getRelationContent();
                        refreshData();
                    } else {
                        //Timber.d("[ItemOnChildClick] relationModel is null!!!!");
                    }

                    break;
            }
        });
    }

    /**
     * 관련글 클릭
     * @param item
     */
    private void onRvClickContentRow(MultiItemEntity item) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if ( item instanceof ContentHeaderModel) {
            ContentHeaderModel contentData = (ContentHeaderModel)item;
            int bodyType = contentData.getBodytype();

            if (PostBodyType.toEnum(bodyType) == PostBodyType.LINK) {
                mPresenter.openLinkActivity(contentData.getLink());
            } else {
                mPresenter.openContentViewActivity(contentData);
            }
        }
    }

    /**
     * 매치정보 클릭
     * @param matchData 매치정보
     */
    private void onRvClickMatchRow(MultiItemEntity matchData) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if ( matchData instanceof MatchModel ) {
            mPresenter.openMatchActivity((MatchModel)matchData);
        }
    }

    private void onRvClickTeam(int teamId) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        mPresenter.openTeamActivity(teamId);
    }

    /**
     * 글 작성자 프로필 클릭
     * @param user
     */
    private void onRvClickAvatar(UserModel user) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        mPresenter.openUserProfile(user);
    }

    private void onRvClickShowComments(ContentHeaderModel item) {
        if ( item.getComments() > 0) {
            mPresenter.openCommentDialogFragment(item.getContentId());
        } else {
            showMessage("등록된 댓글이 없습니다");
        }
    }

    /**
     * 로그인 상태에 따라 댓글입력 풋터 상태를 변경한다.
     */
    private void updateFooterView() {

        if ( footer_login_button == null || footerContainer == null)
            return;

        if ( App.getAccountManager().isAuthorized() ) {
            footer_login_button.setVisibility(View.GONE);
            footerContainer.setVisibility(View.VISIBLE);
        } else {
            footer_login_button.setVisibility(View.VISIBLE);
            footerContainer.setVisibility(View.GONE);
        }
    }

    /***
     * 키보드 상태 리스너
     */
    private void initKeyboardStateObserver() {

        KeyboardVisibilityEvent.setEventListener(
                this,
                isOpen -> {
                    if ( isOpen ) {
                        //hideAdView();
                        mFabHifive.setVisibility(View.GONE);
                        mFabScrollUp.setVisibility(View.GONE);
                    } else {
                        //showAdView();
                        mFabHifive.setVisibility(View.VISIBLE);
                        mFabScrollUp.setVisibility(View.VISIBLE);
                    }
                });
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 상단에 있는 매치정보 데이터를 바인딩한다.
     * contents.arena_id에 값이 있을경우만 노출한다.
     * @param match
     */
    private void bindMatchData(MatchModel match) {

        if ( match == null ) {
            cvMatch.setVisibility(View.GONE);
            return;
        }

        cvMatch.setVisibility(View.VISIBLE);
        tvMatchHeader.setText("매치정보 - " + match.getMatchDateTime());

        // --- 홈팀 ---
        tvHomeTeamName.setText(match.getHomeTeamName());
        tvHomeTeamScore.setText(match.getHomeTeamScoreString());
        displayImageByPicasso(match.getHomeTeamLargeEmblemUrl(), ivHomeEmblem, R.drawable.ic_empty_emblem_vector_1);
//        Glide.with(getApplicationContext())
//                .load(match.getHomeTeamLargeEmblemUrl())
//                .asBitmap()
//                .centerCrop()
//                .transform(new CropCircleTransformation(getApplicationContext()))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                //.placeholder(R.drawable.ic_empty_emblem_vector_1)
//                .placeholder(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_empty_emblem_vector_1))
//                .into(ivHomeEmblem);

        // --- 어웨이팀 ---
        tvAwayTeamName.setText(match.getAwayTeamName());
        tvAwayTeamScore.setText(match.getAwayTeamScoreString());
        displayImageByPicasso(match.getAwayTeamLargeEmblemUrl(), ivAwayEmblem, R.drawable.ic_empty_emblem_vector_1);
//        Glide.with(getApplicationContext())
//                .load(match.getAwayTeamLargeEmblemUrl())
//                .asBitmap()
//                .centerCrop()
//                .transform(new CropCircleTransformation(getApplicationContext()))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                //.placeholder(R.drawable.ic_empty_emblem_vector_1)
//                .placeholder(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_empty_emblem_vector_1))
//                .into(ivAwayEmblem);
    }

    private boolean displayImageByPicasso(String url, ImageView target, int placeholder_id) {

        if ( TextUtils.isEmpty(url)) {
            target.setImageResource(R.drawable.ic_face);
            return false;
        }

        Picasso.get()
                .load(url)
                .placeholder(placeholder_id)
                //.transform(new PicassoCircleTransformation())
                .fit()
                .centerInside()
                .into(target);

        return true;
    }

    @OnClick(R.id.cv_match)
    public void onClickMatch(View view) {
        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        MatchModel matchData = mPresenter.getContentData().getMatch();

        if ( matchData != null) {
            mPresenter.openMatchActivity(matchData);
        } else {
            showMessage("매치정보가 없습니다.");
        }
    }

    @OnClick({R.id.iv_home_emblem, R.id.iv_away_emblem})
    public void onClickTeam(View view) {

        MatchModel matchData = mPresenter.getContentData().getMatch();

        if ( matchData != null ) {
            switch (view.getId()) {
                case R.id.iv_home_emblem:
                    mPresenter.openTeamActivity(matchData.getHomeTeamId());
                    break;
                case R.id.iv_away_emblem:
                    mPresenter.openTeamActivity(matchData.getAwayTeamId());
                    break;
            }
        } else {
            showMessage("매치정보가 없습니다.");
        }
    }

    /**
     * 댓글 리사이클뷰 데이터를 업데이트 한다.
     * @param position
     */
    public void notifyCommentItemChanged(int position) {

        if ( mCommentRvAdapter != null ) {
            position = position + mCommentRvAdapter.getHeaderLayoutCount();
            mCommentRvAdapter.notifyItemChanged(position);
        }
    }

    /**
     * ContentModel 객체를 생성해 반환한다.
     * RecyclerView 베이스 데이터 생성에 사용되며 MainActivity(ContentListFragment)로 데이터를 반환해
     * 변경된 사항을 업데이트 해준다.
     * @param contentHeader
     * @return
     */
//    private ContentModel createContentModel(ContentHeaderModel contentHeader) {
//
//        // ContentHeaderModel.contentId는 반드시 필요함.
//        if ( contentHeader == null )
//            return null;
//
//        ContentModel contentModel = new ContentModel();
//        contentModel.setContent_id(contentHeader.getContentId());
//        contentModel.setTitle(contentHeader.getTitle());
//        contentModel.setUser(contentHeader.getUser());
//        contentModel.setCreated(contentHeader.getCreated());
//
//        return contentModel;
//    }

    /**
     * 웹뷰 이미지 다운로드
     * @param imageUrl
     */
    private void downloadImage(String imageUrl) {

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Uri source = Uri.parse(imageUrl);
                            DownloadManager.Request request = new DownloadManager.Request(source);
                            request.setDescription("이미지를 저장합니다.");
                            request.setTitle("이미지 저장");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                request.allowScanningByMediaScanner();
                                //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            }

                            try {
                                // save the file in the "Downloads" folder of SDCARD
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getFileName(imageUrl));
                                //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
                                // get download service and enqueue file
                                DownloadManager manager = (DownloadManager) App.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
                                manager.enqueue(request);
                                Toasty.success(getApplicationContext(), mSuccessImage, Toast.LENGTH_SHORT, true).show();
                            } catch (Exception ex) {
                                Toasty.error(getApplicationContext(), mFailImage + " : " + ex.getMessage(), Toast.LENGTH_LONG, true).show();
                            }

                        } else {
                            Toasty.warning(getApplicationContext(), mPermissionDenied, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(getApplicationContext(), mFailImage + " : " + e.getMessage(), Toast.LENGTH_LONG, true).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 다운로드할 파일명 가져오기
     * @param url
     * @return
     */
    public String getFileName(String url) {

        // TODO: 2017. 9. 23. 파일명을 어떻게 할지 정해야한다.
        String filenameWithoutExtension = "";
        filenameWithoutExtension = String.valueOf(System.currentTimeMillis() + ".jpg");
        return filenameWithoutExtension;
    }

    /**
     * 화면갱신
     * @param data
     */
    public void updateUI(ContentHeaderModel data) {

        //updateScrap(data.isScraped());
        updateLike(data.isLiked());

        PostBoardType boardType = PostBoardType.toEnum(data.getBoardId());
        if ( boardType != null ) {
            setTitle(boardType.toString());
        }
    }

    /**
     * 스크랩 아이콘 새로고침
     * @param isScraped
     */
    public void updateScrap(boolean isScraped) {
        MenuItem menuItem = mMenu.findItem(R.id.action_scrap);

        if ( isScraped ) {
            menuItem.setIcon(R.drawable.ic_scrap_vector);
        } else {
            menuItem.setIcon(R.drawable.ic_bookmark_border_black_vector);
        }
    }

    /**
     * 좋아요 아이콘 새로고침
     * @param isLiked
     */
    public void updateLike(boolean isLiked) {

        if ( isLiked == true ) {
            mFabHifive.setImageResource(R.drawable.ic_clap_color_vector2);
        } else {
            mFabHifive.setImageResource(R.drawable.ic_clap_color_vector);
        }
    }

//    public void onLoadFinishedHifive(int hifiveCount) {
//
//        if ( mRelationContentRvAdapter != null ) {
//            ContentClapsModel hifiveData = (ContentClapsModel)mRelationContentRvAdapter.getData().get(MultipleItem.CONTENT_HIFIVE);
//            hifiveData.setLikeCount(hifiveCount);
//            mRelationContentRvAdapter.notifyItemChanged(MultipleItem.CONTENT_HIFIVE);
//        }
//    }
//
//    public void onLoadFinishedScrap(int scrapCount) {
//
//        if ( mRelationContentRvAdapter != null ) {
//            ContentClapsModel hifiveData = (ContentClapsModel)mRelationContentRvAdapter.getData().get(MultipleItem.CONTENT_HIFIVE);
//            hifiveData.setScrapCount(scrapCount);
//            mRelationContentRvAdapter.notifyItemChanged(MultipleItem.CONTENT_HIFIVE);
//        }
//    }

    /**
     * 본문 데이터 가져오기 완료
     * @param item
     */
    public void onLoadFinishedBody(ContentHeaderModel item) {

        // 유튜브 있는지 검사
//        if (!TextUtils.isEmpty(extractVideoId(item.getContent()))) {
//            hideAdView();
//        }

        mTitle.setText(item.getTitle());

        PostBodyType bodyType = PostBodyType.toEnum(item.getBodytype());

        if ( bodyType == PostBodyType.HTML ) {

            PostCellType cellType = PostCellType.toEnum(item.getCellType());
            if (cellType == PostCellType.MATCH_REPORT) {
                mContentWebview.setVisibility(View.GONE);
                rvRatings.setVisibility(View.VISIBLE);
                mContentTextView.setVisibility(View.GONE);
            } else {
                mContentTextView.setVisibility(View.GONE);
                rvRatings.setVisibility(View.GONE);
                mContentWebview.setVisibility(View.VISIBLE);

                String html = item.getWrapperHtml(this);
                Timber.i("[html] : " + html);
                mContentWebview.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContentWebview.loadDataWithBaseURL(null, "about:blank", "text/html", "utf-8", null );
                        mContentWebview.loadDataWithBaseURL(null, html, "text/html", "utf-8", null );
                    }
                }, 20);
            }

        } else if (bodyType == PostBodyType.PLAIN) {

            mContentWebview.setVisibility(View.GONE);
            rvRatings.setVisibility(View.GONE);
            mContentTextView.setVisibility(View.VISIBLE);
            mContentTextView.setText(item.getContent());
        }

        bindHifiveNScrap(item);
        bindTags(item.getTags());
        bindUser(item.getUser(), item.getCreatedString());
        bindMatchData(item.getMatch());
        updateUI(item);

        hideLoading();
    }

    /*--------------------------------------------------------------*/

    public void bindHifiveNScrap(ContentHeaderModel item) {

        tvHifiveCount.setText(item.getLikerString() + " 하이파이브");
        //tvScrpCount.setText(item.getScrapString() + " 스크랩");
    }


    private void bindTags(List<String> tags) {

        if ( tags == null || tags.size() == 0 ) {
            fbxTags.setVisibility(View.GONE);
            return;
        }

        fbxTags.setVisibility(View.VISIBLE);
        fbxTags.removeAllViews();

        ChipCloudConfig config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.none)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(getResources().getColor(R.color.md_indigo_50))
                .uncheckedTextColor(getResources().getColor(R.color.md_grey_800));

        ChipCloud chipCloud = new ChipCloud(getApplicationContext(), fbxTags, config);

        for ( int i = 0; i < tags.size(); i++ ) {
            chipCloud.addChip(tags.get(i));
        }
    }

    private void bindUser(UserModel user, String created) {

        if ( user == null ) {
            return;
        }

        int startIndex;
        int endIndex;
        SpannableStringBuilder ssb = new SpannableStringBuilder();



        startIndex = 0;
        ssb.append(user.getNickNameAndUserName())
                .append("  —  ")
                .append(created);
        endIndex = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.md_blue_grey_500)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        startIndex = ssb.length();
        ssb.append("\r\n").append(user.getProfile());
        endIndex = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.md_grey_400)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvUserName.setText(ssb);
        //tvUserName.setText(user.getNickNameAndUserName());
        //tvUserProfile.setText(user.getProfile());
        //tvContentCreated.setText(created);

        GlideApp.with(this)
                .load(user.getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into(ivAvatar);
    }

    /*--------------------------------------------------------------*/

    /**
     * 댓글 데이터 가져오기 완료
     * @param items
     */
    public void onLoadFinishedComments(List<CommentModel> items, int scrollToPosition) {

        if ( items != null && items.size() > 0) {
            mPresenter.getContentData().setComments(items.size());
            mCommentRvAdapter.addCommentData(items);
            mPresenter.getContentData().setComments(items.size());

        } else {
            mPresenter.getContentData().setComments(0);
            mCommentRvAdapter.setEmptyView(noCommentsView);
        }

        tvCommentsCount.setText(mPresenter.getContentData().getCommentString());

        if ( scrollToPosition == mPresenter.COMMENT_SCROLL_POSITION_BOTTOM ) {
            rvContent.scrollToPosition(mRelationContentRvAdapter.getItemCount()-1);
        } else if ( scrollToPosition != mPresenter.COMMENT_SCROLL_POSITION_TOP) {
            //((LinearLayoutManager) rvComments.getLayoutManager()).scrollToPositionWithOffset(scrollToPosition, 4);
            //mNestedScrollView.scrollTo(0,0);
        }

        hideLoading();
    }

    /**
     * 연관 글 가져오기 완료
     * @param items
     */
    public void onLoadFinishedRelationContent(List<ContentHeaderModel> items) {

        if ( items == null || items.size() == 0) {
            //mRelationContentRvAdapter.notifyDataSetChanged();
            //mRelationContentRvAdapter.setEmptyView(noRelationView);
            cvContentContainer.setVisibility(View.GONE);
            hideLoading();
            return;
        }

        cvContentContainer.setVisibility(View.VISIBLE);
        mRelationContentRvAdapter.addData(new SimpleSectionHeaderModel("관련글"));
        mRelationContentRvAdapter.addData(items);
        hideLoading();
    }

    /**
     * 관련 매치 로드 완료
     * @param items
     */
    public void onLoadFinishedRelationMatches(List<MatchModel> items) {

//        int position = MultipleItem.CONTENT_WRITER_INFO + mRelationContentRvAdapter.getRelationItemCount() + 1;
//        mRelationContentRvAdapter.addData(position, items);

        if ( items == null ||  items.size() == 0) {
            //mRelationContentRvAdapter.notifyDataSetChanged();
            //mRelationContentRvAdapter.setEmptyView(noRelationView);
            hideLoading();
            return;
        }

        mRelationContentRvAdapter.addData(new SimpleSectionHeaderModel("관련매치정보"));
        mRelationContentRvAdapter.addData(items);
        hideLoading();
    }

    /**
     * 매치리포트 ( 선수평점 )
     * @param items
     */
    public void onLoadFinishedPlayerRatings(List<PlayerRatingInfoModel> items) {

        if ( items.size() == 0) {
            mRvMatchReport.notifyDataSetChanged();
            hideLoading();
            return;
        }

        mRvMatchReport.addData(0, items);
        mRvMatchReport.addData(0, new SimpleSectionHeaderModel("매치리포트", true));
        hideLoading();
    }

    /**
     * 매치토크 로드 완료
     * @param items
     */
    public void onLoadFinishedMatchTalks(List<MatchTalkModel> items) {

        if ( items.size() == 0) {
            mRvMatchReport.notifyDataSetChanged();
            hideLoading();
            return;
        }

        mRvMatchReport.addData(new SimpleSectionHeaderModel("매치토크"));
        mRvMatchReport.addData(items);
        hideLoading();
    }


    /**
     * 유튜브 포함 여부 - 유튜브가 있는 게시물은 광고 노출이 되면 안된다
     * @param ytUrl
     * @return
     */
    public String extractVideoId(String ytUrl) {

//        http://www.youtube.com/watch?v=0zM4nApSvMg&feature=feedrec_grec_index
//        http://www.youtube.com/user/SomeUser#p/a/u/1/QDK8U-VIH_o
//        http://www.youtube.com/v/0zM4nApSvMg?fs=1&amp;hl=en_US&amp;rel=0
//        http://www.youtube.com/watch?v=0zM4nApSvMg#t=0m10s
//        http://www.youtube.com/embed/0zM4nApSvMg?rel=0
//        http://www.youtube.com/watch?v=0zM4nApSvMg
//        http://youtu.be/0zM4nApSvMg

//        Timber.d("[extractVideoId] data : %s", ytUrl);
        String vId = null;
//        Pattern pattern = Pattern.compile("^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$", Pattern.CASE_INSENSITIVE);
        String regex2 = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern pattern = Pattern.compile(regex2);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }

//        Timber.d("[extractVideoId] result : %s", vId);

        return vId;
    }

    /**
     * 광고 보이기
     */
    private void showAdView() {

        //mAdView.setVisibility(View.VISIBLE);
        //adView.setVisibility(View.VISIBLE);
    }

    /**
     * 광고 숨기기
     */
    private void hideAdView() {

        //mAdView.setVisibility(View.GONE);
        //adView.setVisibility(View.GONE);
    }

    private void hideKeyboard() {

        View focusView = getCurrentFocus();
        if (focusView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    final class MyWebViewClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            showErrorMessage("[onReceivedError]\n" + description + "\n" + failingUrl);
        }
    }


    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            //return super.onJsAlert(view, url, message, result);
            new AlertDialog.Builder(view.getContext())
                    .setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            new AlertDialog.Builder(view.getContext())
                    .setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton("네",
                            new AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton("아니오",
                            new AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }
    }

}
