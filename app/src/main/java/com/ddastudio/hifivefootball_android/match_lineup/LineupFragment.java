package com.ddastudio.hifivefootball_android.match_lineup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.ColorUtils;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class LineupFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.localteam_button) Button localteam_button;
    @BindView(R.id.visitorteam_button) Button visitorteam_button;
    @BindView(R.id.lineup_list) RecyclerView rvLineup;
    //
    // 선수평가 다이얼로그
    //
    PlayerModel selectePlayer;
    MaterialDialog dialog;
    ImageView ivPlayerAvatar;
    TextView tvPlayerName;
    SeekBar seekBar;
    TextView tvRating;
    EditText etComment;
    ImageView ivGoal;
    ImageView ivYellowCard;
    ImageView ivRedCard;
    ImageView ivIn;
    ImageView ivOut;
    Button btnMinusPoint;
    Button btnPlusPoint;

    LineupRvAdapter mRvAdapter;
    LineupPresenter mPresenter;

    public LineupFragment() {
        // Required empty public constructor
    }

    public static LineupFragment newInstance(MatchModel matchData) {
        LineupFragment fragmentFirst = new LineupFragment();
        Bundle args = new Bundle();
        args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lineup, container, false);
        _unbinder = ButterKnife.bind(this, view);

        MatchModel match = Parcels.unwrap(getArguments().getParcelable("ARGS_MATCH"));
        mPresenter = new LineupPresenter(match);
        mPresenter.attachView(this);

        initRefresh();
        initButton(match);
        initRecyclerView();
        initPlayerDialog();

        // 처음 조회할때는 홈팀라인업을 보여준다.
        mPresenter.onLoadLineup(true);

        return view;
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    public void showLoading() {
        super.showLoading();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
        Toasty.normal(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
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

            if ( mRvAdapter != null && mRvAdapter.getData().size() > 0) {
                mPresenter.getLocalTeamList().clear();
                mPresenter.getVisitorTeamList().clear();
                mRvAdapter.getData().clear();
                mRvAdapter.notifyDataSetChanged();
            }

            // 이전에 선택된 팀으로 데이터를 조회한다.
            mPresenter.onLoadLineup(localteam_button.isSelected());
        });
    }

    private void initButton(MatchModel match) {

        localteam_button.setText(match.getHomeTeamName());
        visitorteam_button.setText(match.getAwayTeamName());
        localteam_button.setSelected(true);

        localteam_button.setOnClickListener(v -> {

            localteam_button.setSelected(true);
            visitorteam_button.setSelected(false);

            if ( mPresenter.getLocalTeamList() != null) {
                onLoadFinished(mPresenter.getLocalTeamList());
            }
        });

        visitorteam_button.setOnClickListener(v -> {

            localteam_button.setSelected(false);
            visitorteam_button.setSelected(true);

            if ( mPresenter.getLocalTeamList() != null) {
                onLoadFinished(mPresenter.getVisitorTeamList());
            }
        });
    }

    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();

        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvLineup.getParent(), false);
        mRvAdapter = new LineupRvAdapter(itemList);
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);
        mRvAdapter.setEmptyView(emptyView);

        rvLineup.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLineup.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvLineup.setAdapter(mRvAdapter);

        mRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if ( adapter.getItemViewType(position) == ViewType.LINEUP) {
                    PlayerModel lineup = (PlayerModel)mRvAdapter.getItem(position);
                    //mPresenter.openPlayerRatingActivity(lineup.getTeamId(), lineup.getPlayerId());
                    onRvClickRowItem(lineup);
                }
            }
        });

        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    private void initPlayerDialog() {

        dialog =
                new MaterialDialog.Builder(getContext())
                        .customView(R.layout.dialog_custom_player_rating_item, true)
                        .positiveText(R.string.positive)
                        .negativeText(R.string.negative)
                        .cancelListener(
                                (dialog1) -> {
                                    selectePlayer = null;
                                }
                        )
                        .onNegative(
                                (dialog1, which) -> {
                                    selectePlayer = null;
                                }
                        )
                        .onPositive(
                                (dialog1, which) -> {
                                    if ( selectePlayer != null ) {

                                        if ( tvRating.getText().equals("0.0")) {
                                            showMessage("평점이 입력되지 않았습니다.");
                                            return;
                                        }
                                        mPresenter.onPostPlayerRating(mPresenter.getmMatch().getMatchId(),
                                                selectePlayer.getPlayerId(),
                                                selectePlayer.getTeamId(),
                                                "FT",
                                                Float.parseFloat(tvRating.getText().toString()),
                                                null,
                                                etComment.getText().toString());
                                    }

                                    selectePlayer = null;
                                })
                        .build();

        ivPlayerAvatar = dialog.getCustomView().findViewById(R.id.iv_player_avatar);
        tvPlayerName = dialog.getCustomView().findViewById(R.id.tv_player_name);
        seekBar = dialog.getCustomView().findViewById(R.id.seekBar);
        tvRating = dialog.getCustomView().findViewById(R.id.tv_rating);
        etComment = dialog.getCustomView().findViewById(R.id.et_comment);
        ivGoal= dialog.getCustomView().findViewById(R.id.iv_flag_goal);
        ivYellowCard= dialog.getCustomView().findViewById(R.id.iv_flag_ycard);
        ivRedCard= dialog.getCustomView().findViewById(R.id.iv_flag_rcard);
        ivIn= dialog.getCustomView().findViewById(R.id.iv_flag_in);
        ivOut = dialog.getCustomView().findViewById(R.id.iv_flag_out);
        btnMinusPoint = dialog.getCustomView().findViewById(R.id.btn_minus_point);
        btnPlusPoint = dialog.getCustomView().findViewById(R.id.btn_plus_point);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double rating = (double)progress / 10;
                tvRating.setText(String.valueOf(((double) progress / 10)));
                tvRating.setBackgroundColor(ColorUtils.getRatingBackgroundColor(getContext(), (float)rating));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnMinusPoint.setOnClickListener(v -> seekBar.setProgress(seekBar.getProgress()-1));
        btnPlusPoint.setOnClickListener(v -> seekBar.setProgress(seekBar.getProgress()+1));
    }

    private void onRvClickRowItem(PlayerModel player) {

        if ( dialog != null ) {
            selectePlayer = player;
            tvPlayerName.setText(player.getPlayerName());
            etComment.setText("");
            if ( player.getStats().hasGoals()) {
                ivGoal.setVisibility(View.VISIBLE);
            } else {
                ivGoal.setVisibility(View.GONE);
            }

            if (player.getStats().hasYellowCards()) {
                ivYellowCard.setVisibility(View.VISIBLE);
            } else {
                ivYellowCard.setVisibility(View.GONE);
            }

            if (player.getStats().hasRedCards()) {
                ivRedCard.setVisibility(View.VISIBLE);
            } else {
                ivRedCard.setVisibility(View.GONE);
            }

            if ( player.getSubstitutions().equals("on")) {
                ivIn.setVisibility(View.VISIBLE);
            } else {
                ivIn.setVisibility(View.GONE);
            }

            if ( player.getSubstitutions().equals("off")) {
                ivOut.setVisibility(View.VISIBLE);
            } else {
                ivOut.setVisibility(View.GONE);
            }

            seekBar.setProgress(60);

            GlideApp.with(getContext())
                    .load(player.getPlayerLargeImageUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_person_grey_vector)
                    .into(ivPlayerAvatar);

            dialog.show();
        }
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinished(List<? extends MultiItemEntity> items) {

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);

        hideLoading();
    }

}
