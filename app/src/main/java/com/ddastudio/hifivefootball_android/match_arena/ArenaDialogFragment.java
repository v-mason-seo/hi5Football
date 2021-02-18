package com.ddastudio.hifivefootball_android.match_arena;

import android.os.Bundle;
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

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

/**
 * Created by hongmac on 2018. 3. 21..
 */

public class ArenaDialogFragment extends BottomSheetDialogFragment
        implements BaseContract.View {

    public final static int ARENA_MODE_PARTIFICATION_USERS = 0;

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.rv_list) RecyclerView rvList;
    @BindView(R.id.tv_header_title) TextView tvHeaderTitle;

    Unbinder unbinder;
    ArenaDialogPresenter mPresenter;
    ArenaDialogRvAdapter mRvAdapter;

    public static ArenaDialogFragment newInstance(int mode, MatchModel matchData) {
        final ArenaDialogFragment fragment = new ArenaDialogFragment();
        final Bundle args = new Bundle();
        args.putInt("ARGS_MODE", mode);
        args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_arena_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        int mode = getArguments().getInt("ARGS_MODE");
        MatchModel match = Parcels.unwrap(getArguments().getParcelable("ARGS_MATCH"));

        mPresenter = new ArenaDialogPresenter(mode, match);
        mPresenter.attachView(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //초기화 작업 및 파라미터 받기
        initRecyclerView();

        switch (mPresenter.getMode()) {
            case ARENA_MODE_PARTIFICATION_USERS:
                mPresenter.onLoadParticipationUsers(mPresenter.getMatchData().getMatchId());
                break;
        }
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

        mRvAdapter = new ArenaDialogRvAdapter(new ArrayList<>());
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        //rvList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvList.setAdapter(mRvAdapter);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinishedUserList(List<UserModel> items) {

        if ( items.size() > 0 ) {
            tvHeaderTitle.setText("총 사용자수 : " + String.valueOf(items.size()));
        }

        if ( items.size() == 0) {
            hideLoading();
            return;
        }

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);
        hideLoading();
    }
}
