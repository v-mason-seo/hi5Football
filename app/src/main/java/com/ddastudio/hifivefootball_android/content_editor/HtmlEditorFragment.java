package com.ddastudio.hifivefootball_android.content_editor;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.data.event.EditorEvent;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.content.ContentModel;
import com.ddastudio.hifivefootball_android.data.model.StreamViewerModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.richeditor.RichEditor;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.ddastudio.hifivefootball_android.utils.RxBus;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipListener;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class HtmlEditorFragment extends BaseFragment {

    @BindView(R.id.fbx_board) FlexboxLayout fbxBoard;
    @BindView(R.id.editor_title) EditText etTitle;
    @BindView(R.id.editor) RichEditor mEditor;
    @BindArray(R.array.board_id) int[] mBoardId;
    @BindArray(R.array.board_name) String[] mBoardName;

    StringBuilder mContentText;
    int boardid;
    //int insert_or_update;
    PostEditorType editorType;
    ContentHeaderModel contentHeaderModel;
    ContentModel contentsModel;

    HtmlEditorPresenter mPresenter;
    Unbinder unbinder;
    RxBus _rxBus;
    CompositeDisposable _disposables;

    public HtmlEditorFragment() {
        // Required empty public constructor
    }

    public static HtmlEditorFragment newInstance(int insertOrUpdate, int boardId, ContentHeaderModel board, ContentModel content) {

        HtmlEditorFragment fragment = new HtmlEditorFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("ARGS_INSERT_OR_UPDATE", insertOrUpdate);
        bundle.putInt("ARGS_SELECTED_BOARD_ID", boardId);
        bundle.putParcelable("ARGS_BOARD_MODEL", Parcels.wrap(board));
        bundle.putParcelable("ARGS_CONTENT_MODEL", Parcels.wrap(content));

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int insert_or_update = getArguments().getInt("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        editorType = PostEditorType.toEnum(insert_or_update);
        contentHeaderModel = Parcels.unwrap(getArguments().getParcelable("ARGS_BOARD_MODEL"));
        contentsModel = Parcels.unwrap(getArguments().getParcelable("ARGS_CONTENT_MODEL"));
        boardid = getArguments().getInt("ARGS_SELECTED_BOARD_ID", EntityType.POSTS_GENERAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_html_editor, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPresenter = new HtmlEditorPresenter();
        mPresenter.attachView(this);

        initBoardTags();
        initEditor();
        setData();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _rxBus = App.getInstance().bus();
    }

    @Override
    public void onStart() {
        super.onStart();
        _disposables = new CompositeDisposable();

        _disposables.add(
                _rxBus
                    .asFlowable()
                    .subscribe(
                            event -> eventHandler(event)));
    }

    @Override
    public void onStop() {
        super.onStop();
        if ( _disposables != null ) {
            _disposables.clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ( unbinder != null ) {
            unbinder.unbind();
        }
    }

    /**
     * 이벤트 처리
     * @param event
     */
    private void eventHandler(Object event) {

        if (event instanceof EditorEvent.SelectBoardEvent) {

            // *** 게시판 선택 ***
            boardid = ((EditorEvent.SelectBoardEvent) event).getBoardId();

        } else if ( event instanceof EditorEvent.ActionButtonClickEvent) {

            // *** 글관련 버튼(진하게 글씨크기 등 ) ***
            onClick(((EditorEvent.ActionButtonClickEvent) event).getViewId(),
                    ((EditorEvent.ActionButtonClickEvent) event).getValue());

        } else if ( event instanceof EditorEvent.PostContentEvent) {

            // *** 글 보내기 ***

            if ( !(((EditorEvent.PostContentEvent) event).getEditorType() == PostBodyType.HTML)) {
                return;
            }

            if ( !checkContentData()) {
                return;
            }

            String jsonImageList = getJsonImageList();

            switch (editorType) {
                case INSERT:
                    mPresenter.postContent(App.getAccountManager().getHifiveAccessToken(),
                            boardid,
                            etTitle.getText().toString(),
                            getPreview(),
                            mEditor.getHtml(),
                            null, // ip
                            "", // tag
                            TextUtils.isEmpty(jsonImageList) ? null : jsonImageList, // Image
                            PostBodyType.HTML.value(), // boardtyoe
                            PostCellType.BASE,
                            1
                            //((EditorEvent.PostContentEvent) event).isAllowComment() ? 1 : 0
                    ); // allow_comment
                    break;
                case EDIT:
                    mPresenter.updateContent(App.getAccountManager().getHifiveAccessToken(),
                            contentHeaderModel.getContentId(),
                            etTitle.getText().toString(),
                            getPreview(),
                            mEditor.getHtml(),
                            boardid,
                            "", // tags
                            TextUtils.isEmpty(jsonImageList) ? null : jsonImageList,
                            1, // allow_comment
                            PostBodyType.HTML.value(),
                            1 // userid
                    );
                    break;
            }
        }
    }

    ChipCloud chipCloud;

    /**
     * 게시판 태그 초기화
     * @param selectedBoardType
     */
    private void initBoardTags() {

        ChipCloudConfig config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.single)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                .uncheckedTextColor(Color.parseColor("#000000"));

        chipCloud = new ChipCloud(getContext(), fbxBoard, config);

        for(int i=0; i < mBoardName.length; i++) {
            chipCloud.addChip(mBoardName[i]);
        }

        chipCloud.setChecked(getBoardIndex(boardid));
        chipCloud.setListener(new ChipListener() {
            @Override
            public void chipCheckedChange(int index, boolean checked, boolean userClick) {

                if ( checked == true ) {
                    boardid = mBoardId[index];
                }
            }
        });
    }

    private int getBoardIndex(int boardId) {

        for (int i = 0; i < mBoardId.length; i++) {
            if ( boardId == mBoardId[i]) {
                return i;
            }
        }

        return -1;
    }

    private void initEditor() {

        //mEditor.setNestedScrollingEnabled(true);

        mContentText = new StringBuilder();
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(14);
        mEditor.setEditorFontColor(CommonUtils.getColor(getContext(), R.color.md_grey_800));
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder(" ( Insert text here... )");
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setInputEnabled(false);

        mEditor.setOnTextChangeListener(text -> mContentText.append(text));
    }

    private void setData() {

        if ( editorType == PostEditorType.INSERT) {
            return;
        }

        if ( contentHeaderModel != null ) {

            etTitle.setText(contentHeaderModel.getTitle());
        }

        if ( contentsModel != null && contentsModel.getBodytype() == PostBodyType.HTML) {

            mEditor.setHtml(contentsModel.getContent());
            boardid = contentsModel.getBoardId();
        }
    }

    public void onClick(int viewId, String value) {

        switch (viewId) {
            case R.id.action_undo:
                mEditor.undo();
                break;
            case R.id.action_redo:
                mEditor.redo();
                break;
            case R.id.action_heading1:
                mEditor.setHeading(1);
                break;
            case R.id.action_heading2:
                mEditor.setHeading(2);
                break;
            case R.id.action_bold:
                mEditor.setBold();
                break;
            case R.id.action_strikethrough:
                mEditor.setStrikeThrough();
                break;
            case R.id.action_quote:
                mEditor.setBlockquote();
                break;
            case R.id.action_insert_image:
                mEditor.focusEditor();
                mEditor.insertImage(value, "");
                break;
            case R.id.action_insert_link:
                mEditor.focusEditor();
                mEditor.insertLink(value, "");
                break;
        }
    }

    private String getJsonImageList() {

        //Log.d("hong", "HTML - " + mEditor.getHtml());
        String jsonImageList = "";
        Document doc = Jsoup.parse(mEditor.getHtml());
        Elements elements = doc.select("img");

        List<StreamViewerModel> imageList = new ArrayList<>();
        for (Element element : elements) {
            String url = element.absUrl("src");
            imageList.add(new StreamViewerModel(StreamViewerModel.IMAGE, url));
        }

        if ( imageList.size() > 0) {
            Gson gson = new Gson();
            jsonImageList = gson.toJson(imageList);
        }

        return jsonImageList;
    }

    private boolean checkContentData() {

        if ( !App.getAccountManager().isAuthorized() ) {
            Snackbar.make(getActivity().getWindow().getDecorView(), "로그인을 해주세요", Snackbar.LENGTH_LONG)
                    //.setAction("A", myOnClickListener)
                    .setAction("이동", v -> mPresenter.openLoginActivity())
                    .show();
            return false;
        }

        if (TextUtils.isEmpty(etTitle.getText())) {
            Snackbar.make(getActivity().getWindow().getDecorView(), "제목을 입력해주세요", Snackbar.LENGTH_LONG)
                    //.setAction("A", myOnClickListener)
                    .setAction("이동", v -> etTitle.requestFocus())
                    .show();
            return false;
        }

        if ( TextUtils.isEmpty(mEditor.getHtml())) {
            Snackbar.make(getActivity().getWindow().getDecorView(), "내용을 입력해주세요", Snackbar.LENGTH_LONG)
                    //.setAction("A", myOnClickListener)
                    .setAction("이동", v -> mEditor.focusEditor())
                    .show();
            return false;
        }

        return true;
    }

    private String getPreview() {

        Document doc = Jsoup.parse(mEditor.getHtml());
        return doc.text();
    }
}
