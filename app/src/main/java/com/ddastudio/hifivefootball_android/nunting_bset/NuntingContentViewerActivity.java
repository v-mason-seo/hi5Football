package com.ddastudio.hifivefootball_android.nunting_bset;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.nunting_bset.model.PostsListModel;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.ui.widget.ObservableWebView;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NuntingContentViewerActivity extends BaseActivity {

    @BindView(R.id.contentsToolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_site_name) TextView tvSiteName;
    @BindView(R.id.tv_posts_link) TextView tvPostsLink;
    @BindView(R.id.swPostsContents)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.nsMain)
    NestedScrollView nestedScrollView;
    @BindView(R.id.cvHeader)
    CardView cvHeader;
    @BindView(R.id.tvContentsTitle) TextView tvContentsTitle;
    @BindView(R.id.tvContentsUser) TextView tvContentsUser;
    @BindView(R.id.tvContentsRegdate) TextView tvContentsRegdate;
    @BindView(R.id.tvContentsUrl)
    TextView tvContentsUrl;
    @BindView(R.id.wvContents)
    ObservableWebView wvContents;
//    @BindView(R.id.bottom_sheet)
//    View bottomSheet;
    //@BindView(R.id.fabContents) FloatingActionMenu mFabContents;
    //@BindView(R.id.fabContentsTop) FloatingActionButton mFabContentsTop;
//    @BindView(R.id.fabContentsShare)
//    FloatingActionButton mFabContentsShare;
    @BindView(R.id.rvContentsReplay)
    RecyclerView mRvContentsReplay;
//    @BindView(R.id.adViewContentsBottom)
//    AdView mAdview;

    Menu mMenu;
    ActionBar mActionBar;
    NuntingContentViewerPresenter mPresenter;
    //PostsContentsReplayRVAdapter mAdapter;
    //BottomSheetBehavior mBottomSheetBehavior;
    //BottomSheetDialogFragment bottomSheetDialogFragment;
    int mWebviewLeftPositionOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_nunting_content_viewer);

        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_nunting_content_viewer)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener((v, delta, x, y) -> {

                    if ( v == wvContents ) {
                        return (mWebviewLeftPositionOffset > 0);

                    }

                    return false;
                });
        ButterKnife.bind(this);
        SiteBoardModel.Name site = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_SITE"));
        SiteBoardModel.Boards board = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_BOARD"));
        PostsListModel posts = getIntent().getParcelableExtra("ARGS_POSTS_MODEL");

        mPresenter = new NuntingContentViewerPresenter();
        mPresenter.attachView(this);
        mPresenter.setSite(site);
        mPresenter.setBoard(board);
        mPresenter.setPosts(posts);

//        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//        bottomSheetDialogFragment = new ShareBottomSheetFragment();

        initializeToolbar();
        initializeSwipeRefresh();
        //initializeRecyclerView();
        //initializeFAB();
        initializeWebview();
        //initializeAdView();

        onLoadContentsData();
    }

    @Override protected void onResume() {
        super.onResume();

        if ( wvContents != null) {
            wvContents.onResume();
        }
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            if ( wvContents != null) {
                wvContents.onPause();
            }
        } catch (Exception ex) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nunting_post_content_viewer, menu);
        //mPresenter.isScrapped(mPostsList.getContentsMobileUrl());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        supportFinishAfterTransition();
    }

    private void initializeToolbar() {

        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_vector);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("");
        tvSiteName.setText(mPresenter.getSite().getNm());
        tvPostsLink.setText(mPresenter.getPosts().getContentsMobileUrl());
    }

    private void initializeSwipeRefresh() {

        swipeRefreshLayout.setColorSchemeResources(R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener(() -> wvContents.reload());
    }

//    private void initializeFAB() {
//
//        mFabContents.setClosedOnTouchOutside(true);
//        View.OnClickListener fabClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                switch (v.getId()) {
//                    case R.id.fabContentsTop:
//                        nestedScrollView.scrollTo(0, 0);
//                        break;
//                    case R.id.fabContentsShare:
//                        mPresenter.openSharedIntent(mPresenter.getPosts().getContentsMobileUrl());
//                        break;
//                }
//
//                mFabContents.toggle(true);
//            }
//        };
//
//        mFabContents.setClosedOnTouchOutside(true);
//        mFabContentsTop.setOnClickListener(fabClickListener);
//        mFabContentsShare.setOnClickListener(fabClickListener);
//    }

    public void initializeWebview()
    {
        wvContents.setWebViewClient(new WebViewClientClass());
        wvContents.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                //return super.onJsAlert(view, url, message, result);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("알림")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                (dialog, which) -> result.confirm())
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
                                (dialog, which) -> result.confirm())
                        .setNegativeButton("아니오",
                                (dialog, which) -> result.cancel())
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });
        WebSettings setting = wvContents.getSettings();
        setting.setJavaScriptEnabled(true);
        //setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setPluginState(WebSettings.PluginState.ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            wvContents.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        wvContents.setOnScrollChangedCallback((l, t) -> mWebviewLeftPositionOffset = l);
    }


//    private void initializeRecyclerView() {
//
//        mAdapter = new PostsContentsReplayRVAdapter(getApplicationContext());
//
//        // Nested ScrollView 사용시 아래 코드가 있어야 부르더운 스크롤이 가능해진다.
//        mRvContentsReplay.setNestedScrollingEnabled(false);
//        mRvContentsReplay.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        mRvContentsReplay.setAdapter(mAdapter);
//        mRvContentsReplay.addItemDecoration(new SimpleDivider(getResources().getDrawable(R.drawable.divider)));
//    }

    /**
     * 광고 초기화
     */
