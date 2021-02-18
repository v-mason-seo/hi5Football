package com.ddastudio.hifivefootball_android.match_lineup;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
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
import com.ddastudio.hifivefootball_android.data.model.EmptyModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.SubLineupTeamSectionModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.ColorUtils;
import com.ddastudio.hifivefootball_android.ui.utils.GridDividerDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class LineupGridFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_lineup) RecyclerView rvLineup;
    @BindView(R.id.rv_home_sub_lineup) RecyclerView rvSubLineup;

    //
    // 선수평가 다이얼로그 관련 뷰
    //
    PlayerModel selectPlayer;
    MaterialDialog dialog;
    ImageView ivPlayerAvatar;
    TextView tvPlayerName;
    SeekBar seekBar;
    TextView tvRating;
    EditText etComment;
    Button btnMinusPoint;
    Button btnPlusPoint;

    TextView tvPlayerStatInfo;

    // 주전선수 리사이클러뷰 어댑터
    LineupGridRvAdapter mRvAdapter;

    // 후보선수 리사이클러뷰 어댑터
    SubLineupRvAdapter mRvSubLineupAdapter;

    // 프리젠터
    LineupGridPresenter mPresenter;

    List<Integer> spanCount = new ArrayList<>(15);

    public LineupGridFragment() {
        // Required empty public constructor
    }

    /**
     * 프래그먼트 객체 생성 ( 싱글톤 )
     * @param matchData
     * @return
     */
    public static LineupGridFragment newInstance(MatchModel matchData) {
        LineupGridFragment fragmentFirst = new LineupGridFragment();
        Bundle args = new Bundle();
        args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lineup_grid, container, false);
        _unbinder = ButterKnife.bind(this, view);

        MatchModel match = Parcels.unwrap(Objects.requireNonNull(getArguments()).getParcelable("ARGS_MATCH"));
        mPresenter = new LineupGridPresenter(match);
        mPresenter.attachView(this);

        for ( int i = 0; i < 15; i++) {
            spanCount.add(0);
        }

        initRefresh();
        initRecyclerView();
        initRecyclerView2();
        initPlayerDialog();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 라인업 정보를 불러온다.
        mPresenter.onLoadLineup();
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
        Toasty.normal(Objects.requireNonNull(getContext()), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(Objects.requireNonNull(getContext()), errMessage, Toast.LENGTH_LONG).show();
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

            mPresenter.getLocalTeamList().clear();
            mPresenter.getVisitorTeamList().clear();

            if ( mRvSubLineupAdapter != null ) {
                mRvSubLineupAdapter.getData().clear();
            }
            mPresenter.onLoadLineup();
        });
    }


    /**
     * 후보 및 교체선수 리사이클뷰 초기화
     */
    private void initRecyclerView2() {

        List<MultiItemEntity> itemList = new ArrayList<>();
        mRvSubLineupAdapter = new SubLineupRvAdapter(itemList);

        rvSubLineup.setNestedScrollingEnabled(false);
        rvSubLineup.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSubLineup.addItemDecoration(new FlexibleItemDecoration(Objects.requireNonNull(getContext())).withDefaultDivider().withDrawOver(true));
        rvSubLineup.setAdapter(mRvSubLineupAdapter);

        /*-----------------------------------------------------------------*/

        mRvSubLineupAdapter.setOnItemClickListener((adapter, view, position) -> {
            if ( adapter.getItemViewType(position) == ViewType.PLAYER) {
                PlayerModel lineup = (PlayerModel)adapter.getItem(position);
                onRvClickRowItem(lineup);
            }
        });

        mRvSubLineupAdapter.setOnItemChildClickListener((adapter, view, position) -> {

            PlayerModel player = (PlayerModel)mRvSubLineupAdapter.getData().get(position);
            switch (view.getId()) {
                case R.id.player_image:
                    mPresenter.openPlayerActivity(player.getPlayerId(), player.getPlayerName());
                    break;
            }
        });
    }

    /**
     * 주전 라인업 리사이클뷰 초기화
     */
    private void initRecyclerView() {

        // 데이터가 없을 때 화면에 표시할 뷰 생성
        View emptyView = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvLineup.getParent(), false);
        mRvAdapter = new LineupGridRvAdapter( new ArrayList<>());
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);
        mRvAdapter.setEmptyView(emptyView);

        rvLineup.setNestedScrollingEnabled(false);
        GridLayoutManager gridLayout = new GridLayoutManager(getContext(), 12);
        rvLineup.setLayoutManager(gridLayout);
        rvLineup.setAdapter(mRvAdapter);
        rvLineup.addItemDecoration(new GridDividerDecoration(Objects.requireNonNull(getContext())));

        //
        // 로우 클릭 리스너
        //
        mRvAdapter.setOnItemClickListener((adapter, view, position) -> {
            if ( adapter.getItemViewType(position) == ViewType.PLAYER) {
                PlayerModel lineup = (PlayerModel)mRvAdapter.getItem(position);
                //mPresenter.openPlayerRatingActivity(lineup.getTeamId(), lineup.getPlayerId());
                onRvClickRowItem(lineup);
            }
        });

        //
        // 로우안에 있는 뷰 클릭 리스너
        //
        mRvAdapter.setOnItemChildClickListener((adapter, view, position) -> {

            PlayerModel player = (PlayerModel)mRvSubLineupAdapter.getData().get(position);
            switch (view.getId()) {
                case R.id.player_image:
                    mPresenter.openPlayerActivity(player.getPlayerId(), player.getPlayerName());
                    break;
            }
        });

        mRvAdapter.setSpanSizeLookup((gridLayoutManager, position) -> {


            if (mRvAdapter.getData().get(position) instanceof EmptyModel ) {
                return 12;
            }

            PlayerModel lineup = (PlayerModel)mRvAdapter.getData().get(position);

            boolean isHomeTeam = lineup.getTeamId() == mPresenter.getmMatch().getHomeTeamId() ? true : false;
            switch (lineup.getPos()) {
                // [Row - 0]-------------------------------
                case "LS":
                case "F":
                case "ST":
                case "RS":
                    return 12 / ( isHomeTeam == true ? spanCount.get(0) : spanCount.get(8) );
                // [Row - 1]-------------------------------
                case "LF":
                case "CF-L":
                case "LCF":
                case "CF":
                case "CF-R":
                case "RCF":
                case "RF":
                    //return 100 / spanCount.get(9);
                    return 12 / ( isHomeTeam == true ? spanCount.get(1) : spanCount.get(9) );
                // [Row - 2]-------------------------------
                case "AM-L":
                case "AM":
                case "AM-R":
                    //return 100 / spanCount.get(10);
                    return 12 / ( isHomeTeam == true ? spanCount.get(2) : spanCount.get(10) );
                // [Row - 3]-------------------------------
                case "LM":
                case "LCM":
                case "CM-L":
                case "CM":
                case "RCM":
                case "CM-R":
                case "RM":
                    //return 100 / spanCount.get(11);
                    return 12 / ( isHomeTeam == true ? spanCount.get(3) : spanCount.get(11) );
                // [Row - 4]-------------------------------
                case "LWB":
                case "DM-L":
                case "LDM":
                case "DM":
                case "SW":
                case "DM-R":
                case "RDM":
                case "RWB":
                    //return 100 / spanCount.get(12);
                    return 12 / ( isHomeTeam == true ? spanCount.get(4) : spanCount.get(12) );
                // [Row - 5]-------------------------------
                case "LB":
                case "CD-L":
                case "LCD":
                case "CD":
                case "CD-R":
                case "RCD":
                case "RB":
                    //return 100 / spanCount.get(13);
                    return 12 / ( isHomeTeam == true ? spanCount.get(5) : spanCount.get(13) );
                // [Row - X]-------------------------------
                case "G":
                case "GK":
                    //return 100 / spanCount.get(14);
                    return 12 / ( isHomeTeam == true ? spanCount.get(6) : spanCount.get(14) );
            }

            return 12;
        });
    }

    /**
     * 리사이클러뷰 로우 클릭
     *  - 선수정보를 다이얼로그에 바인딩한다.
     * @param player
     */
    private void onRvClickRowItem(PlayerModel player) {

        if ( dialog != null ) {
            selectPlayer = player;

            // 선수이름, 평점 입력
            tvPlayerName.setText(getPlayerInfoSpannable(player));

            // 플레이어 스탯정보 입력
            tvPlayerStatInfo.setText(getPlayerStatInfoSpannable(player));

            // 선수평은 빈값으로 초기화
            etComment.setText("");

            // 기본점수로 6.0을 입력한다.
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

    /**
     * 선수평가 다이얼로그
     *
     *  - 다이얼로그에서 선수에 대한 평점을 줄 수 있다.
     */
    private void initPlayerDialog() {

        //
        // 1. 다이얼로그 생성
        //
        dialog =
                new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                        .customView(R.layout.dialog_custom_player_rating_item, true)
                        .positiveText(R.string.positive)
                        .negativeText(R.string.negative)
                        .cancelListener(
                                (dialog1) -> selectPlayer = null
                        )
                        .onNegative(
                                //
                                // 취소버튼
                                //
                                (dialog1, which) -> selectPlayer = null
                        )
                        .onPositive(
                                //
                                // 확인버튼
                                //
                                (dialog1, which) -> {
                                    if ( selectPlayer != null ) {

                                        // 1. 입력 데이터 확인
                                        if ( tvRating.getText().equals("0.0")) {
                                            showMessage("평점이 입력되지 않았습니다.");
                                            return;
                                        }

                                        // 2. 선수평점 데이터 생성
                                        mPresenter.onPostPlayerRating(mPresenter.getmMatch().getMatchId(),
                                                selectPlayer.getPlayerId(),
                                                selectPlayer.getTeamId(),
                                                "FT",
                                                Float.parseFloat(tvRating.getText().toString()),
                                                null,
                                                etComment.getText().toString());
                                    }

                                    selectPlayer = null;
                                })
                        .build();

        //
        // 2. 커스텀 다이얼로그 뷰 바인딩
        //
        ivPlayerAvatar = dialog.getCustomView() != null ? dialog.getCustomView().findViewById(R.id.iv_player_avatar) : null;
        tvPlayerName = Objects.requireNonNull(dialog.getCustomView()).findViewById(R.id.tv_player_name);
        seekBar = dialog.getCustomView().findViewById(R.id.seekBar);
        tvRating = dialog.getCustomView().findViewById(R.id.tv_rating);
        etComment = dialog.getCustomView().findViewById(R.id.et_comment);
        btnMinusPoint = dialog.getCustomView().findViewById(R.id.btn_minus_point);
        btnPlusPoint = dialog.getCustomView().findViewById(R.id.btn_plus_point);
        tvPlayerStatInfo = dialog.getCustomView().findViewById(R.id.tv_player_stat_info);

        //
        // SeekBar 변경 리스너
        //
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double rating = (double)progress / 10;
                tvRating.setText(String.valueOf(((double) progress / 10)));
                tvRating.setBackgroundColor(ColorUtils.getRatingBackgroundColor(getContext(), (float)rating));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        //
        // 평점을 -0.1씩 이동한다. ( SeekBar 이동시 세밀하게 조정하기 어렵기 때문에 )
        //
        btnMinusPoint.setOnClickListener(v -> seekBar.setProgress(seekBar.getProgress()-1));
        //
        // 평점을 +0.1씩 이동한다. ( SeekBar 이동시 세밀하게 조정하기 어렵기 때문에 )
        //
        btnPlusPoint.setOnClickListener(v -> seekBar.setProgress(seekBar.getProgress()+1));
    }


    /**
     * 선수 스탯(골, 옐로우, 레드 카드, 교체 정보 ) 스패너블 반환
     * @param player
     * @return
     */
    private Spannable getPlayerStatInfoSpannable(PlayerModel player) {

        int index = 0;
        SpannableStringBuilder ssbPlayerStat = new SpannableStringBuilder();

        // 1. 골
        if ( player.getStats().hasGoals()) {
            index = ssbPlayerStat.length();
            ssbPlayerStat.append("G");
            ssbPlayerStat.setSpan(new ImageSpan(getContext(), R.drawable.ic_soccer_ball_vector2, ImageSpan.ALIGN_BOTTOM), index, index+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 2. 옐로우 카드
        if (player.getStats().hasYellowCards()) {
            index = ssbPlayerStat.length();
            ssbPlayerStat.append("Y");
            ssbPlayerStat.setSpan(new ImageSpan(getContext(), R.drawable.ic_yellow_card_vector, ImageSpan.ALIGN_BOTTOM), index, index+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 3. 레드 카드
        if (player.getStats().hasRedCards()) {
            index = ssbPlayerStat.length();
            ssbPlayerStat.append("R");
            ssbPlayerStat.setSpan(new ImageSpan(getContext(), R.drawable.ic_red_card_vector, ImageSpan.ALIGN_BOTTOM), index, index+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 4. 교체 ( 들어옴 )
        if ( player.getSubstitutions().equals("on")) {
            index = ssbPlayerStat.length();
            ssbPlayerStat.append("I " + player.getSubsMinute());
            ssbPlayerStat.setSpan(new ImageSpan(getContext(), R.drawable.ic_substitution_in, ImageSpan.ALIGN_BOTTOM), index, index+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 5. 교체 ( 나감 )
        if ( player.getSubstitutions().equals("off")) {
            index = ssbPlayerStat.length();
            ssbPlayerStat.append("O " + player.getSubsMinute());
            ssbPlayerStat.setSpan(new ImageSpan(getContext(), R.drawable.ic_substitution_out, ImageSpan.ALIGN_BOTTOM), index, index+1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        return ssbPlayerStat;
    }


    /**
     * 선수이름, 공식평점 스패너블 반환
     * @param lineup
     * @return
     */
    private Spannable getPlayerInfoSpannable(PlayerModel lineup) {

        // spannable start index
        int start;
        // spannable end index
        int end;
        // 평점
        SpannableStringBuilder ssbRating = new SpannableStringBuilder();

        // 1.공식평점
        start = 0;
        ssbRating.append(" 전 " + lineup.getRatingString() + " ");
        end = ssbRating.length();
        ssbRating.setSpan(new BackgroundColorSpan(ColorUtils.getRatingBackgroundColor(getContext(), lineup.getRating())), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssbRating.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.md_grey_100)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 2.공백, 선수 이름
        ssbRating.append("  " + lineup.getPlayerName());

        return ssbRating;
    }


    /*---------------------------------------------------------------------------------------------*/


    /**
     * 라인업 로드 완료
     * @param items
     */
    public void onLoadFinished(List<PlayerModel> items) {

        mRvAdapter.getData().clear();

        for ( int i = 0; i < 15; i++) {
            spanCount.set(i, 0);
        }

        /*
          홈 팀
         */
        Collections.sort(mPresenter.getLocalTeamList());
        Collections.reverse(mPresenter.getLocalTeamList());

        mRvSubLineupAdapter.addData(new SubLineupTeamSectionModel(mPresenter.getmMatch().getHomeTeamName(),
                ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.md_blue_500)));

        for ( int i = 0; i < mPresenter.getLocalTeamList().size(); i++ ) {
            PlayerModel lineup = mPresenter.getLocalTeamList().get(i);
            if ( !lineup.getPos().equals("SUB") ) {

                switch (lineup.getPos()) {
                    // [Row - 0]-------------------------------
                    case "LS":
                    case "F":
                    case "ST":
                    case "RS":
                        lineup.setBackground(0);
                        spanCount.set(0, spanCount.get(0) + 1);
                        break;
                    // [Row - 1]-------------------------------
                    case "LF":
                    case "CF-L":
                    case "LCF":
                    case "CF":
                    case "CF-R":
                    case "RCF":
                    case "RF":
                        lineup.setBackground(1);
                        spanCount.set(1, spanCount.get(1) + 1);
                        break;
                    // [Row - 2]-------------------------------
                    case "AM-L":
                    case "AM":
                    case "AM-R":
                        lineup.setBackground(0);
                        spanCount.set(2, spanCount.get(2) + 1);
                        break;
                    // [Row - 3]-------------------------------
                    case "LM":
                    case "LCM":
                    case "CM-L":
                    case "CM":
                    case "RCM":
                    case "CM-R":
                    case "RM":
                        lineup.setBackground(1);
                        spanCount.set(3, spanCount.get(3) + 1);
                        break;
                    // [Row - 4]-------------------------------
                    case "LWB":
                    case "DM-L":
                    case "LDM":
                    case "DM":
                    case "SW":
                    case "DM-R":
                    case "RDM":
                    case "RWB":
                        lineup.setBackground(0);
                        spanCount.set(4, spanCount.get(4) + 1);
                        break;
                    // [Row - 5]-------------------------------
                    case "LB":
                    case "CD-L":
                    case "LCD":
                    case "CD":
                    case "CD-R":
                    case "RCD":
                    case "RB":
                        lineup.setBackground(1);
                        spanCount.set(5, spanCount.get(5) + 1);
                        break;
                    // [Row - 6]-------------------------------
                    case "G":
                    case "GK":
                        lineup.setBackground(0);
                        spanCount.set(6, spanCount.get(6) + 1);
                        break;
                }
                mRvAdapter.addData(lineup);
            } else {
                mRvSubLineupAdapter.addData(lineup);
            }
        }

        mRvAdapter.addData(new EmptyModel());

        /*
          어웨이팀
         */
        //Collections.sort(items);
        Collections.sort(mPresenter.getVisitorTeamList());

        mRvSubLineupAdapter.addData(new SubLineupTeamSectionModel(mPresenter.getmMatch().getAwayTeamName(), ContextCompat.getColor(getContext(), R.color.md_deep_purple_400)));

        for ( int i = 0; i < mPresenter.getVisitorTeamList().size(); i++ ) {
            //LineupModel lineup = (LineupModel)items.get(i);
            PlayerModel lineup = mPresenter.getVisitorTeamList().get(i);

            if ( !lineup.getPos().equals("SUB") ) {

                switch (lineup.getPos()) {
                    // [Row - 0]-------------------------------
                    case "LS":
                    case "F":
                    case "ST":
                    case "RS":
                        spanCount.set(8, spanCount.get(8) + 1);
                        break;
                    // [Row - 1]-------------------------------
                    case "LF":
                    case "CF-L":
                    case "LCF":
                    case "CF":
                    case "CF-R":
                    case "RCF":
                    case "RF":
                        spanCount.set(9, spanCount.get(9) + 1);
                        break;
                    // [Row - 2]-------------------------------
                    case "AM-L":
                    case "AM":
                    case "AM-R":
                        spanCount.set(10, spanCount.get(10) + 1);
                        break;
                    // [Row - 3]-------------------------------
                    case "LM":
                    case "LCM":
                    case "CM-L":
                    case "CM":
                    case "RCM":
                    case "CM-R":
                    case "RM":
                        spanCount.set(11, spanCount.get(11) + 1);
                        break;
                    // [Row - 4]-------------------------------
                    case "LWB":
                    case "DM-L":
                    case "LDM":
                    case "DM":
                    case "SW":
                    case "DM-R":
                    case "RDM":
                    case "RWB":
                        spanCount.set(12, spanCount.get(12) + 1);
                        break;
                    // [Row - 5]-------------------------------
                    case "LB":
                    case "CD-L":
                    case "LCD":
                    case "CD":
                    case "CD-R":
                    case "RCD":
                    case "RB":
                        spanCount.set(13, spanCount.get(13) + 1);
                        break;
                    // [Row - 6]-------------------------------
                    case "G":
                    case "GK":
                        spanCount.set(14, spanCount.get(14) + 1);
                        break;
                }

                mRvAdapter.addData(lineup);
            } else {

                mRvSubLineupAdapter.addData(lineup);
            }
        }

        hideLoading();
    }
}
