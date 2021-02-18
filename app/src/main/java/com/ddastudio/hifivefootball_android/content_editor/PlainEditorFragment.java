package com.ddastudio.hifivefootball_android.content_editor;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.data.event.EditorEvent;
import com.ddastudio.hifivefootball_android.data.event.MatchEvent;
import com.ddastudio.hifivefootball_android.content_editor.model.EditorImageModel;
import com.ddastudio.hifivefootball_android.content_editor.model.EditorLinkModel;
import com.ddastudio.hifivefootball_android.content_editor.model.EditorTextModel;
import com.ddastudio.hifivefootball_android.data.model.OpenGraphModel;
import com.ddastudio.hifivefootball_android.data.model.StreamViewerModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.community.EditorTitleModel;
import com.ddastudio.hifivefootball_android.data.model.community.EditorVideoModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.OrderTest.MatchListDialogFragment;
import com.ddastudio.hifivefootball_android.ui.OrderTest.Product;
import com.ddastudio.hifivefootball_android.ui.adapter.ItemDragAndSwipeCallback2;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.zhihu.matisse.Matisse;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlainEditorFragment extends BaseFragment {

    @BindView(R.id.fl_container) FrameLayout flContainer;
    @BindView(R.id.editor_list) RecyclerView rvList;
    @BindView(R.id.fl_progress) FrameLayout flProgress;
    @BindView(R.id.tv_progress_info) TextView tvProgressInfo;
    @BindView(R.id.editor_loading) LottieAnimationView mLoading;
    @BindView(R.id.hsv_main) HorizontalScrollView hsvEditorTools;
    @BindView(R.id.layout_chatbox) LinearLayout llChatContainer;
    @BindView(R.id.et_chatbox) EditText etChatBox;
    @BindView(R.id.iv_attachment) ImageView ivAttachment;
    @BindView(R.id.iv_send_message) ImageView ivSendMessage;
    @BindView(R.id.tv_tag) TextView tvTag;
    @BindArray(R.array.board_id) int[] mBoardId;

    private EditText etHeaderTitle;
    private FlexboxLayout footerTagBox;
    private FlexboxLayout footerMatchTagBox;
    private ChipCloud mChip;
    private ChipCloud mMatchChip;
    private ItemTouchHelper mItemTouchHelper;
    private ItemDragAndSwipeCallback2 mItemDragAndSwipeCallback;

    private Menu mMenu;
    private PlainEditorRvAdapter mRvAdapter;
    private PlainEditorPresenter mPresenter;

    private long mLastClickTime = 0;

    private ChatAndAttributeModel mSelectedItem;
    private MutableLiveData<ChatAndAttributeModel> mSelectedLiveItem;

    PostCellType mCellType;

    //
    //
    //
    PlainEditorViewModel viewModel;

    public PlainEditorFragment() {
        // Required empty public constructor
    }


    public static PlainEditorFragment newInstance(int insertOrUpdate, int boardId, int cellType, ContentHeaderModel board, ContentHeaderModel content) {

        //Log.i("hong", "== 1 ==");

        PlainEditorFragment fragment = new PlainEditorFragment();
        Bundle bundle = new Bundle();

        // 테스트
        bundle.putInt("ARGS_MODE", 1);

        bundle.putInt("ARGS_INSERT_OR_UPDATE", insertOrUpdate);
        bundle.putInt("ARGS_SELECTED_BOARD_ID", boardId);
        bundle.putInt("ARGS_CELL_TYPE", cellType);
        bundle.putParcelable("ARGS_BOARD_MODEL", Parcels.wrap(board));
        bundle.putParcelable("ARGS_CONTENT_MODEL", Parcels.wrap(content));
        fragment.setArguments(bundle);

        return fragment;
    }

    public static PlainEditorFragment newInstance(int insertOrUpdate,
                                                  int boardId,
                                                  int cellType,
                                                  PlayerModel player,
                                                  TeamModel team,
                                                  MatchModel match,
                                                  ContentHeaderModel board,
                                                  ContentHeaderModel content) {

        //Log.i("hong", "== 2 ==");

        PlainEditorFragment fragment = new PlainEditorFragment();
        Bundle bundle = new Bundle();

        // 테스트
        bundle.putInt("ARGS_MODE", 2);

        bundle.putInt("ARGS_INSERT_OR_UPDATE", insertOrUpdate);
        bundle.putInt("ARGS_SELECTED_BOARD_ID", boardId);
        bundle.putInt("ARGS_CELL_TYPE", cellType);
        bundle.putParcelable("ARGS_PLAYER", Parcels.wrap(player));
        bundle.putParcelable("ARGS_TEAM", Parcels.wrap(team));
        bundle.putParcelable("ARGS_MATCH", Parcels.wrap(match));
        bundle.putParcelable("ARGS_BOARD_MODEL", Parcels.wrap(board));
        bundle.putParcelable("ARGS_CONTENT_MODEL", Parcels.wrap(content));
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plain_editor, container, false);
        _unbinder = ButterKnife.bind(this, view);

        assert getArguments() != null;
        int editorType = getArguments().getInt("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        int selectedBoardId = getArguments().getInt("ARGS_SELECTED_BOARD_ID", PostBoardType.FOOTBALL.value());
        int argsCellType = getArguments().getInt("ARGS_CELL_TYPE", PostCellType.BASE.value());
        mCellType = PostCellType.toEnum(argsCellType);
        PostBoardType selectedBoardType = PostBoardType.toEnum(selectedBoardId);

        // 테스트
        int mode = getArguments().getInt("ARGS_MODE", -1);

        mSelectedLiveItem = new MutableLiveData<>();

        if ( mode == 2) {

            // 테스트
            ChatAndAttributeModel testChatAttr = new ChatAndAttributeModel();

            ChatModel testChat = new ChatModel();
            PlayerModel player = Parcels.unwrap(getArguments().getParcelable("ARGS_PLAYER"));
            TeamModel team = Parcels.unwrap(getArguments().getParcelable("ARGS_TEAM"));
            MatchModel match = Parcels.unwrap(getArguments().getParcelable("ARGS_MATCH"));

            if ( player != null ) {
                testChat.mentionType = "P";
                testChat.player = player;
                testChat.mentionId = player.getPlayerId();

            } else if ( team != null ) {
                testChat.mentionType = "T";
                testChat.team = team;
                testChat.mentionId = team.getTeamId();
            } else if ( match != null ) {
                testChat.mentionType = "M";
                testChat.match = match;
                testChat.mentionId = match.getMatchId();
            }

            ChatAttributeModel testAttr = new ChatAttributeModel();


            testChatAttr.chat = testChat;
            testChatAttr.attr = testAttr;

            mSelectedItem = testChatAttr;
            mSelectedLiveItem.setValue(testChatAttr);

        } else {

            LiveData<ChatAndAttributeModel> footbllChat = AppDatabase.getInstance(getActivity()).chatDao().loadSelectedChatAndAttr();
            footbllChat.observe(this, item -> {
                if ( item != null ) {

                    mSelectedItem = item;
                    mSelectedLiveItem.setValue(item);

//                switch (item.getMentionType()) {
//                    case "P":
//                        PlayerModel player = item.getChat().player;
//
//                        View playerHeader = getActivity().getLayoutInflater().inflate(R.layout.row_editor_player_header_item, (ViewGroup)rvList.getParent(), false);
//                        TextView tvPlayerName = playerHeader.findViewById(R.id.tv_player_name);
//                        TextView tvPlayerTeamName = playerHeader.findViewById(R.id.tv_player_team_name);
//                        ImageView ivPlayerAvatar = playerHeader.findViewById(R.id.iv_player_avatar);
//                        tvPlayerName.setText(player.getPlayerName());
//                        tvPlayerTeamName.setText(player.getTeamName());
//
//                        GlideApp.with(getContext())
//                                .load(player.getPlayerLargeImageUrl())
//                                .centerCrop()
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .placeholder(R.drawable.ic_person_grey_vector)
//                                .into(ivPlayerAvatar);
//
//                        mRvAdapter.addHeaderView(playerHeader, 0);
//                        break;
//                    case "T":
//
//                        TeamModel team = item.getChat().team;
//                        View teamHeader = getActivity().getLayoutInflater().inflate(R.layout.row_editor_team_header_item, (ViewGroup)rvList.getParent(), false);
//                        TextView tvTeamName = teamHeader.findViewById(R.id.tv_team_name);
//                        ImageView ivEmblem = teamHeader.findViewById(R.id.iv_emblem);
//                        tvTeamName.setText(team.getTeamName());
//
//                        GlideApp.with(getContext())
//                                .load(team.getEmblemUrl())
//                                .centerCrop()
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .placeholder(R.drawable.ic_empty_emblem_vector_1)
//                                .into(ivEmblem);
//
//                        mRvAdapter.addHeaderView(teamHeader, 0);
//
//                        break;
//                    case "M":
//                        MatchModel match = item.getChat().match;
//                        View matchHeader = getActivity().getLayoutInflater().inflate(R.layout.row_editor_match_header_item, (ViewGroup)rvList.getParent(), false);
//
//                        LinearLayout llComp = matchHeader.findViewById(R.id.ll_competition_container);
//                        llComp.setVisibility(View.GONE);
//                        //tv_match_date
//                        TextView tcMatchDate = matchHeader.findViewById(R.id.tv_match_date);
//                        tcMatchDate.setText(match.getMatchDate4());
//                        TextView tvHomeTeamName = matchHeader.findViewById(R.id.tv_home_team_name);
//                        TextView tvAwayTeamName = matchHeader.findViewById(R.id.tv_away_team_name);
//                        ImageView ivHomeEmblem = matchHeader.findViewById(R.id.iv_home_emblem);
//                        ImageView ivAwayEmblem = matchHeader.findViewById(R.id.iv_away_emblem);
//                        tvHomeTeamName.setText(match.getHomeTeamName());
//                        tvAwayTeamName.setText(match.getAwayTeamName());
//
//                        GlideApp.with(getContext())
//                                .load(match.getHomeTeamEmblemUrl())
//                                .centerCrop()
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .placeholder(R.drawable.ic_empty_emblem_vector_1)
//                                .into(ivHomeEmblem);
//
//                        GlideApp.with(getContext())
//                                .load(match.getAwayTeamEmblemUrl())
//                                .centerCrop()
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .placeholder(R.drawable.ic_empty_emblem_vector_1)
//                                .into(ivAwayEmblem);
//
//                        mRvAdapter.addHeaderView(matchHeader, 0);
//                        break;
//                    case "B":
//                        View boardHeader = getActivity().getLayoutInflater().inflate(R.layout.row_editor_board_header_item, (ViewGroup)rvList.getParent(), false);
//
//                        TextView tvBoardTitle = boardHeader.findViewById(R.id.tv_board_title);
//                        ImageView ivBoard = boardHeader.findViewById(R.id.iv_board);
//
//                        TextDrawable textDrawable = TextDrawable.builder()
//                                .beginConfig()
//                                .textColor(ColorGenerator.MATERIAL.getColor(item.getBoardName()))
//                                .useFont(Typeface.DEFAULT)
//                                .fontSize(toPx(38)) /* size in px */
//                                .bold()
//                                .toUpperCase()
//                                .endConfig()
//                                .buildRect("#", Color.TRANSPARENT);
//
//                        tvBoardTitle.setText(item.getBoardName());
//                        ivBoard.setImageDrawable(textDrawable);
//
//                        mRvAdapter.addHeaderView(boardHeader, 0);
//                        break;
//                }
                }
            });
        }

        mPresenter = new PlainEditorPresenter(PostEditorType.toEnum(editorType));
        mPresenter.attachView(this);

        // 뷰모델 초기화
        initViewModel();

        // 프래그먼트에서 옵션 메뉴 사용하기
        setHasOptionsMenu(true);
        // 뷰 추기화
        initView();
        // 리사이클러뷰 초기화
        initRecyclerView();
        // 게시판 선택
        //initSpinner();
        // 키보드 상태 리스너 초기화
        initKeyboardStateObserver();
        // 수정모드일 경우 데이터를 바인딩한다.
        setData();


        mSelectedLiveItem.observe(this, item -> {

            if ( item != null ) {
                //Log.i("hong", "== ~~~ == ");

                switch (item.getMentionType()) {
                    case "P":
                        PlayerModel player = item.getChat().player;

                        View playerHeader = getActivity().getLayoutInflater().inflate(R.layout.row_editor_player_header_item, (ViewGroup)rvList.getParent(), false);
                        TextView tvPlayerName = playerHeader.findViewById(R.id.tv_player_name);
                        TextView tvPlayerTeamName = playerHeader.findViewById(R.id.tv_player_team_name);
                        ImageView ivPlayerAvatar = playerHeader.findViewById(R.id.iv_player_avatar);
                        tvPlayerName.setText(player.getPlayerName());
                        tvPlayerTeamName.setText(player.getTeamName());

                        GlideApp.with(getContext())
                                .load(player.getPlayerLargeImageUrl())
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_person_grey_vector)
                                .into(ivPlayerAvatar);

                        mRvAdapter.addHeaderView(playerHeader, 0);
                        break;
                    case "T":

                        TeamModel team = item.getChat().team;
                        View teamHeader = getActivity().getLayoutInflater().inflate(R.layout.row_editor_team_header_item, (ViewGroup)rvList.getParent(), false);
                        TextView tvTeamName = teamHeader.findViewById(R.id.tv_team_name);
                        ImageView ivEmblem = teamHeader.findViewById(R.id.iv_emblem);
                        tvTeamName.setText(team.getTeamName());

                        GlideApp.with(getContext())
                                .load(team.getEmblemUrl())
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                                .into(ivEmblem);

                        mRvAdapter.addHeaderView(teamHeader, 0);

                        break;
                    case "M":
                        MatchModel match = item.getChat().match;
                        View matchHeader = getActivity().getLayoutInflater().inflate(R.layout.row_editor_match_header_item, (ViewGroup)rvList.getParent(), false);

                        LinearLayout llComp = matchHeader.findViewById(R.id.ll_competition_container);
                        llComp.setVisibility(View.GONE);
                        //tv_match_date
                        TextView tcMatchDate = matchHeader.findViewById(R.id.tv_match_date);
                        tcMatchDate.setText(match.getMatchDate4());
                        TextView tvHomeTeamName = matchHeader.findViewById(R.id.tv_home_team_name);
                        TextView tvAwayTeamName = matchHeader.findViewById(R.id.tv_away_team_name);
                        ImageView ivHomeEmblem = matchHeader.findViewById(R.id.iv_home_emblem);
                        ImageView ivAwayEmblem = matchHeader.findViewById(R.id.iv_away_emblem);
                        tvHomeTeamName.setText(match.getHomeTeamName());
                        tvAwayTeamName.setText(match.getAwayTeamName());

                        GlideApp.with(getContext())
                                .load(match.getHomeTeamEmblemUrl())
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                                .into(ivHomeEmblem);

                        GlideApp.with(getContext())
                                .load(match.getAwayTeamEmblemUrl())
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                                .into(ivAwayEmblem);

                        mRvAdapter.addHeaderView(matchHeader, 0);
                        break;
                    case "B":
                        View boardHeader = getActivity().getLayoutInflater().inflate(R.layout.row_editor_board_header_item, (ViewGroup)rvList.getParent(), false);

                        TextView tvBoardTitle = boardHeader.findViewById(R.id.tv_board_title);
                        ImageView ivBoard = boardHeader.findViewById(R.id.iv_board);

                        TextDrawable textDrawable = TextDrawable.builder()
                                .beginConfig()
                                .textColor(ColorGenerator.MATERIAL.getColor(item.getBoardName()))
                                .useFont(Typeface.DEFAULT)
                                .fontSize(toPx(38)) /* size in px */
                                .bold()
                                .toUpperCase()
                                .endConfig()
                                .buildRect("#", Color.TRANSPARENT);

                        tvBoardTitle.setText(item.getBoardName());
                        ivBoard.setImageDrawable(textDrawable);

                        mRvAdapter.addHeaderView(boardHeader, 0);
                        break;
                }
            }
        });



        return view;
    }

    public int toPx(int dp) {
        Resources resources = getActivity().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // destroy all menu and re-call onCreateOptionsMenu
        Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        mItemTouchHelper = null;
        mItemDragAndSwipeCallback = null;
        mPresenter.detachView();

        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        inflater.inflate(R.menu.menu_editor_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            //
            // 글보내기 옵션 메뉴
            //
            case R.id.action_send_content:

                CommonUtils.hideKeyboard(Objects.requireNonNull(getActivity()), getView());
                //menuDisable(R.id.action_send_content);
                //item.setEnabled(false);

                // 버튼 연속 클릭 방지
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return false;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                showLoading();
                createContent();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PlainEditorPresenter.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
                
            // 이미지 선택 -> 이미지 압축 -> 이미지 S3 업로드
            List<Uri> obtainResults = Matisse.obtainResult(data);
            // gif -> mp4 변환을 위해서 임시로 주석
            mPresenter.imageCompressNUpload(obtainResults);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // 게시판 제목 저장
        if ( etHeaderTitle != null && !TextUtils.isEmpty(etHeaderTitle.getText().toString())) {
            viewModel.setTitle(etHeaderTitle.getText().toString());
        }

        // 태그정보 저장
        if ( footerTagBox != null ) {
            viewModel.cleardTag();
            for ( int i = 0; i < footerTagBox.getChildCount(); i++ ) {
                viewModel.addTag(mChip.getLabel(i));
            }
        }

        // 입력 데이터 저장
        if ( mRvAdapter != null && mRvAdapter.getData().size() > 0 ) {
            viewModel.clearItem();
            viewModel.addItems(mRvAdapter.getData());
        }

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if ( savedInstanceState != null ) {

            etHeaderTitle.setText(viewModel.getTitle());
            List<String> tagList = viewModel.getTagList();

            if ( tagList != null ) {
                for ( int i =0; i < tagList.size(); i++) {
                    addTag(tagList.get(i));
                }
            }

            if ( mRvAdapter != null ) {
                if ( mRvAdapter.getData() != null ) {
                    if ( mRvAdapter.getData().size() == 0) {
                        if ( viewModel.getItemList() != null && viewModel.getItemList().size() > 0 ) {
                            mRvAdapter.addData(viewModel.getItemList());
                        }
                    }
                }
            }
        }

    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        eventHandler(event);
    }

    /**
     * 이벤트 처리
     * @param event
     */
    private void eventHandler(Object event) {
        //
        // 경기일정 삽입
        //
        if ( event instanceof MatchEvent.ChoiceMatchEvent) {

            MatchEvent.ChoiceMatchEvent matchEvent = (MatchEvent.ChoiceMatchEvent)event;
            addMatchTag(matchEvent.getMatchTag());
            Toast.makeText(getContext(), "경기일정이 등록되었습니다. 게시글에 경기정보가 표시됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 프로그래스 보이기
     */
    @Override
    public void showLoading() {
        super.showLoading();
        flProgress.setVisibility(View.VISIBLE);
        mLoading.playAnimation();
    }

    public void showLoadingInfo(String text) {
        tvProgressInfo.setText(text);
    }

    /**
     * 프로그래스 감추기
     */
    @Override
    public void hideLoading() {
        super.hideLoading();
        flProgress.setVisibility(View.GONE);
        tvProgressInfo.setText("");
        mLoading.cancelAnimation();
    }

    /**
     * 일반 메시지(토스트)
     * @param message
     */
    @Override
    public void showMessage(String message) {
        super.showMessage(message);
        Toasty.info(getContext(), message).show();
    }

    /**
     * 에러 메시지(토스트)
     * @param errMessage
     */
    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getContext(), errMessage).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(PlainEditorViewModel.class);
    }


    /**
     *
     * 수정모드일 경우 기존데이터 바인딩
     *
     */
    private void setData() {

        if ( mPresenter.getPostEditorType() == PostEditorType.EDIT ) {
            assert getArguments() != null;
            ContentHeaderModel content = Parcels.unwrap(getArguments().getParcelable("ARGS_CONTENT_MODEL"));
            if ( content == null ) {
                return;
            }

            mPresenter.setContentData(content);
            etHeaderTitle.setText(content.getTitle());
            mPresenter.getParsingContent(content.getContent());
            List<String> tags = content.getTags();
            if ( tags != null && tags.size() > 0) {
                for(String tag : tags) {
                    addTag(tag);
                }
            }
        }
    }

    /**
     * 뷰 초기화
     */
    private void initView() {

        etChatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                if ( s.length() == 0 ) {
                    ivSendMessage.setVisibility(View.GONE);
                    tvTag.setVisibility(View.VISIBLE);
                } else {
                    ivSendMessage.setVisibility(View.VISIBLE);
                    tvTag.setVisibility(View.GONE);
                }
            }
        });

    }


    /**
     * 글쓰기 가능한 게시판을 불러와서 스피너에 바인딩한다.
     * 게시판형태를 서버에서 가져오도록 수정한다면 아래 코드는 변경되어야 한다.
     */
