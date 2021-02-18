package com.ddastudio.hifivefootball_android.content_viewer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.ContentViewerEvent;
import com.ddastudio.hifivefootball_android.data.model.CommentInputModel;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.ui.widget.CustomRecyclerView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;


public class CommentInputFragment extends BottomSheetDialogFragment
        implements BaseContract.View {

    public final static int COMMENT_TYPE_BASIC = 187;
    public final static int COMMENT_TYPE_GROUP = 247;

    @BindView(R.id.progressBar_cyclic) ProgressBar progressBar;
    @BindView(R.id.iv_close) ImageView ivClose;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.comment_list) CustomRecyclerView rvCommentList;

    int mCommentPosition;
    Unbinder unbinder;
    CommentGroupRvAdapter mRvAdapter;
    CommentInputPresenter mPresenter;

    public static CommentInputFragment newInstance(int type, int contentId) {

        final CommentInputFragment fragment = new CommentInputFragment();
        final Bundle args = new Bundle();
        args.putInt("ARGS_COMMENT_TYPE", type);
        args.putInt("ARGS_CONTENT_ID", contentId);
        fragment.setArguments(args);
        return fragment;
    }

    public static CommentInputFragment newInstance(int type, CommentModel commentData, int scrollPosition) {

        final CommentInputFragment fragment = new CommentInputFragment();
        final Bundle args = new Bundle();
        args.putParcelable("ARGS_COMMENT_MODEL", Parcels.wrap(commentData));
        args.putInt("ARGS_COMMENT_TYPE", type);
        args.putInt("ARGS_CONTENT_ID", commentData.getCommentId());
        args.putInt("ARGS_COMMENT_POSITION", scrollPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comment_input, container, false);
        unbinder = ButterKnife.bind(this, view);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        int type = getArguments().getInt("ARGS_COMMENT_TYPE");
        int contentId = getArguments().getInt("ARGS_CONTENT_ID");
        mPresenter = new CommentInputPresenter(type, contentId);
        mPresenter.attachView(this);

        initRecyclerView();

        if ( type == COMMENT_TYPE_BASIC) {

            tvTitle.setText("댓글입력");
            View footer = getFooterView();
            View emptyView = getEmptyView();
            mRvAdapter.setHeaderFooterEmpty(false, true);
            mRvAdapter.setEmptyView(emptyView);
            mRvAdapter.setFooterView(footer);

            mPresenter.getComments();

        } else if ( type == COMMENT_TYPE_GROUP) {
            tvTitle.setText("대댓글입력");
            CommentModel commentData = Parcels.unwrap(getArguments().getParcelable("ARGS_COMMENT_MODEL"));
            mCommentPosition = getArguments().getInt("ARGS_COMMENT_POSITION");
            mRvAdapter.setTargetCommentId(commentData.getCommentId());
            mPresenter.setTargetCommentData(commentData);
            mPresenter.getGroupComments();
        }

        return view;
    }

    private void hideKeyboard(View etInput) {

        View focusView = getActivity().getCurrentFocus();
        if (focusView != null) {

            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                //BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if ( mPresenter != null )
            mPresenter.detachView();

        if ( unbinder != null )
            unbinder.unbind();
    }

    /*---------------------------------------------------------------------------------------------*/

    @OnClick(R.id.iv_close)
    public void onClickClose(View view) {
        dismiss();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 메시지를 보여준다.
     * @param message
     */
    public void showMessage(String message) {
        Toasty.normal(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 에러 메시지를 보여준다.
     * @param errMessage
     */
    public void showErrorMessage(String errMessage ) {
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * 로딩화면 보이기
     */
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 로딩화면 감추기
     */
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initRecyclerView() {

        List<MultipleItem> itemList = new ArrayList<>();
        mRvAdapter = new CommentGroupRvAdapter(itemList);

        rvCommentList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCommentList.setAdapter(mRvAdapter);

        mRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                // --- 댓글 입력 에디터 삽입 ---
                if ( adapter.getItemViewType(position) == MultipleItem.COMMENT_IN
                        || adapter.getItemViewType(position) == MultipleItem.COMMENT_OUT) {
                    mRvAdapter.addCommentInput(position);
                }
            }
        });

        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()) {
                    case R.id.avatar:
                        //Toasty.info(getContext(), "avatar clikc : " + position + ", view : " + view.toString() + ", view id : " + view.getId(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.dismiss_icon:
                        mRvAdapter.remove(position);
                        break;
                    case R.id.btn_send:

                        CommentInputModel input = (CommentInputModel)adapter.getItem(position);
                        EditText inputText = (EditText)mRvAdapter.getViewByPosition(rvCommentList, position, R.id.comment_input_text);
                        if (TextUtils.isEmpty(inputText.getText().toString()))
                            break;

                        // 키보드 내리기
                        View keyboardView = getActivity().getCurrentFocus();
                        if (keyboardView != null) {
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        CommentInputModel commentInputModel
                                = new CommentInputModel(input.getParentCommentId(), input.getParentGroupId(), input.getDepth(), inputText.getText().toString());

                        if ( mPresenter.getType() == COMMENT_TYPE_BASIC) {
                            // 댓글 보기모드에서 실행되면 커멘트 입력작업을 하고 새로고침을 한다.
                            mPresenter.createComment(commentInputModel.getParentCommentId(),
                                    commentInputModel.getParentGroupId(),
                                    commentInputModel.getDepth(),
                                    commentInputModel.getContent(),
                                    0/*((ContentViewerEvent.PostCommentEvent) event).getScrollPosition()*/);

                        } else {
                            // ContentViewerActivity에서 호출되면 액티비티에서 인서트 작업을 할 수 있도록 이벤트를 전송후 종료한다.
                            App.getInstance().bus().send(new ContentViewerEvent.PostCommentEvent(commentInputModel, mCommentPosition));
                            dismiss();
                        }
                        break;

                    case R.id.comment_input_text:
                        break;
                }
            }
        });
    }

    public void onLoadFinishedComments(List<CommentModel> items) {

        if ( items == null && items.size() == 0) {
            return;
        }

        mRvAdapter.getData().clear();
        mRvAdapter.addCommentData(items);

        int targetPosition = mRvAdapter.findTargetPosition();
        if ( targetPosition >= 0) {
            mRvAdapter.addCommentInput(targetPosition);
            rvCommentList.scrollToPosition(targetPosition);
        }
    }

    public void onLoadFinishedGroupComments(List<CommentModel> items) {

        if ( items == null && items.size() == 0) {
            return;
        }

        mRvAdapter.getData().clear();
        mRvAdapter.addCommentData(items);

        // 대댓글 입력시에는 해당 댓글로 이동해 댓글입력박스를 넣어준다.
        int targetPosition = mRvAdapter.findTargetPosition();
        if ( targetPosition >= 0) {
            mRvAdapter.addCommentInput(targetPosition);
            rvCommentList.scrollToPosition(targetPosition);
        }
    }

    /*-----------------------------------------------------------------------*/

    private View getEmptyView() {

        View view = getActivity().getLayoutInflater().inflate(R.layout.row_comment_empty_item,
                (ViewGroup) rvCommentList.getParent(), false);
        return view;
    }

    private View getFooterView() {
        View view = getLayoutInflater().inflate(R.layout.row_content_viewer_footer,
                (ViewGroup)rvCommentList.getParent(), false);

        EditText etInput = view.findViewById(R.id.comment_input_text);
        ImageButton ibSend = view.findViewById(R.id.comment_send);

        ibSend.setOnClickListener(v -> {

            // TODO: 2017. 9. 23. 글자 입력 상태에 따라 enable, disable 상태값을 변경하고 색깔을 변경해주자
            if ( etInput.getText().length() > 0 ) {
                // 전송 버튼 클릭 후 키보드 내리기
                hideKeyboard(etInput);

                mPresenter.createComment(0, 0, 1, etInput.getText().toString(), 0/*mPresenter.COMMENT_SCROLL_POSITION_BOTTOM*/);
                etInput.setText("");
            }
        });

        return view;
    }
}
