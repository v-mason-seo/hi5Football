package com.ddastudio.hifivefootball_android.match_arena;


import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.event.ArenaChatEvent;
import com.ddastudio.hifivefootball_android.data.model.arena_chat.ArenaImageModel;
import com.ddastudio.hifivefootball_android.data.model.arena_chat.ReactionModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.Glide4Engine;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.GravitySnapHelper;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArenaChatFragment extends BaseFragment {

    @BindView(R.id.rv_arena_image) RecyclerView rvArenaImage;
    @BindView(R.id.rv_arena_chat) RecyclerView rvArenaChat;
    @BindView(R.id.layout_chatbox) LinearLayout llChatContainer;
    @BindView(R.id.et_chatbox) EditText etChatBox;

    View notDataView;
    ArenaChatPresenter mPresenter;
    ArenaChatRvAdapter mRvAdapter;
    ArenaImageRvAdapter mRvAdapter2;

    public static final int REQUEST_CODE_CHOOSE = 23;

    public ArenaChatFragment() {
        // Required empty public constructor
    }

    public static ArenaChatFragment newInstance(MatchModel matchData) {
        ArenaChatFragment fragment = new ArenaChatFragment();
        Bundle args = new Bundle();
        args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arena_chat, container, false);
        _unbinder = ButterKnife.bind(this, view);

        MatchModel match = Parcels.unwrap(getArguments().getParcelable("ARGS_MATCH"));
        mPresenter = new ArenaChatPresenter();
        mPresenter.setMatchData(match);
        mPresenter.attachView(this);

        initRecyclerView();
        initImageRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if ( !App.getAccountManager().isAuthorized() ) {
//            llmChatBox.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            // 이미지 선택 -> 이미지 압축 -> 이미지 S3 업로드
            List<Uri> obtainResults = Matisse.obtainResult(data);
            mPresenter.imageCompressNUpload(obtainResults);
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 메시지 전송
     * @param view
     */
    @OnClick(R.id.button_chatbox_send)
    public void onClickSend(View view) {

        String message = etChatBox.getText().toString();
        etChatBox.setText("");

        if (!TextUtils.isEmpty(message)) {

            mPresenter.createMatchTalk(mPresenter.getMatchId(), message, 0, mPresenter.getMatchData().getStatus());
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    public void publishReactionMessage(Integer talkId, String message) {

        ReactionModel reaction = new ReactionModel(mPresenter.isChoiceHomeTeam() == true
                                                    ? ViewType.REACTION_HOME_MESSAGE
                                                    : ViewType.REACTION_AWAY_MESSAGE
                , mPresenter.isChoiceHomeTeam() ? ReactionModel.HOME : ReactionModel.AWAY
                , message
                , App.getAccountManager().getHifiveUser());

        reaction.setTalkId(talkId);
        App.getInstance().bus().send(new ArenaChatEvent.ArenaSendMessage(reaction));
    }

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if ( event instanceof ArenaChatEvent.ArenaMessageArrived ) {

            if ( mRvAdapter == null )
                return;

            ReactionModel reaction = ((ArenaChatEvent.ArenaMessageArrived) event).getReaction();

            if ( reaction.getItemType() == ViewType.REACTION_HOME_IMAGE
                    || reaction.getItemType() == ViewType.REACTION_AWAY_IMAGE) {

                addArenaImage(reaction.getImage());
                return;
            } else if (
                reaction.getItemType() == ViewType.REACTION_HOME_MESSAGE
                        || reaction.getItemType() == ViewType.REACTION_AWAY_MESSAGE
                        || reaction.getItemType() == ViewType.REACTION_BOT_MESSAGE) {

                addChatItem(reaction);
            }

        } else if ( event instanceof ArenaChatEvent.ArenaConnectComplete ) {

        } else if ( event instanceof ArenaChatEvent.ArenaConnectLost ) {

        } else if ( event instanceof  ArenaChatEvent.ArenaDeliveryComplete ) {

        } else if ( event instanceof ArenaChatEvent.ArenaChoiceTeam ) {

            boolean isHome = ((ArenaChatEvent.ArenaChoiceTeam) event).isHomeTeam();
            mPresenter.setChoiceHomeTeam(isHome);
        }


    }

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
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

    /**
     * 이미지 삽입 타입 선택
     *  - 로컬이미지
     *  - 링크이미지
     */
    @OnClick(R.id.iv_attachment)
    public void onClickAttachment(View view) {
        new MaterialDialog.Builder(getContext())
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

    public void openLocalImagePicker() {

        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {

                            Matisse.from(getActivity())
                                    .choose(MimeType.ofAll(), false)
                                    .countable(true)
                                    .maxSelectable(5)
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new Glide4Engine())
                                    .forResult(REQUEST_CODE_CHOOSE);
                        } else {
                            showErrorMessage(getString(R.string.permission_request_denied));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 이미지 링크 입력 다이얼로그
     */
    public void openLinkImageDialog() {

        new MaterialDialog.Builder(getContext())
                .title("이미지 링크")
                .content("이미지 링크 주소를 입력해주세요")
                .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                .input("https://", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        addArenaImage(input.toString());
                    }
                }).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initImageRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();
        mRvAdapter2 = new ArenaImageRvAdapter(itemList);
//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(rvArenaImage);

        SnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER_HORIZONTAL);
        snapHelper.attachToRecyclerView(rvArenaImage);
        rvArenaImage.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvArenaImage.setAdapter(mRvAdapter2);

        mRvAdapter2.addFooterView(getFooterView());

        mRvAdapter2.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (adapter.getItemViewType(position)) {
                    case ViewType.ARENA_IMAGE:
                        //mPresenter.openImageViewerActivity();
                        break;
                }
            }
        });
    }

    private View getFooterView() {

        View view = getLayoutInflater().inflate(R.layout.row_arena_add_image_item, (ViewGroup) rvArenaImage.getParent(), false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(getContext())
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
        });

        return view;
    }

    public void addArenaImage(String url) {

        ArenaImageModel aim = new ArenaImageModel(url);
        mRvAdapter2.addData(aim);
        mRvAdapter2.notifyDataSetChanged();
    }

    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();
        mRvAdapter = new ArenaChatRvAdapter(itemList);
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);

        rvArenaChat.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true));
        //rvArenaChat.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 64));
        rvArenaChat.setAdapter(mRvAdapter);

        notDataView = getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvArenaChat.getParent(), false);
        //mRvAdapter.setEmptyView(notDataView);

        rvArenaChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(getActivity(), v);
            }
        });

        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()) {
                    case R.id.iv_hifive:
                        break;
                    case R.id.iv_avatar:
                        break;
                    case R.id.tv_player_name:
                        break;
                    case R.id.comment_box:
                        break;
//                    case R.id.iv_more:
//                        break;
                }
            }
        });
    }

    /*---------------------------------------------------------------------------------------------*/

    public void addChatItem(ReactionModel reaction) {
        if ( reaction.getItemType() == ViewType.REACTION_HOME_MESSAGE
                || reaction.getItemType() == ViewType.REACTION_AWAY_MESSAGE
                || reaction.getItemType() == ViewType.REACTION_HOME_IMAGE
                || reaction.getItemType() == ViewType.REACTION_AWAY_IMAGE
                || reaction.getItemType() == ViewType.REACTION_BOT_MESSAGE) {
            mRvAdapter.addData(0, reaction);

            LinearLayoutManager llm = (LinearLayoutManager)rvArenaChat.getLayoutManager();
            int firstVisibleItemPosition = llm.findFirstVisibleItemPosition();
            if ( firstVisibleItemPosition == 0 ) {
                rvArenaChat.scrollToPosition(0);
            }
        }
    }
}