//    private void initSpinner() {
//
//        ArrayAdapter<BoardMasterModel> spinnerAdapter
//                = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_dropdown_item);
//
//        int position = 0;
//
//        // 글쓰기 가능한 게시판 목록을 가져온다.
//        List<BoardMasterModel> boards = AppDatabase.getInstance(getActivity()).boardsDao().getAvaibleCreateContentBoardList();
//
//        for(int i = 0; i < boards.size(); i++) {
//            spinnerAdapter.add(boards.get(i));
//
//            if ( boards.get(i).isSelected() == 1 ) {
//                position = i;
//            }
//        }
//
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnBoardList.setAdapter(spinnerAdapter);
//        spnBoardList.setSelection(position);
//    }

    /**
     * 태그 ChipClould 초기화
     */
    private void initTag() {

        ChipCloudConfig config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.none)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#efefef"))
                .uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(false)
                .showClose(Color.parseColor("#a6a6a6"), 500);

        mChip = new ChipCloud(getContext(), footerTagBox, config);
    }

    /**
     * 매치태그 ChipClould 초기화
     */
    private void initMatchTag() {

        ChipCloudConfig config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.none)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#EF5350"))
                .uncheckedTextColor(Color.parseColor("#EEEEEE"))
                .useInsetPadding(false)
                .showClose(Color.parseColor("#EEEEEE"), 500);

        mMatchChip = new ChipCloud(getContext(), footerMatchTagBox, config);
    }


    /**
     * RecyclerView 초기화
     */
    private void initRecyclerView() {

        OnItemDragListener listener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) { }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) { }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) { }
        };

        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) { }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) { }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
                canvas.drawColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.md_light_blue_100));
            }
        };

        /*--------------------------------------------------------*/

        mRvAdapter = new PlainEditorRvAdapter(new ArrayList<>());

        mItemDragAndSwipeCallback = new ItemDragAndSwipeCallback2(mRvAdapter);
        mItemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
        mItemTouchHelper.attachToRecyclerView(rvList);

        mRvAdapter.enableSwipeItem();
        mRvAdapter.setOnItemSwipeListener(onItemSwipeListener);
        mRvAdapter.enableDragItem(mItemTouchHelper);
        mRvAdapter.setOnItemDragListener(listener);
        mRvAdapter.setHeaderFooterEmpty(true, true);

        //
        // PlayerTalk ( CellType : 5 )
        //
