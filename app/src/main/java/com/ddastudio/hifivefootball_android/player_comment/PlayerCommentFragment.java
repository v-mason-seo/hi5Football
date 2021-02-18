package com.ddastudio.hifivefootball_android.player_comment;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.PlayerRatingEvent;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerCommentFragment extends BaseFragment {

    public final static int PLAYER_COMMENT_TYPE_CONTENT = 0;
    public final static int PLAYER_COMMENT_TYPE_MATCH = 1;
    /**
     * 이벤트로 넘어온 파라미터로 데이터를 조회한다.
     */
    public final static int PLAYER_COMMENT_TYPE_EVENT = 2;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_player_comments) RecyclerView rvPlayerComments;

    View notDataView;
    PlayerCommentPresenter mPresenter;
    PlayerCommentRvAdapter mRvAdapter;

    long mLastClickTime = 0;

    public PlayerCommentFragment() {
        // Required empty public constructor
    }

    /**
     * 매치리포트 - contentid로 선수평가 데이터를 조회한다.
     * @param contentId
     * @return
     */
    public static PlayerCommentFragment newInstance(int contentId) {
        PlayerCommentFragment fragmentFirst = new PlayerCommentFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_PLAYER_COMMENT_TYPE", PLAYER_COMMENT_TYPE_CONTENT);
        args.putInt("ARGS_CONTENT_ID", contentId);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public static PlayerCommentFragment newInstance(MatchModel matchData) {
        PlayerCommentFragment fragmentFirst = new PlayerCommentFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_PLAYER_COMMENT_TYPE", PLAYER_COMMENT_TYPE_MATCH);
        args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public static PlayerCommentFragment newInstance() {
        PlayerCommentFragment fragmentFirst = new PlayerCommentFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_PLAYER_COMMENT_TYPE", PLAYER_COMMENT_TYPE_EVENT);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int dataType = getArguments().getInt("ARGS_PLAYER_COMMENT_TYPE");
        int contentId = getArguments().getInt("ARGS_CONTENT_ID");
        MatchModel match = Parcels.unwrap(getArguments().getParcelable("ARGS_MATCH"));
        // 프리젠터 매치 파라미터 수정해야됨..
        mPresenter = new PlayerCommentPresenter(match);
        mPresenter.setDataType(dataType);
        mPresenter.setContentId(contentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_comment, container, false);
        _unbinder = ButterKnife.bind(this, view);

        mPresenter.attachView(this);

        initRefresh();
        initRecyclerView();

        if ( mPresenter.getDataType() == PLAYER_COMMENT_TYPE_MATCH) {
            mPresenter.onLoadPlayerComment(mPresenter.getMatch().getMatchId());
        } else if(mPresenter.getDataType() == PLAYER_COMMENT_TYPE_CONTENT){
            mPresenter.onLoadRelationPlayerRatings(mPresenter.getContentId());
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        /**
         * PlayerRatingEvent.ShowPlayerComment - 선수평가(PlayerRatingListFragment)에서 코멘트 클릭시 넘어오는 이벤트
         */
        if ( event instanceof PlayerRatingEvent.ShowPlayerComment) {

            if ( mRvAdapter != null ) {
                int matchid = ((PlayerRatingEvent.ShowPlayerComment) event).getMatchId();
                int playerid = ((PlayerRatingEvent.ShowPlayerComment) event).getPlayerId();
                mRvAdapter.getData().clear();
                mRvAdapter.notifyDataSetChanged();

                mPresenter.onLoadPlayerComment(playerid, matchid);
            }
        }
    }

    /**
     * 메시지를 보여준다.
     * @param message
     */
    @Override
    public void showMessage(String message) {
        Toasty.normal(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 에러 메시지를 보여준다.
     * @param errMessage
     */
    @Override
    public void showErrorMessage(String errMessage ) {
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * 로딩화면 보이기
     */
    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * 로딩화면 감추기
     */
    @Override
    public void hideLoading() {

        swipeRefreshLayout.setRefreshing(false);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * SwipeRefreshLayout 초기화
     */
    public void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> {

            if ( mPresenter.getDataType() == PLAYER_COMMENT_TYPE_MATCH) {
                mPresenter.onLoadPlayerComment(mPresenter.getMatch().getMatchId());
            } else if(mPresenter.getDataType() == PLAYER_COMMENT_TYPE_CONTENT){
                mPresenter.onLoadRelationPlayerRatings(mPresenter.getContentId());
            }
        });
    }

    /**
     * 리사이클뷰 초기화
     */
    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();
        mRvAdapter = new PlayerCommentRvAdapter(itemList);
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);

        rvPlayerComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlayerComments.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 64));
        rvPlayerComments.setAdapter(mRvAdapter);

        notDataView = getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvPlayerComments.getParent(), false);
        mRvAdapter.setEmptyView(notDataView);

        mRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if ( adapter.getItem(position) instanceof PlayerRatingInfoModel ) {

                    PlayerRatingInfoModel ratingData = (PlayerRatingInfoModel)adapter.getItem(position);
                    mPresenter.openPlayerRatingActivity(ratingData.getPlayer().getTeamId(), ratingData.getPlayerId());
                }
            }
        });

        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                PlayerRatingInfoModel playerComment = (PlayerRatingInfoModel)mRvAdapter.getData().get(position);

                switch (view.getId()) {
                    case R.id.iv_hifive:
                        onRvClickCommentHifive(position, playerComment);
                        break;
                    case R.id.iv_avatar:
                        //onRvClickAvatar(playerComment.getUser());
                        break;
                    case R.id.tv_player_name:
                        onRvClickPlayer(playerComment.getPlayerId(), playerComment.getPlayerName());
                        break;
                    case R.id.comment_box:
                        onRvClickComment();
                        break;
                }
            }
        });
    }

    /**
     * 댓글 하이파이브 클릭
     * @param comment
     */
    private void onRvClickCommentHifive(int position, PlayerRatingInfoModel comment) {

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

    public void onLoadFinishedPlayerRatings(List<PlayerRatingInfoModel> items) {
        showMessage("PlayerRatingInfoModel items size : " + items.size() );

        if ( items.size() == 0) {
            hideLoading();
            return;
        }

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);
        hideLoading();
    }

    public void onLoadFinished(List<PlayerRatingInfoModel> items) {

        if ( items.size() == 0) {
            mRvAdapter.setEmptyView(notDataView);
            hideLoading();
            return;
        }

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);

        Timber.d("[onLoadFinished] items size : %d", items.size());

        hideLoading();
    }

    /**
     * 플레이어 한줄평 로드 완료
     * @param items
     */
//    public void onLoadFinishedPlayerComment(List<PlayerCommentModel> items) {
//
//        if ( items.size() == 0) {
//            mRvAdapter.setEmptyView(notDataView);
//            hideLoading();
//            return;
//        }
//
//        mRvAdapter.getData().clear();
//        mRvAdapter.addData(items);
//
//        hideLoading();
//    }

    /**
     * 하이파이브수를 업데이트 한다.b
     * @param position
     * @param playerComment
     * @param hifiveCount
     */
    public void updateHifive(int position, PlayerRatingInfoModel playerComment, int hifiveCount) {

//        PlayerCommentModel
//        PlayerRatingInfoModel
        if ( mRvAdapter != null && mRvAdapter.getData().size() > 0 ) {
            playerComment.addHifiveCount(hifiveCount);
            mRvAdapter.setData(position, playerComment);
            mRvAdapter.notifyItemChanged(position);
        }

        hideLoading();
    }
}