//    private void initializeAdView() {
//
//        mPresenter.initializeAdView(mAdview);
//    }


    public void changeFavoriteMenuIcon(boolean isScrapped) {

        //Log.i("hong", "changeFavoriteMenuIcon");

        MenuItem menuIte = mMenu.findItem(R.id.action_scrap);

        if ( isScrapped == true) {
            menuIte.setIcon(getResources().getDrawable(R.drawable.ic_star_vector));
        } else {
            menuIte.setIcon(getResources().getDrawable(R.drawable.ic_star_border_vector));
        }
    }



    private class WebViewClientClass extends WebViewClient
    {
        /*@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.i("hong", "class getCurrentUrl : " + mPresenter.getCurrentUrl());
            Log.i("hong", "class url : " + mPresenter.getPosts().getContentsUrl());
            Log.i("hong", "class mobile url : " + mPresenter.getPosts().getContentsMobileUrl());

            Log.i("hong", "webview url : " + url);
            if ( mPresenter.getPosts().getContentsMobileUrl().equals(url)) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }*/

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            //Log.i("hong", "onPageStarted");
            super.onPageStarted(view, url, favicon);
            swipeRefreshLayout.setRefreshing(true);


        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            //Log.i("hong", "onPageFinished");
            super.onPageFinished(view, url);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setAdviewVisibility(int visibility){

        //mAdview.setVisibility(visibility);
    }



    private void onLoadContentsData() {

        //mAdview.setVisibility(View.VISIBLE);

        String url = mPresenter.getPosts().getContentsMobileUrl();
        //Log.i("hong", "SITE URL: " + url);
//        if ( mPresenter.getSite().getId().equals("a82cook") == true
//                ||  mPresenter.getSite().getNm().equals("82CooK") == true) {
//
//            mPresenter.setCurrentUrl(url);
//            mPresenter.getPostsContents(url);
//            mPresenter.getContentsReplay(url);
//
//        } else {
//
//            mPresenter.checkYouTube(url);
//            cvHeader.setVisibility(View.GONE);
//            wvContents.loadUrl(url);
//        }

        mPresenter.checkYouTube(url);
        cvHeader.setVisibility(View.GONE);
        wvContents.loadUrl(url);
    }

    /**
     * 자체파싱 사이트
     * 헤더를 채운다.
     * @param title
     * @param user
     * @param reg
     */
    public void setHeader(String title, String user, String reg) {
        tvContentsTitle.setText(title);
        tvContentsUser.setText(user);
        tvContentsRegdate.setText(reg);
        tvContentsUrl.setText(mPresenter.getPosts().getContentsMobileUrl());
    }


    /**
     * 자체파싱 사이트
     * 댓글정보를 채운다.
     * @param items
     */
//    public void setReplayItems(List<ContentsReplayModel> items) {
//
//        mAdapter.setItems((ArrayList<ContentsReplayModel>) items);
//        swipeRefreshLayout.setRefreshing(false);
//    }
//
//    public void setAddReplayItems(List<ContentsReplayModel> items) {
//
//        mAdapter.addAll((ArrayList<ContentsReplayModel>) items);
//        swipeRefreshLayout.setRefreshing(false);
//
//    }

    public void bindData(String htmlCode) {

        Float textSize_f;
        Float lineSpacing_f;
        String webview_backgound_color;
        String webView_text_color;
        String webView_link_color;

        textSize_f = 16.0f;
        lineSpacing_f = 1.5f;
        webView_text_color= toHtmlColor(this, R.color.md_grey_800);
        webView_link_color = toHtmlColor(this, R.color.colorPrimary);
        webview_backgound_color = toHtmlColor(this, R.color.white);

        htmlCode = this.getString(R.string.html
                , htmlCode
                , webView_text_color
                , webView_link_color
                , textSize_f
                , 8.5f
                , lineSpacing_f
                , webview_backgound_color);

        /*String testHtmlCode = "<html> <head> <style type=text/css> body { font-size: 16.000000px; line-height: 1.500000; color: #424242; background-color: #FFFFFF; margin: 8.500000px 8.500000px 8.500000px 8.500000px; } a {color: #009688;} img {display: inline; height: auto; max-width: 100%;} pre {white-space: pre-wrap;} iframe {width: 90vw; height: 50.625vw;} *//* 16:9 *//* </style> </head> \n" +
                "<body>\n" +
                "    <embed src=\"http://www.youtube.com/v/wgOFGNg4u6s?version=3&amp;hl=ko_KR&amp;vq=hd720\" type=\"\" width=\"640\" height=\"360\" =\"always\" allowfullscreen=\"true\" allowscriptaccess=\"never\">\n" +
                "    <iframe width=\"853\" height=\"480\" src=\"https://www.youtube.com/embed/yyB3bhKiffM\" frameborder=\"0\" allowfullscreen></iframe>\n" +
                "    <iframe width=\"853\" height=\"480\" src=\"http://www.youtube.com/embed/wgOFGNg4u6s\" frameborder=\"0\" allowfullscreen></iframe>\n" +
                "    </body> \n" +
                "</html>";*/

        wvContents.loadDataWithBaseURL(null, htmlCode, "text/html", "utf-8", null );

        // 유투브 관련 코드가 있다면 광고를 안보이게 설정함.
        mPresenter.checkYouTube_Html(htmlCode);
    }

    private String toHtmlColor(Context context, int colorid) {

        String FORMAT_HTML_COLOR = "%06X";
        return String.format(FORMAT_HTML_COLOR, 0xFFFFFF &
                ContextCompat.getColor(context, colorid));
    }
}
