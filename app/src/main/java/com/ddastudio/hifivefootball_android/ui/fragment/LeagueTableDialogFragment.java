package com.ddastudio.hifivefootball_android.ui.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.SelectedItemEvent;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.PlayerStatsPairModel;
import com.ddastudio.hifivefootball_android.data.model.community.SimpleSectionHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.PlayerStatsModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;
import com.ddastudio.hifivefootball_android.ui.rvadapter.LeagueTableDialogRvAdapter;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.ui.presenter.LeagueTableDialogPresenter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeagueTableDialogFragment extends BottomSheetDialogFragment
        implements BaseContract.View {

    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.league_table_list) RecyclerView rvLeagueTable;
    @BindView(R.id.progress) ProgressBar progress;

    View emptyView;
    int selectType;
    Unbinder unbinder;
    LeagueTableDialogRvAdapter mRvAdapter;
    LeagueTableDialogPresenter mPresenter;

    public LeagueTableDialogFragment() {
        // Required empty public constructor
    }

    /**
     * MultipleItem.BOTTOM_PLAYER_LIST
     * MultipleItem.BOTTOM_TEAM_LIST
     * @param type
     * @return
     */
    public static LeagueTableDialogFragment newInstance(int type) {
        final LeagueTableDialogFragment fragment = new LeagueTableDialogFragment();
        final Bundle args = new Bundle();
        args.putInt("ARGS_TYPE", type);
        fragment.setArguments(args);
        return fragment;
    }

    public static LeagueTableDialogFragment newInstance(int type, int competitionId) {
        final LeagueTableDialogFragment fragment = new LeagueTableDialogFragment();
        final Bundle args = new Bundle();
        args.putInt("ARGS_TYPE", type);
        args.putInt("ARGS_COMPETITION_ID", competitionId);
        fragment.setArguments(args);
        return fragment;
    }

    public static LeagueTableDialogFragment newInstance(int type, int match_id, PlayerStatsModel playerStats) {
        final LeagueTableDialogFragment fragment = new LeagueTableDialogFragment();
        final Bundle args = new Bundle();
        args.putInt("ARGS_TYPE", type);
        args.putInt("ARGS_MATCH_ID", match_id);
        args.putParcelable("ARGS_PLAYER_STATS", Parcels.wrap(playerStats));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_league_table_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        selectType = getArguments().getInt("ARGS_TYPE", 0);
        mPresenter = new LeagueTableDialogPresenter(selectType);
        mPresenter.attachView(this);
        initRecyclerView();

        if ( selectType == MultipleItem.BOTTOM_STANDING) {

            // -----------------
            // 리그 순위표
            // -----------------
            //mRvAdapter.addHeaderView(getLeagueTableHeaderView());

            tvTitle.setText("순위표");
            int competitionId = getArguments().getInt("ARGS_COMPETITION_ID", 0);
            mPresenter.setCompetitionId(competitionId);
            mPresenter.onLoadStandings();

        } else if ( selectType == MultipleItem.BOTTOM_MATCH_PLAYER_STATS ) {

            // -----------------
            // 플레이어 스탯
            // -----------------
            tvTitle.setText("플레이어 스탯");
            int matchId = getArguments().getInt("ARGS_MATCH_ID", 0);
            mPresenter.setMatchId(matchId);
            PlayerStatsModel playerStats = Parcels.unwrap(getArguments().getParcelable("ARGS_PLAYER_STATS"));
            //Log.i("hong", "player stats : " + playerStats.getName());

            if ( playerStats != null ) {
                List<PlayerStatsPairModel> statList = new ArrayList<>();
                statList.add(new PlayerStatsPairModel("득점", playerStats.getGoals(), R.drawable.icons8_soccer_ball_48));
                statList.add(new PlayerStatsPairModel("어시스트", playerStats.getAssists(), R.drawable.ic_border_circle));
                statList.add(new PlayerStatsPairModel("shots_total", playerStats.getShots_total(), R.drawable.ic_border_circle));
                statList.add(new PlayerStatsPairModel("유효슈팅", playerStats.getShots_on_goal(), R.drawable.ic_border_circle));
                statList.add(new PlayerStatsPairModel("오프사이드", playerStats.getOffsides(), R.drawable.ic_border_circle));
                statList.add(new PlayerStatsPairModel("pen_miss", playerStats.getPen_miss(), R.drawable.ic_border_circle));
                statList.add(new PlayerStatsPairModel("옐로우카드", playerStats.getYellowcards(), R.drawable.ic_border_circle));
                statList.add(new PlayerStatsPairModel("레드카드", playerStats.getRedcards(), R.drawable.ic_border_circle));
                statList.add(new PlayerStatsPairModel("pen_score", playerStats.getPen_score(), R.drawable.ic_border_circle));
                statList.add(new PlayerStatsPairModel("fouls_drawn", playerStats.getFouls_drawn(), R.drawable.ic_border_circle));
                statList.add(new PlayerStatsPairModel("fouls_committed", playerStats.getFouls_committed(), R.drawable.ic_border_circle));

                onLoadFinished(statList);
            } else {
                // TODO: 2017. 11. 23. 스탯값이 널일 경우 데이터가 없는 메시지를 보여주자
            }


        } else if ( selectType == MultipleItem.BOTTOM_TEAM_LIST) {
            tvTitle.setText("팀 리스트");
            mRvAdapter.addHeaderView(getLeagueTableHeaderView());
            mPresenter.onLoadTeamList(0, 30);

        } else if ( selectType == MultipleItem.BOTTOM_PLAYER_LIST) {
            tvTitle.setText("플레이어 리스트");
            mRvAdapter.addHeaderView(getLeagueTableHeaderView());
            mPresenter.onLoadPlayerList(0, 30);
        }

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior =BottomSheetBehavior.from(bottomSheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);

                behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {

                        if ( newState ==  BottomSheetBehavior.STATE_HIDDEN ) {
                            if ( mRvAdapter != null && selectType == MultipleItem.BOTTOM_PLAYER_LIST) {
                                // 플레이어
                                App.getInstance().bus().send(new SelectedItemEvent.PlayerItem(mRvAdapter.getSelectedPlayerData()));

                            } else if ( mRvAdapter != null && selectType == MultipleItem.BOTTOM_TEAM_LIST) {
                                // 팀
                                App.getInstance().bus().send(new SelectedItemEvent.TeamItem(mRvAdapter.getSelectedTeamData()));
                            }

                            // BotomSheetFragment 종료
                            dismiss();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public void onDestroyView() {

        if ( unbinder != null ) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        progress.setVisibility(View.VISIBLE);
    }

    /**
     * 로딩화면 감추기
     */
    public void hideLoading() {

        progress.setVisibility(View.INVISIBLE);
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();

        emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvLeagueTable.getParent(), false);
        mRvAdapter = new LeagueTableDialogRvAdapter(itemList);
        mRvAdapter.setEnableLoadMore(true);


        rvLeagueTable.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLeagueTable.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider().withDrawOver(true));
        rvLeagueTable.setAdapter(mRvAdapter);
    }

    private View getLeagueTableHeaderView() {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.row_league_table_item, (ViewGroup) rvLeagueTable.getParent(), false);

        view.setBackgroundResource(R.drawable.backgroud_gradient_grey);

        return view;
    }


    public void onLoadFinished(List<? extends MultiItemEntity> items) {

        mRvAdapter.getData().clear();

        if ( items == null && items.size() == 0 ) {
            mRvAdapter.setEmptyView(emptyView);
            mRvAdapter.notifyDataSetChanged();
            hideLoading();
            return;
        }

        if ( selectType == MultipleItem.BOTTOM_STANDING) {
            StandingModel headerStanding = new StandingModel();
            headerStanding.setItemType(MultipleItem.BOTTOM_STANDING_HEADER);
            mRvAdapter.addData(headerStanding);
        }

        String grooupName = "";
        for ( int i =0; i < items.size(); i++ ) {

            StandingModel standing = (StandingModel)items.get(i);
            if ( i == 0 ) {
                tvTitle.setText("순위표  - " + standing.getSeason() + "  " + standing.getRound() + "라운드");
                grooupName = standing.getCompGroup();
                if ( !grooupName.equals("")) {
                    mRvAdapter.addData(new SimpleSectionHeaderModel(standing.getCompGroup()));
                }
            }

            if ( !grooupName.equals(standing.getCompGroup())) {
                grooupName = standing.getCompGroup();
                mRvAdapter.addData(new SimpleSectionHeaderModel(standing.getCompGroup()));
            }

            mRvAdapter.addData(standing);
        }

        hideLoading();
    }

}
