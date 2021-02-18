package com.ddastudio.hifivefootball_android.content_list;


import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentListContainerFragment extends BaseFragment implements MainActivity.OnBackPressedListener {

    @BindView(R.id.tabs_post_list_container) SmartTabLayout mTabLayout;
    @BindView(R.id.vp_post_list_container) ViewPager mViewPager;
    @BindArray(R.array.content_list_container_tabs_array) String[] mVpTitles;

    ContentListContainerVpAdapter mVpAdapter;

    public ContentListContainerFragment() {
        // Required empty public constructor
    }

    public static ContentListContainerFragment newInstance() {

        return new ContentListContainerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content_list_container, container, false);
        _unbinder = ButterKnife.bind(this, view);

        initViewPager();
        return view;
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if ( event instanceof ContentListEvent.SelectedBoardEvent) {

            if ( mVpAdapter != null ) {
                // 1. 게시판이 선택되면 게시글 항목으로 이동한다.
                mViewPager.setCurrentItem(1);
            }
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
        Toasty.normal(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getContext(), errMessage, Toast.LENGTH_SHORT).show();
    }


    /*---------------------------------------------------------------------------------------------*/


    /*---------------------------------
     * 뷰페이저 초기화
     * @param boardList
     *---------------------------------*/
    private void initViewPager(/*List<BoardMasterModel> boardList*/) {

        mVpAdapter = new ContentListContainerVpAdapter(getChildFragmentManager(), mVpTitles/*, boardList*/);
        mViewPager.setAdapter(mVpAdapter);
        mTabLayout.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                App.getInstance().bus().send(new ContentListEvent.ContentContainerPageSelected(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity)context).setOnBackPressedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity activity = (MainActivity)getActivity();
        activity.setOnBackPressedListener(null);
    }

    @Override
    public boolean onBack() {
        //Log.i("hong", "onBack");
        if ( mVpAdapter != null && mViewPager != null
                && mViewPager.getCurrentItem() > 0 ) {

            int position = mViewPager.getCurrentItem() - 1;
            mViewPager.setCurrentItem(position);

            return true;
        }

        return false;
    }
}
