package com.ddastudio.hifivefootball_android.player_comment;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.PlayerCommentModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class PlayerCommentListDialogFragment extends BottomSheetDialogFragment
        implements BaseContract.View {

    /**
     * 하나의 매치에 대한 선수 한줄평 리스트
     */
    public final static int MODE_MATCH_PLAYER = 0;
    /**
     * 선수에 대한 모든 한줄평 리스트
     */
    public final static int MODE_OVERALL_PLAYER = 1;

    @BindView(R.id.progressBar_cyclic) ProgressBar progressBar;
    @BindView(R.id.rv_player_comments) RecyclerView rvPlayerComments;
    @BindView(R.id.tv_header_title) TextView tvHeaderTitle;

    long mLastClickTime = 0;
    Unbinder unbinder;
    PlayerCommentListDialogPresenter mPresenter;
    PlayerCommentRvAdapter mRvAdapter;

    public static PlayerCommentListDialogFragment newInstance(int playerId) {
        final PlayerCommentListDialogFragment fragment = new PlayerCommentListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt("ARGS_MODE", MODE_OVERALL_PLAYER);
        args.putInt("ARGS_PLAYER_ID", playerId);
        fragment.setArguments(args);
        return fragment;
    }

    public static PlayerCommentListDialogFragment newInstance(int matchId, int playerId) {
        final PlayerCommentListDialogFragment fragment = new PlayerCommentListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt("ARGS_MODE", MODE_MATCH_PLAYER);
        args.putInt("ARGS_MATCH_ID", matchId);
        args.putInt("ARGS_PLAYER_ID", playerId);
        fragment.setArguments(args);
        return fragment;
    }

//    public static PlayerCommentListDialogFragment newInstance(int matchId, int playerId, MatchModel matchData) {
//        final PlayerCommentListDialogFragment fragment = new PlayerCommentListDialogFragment();
//        final Bundle args = new Bundle();
//        args.putInt("ARGS_MATCH_ID", matchId);
//        args.putInt("ARGS_PLAYER_ID", playerId);
//        args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_player_comment_list_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        int mode = getArguments().getInt("ARGS_MODE");
        int matchId = getArguments().getInt("ARGS_MATCH_ID", -1);
        int playerId = getArguments().getInt("ARGS_PLAYER_ID", -1);

        mPresenter = new PlayerCommentListDialogPresenter(mode, matchId, playerId);
        mPresenter.attachView(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //초기화 작업 및 파라미터 받기
        initRecyclerView();

        if ( mPresenter.getMode() == MODE_MATCH_PLAYER ) {
            mPresenter.onLoadPlayerComment(mPresenter.getMatchId(), mPresenter.getPlayerId());
        } else {
            mPresenter.onLoadPlayerOverallComment(15, 0);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ( unbinder != null ) {
            unbinder.unbind();
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 창 내리기 버튼 클릭
     */
    @OnClick(R.id.iv_close)
    public void onClickClose() {
        dismiss();
    }

    /*---------------------------------------------------------------------------------------------*/


    /**
     * 리사이크뷰 초기화
     */
    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();
        mRvAdapter = new PlayerCommentRvAdapter(itemList);
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);

        rvPlayerComments.setLayoutManager(new LinearLayoutManager(getContext()));
        //rvPlayerComments.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider().withDrawOver(true));
        rvPlayerComments.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 64));
        rvPlayerComments.setAdapter(mRvAdapter);


        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                PlayerCommentModel playerComment = (PlayerCommentModel)mRvAdapter.getData().get(position);

                switch (view.getId()) {
                    case R.id.iv_hifive:
                        onRvClickCommentHifive(position, playerComment);
                        break;
                    case R.id.iv_avatar:
                        onRvClickAvatar(playerComment.getUser());
                        break;
                    case R.id.tv_player_name:
                        onRvClickPlayer(playerComment.getPlayerId(), playerComment.getPlayerName());
                        break;
                    case R.id.comment_box:
                        onRvClickComment();
                        break;
//                    case R.id.iv_more:
//                        onRvClickMoreMenu();
//                        break;
                }
            }
        });
    }

    /**
     * 댓글 하이파이브 클릭
     * @param comment
     */
    private void onRvClickCommentHifive(int position, PlayerCommentModel comment) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if ( comment == null ) {
            Toasty.normal(getContext(), "데이터가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        mPresenter.setPlayerCommentHifive(position, comment, 1);
    }

    /**
     * 아바타 글릭
     * @param user
     */
    private void onRvClickAvatar(UserModel user) {

        if ( user == null ) {
            Toasty.normal(getContext(), "사용자 정보가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        mPresenter.openUserProfile(user);
    }

    /**
     * 플레이어 이름 클릭
     * @param playerId
     * @param playerName
     */
    private void onRvClickPlayer(int playerId, String playerName) {

        mPresenter.openPlayerActivity(playerId, playerName);
    }

    /**
     * 댓글버튼 클릭 - 작성된 댓글 리스트를 보여준다.
     */
    private void onRvClickComment() {
        Toasty.normal(getContext(), "댓글 기능은 작업중입니다", Toast.LENGTH_SHORT).show();
    }

    /**
     * 더보기 아이콘 클릭
     *  - 신고하기
     */
    private void onRvClickMoreMenu() {
        Toasty.normal(getContext(), "작업중입니다", Toast.LENGTH_SHORT).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 플레이어 한줄평 로드 완료
     * @param items
     */
    public void onLoadFinishedPlayerComment(List<PlayerRatingInfoModel> items) {

        if ( items.size() > 0 ) {
            tvHeaderTitle.setText(items.get(0).getPlayerName());
        }

        if ( items.size() == 0) {
            hideLoading();
            return;
        }

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);
        hideLoading();
    }

    /**
     * 하이파이브수를 업데이트 한다.
     * @param position
     * @param playerComment
     * @param hifiveCount
     */
    public void updateHifive(int position, PlayerCommentModel playerComment, int hifiveCount) {

        if ( mRvAdapter != null && mRvAdapter.getData().size() > 0 ) {
            playerComment.addHifiveCount(hifiveCount);
            mRvAdapter.setData(position, playerComment);
            mRvAdapter.notifyItemChanged(position);
        }

        hideLoading();
    }

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
}