//        if ( mCellType == PostCellType.PLAYER_TALK) {
//            View playerHeader
//                    = getActivity().getLayoutInflater().inflate(R.layout.row_editor_player_header_item, (ViewGroup)rvList.getParent(), false);
//
//            if ( mPresenter.getPlayer() != null ) {
//                TextView tvPlayerName = playerHeader.findViewById(R.id.tv_player_name);
//                TextView tvTeamName = playerHeader.findViewById(R.id.tv_player_team_name);
//                ImageView ivPlayerAvatar = playerHeader.findViewById(R.id.iv_player_avatar);
//                tvPlayerName.setText(mPresenter.getPlayer().getPlayerName());
//                tvTeamName.setText(mPresenter.getPlayer().getTeamName());
//
//                GlideApp.with(getContext())
//                        .load(mPresenter.getPlayer().getPlayerLargeImageUrl())
//                        .centerCrop()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.ic_person_grey_vector)
//                        .into(ivPlayerAvatar);
//            }
//
//            mRvAdapter.addHeaderView(playerHeader);
//        }

        //
        // TeamTalk ( CellType : 6 )
        //
//        if ( mCellType == PostCellType.TEAM_TALK) {
//            View playerHeader
//                    = getActivity().getLayoutInflater().inflate(R.layout.row_editor_team_header_item, (ViewGroup)rvList.getParent(), false);
//
//            if ( mPresenter.getTeam() != null ) {
//                TextView tvTeamName = playerHeader.findViewById(R.id.tv_team_name);
//                ImageView ivEmblem = playerHeader.findViewById(R.id.iv_emblem);
//                tvTeamName.setText(mPresenter.getTeam().getTeamName());
//
//                GlideApp.with(getContext())
//                        .load(mPresenter.getTeam().getEmblemUrl())
//                        .centerCrop()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.ic_empty_emblem_vector_1)
//                        .into(ivEmblem);
//            }
//
//
//            mRvAdapter.addHeaderView(playerHeader);
//        }

