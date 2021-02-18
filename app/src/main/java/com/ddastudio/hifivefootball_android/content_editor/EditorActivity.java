package com.ddastudio.hifivefootball_android.content_editor;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.data.event.EditorEvent;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.ui.OrderTest.MatchListDialogFragment;
import com.ddastudio.hifivefootball_android.ui.OrderTest.Product;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.zhihu.matisse.Matisse;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class EditorActivity extends BaseActivity {

    @BindView(R.id.editor_toolbar) Toolbar mToolbar;
    @BindView(R.id.editor_loading) LottieAnimationView mLoading;
    @BindView(R.id.action_heading1) ImageView ivHeading1;
    @BindView(R.id.action_heading2) ImageView ivHeadgin2;
    @BindView(R.id.action_quote) ImageView ivQuote;
    @BindView(R.id.action_insert_image) ImageView ivInsertImage;
    @BindView(R.id.action_insert_link) ImageView ivInsertLink;
    @BindView(R.id.action_undo) ImageView ivUndo;
    @BindView(R.id.action_redo) ImageView ivRedo;
    @BindView(R.id.action_bold) ImageView ivBold;
    @BindView(R.id.action_strikethrough) ImageView ivStrikethrough;
    @BindArray(R.array.posts_editor_tabs_array) String[] mVpTitles;
    @BindString(R.string.activity_title_editor_post) String mTitleNew;
    @BindString(R.string.activity_title_editor_modify) String mTitleModify;

    EditorPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        // 전달된 파라미터 받기
        //
        Intent intent = getIntent();
        int insertOrUpdate = intent.getIntExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        int selectedBoardId = intent.getIntExtra("ARGS_BOARD_TYPE", PostBoardType.FOOTBALL.value());
        int argsCellType = intent.getIntExtra("ARGS_CELL_TYPE", PostCellType.BASE.value());
        int testMode = intent.getIntExtra("ARGS_TEST_MODE", 0);
        ContentHeaderModel content = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_CONTENT"));
        PostEditorType editorType = PostEditorType.toEnum(insertOrUpdate);
        PostCellType cellType = PostCellType.toEnum(argsCellType);

        //
        // 수정모드일 경우 테마를 변경한다.
        //
        if (  editorType == PostEditorType.EDIT ) {
            setTheme(R.style.ModifyTheme);
        }
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        mPresenter = new EditorPresenter(selectedBoardId);
        mPresenter.attachView(this);

        initKeyboardStateObserver();
        initToolbar(editorType);

        if ( savedInstanceState == null ) {

            if ( testMode == 1) {
                // 테스트 모드
                initEditorFragment2();
            } else {
                // 배포모드
                if ( cellType == PostCellType.PLAYER_TALK  || cellType == PostCellType.TEAM_TALK || cellType == PostCellType.MATCH_CHAT ) {
                    // 선수
                    PlayerModel player = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_PLAYER"));
                    // 팀
                    TeamModel team = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_TEAM"));
                    // 매치
                    MatchModel match = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_MATCH"));
                    initEditorFragment(insertOrUpdate, selectedBoardId, argsCellType, player, team, match, content);
                } else {
                    initEditorFragment(insertOrUpdate, selectedBoardId, argsCellType, content);
                }
            }

        }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mPresenter.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {

            // 이미지 선택 -> 이미지 압축 -> 이미지 S3 업로드
            List<Uri> obtainResults = Matisse.obtainResult(data);
            mPresenter.imageCompressNUpload(obtainResults);
        }
    }

    @Override
    protected void onDestroy() {

        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
        eventHandler(event);
    }

    /**
     * 로딩 보이기
     */
    @Override
    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
        mLoading.playAnimation();
    }

    /**
     * 로딩 감추기
     */
    @Override
    public void hideLoading() {
        mLoading.setVisibility(View.INVISIBLE);
        mLoading.cancelAnimation();
    }

    /**
     * 토스트 메시지 보여주기
     * @param message
     */
    @Override
    public void showMessage(String message) {
        super.showMessage(message);
        Toasty.normal(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    /**
     * 토스트 에러 메시지 보여주기
     * @param errMessage
     */
    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getApplicationContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 이미지 삽입 타입 선택
     *  - 로컬이미지
     *  - 링크이미지
     */
    public void choiceInsertImageTypeDialog() {

        new MaterialDialog.Builder(this)
                .title(R.string.insert_image_title)
                .items(R.array.insert_image_array)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if ( which == 0 ) {
                            mPresenter.openLocalImagePicker();
                        } else {
                            openLinkImageDialog();
                        }
                    }
                })
                .show();
    }

    /**
     * 링크 삽입 다이얼로그
     */
    public void openLinkDialog() {

        new MaterialDialog.Builder(this)
                .title("링크")//R.string.input
                .content("링크 주소를 입력해주세요") //R.string.input_content
                .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        hideLoading();
                    }
                })
                .input("http://", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        showLoading();
                        //mPresenter.getOpenGraphMetaTag(input.toString());
                        mPresenter.getLinkPreviewInfo(input.toString());
                    }
                }).show();
    }

    public void openTagDialog() {

        new MaterialDialog.Builder(this)
                .title("태그")//R.string.input
                .content("태그를 입력하세요") //R.string.input_content
                .inputType(InputType.TYPE_CLASS_TEXT)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        hideLoading();
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        hideLoading();
                    }
                })
                .input("쉼표(,)로 태그 구분", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_tag, input.toString(), PostBodyType.PLAIN, null));
                    }
                }).show();
    }

    /**
     * 비디오 링크 삽입
     */
    public void openVideoDialog() {

        new MaterialDialog.Builder(this)
                .title("동영상 링크")//R.string.input
                .content("동영상 링크를 입력해주세요\n(현재 소스코드 복사 기능은 동작하지 않습니다.)")
                .inputType(InputType.TYPE_TEXT_VARIATION_URI/* | InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT*/)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                })
                .input("http://", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        showLoading();
                        mPresenter.getLinkPreviewInfo(input.toString());
                        //App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_video, input.toString(), PostBodyType.PLAIN));
                    }
                }).show();
    }

    /**
     * 이미지 링크 입력 다이얼로그
     */
    public void openLinkImageDialog() {

        new MaterialDialog.Builder(this)
                .title("이미지 링크")
                .content("이미지 링크 주소를 입력해주세요")
                .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                .input("https://", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_image, input.toString(), PostBodyType.PLAIN));
                    }
                }).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void eventHandler(Object event) {

        if ( event instanceof EditorEvent.PostContentStatusEvent ) {

            //
            // 글 작성 완료
            //
            if ( ((EditorEvent.PostContentStatusEvent) event).getResult()
                    == EditorEvent.PostContentStatusEvent.STATUS_COMPLETE) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("RETURN_SELECTED_BOARD_ID", mPresenter.getSelectedBoardId());
                setResult(RESULT_OK, returnIntent);
                finish();
            } else if ( ((EditorEvent.PostContentStatusEvent) event).getResult()
                    == EditorEvent.PostContentStatusEvent.STATUS_ERROR) {
                /*MenuItem item = mMenu.findItem(R.id.action_send_content);
                if (item != null) {
                    item.setEnabled(true);
                }*/
            }
        }
    }

    /**
     * 키보드 상태 리스너
     */
    private void initKeyboardStateObserver() {

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if ( isOpen ) {

                        } else {

                        }
                    }
                });
    }


    /**
     * 툴바 초기화
     */
    private void initToolbar(PostEditorType editorType) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if ( editorType == PostEditorType.INSERT) {
            setTitle(mTitleNew);
        } else {
            setTitle(mTitleModify);
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.md_blue_600));
        }
    }

    /**
     * 에디터 프래그먼트 삽입
     * @param insertOrUpdate
     * @param selectedBoardId
     */
    private void initEditorFragment(int insertOrUpdate,
                                    int selectedBoardId,
                                    int cellType,
                                    ContentHeaderModel content) {

        //Log.i("hong", "initEditorFragment()-1");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PlainEditorFragment plainEditorFragment = PlainEditorFragment.newInstance(insertOrUpdate, selectedBoardId, cellType, null, content);
        fragmentTransaction.replace(R.id.editor_fragment_container, plainEditorFragment);
        fragmentTransaction.commit();
    }

    /**
     * 에디터 프래그먼트 삽입
     *  - 셀타입이 PostCellType.PLAYER_TALK(5) 또는 PostCellType.TEAM_TALK(6) 일경우 호출
     * @param insertOrUpdate
     * @param selectedBoardId
     * @param cellType
     * @param player
     * @param team
     * @param content
     */
    private void initEditorFragment(int insertOrUpdate,
                                    int selectedBoardId,
                                    int cellType,
                                    PlayerModel player,
                                    TeamModel team,
                                    MatchModel match,
                                    ContentHeaderModel content) {

        //Log.i("hong", "initEditorFragment()-2");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PlainEditorFragment plainEditorFragment
                = PlainEditorFragment.newInstance(insertOrUpdate,
                                                selectedBoardId,
                                                cellType,
                                                player,
                                                team,
                                                match,
                                                null,
                                                content);
        fragmentTransaction.replace(R.id.editor_fragment_container, plainEditorFragment);
        fragmentTransaction.commit();
    }

    private void initEditorFragment2() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TextEditorFragment plainEditorFragment = TextEditorFragment.newInstance();
        fragmentTransaction.add(R.id.editor_fragment_container, plainEditorFragment);
        fragmentTransaction.commit();
    }

    /**
     * 에디터 프래그먼트 삽입 -< 이걸 사용할 수 있도록 변경하자.
     * @param insertOrUpdate
     * @param content
     */
    private void initEditorFragment(int insertOrUpdate, ContentHeaderModel content) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PlainEditorFragment plainEditorFragment = PlainEditorFragment.newInstance(insertOrUpdate, content.getBoardId(), content.getCellType(), content, content);
        fragmentTransaction.replace(R.id.editor_fragment_container, plainEditorFragment);
        fragmentTransaction.commit();
    }

    /*---------------------------------------------------------------------------------------------*/

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
            R.id.action_tag})
    public void onClickEditor(View view) {

        if ( view.getId() == R.id.action_insert_image) {
            //
            // 이미지 삽입 - 로컬, 링크
            //
            choiceInsertImageTypeDialog();

        } else if ( view.getId() == R.id.action_insert_link) {
            //
            // 링크URL 삽입
            //
            openLinkDialog();

        } else if (view.getId() == R.id.action_attachment_match) {
            //
            // 경기일정 정보 입력
            //
            final List<Product> fakeProducts = Product.createFakeProducts();
            MatchListDialogFragment.newInstance(fakeProducts.get(0)).show(getSupportFragmentManager(), null);
        } else if (view.getId() == R.id.action_insert_video ){
            openVideoDialog();
        } else if ( view.getId() == R.id.action_tag ) {
            //
            // 태그입력
            //
            openTagDialog();
        }
    }
}
