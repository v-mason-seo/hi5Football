package com.ddastudio.hifivefootball_android.player_rating;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.PlayerRatingEvent;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.player_comment.PlayerCommentListDialogFragment;
import com.ddastudio.hifivefootball_android.ui.rvadapter.RecyclerViewClickListener;
import com.ddastudio.hifivefootball_android.ui.rvadapter.StackViewAdapter;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.loopeer.cardstack.CardStackView;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerRatingListFragment extends BaseFragment {

    @BindView(R.id.progress) ProgressBar progress;
    @BindView(R.id.stackview_main) CardStackView cardStackView;

    PlayerRatingListPresenter mPresenter;
    StackViewAdapter stackViewAdapter;

    long mLastClickTime = 0;

    public PlayerRatingListFragment() {
        // Required empty public constructor
    }

    public static PlayerRatingListFragment newInstance(MatchModel matchData,
                                                       boolean isHome,
                                                       int selectedTeamId,
                                                       int selectedPlayerId) {

        PlayerRatingListFragment fragmentFirst = new PlayerRatingListFragment();
        Bundle args = new Bundle();
        args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        args.putBoolean("ARGS_IS_HOME", isHome);
        args.putInt("ARGS_SELECTED_TEAM_ID", selectedTeamId);
        args.putInt("ARGS_SELECTED_PLAYER_ID", selectedPlayerId);

        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MatchModel match = Parcels.unwrap(getArguments().getParcelable("ARGS_MATCH"));
        boolean isHome = getArguments().getBoolean("ARGS_IS_HOME");
        int selectedTeamId = getArguments().getInt("ARGS_SELECTED_TEAM_ID", 0);
        int selectedPlayerId = getArguments().getInt("ARGS_SELECTED_PLAYER_ID", 0);

        mPresenter = new PlayerRatingListPresenter(match, isHome);
        mPresenter.setSelectedTeamId(selectedTeamId);
        mPresenter.setSelectedPlayerId(selectedPlayerId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_player_rating_list, container, false);
        _unbinder = ButterKnife.bind(this, view);
        mPresenter.attachView(this);

        mPresenter.onLoadLineup();

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

        if ( event instanceof PlayerRatingEvent.ChoiceTeam ) {

            if ( stackViewAdapter == null ) {
                return;
            }

            showLoading();

            boolean ishome = ((PlayerRatingEvent.ChoiceTeam) event).isHomeTeam();

//            if ( ishome == true ) {
//                stackViewAdapter.updateData(mPresenter.getHomeLineupList());
//            } else {
//                stackViewAdapter.updateData(mPresenter.getAwayLineupList());
//            }

//            if ( ishome == true ) {
//                onLoadFinished(mPresenter.getHomeLineupList());
//            } else {
//                onLoadFinished(mPresenter.getAwayLineupList());
//            }

            hideLoading();
        }
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
        progress.setVisibility(View.VISIBLE);
    }

    /**
     * 로딩화면 감추기
     */
    public void hideLoading() {
        progress.setVisibility(View.INVISIBLE);
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initStackView(List<PlayerModel> playerList) {

        if ( playerList == null || !(playerList.size() > 0) ){
            return;
        }

        stackViewAdapter = new StackViewAdapter(getContext(), true);
        cardStackView.setAdapter(stackViewAdapter);
        stackViewAdapter.updateData(playerList);

        stackViewAdapter.setRecyclerViewClickListener(new RecyclerViewClickListener() {
            @Override
            public void onRowClicked(int position) {

            }

            @Override
            public void onViewClicked(View v, int position) {

                /**
                 * 저장하기
                 */
                if ( v.getId() == R.id.btn_save) {
                    showMessage("Button click, position : " + position);
                    PlayerModel lineup = stackViewAdapter.getItem(position);

                    mPresenter.onPostPlayerRating(mPresenter.getmMatch().getMatchId(),
                                                lineup.getPlayerId(),
                                                lineup.getTeamId(),
                                                "FT",
                                                lineup.getRating(),
                                                null,
                                                lineup.getPlayerComment(),
                                                position);

                } else if ( v.getId() == R.id.ll_player_comment) {

                    PlayerModel lineup = stackViewAdapter.getItem(position);
                    //App.getInstance().bus().send(new PlayerRatingEvent.ShowPlayerComment(mPresenter.getmMatch().getMatchId(), lineup.getPlayerId()));
                    PlayerCommentListDialogFragment dialogFragment
                            = PlayerCommentListDialogFragment.newInstance(mPresenter.getmMatch().getMatchId(), lineup.getPlayerId());
                    dialogFragment.show(getActivity().getSupportFragmentManager(), "PlayerComments");

                } else {

                    showErrorMessage("Button click, position : " + position);
                }
            }

            @Override
            public void onViewRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });

        cardStackView.setItemExpendListener(new CardStackView.ItemExpendListener() {
            @Override
            public void onItemExpend(boolean expend) {

            }
        });


        // updateSelectPosition() 코드는 setItemExpendListener 아래에 있어야 한다.
        for ( int i = 0 ; i < playerList.size(); i++ ) {
            if ( playerList.get(i).getPlayerId() == mPresenter.getSelectedPlayerId()) {
                // 선택한 선수를 expanding 한다.
                cardStackView.updateSelectPosition(i);
                return;
            }
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinished(List<PlayerModel> items) {

        initStackView(items);

        hideLoading();
    }

    public void onPostFinishedRating(int position, boolean ratingyn, boolean commentyn) {

        CommonUtils.hideKeyboard(getActivity(), getView());

        if ( stackViewAdapter != null ) {
            PlayerModel ratingInfo = stackViewAdapter.getItem(position);

            if ( ratingyn ) {
                ratingInfo.addRatingCount();
            }

            if ( commentyn ) {
                ratingInfo.addCommentCount();
            }

            cardStackView.clearSelectPosition();
            stackViewAdapter.notifyDataSetChanged();
        }
    }

}