//        if ( mCellType == PostCellType.MATCH_CHAT) {
//
//            MatchModel match = mPresenter.getMatch();
//            if ( match != null ) {
//
//                View matchHeader
//                        = getActivity().getLayoutInflater().inflate(R.layout.row_editor_match_header_item, (ViewGroup)rvList.getParent(), false);
//
//                LinearLayout llComp = matchHeader.findViewById(R.id.ll_competition_container);
//                llComp.setVisibility(View.GONE);
//                //tv_match_date
//                TextView tcMatchDate = matchHeader.findViewById(R.id.tv_match_date);
//                tcMatchDate.setText(match.getMatchDate4());
//                TextView tvHomeTeamName = matchHeader.findViewById(R.id.tv_home_team_name);
//                TextView tvAwayTeamName = matchHeader.findViewById(R.id.tv_away_team_name);
//                ImageView ivHomeEmblem = matchHeader.findViewById(R.id.iv_home_emblem);
//                ImageView ivAwayEmblem = matchHeader.findViewById(R.id.iv_away_emblem);
//                tvHomeTeamName.setText(match.getHomeTeamName());
//                tvAwayTeamName.setText(match.getAwayTeamName());
//
//                GlideApp.with(getContext())
//                        .load(match.getHomeTeamEmblemUrl())
//                        .centerCrop()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.ic_empty_emblem_vector_1)
//                        .into(ivHomeEmblem);
//
//                GlideApp.with(getContext())
//                        .load(match.getAwayTeamEmblemUrl())
//                        .centerCrop()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.ic_empty_emblem_vector_1)
//                        .into(ivAwayEmblem);
//
//                mRvAdapter.addHeaderView(matchHeader);
//            }
//        }

