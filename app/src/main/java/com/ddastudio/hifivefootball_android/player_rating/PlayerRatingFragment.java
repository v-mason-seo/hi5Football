package com.ddastudio.hifivefootball_android.player_rating;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingRecordModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.widget.DiscreteScrollViewOptions;
import com.ddastudio.hifivefootball_android.ui.widget.ForecastView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * 사용안함
 */
public class PlayerRatingFragment extends BaseFragment
        implements
        DiscreteScrollView.ScrollStateChangeListener<PlayerRatingRvAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<PlayerRatingRvAdapter.ViewHolder>,
        View.OnClickListener{

    @BindView(R.id.forecast_city_picker) DiscreteScrollView cityPicker;
    @BindView(R.id.forecast_view) ForecastView forecastView;


    PlayerRatingPresenter mPresenter;
    PlayerRatingRvAdapter mRvAdapter;

    public PlayerRatingFragment() {
        // Required empty public constructor
    }

    public static PlayerRatingFragment newInstance(int matchId, String homeTeamUrl, String awayTeamUrl) {
        PlayerRatingFragment fragmentFirst = new PlayerRatingFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_MATCH_ID", matchId);
        args.putString("ARGS_MATCH_LOCAL_TEAM_IMAGE_URL", homeTeamUrl);
        args.putString("ARGS_MATCH_VISITOR_TEAM_IMAGE_URL", awayTeamUrl);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_rating, container, false);
        _unbinder = ButterKnife.bind(this, view);


        int matchId = getArguments().getInt("ARGS_MATCH_ID", 0);
        String localTeamImageUrl = getArguments().getString("ARGS_MATCH_LOCAL_TEAM_IMAGE_URL");
        String visitorTeamImageUrl = getArguments().getString("ARGS_MATCH_VISITOR_TEAM_IMAGE_URL");

        mPresenter = new PlayerRatingPresenter(matchId, localTeamImageUrl, visitorTeamImageUrl);
        mPresenter.attachView(this);

        initView();
        initRecyclerView();

        mPresenter.onLoadPlayerRating();

        return view;
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initView() {

        //findViewById(R.id.home).setOnClickListener(this);

        forecastView.findViewById(R.id.local_team_emblem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( mPresenter.getLocalPlayerList() != null) {
                    onLoadFinished(mPresenter.getLocalPlayerList());
                }
            }
        });

        forecastView.findViewById(R.id.visitor_team_emblem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( mPresenter.getVisitorPlayerList() != null) {
                    onLoadFinished(mPresenter.getVisitorPlayerList());
                }
            }
        });
    }

    private void initRecyclerView() {

        mRvAdapter = new PlayerRatingRvAdapter();


        cityPicker.setSlideOnFling(true);
        cityPicker.setAdapter(mRvAdapter);
        cityPicker.addOnItemChangedListener(this);
        cityPicker.addScrollStateChangeListener(this);
        cityPicker.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime());
        cityPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
    }

    @OnClick(R.id.player_rating_complete)
    public void onClickCompete(View view) {

//        Timber.d("[onClickCompete] - 1");
        if ( mRvAdapter == null || mRvAdapter.getItemCount() == 0)
            return;

//        Timber.d("[onClickCompete] - 2");
        for ( int i = 0; i < mRvAdapter.getItemCount(); i++ ) {
            PlayerRatingInfoModel item = mRvAdapter.getItem(i);

            final int index = i;

            if ( item.getUserUpNDonw() != 0 ) {

                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        double avgRating = item.getAvgRating();
//
//                        if ( item.getUserUpNDonw() == 1 ) {
//                            avgRating += 1.0f;
//                        } else {
//                            avgRating -= 1.0f;
//                        }
//                        mPresenter.insertPlayerRating(item.getPlayerId(), "50", avgRating, item.getUserUpNDonw(), index == mRvAdapter.getItemCount()-1);
                    }
                }, 100);
            }
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable PlayerRatingRvAdapter.ViewHolder holder, int position) {
        //viewHolder will never be null, because we never remove items from adapter's list
        if (holder != null) {

            PlayerRatingInfoModel playerData = mRvAdapter.getItem(position);
            forecastView.setPlayer(playerData, mPresenter.getmLocalTeamImageUrl(), mPresenter.getmVisitorTeamImageUrl(), position);
            holder.showText();
        }
    }

    @Override
    public void onScrollStart(@NonNull PlayerRatingRvAdapter.ViewHolder holder, int position) {
        holder.hideText();
    }

    @Override
    public void onScroll( float position,
                          int currentIndex, int newIndex,
                          @Nullable PlayerRatingRvAdapter.ViewHolder currentHolder,
                          @Nullable PlayerRatingRvAdapter.ViewHolder newHolder) {

        if (newIndex >= 0 && newIndex < cityPicker.getAdapter().getItemCount()) {
            //Forecast next = forecasts.get(newIndex);
            forecastView.onScroll(1f - Math.abs(position), currentIndex, newIndex);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                //finish();
                break;
        }
    }

    @Override
    public void onScrollEnd(@NonNull PlayerRatingRvAdapter.ViewHolder holder, int position) {

    }

    /*-------------------------------------------------------------------------*/

    public void onLoadFinished(List<PlayerRatingInfoModel> items) {

        if ( items != null && items.size() > 0 ) {
            mRvAdapter.setItems(items);
            //cityPicker.scrollToPosition(0);
            forecastView.setPlayer(items.get(0), mPresenter.getmLocalTeamImageUrl(), mPresenter.getmVisitorTeamImageUrl(), 0);
            //mPresenter.onLoadPlayerRatingRecord(items.get(0).getPlayerId(), 180);
        } else {
            Toasty.normal(getContext(), "플레이어 데이터가 존재하지 않습니다", Toast.LENGTH_LONG).show();
            //finish();
        }
    }

    public void onLoadPlayerRatingRecord(List<PlayerRatingRecordModel> items) {

        forecastView.setRatingRecord(items);
        //forecastView.setData(8, 12);
        //forecastView.initChardView(items);
        //forecastView.invalidate();
    }

    public void insertFinishedPlayerRating() {

        Toasty.info(getContext(), "데이터 저장이 완료되었습니다", Toast.LENGTH_SHORT).show();
        //finish();
    }

}
