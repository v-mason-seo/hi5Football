package com.ddastudio.hifivefootball_android.football_chat;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.data.manager.FootballChatManager;
import com.ddastudio.hifivefootball_android.football_chat.api.ChatService;
import com.ddastudio.hifivefootball_android.football_chat.api.ChatServiceFactory;
import com.ddastudio.hifivefootball_android.football_chat.dao.ChatDao;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;
import com.ddastudio.hifivefootball_android.football_chat.repo.ChatResponse;
import com.ddastudio.hifivefootball_android.football_chat.repo.Resource;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.ui.utils.EndlessRecyclerViewScrollListener;
import com.ddastudio.hifivefootball_android.utils.ItemClickSupport;
import com.ddastudio.hifivefootball_android.utils.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class FootballChatListFragment extends Fragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    @BindView(R.id.lottie_main)
    LottieAnimationView lottieAnimationView;

    protected RxBus mRxBus;
    protected Unbinder mUnbinder;

    @NonNull
    CompositeDisposable mDisposables;
    @NonNull
    CompositeDisposable mNetworkDisposables;

    FootballChatManager mFootballChatManager;
    FootballChatRvAdapter mRvAdapter;
    EndlessRecyclerViewScrollListener scrollListener;

    FootballChatViewModel mViewModel;

    public FootballChatListFragment() {
        // Required empty public constructor
    }

    public static FootballChatListFragment newInstance() {

        return new FootballChatListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRxBus = App.getInstance().bus();
        mNetworkDisposables = new CompositeDisposable();
        mDisposables = new CompositeDisposable();
        mDisposables.add(
                mRxBus.asFlowable()
                        .subscribe( event -> onEvent(event)));

        this.mFootballChatManager = FootballChatManager.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_football_chat_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        // 스와이프 리프레쉬 초기화
        initRefresh();
        // 리사이클러뷰 초기화
        initRecyclerView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 뷰모델 초기화
        mViewModel = getViewModel();
        // 뷰모델 데이터 관찰
        mViewModel.getResult().observe(this, new Observer<Resource<ChatResponse>>() {
            @Override
            public void onChanged(@Nullable Resource<ChatResponse> repoResponseResource) {
                swipeRefreshLayout.setRefreshing(false);

                switch (repoResponseResource.getStatus()){

                    // 로딩
                    case LOADING: {
                        swipeRefreshLayout.setRefreshing(true);
                        ChatResponse data = repoResponseResource.getData();
                        break;
                    }

                    // 에러
                    case ERROR:
                        swipeRefreshLayout.setRefreshing(false);
                        Log.e("Error", repoResponseResource.getException().getMessage(), repoResponseResource.getException());
                        break;

                    // 성공 : 데이터 로드 오나료
                    case SUCCESS: {
                        swipeRefreshLayout.setRefreshing(false);
                        ChatResponse data = repoResponseResource.getData();

                        if ( data.getPage() == 0 ) {
                            mRvAdapter.onNewData(data.getList());
                        } else {
                            mRvAdapter.addItems(data.getList());
                        }

                        break;
                    }
                }
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        mViewModel.load(0, 15);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ItemClickSupport.removeFrom(rvList);

        if ( mUnbinder != null ) {
            mUnbinder.unbind();
        }

        if ( mNetworkDisposables != null ) {
            mNetworkDisposables.clear();
        }

        if ( mDisposables != null ) {
            mDisposables.clear();
        }
    }


    /*--------------------------------------------------------------------------------------*/


    protected void onEvent(Object event) {

        if ( event instanceof ContentListEvent.RefreshListEvent) {
            //
            // 글 작성이 완료되면 챗리스트를 새로고침 한다.
            //
            scrollListener.resetState();
            mViewModel.load(0, 15);

        }
    }


    /**
     * 로딩화면 보이기
     */
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }


    /**
     * 로딩화면 감추기
     */
    public void hideLoading() {

        swipeRefreshLayout.setRefreshing(false);
    }

    /*--------------------------------------------------------------------------------------*/

    /**
     * 뷰모델 생성
     * @return
     */
    @NonNull
    private FootballChatViewModel getViewModel() {

        ChatService api = ChatServiceFactory.getService();
        ChatDao dao = AppDatabase.getInstance(getActivity().getApplicationContext()).chatDao();
        ViewModelProvider.Factory factory = new ChatViewModelFactory(api, dao);

        return ViewModelProviders.of(this, factory).get(FootballChatViewModel.class);
    }


    /**
     * SwipeRefreshLayout 초기화
     */
    public void initRefresh() {

        swipeRefreshLayout.setOnRefreshListener( () -> {

            mViewModel.load(0, 15);
        });
    }

    /**
     * 리사이클러 뷰 초기화
     */
    private void initRecyclerView() {

        mRvAdapter = new FootballChatRvAdapter(getContext());
        rvList.setAdapter(mRvAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(linearLayoutManager);

        //linearLayoutManager.elevateHeaders(true); // Default elevation of 5dp
        rvList.setLayoutManager(linearLayoutManager);

        // 성능향상
        rvList.setHasFixedSize(true);

        // 디바이더
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvList.addItemDecoration(itemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                mViewModel.load(totalItemsCount, 15);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvList.addOnScrollListener(scrollListener);

        //-----------------------------------------
        // 클릭 리스너
        //-----------------------------------------
        ItemClickSupport.addTo(rvList).setOnItemClickListener( (recyclerView, position, v) -> {

            ChatAndAttributeModel item = mRvAdapter.getItem(position);
            ChatModel chat = item.getChat();
            if ( chat != null ) {

                ChatAttributeModel attr = new ChatAttributeModel(chat.getMentionType(), chat.getMentionId(), true, chat.count);

                // 1. 기존 데이터 is_Sel 값을 초기화하고 선택된 아이템으로 인서트해준다.
                AppDatabase.getInstance(getActivity()).chatDao().clearAndChatAttributeInsertAt(attr);

                // 2. ContentListContainerFragment에서 뷰페이저 게시글 탭으로 이동시키기 위한 이벤트 발생
                App.getInstance().bus().send(new ContentListEvent.SelectedBoardEvent(null));
            }
        });
    }

    /*--------------------------------------------------------------------------------------*/

}