//        View allowCommentHeader
//                = getActivity().getLayoutInflater().inflate(R.layout.row_editor_allow_comment_option_header_item, (ViewGroup)rvList.getParent(), false);
//        spnBoardList = allowCommentHeader.findViewById(R.id.editor_board);
//        swAllowcomment = allowCommentHeader.findViewById(R.id.allow_comment);
//        mRvAdapter.addHeaderView(allowCommentHeader);

        View headerView = getLayoutInflater().inflate(R.layout.row_editor_title_item, (ViewGroup) rvList.getParent(), false);
        etHeaderTitle = headerView.findViewById(R.id.et_title);
        etHeaderTitle.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            //
            // 제목 입력후 다음 버튼을 누르면 내용입력차으로 포커스를 변경한다.
            //
            if ( actionId == EditorInfo.IME_ACTION_NEXT) {
                llChatContainer.setVisibility(View.VISIBLE);
                etChatBox.requestFocus();
                handled = true;
            }
            return handled;
        });

        //mRvAdapter.setHeaderView(headerView, 1);
        mRvAdapter.addHeaderView(headerView);

        View footerView = getLayoutInflater().inflate(R.layout.row_editor_tag_container_item, (ViewGroup) rvList.getParent(), false);
        footerTagBox = footerView.findViewById(R.id.fbx_tag_box);
        initTag();
        mRvAdapter.setFooterView(footerView, 0);

        View footerView2 = getLayoutInflater().inflate(R.layout.row_editor_tag_container_item, (ViewGroup) rvList.getParent(), false);
        footerMatchTagBox = footerView2.findViewById(R.id.fbx_tag_box);
        initMatchTag();
        mRvAdapter.setFooterView(footerView2, 1);

        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(mRvAdapter);

        mRvAdapter.setOnItemClickListener((adapter, view, position) -> {

            MultiItemEntity item = mRvAdapter.getItem(position);

            if ( item instanceof EditorTextModel ) {
                etChatBox.setText(((EditorTextModel) item).getMainText());
                etChatBox.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etChatBox, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        mRvAdapter.setOnItemChildClickListener((adapter, view, position) -> {

            if ( view.getId() == R.id.editor_selected ) {
                mRvAdapter.setAllSelectable(false);
                EditorImageModel data = (EditorImageModel)mRvAdapter.getData().get(position);
                data.setSelected(true);
                mRvAdapter.notifyDataSetChanged();
            }
        });

        mRvAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rvList.smoothScrollToPosition(mRvAdapter.getItemCount());
            }
        });
    }


    private void initKeyboardStateObserver() {

        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                isOpen -> {
                    if ( isOpen ) {
                        if ( etHeaderTitle.isFocused()) {
                            //
                            // 제목을 입력할때는 글쓰기, 글쓰기 메뉴는 숨김처리한다.
                            //
                            hsvEditorTools.setVisibility(View.GONE);
                            llChatContainer.setVisibility(View.GONE);
                        } else {
                            if ( mRvAdapter != null && rvList != null ) {
                                rvList.smoothScrollToPosition(mRvAdapter.getItemCount());
                            }
                        }
                    } else {
                        //
                        // 키보드가 내려가면 글쓰기 메뉴는 숨김처리한다.
                        //
                        if ( llChatContainer != null ) {
                            llChatContainer.setVisibility(View.VISIBLE);
                        }
                        hsvEditorTools.setVisibility(View.GONE);
                        ivAttachment.setImageResource(R.drawable.ic_add_attachment);
                        ivAttachment.setSelected(false);
                    }
                });
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 내용입력
     * @param view
     */
    @OnClick(R.id.iv_send_message)
    public void onClickSend(View view) {

        String message = etChatBox.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            etChatBox.setText("");
            addBodyContent(message);
        }
    }

    /**
     * 글쓰기 메뉴 열기 및 접기
     * @param view
     */
    @OnClick(R.id.iv_attachment)
    public void onClickAttachment(View view) {

        boolean b = view.isSelected();
        if ( b ) {
            hsvEditorTools.setVisibility(View.GONE);
            ivAttachment.setImageResource(R.drawable.ic_add_attachment);
        } else {
            hsvEditorTools.setVisibility(View.VISIBLE);
            ivAttachment.setImageResource(R.drawable.ic_close);
        }
        view.setSelected(!b);
    }

    /**
     * 글쓰기 메뉴 버튼 클릭
     * @param view
     */
    @OnClick({R.id.action_undo,
            R.id.action_redo,
            R.id.action_bold,
            R.id.action_heading1,
            R.id.action_heading2,
            R.id.action_insert_image,
            R.id.action_insert_link,
            R.id.action_strikethrough,
            R.id.action_quote,
            R.id.action_attachment_match,
            R.id.action_insert_video,
            R.id.tv_tag})
    public void onClickEditor(View view) {

        switch (view.getId()) {
            case R.id.action_insert_image:
                //
                // 이미지 삽입 - 로컬, 링크
                //
                choiceInsertImageTypeDialog();

                break;
            case R.id.action_insert_link:
                //
                // 링크URL 삽입
                //
                openLinkDialog();

                break;
            case R.id.action_attachment_match:
                //
                // 경기일정 정보 입력
                //
                final List<Product> fakeProducts = Product.createFakeProducts();
                MatchListDialogFragment.newInstance(fakeProducts.get(0)).show(getFragmentManager(), null);
                break;
            case R.id.action_insert_video:
                openVideoDialog();
                break;
            case R.id.tv_tag:
                //
                // 태그입력
                //
                openTagDialog();
                break;
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 이미지 삽입 타입 선택
     *  - 로컬이미지
     *  - 링크이미지
     */
    private void choiceInsertImageTypeDialog() {

        new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .title(R.string.insert_image_title)
                .items(R.array.insert_image_array)
                .itemsCallback((dialog, view, which, text) -> {
                    if ( which == 0 ) {
                        mPresenter.openLocalImagePicker();
                    } else {
                        openLinkImageDialog();
                    }
                })
                .show();
    }

    /**
     * 링크 삽입 다이얼로그
     */
    private void openLinkDialog() {

        new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .title("링크")//R.string.input
                .content("링크 주소를 입력해주세요") //R.string.input_content
                .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                .cancelListener(dialog -> hideLoading())
                .input("http://", null, (dialog, input) -> {
                    showLoading();
                    mPresenter.getLinkPreviewInfo(input.toString());
                }).show();
    }

    /**
     * 이미지 링크 입력 다이얼로그
     */
    private void openLinkImageDialog() {

        new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .title("이미지 링크")
                .content("이미지 링크 주소를 입력해주세요")
                .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                .input("https://", "", (dialog, input) -> App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_image, input.toString(), PostBodyType.PLAIN))).show();
    }

    /**
     * 태그 입력 다이얼로그
     */
    private void openTagDialog() {

        new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .title("태그")//R.string.input
                .content("태그를 입력하세요") //R.string.input_content
                .inputType(InputType.TYPE_CLASS_TEXT)
                .onNegative((dialog, which) -> hideLoading())
                .cancelListener(dialog -> hideLoading())
                .input("쉼표(,)로 태그 구분", null, (dialog, input) -> {
                    if (mRvAdapter != null) {
                        if ( mChip != null && !TextUtils.isEmpty(input.toString()) ) {
                            String[] tags = input.toString().split(",");
                            for(String tag : tags) {

                                if ( !TextUtils.isEmpty(tag.trim())) {
                                    addTag(tag.trim());
                                }
                            }
                        }
                    }
                }).show();
    }

    /**
     * 비디오 링크 삽입
     */
    private void openVideoDialog() {

        new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .title("동영상 링크")//R.string.input
                .content("동영상 링크를 입력해주세요\n(현재 소스코드 복사 기능은 동작하지 않습니다.)")
                .inputType(InputType.TYPE_TEXT_VARIATION_URI/* | InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT*/)
                .cancelListener(dialog -> {
                })
                .input("http://", null, (dialog, input) -> {
                    showLoading();
                    mPresenter.getLinkPreviewInfo(input.toString());
                }).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void createContent() {

        if ( !checkContentData()) {
            hideLoading();
            return;
        }

        int boardId = getSelectedBoardid();
        String bodyText = makeHtml();
        String jsonImageList = getJsonImageList();

        switch (mPresenter.getPostEditorType()) {
            case INSERT:

                if ( boardId == EntityType.BOARD_ISSUE ) {
                    mPresenter.postIssue(App.getAccountManager().getHifiveAccessToken(),
                            boardId,
                            getTitle(),
                            bodyText,
                            getTagJson(),
                            TextUtils.isEmpty(jsonImageList) ? null : jsonImageList, // Image
                            PostBodyType.HTML.value(),
                            EntityType.EDITOR_PLATFORM_ANDROID);

                } else {
                    mPresenter.postContent(App.getAccountManager().getHifiveAccessToken(),
                            boardId,
                            getTitle(),
                            getPreview(),
                            bodyText,
                            mSelectedItem != null && mSelectedItem.getMentionType().equals("M") ? mSelectedItem.getMentionId() : null,
                            mSelectedItem != null && mSelectedItem.getMentionType().equals("P") ? mSelectedItem.getMentionId() : null,
                            mSelectedItem != null && mSelectedItem.getMentionType().equals("T") ? mSelectedItem.getMentionId() : null,
                            getTagJson(), // tag
                            TextUtils.isEmpty(jsonImageList) ? null : jsonImageList, // Image
                            PostBodyType.HTML, // boardtyoe
                            mCellType,
                            getAllowCommentValue()); // allow_comment
                }

                break;
            case EDIT:
                    mPresenter.updateContent(App.getAccountManager().getHifiveAccessToken(),
                            mPresenter.getContentId(),
                            boardId,
                            getTitle(),
                            getPreview(),
                            bodyText,
                            null,
                            mCellType == PostCellType.PLAYER_TALK && mPresenter.getPlayer() != null ? mPresenter.getPlayer().getPlayerId() : null,
                            mCellType == PostCellType.TEAM_TALK && mPresenter.getTeam() != null ? mPresenter.getTeam().getTeamId() : null,
                            getTagJson(), // tags
                            TextUtils.isEmpty(jsonImageList) ? null : jsonImageList,
                            PostBodyType.HTML,
                            PostCellType.BASE,
                            getAllowCommentValue()); // allow_comment
                break;
        }
    }

    private String getTagJson() {

        List<String> tagList = new ArrayList<>();
        // 일반태그
        for ( int i = 0; i < footerTagBox.getChildCount(); i++ ) {
            tagList.add(mChip.getLabel(i));
        }

        // 매치태그
        for ( int i = 0; i < footerMatchTagBox.getChildCount(); i++ ) {
            tagList.add(mMatchChip.getLabel(i));
        }

        Gson gson = new Gson();

        return gson.toJson(tagList);
    }

    /*--------------------------------------------------
     * 업로드된 이미지 경로를 josn 형태로 리턴한다.
     * @return
     *--------------------------------------------------*/
    private String getJsonImageList() {

        List<StreamViewerModel> imageList = new ArrayList<>();

        for ( int i = 0; i < mRvAdapter.getData().size(); i++) {

            MultiItemEntity item = mRvAdapter.getItem(i);

            // 1. 이미지
            if ( item instanceof EditorImageModel) {
                imageList.add(new StreamViewerModel(StreamViewerModel.IMAGE, ((EditorImageModel) item).getUrl()));
            }

            // 2. mp4
            if ( item instanceof EditorVideoModel) {
                imageList.add(new StreamViewerModel(StreamViewerModel.MP4, ((EditorVideoModel) item).getUrl()));
            }
        }

        String jsonImageList = "";

        if ( imageList.size() > 0) {
            Gson gson = new Gson();
            jsonImageList = gson.toJson(imageList);
        }

        return jsonImageList;
    }


    /*----------------------------------------------
     * 스피너에서 선택한 게시판 정보를 가져온다.
     * @return
     *----------------------------------------------*/
    private int getSelectedBoardid() {

        //BoardMasterModel board = (BoardMasterModel)spnBoardList.getSelectedItem();
        //return board.getBoardId();

        if ( mSelectedItem == null )
            return -1;

        return mSelectedItem.getBoardId();
    }


    /*--------------------------------------------------
     * 입력한 내용을 html로 만든다.
     * @return
     *--------------------------------------------------*/
    @NonNull
    private String makeHtml() {

        StringBuilder html = new StringBuilder();

        for(int i =0;  i < mRvAdapter.getData().size(); i++ ) {

            MultiItemEntity data = mRvAdapter.getData().get(i);
            if ( data instanceof EditorImageModel) {
                html.append(((EditorImageModel) data).getHtml());
            } else if ( data instanceof EditorTextModel ) {
                html.append(((EditorTextModel) data).getHtml());
            } else if ( data instanceof EditorLinkModel ) {
                html.append(((EditorLinkModel) data).getHtml(getContext()));
            } else if ( data instanceof EditorVideoModel ) {
                // 비디오가 첫번째 위치면 자동재생 되도록 설정
                html.append(((EditorVideoModel) data).getHtml(getContext(), i == 0 ? true : false));
            }
        }

        return html.toString();
    }


    private boolean checkContentData() {

        if ( !App.getAccountManager().isAuthorized() ) {
            Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView(), "로그인을 해주세요", Snackbar.LENGTH_LONG)
                    //.setAction("A", myOnClickListener)
                    .setAction("이동", v -> mPresenter.openLoginActivity())
                    .show();
            return false;
        }

        if (TextUtils.isEmpty(etHeaderTitle.getText())
                && mRvAdapter.getData().size() == 0) {
            Snackbar.make(flContainer, "제목 또는 내용을 입력해주세요", Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> etHeaderTitle.requestFocus())
                    .show();
            return false;
        }


        //
        // 내용만 입력한 경우
        //  - 내용이 텍스트인 경우     : 내용의 일부를 제목으로 넣는다.
        //  - 내용이 텍스크가 아닌 경우 : 제목을 입력하라는 메시지를 띄운다.
        //
        if (TextUtils.isEmpty(etHeaderTitle.getText())
                && mRvAdapter.getData().size() > 0 ) {

            if ( mRvAdapter.getData().get(0) instanceof EditorTextModel ) {

                EditorTextModel item = (EditorTextModel) mRvAdapter.getData().get(0);

                if ( item.getMainText().length() > 50 ){
                    etHeaderTitle.setText(item.getMainText().substring(0, 49));
                } else {
                    etHeaderTitle.setText(item.getMainText());
                }
            } else {
                Snackbar.make(flContainer, "제목을 입력해주세요", Snackbar.LENGTH_LONG)
                        .setAction("이동", v -> etHeaderTitle.requestFocus())
                        .show();
                return false;
            }
        }


//        if (TextUtils.isEmpty(etHeaderTitle.getText())) {
//            Snackbar.make(flContainer, "제목을 입력해주세요", Snackbar.LENGTH_LONG)
//                    .setAction("이동", v -> etHeaderTitle.requestFocus())
//                    .show();
//            return false;
//        }
//
//        if ( mRvAdapter.getData().size()  < 1) {
//            Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView(), "내용을 입력해주세요\n내용입력후 추가 버튼을 눌러주세요.", Snackbar.LENGTH_LONG)
//                    .show();
//            return false;
//        }

        return true;
    }

    private String getTitle() {

        if ( etHeaderTitle != null ) {
            return etHeaderTitle.getText().toString();
        }

        return "";
    }


    private int getBoardIdPosition(int boardId) {

        for ( int i =0 ; i < mBoardId.length; i++) {

            if ( mBoardId[i] == boardId) {
                return i;
            }
        }

        return 0;
    }

    /**
     * 댓글 허용 여부 헤더
     * @return
     */
//    private View getAllowCommentHeader() {
//        View allowCommentHeader
//                = getActivity().getLayoutInflater().inflate(R.layout.row_editor_allow_comment_option_header_item, (ViewGroup)rvList.getParent(), false);
//
//        return allowCommentHeader;
//    }

    private int getAllowCommentValue() {

        return 1;
    }

    private String getPreview() {

        StringBuilder preview = new StringBuilder();

        for(int i =0;  i < mRvAdapter.getData().size(); i++ ) {

            MultiItemEntity item = mRvAdapter.getItem(i);

            if ( item instanceof EditorTextModel) {
                preview.append(((EditorTextModel) item).getMainText());
            }
        }

        return preview.toString().length() > 200 ? preview.toString().substring(0, 200) : preview.toString();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 제목 헤더
     * @return
     */




    private void addTitle(String text) {

        if ( mRvAdapter != null && !TextUtils.isEmpty(text) ) {

            EditorTitleModel titleModel = (EditorTitleModel)mRvAdapter.getData().get(0);
            titleModel.setTitle(text);
            mRvAdapter.notifyItemChanged(0);
        }
    }

    private void addBodyContent(String text) {

        if ( mRvAdapter != null && !TextUtils.isEmpty(text) ) {

            Handler handler = new Handler();
            final Runnable r = () -> mRvAdapter.addData(new EditorTextModel(text));
            handler.post(r);
        }
    }

    public void addLinkContent(String link, OpenGraphModel og) {
        EditorLinkModel linkModel = new EditorLinkModel();
        linkModel.setLink(link);
        linkModel.setOg(og);
        mRvAdapter.addData(linkModel);
    }

    public void addTextContent(String text) {

        if ( mRvAdapter != null && !TextUtils.isEmpty(text) ) {
            mRvAdapter.addData(new EditorTextModel(text));
        }
    }

    /**
     * 이미지 삽입
     * @param src
     */
    public void addImageContent(String src) {

        if ( mRvAdapter != null && !TextUtils.isEmpty(src) ) {
            EditorImageModel imageModel = new EditorImageModel(src);
            mRvAdapter.addData(imageModel);
        }
    }


    public void addVideoContent(String src) {

        if ( mRvAdapter != null && !TextUtils.isEmpty(src) ) {
            EditorVideoModel imageModel = new EditorVideoModel(src);
            mRvAdapter.addData(imageModel);
        }
    }

    public void addCardLinkContent(String href, String src, String title, String desc) {

        if ( mRvAdapter != null && !TextUtils.isEmpty(href) ) {
            EditorLinkModel linkModel = new EditorLinkModel();
            linkModel.setLink(href);
            OpenGraphModel og = new OpenGraphModel();
            og.setImage(src);
            og.setTitle(title);
            //og.setDescription(desc);
            og.setCannonicalUrl(desc);
            linkModel.setOg(og);
            mRvAdapter.addData(linkModel);
        }
    }

    /**
     * 태그 추가
     * todo 태그명이 같은 경우 필터처리해 주는 로직이 필요함.
     * @param name
     */
    private void addTag(String name) {

        if ( mChip == null )
            return;

        mChip.addChip(name);
    }

    /**
     * 경기일정 태그 추
     * @param name
     */
    private void addMatchTag(String name) {

        mMatchChip.addChip(name);
    }

    public boolean isTitleAvailable() {

        if ( etHeaderTitle == null) {
            return false;
        }

        if ( !TextUtils.isEmpty(etHeaderTitle.getText())) {
            return false;
        }

        return true;
    }

    public void setTitle(String text) {

        if ( etHeaderTitle != null ) {
            etHeaderTitle.setText(text);
        }
    }


}
