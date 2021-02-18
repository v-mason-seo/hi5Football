package com.ddastudio.hifivefootball_android.content_report;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.data.event.SelectedItemEvent;
import com.ddastudio.hifivefootball_android.data.model.CommonInfoModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.footballdata.PlayerStatsModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;
import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.utils.ItemClickSupport;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentReportFragment extends BottomSheetDialogFragment
        implements BaseContract.View {

    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.rv_list) RecyclerView rvList;
    @BindView(R.id.progress) ProgressBar progress;

    Unbinder unbinder;

    ContentReportPresenter mPresenter;
    ContentReportRvAdpater mRvAdapter;

    public ContentReportFragment() {
        // Required empty public constructor
    }

    public static ContentReportFragment newInstance(ContentHeaderModel content) {
        final ContentReportFragment fragment = new ContentReportFragment();
        final Bundle args = new Bundle();
        args.putParcelable("ARGS_CONTENT", Parcels.wrap(content));
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_content_report, container, false);
        unbinder = ButterKnife.bind(this, view);
        tvTitle.setText("게시글 신고");

        ContentHeaderModel content = Parcels.unwrap(getArguments().getParcelable("ARGS_CONTENT"));
        mPresenter = new ContentReportPresenter(content);
        mPresenter.attachView(this);

        initRecyclerView();

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

        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        ItemClickSupport.removeFrom(rvList);

        if ( unbinder != null ) {
            unbinder.unbind();
        }

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

        List<CommonInfoModel> items = getReportTypeList();
        mRvAdapter = new ContentReportRvAdpater(getContext(), items);
        rvList.setAdapter(mRvAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(linearLayoutManager);

        // 성능향상
        rvList.setHasFixedSize(true);

        // 디바이더
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvList.addItemDecoration(itemDecoration);

        //-----------------------------------------
        // 클릭 리스너
        //-----------------------------------------
        ItemClickSupport.addTo(rvList).setOnItemClickListener( (recyclerView, position, v) -> {

            CommonInfoModel item = mRvAdapter.getItem(position);

            if ( item != null ) {

                ContentHeaderModel content = mPresenter.getContent();
                if ( content != null ) {
                    String contentText = String.format("게시글 번호 : %d\n게시글 제목 : %s\n사유 : %s\n 위와 같은 이유로 신고되었습니다.\n확인 후 처리될 예정입니다.", content.getContentId(), content.getTitle(), item.getCodeName());
                    mPresenter.onPostReport(1000, item.getCodeName(), contentText, 1);
                } else {
                    Toast.makeText(getActivity(), "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                    dismiss();
                }

            }
        });
    }

    /**
     * 신고하기 처리 완료
     */
    public void onPostFinished() {

        Toast.makeText(getActivity(), "게시글 신고를 정상적으로 처리하였습니다.\n관리자가 확인 후 적절한 방법으로 처리할 것입니다. 감사합니다.", Toast.LENGTH_LONG).show();
        dismiss();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 신고하기 유형 가져오기
     *  - 디비 CommonInfo에서 가져올 수 있도록 수정해야 한다.
     * @return
     */
    private List<CommonInfoModel> getReportTypeList() {

        List<CommonInfoModel> items = new ArrayList<>();

        // == 1 ==
        CommonInfoModel item1 = new CommonInfoModel();
        item1.setLargeCode("R100");
        item1.setMidiumCode("R100");
        item1.setSmallCode("1");
        item1.setCodeName("상대방 존중 안함(No Respect)");
        items.add(item1);

        // == 2 ==
        CommonInfoModel item2 = new CommonInfoModel();
        item2.setLargeCode("R100");
        item2.setMidiumCode("R100");
        item2.setSmallCode("2");
        item2.setCodeName("차별(Racism)");
        items.add(item2);

        // == 3 ==
        CommonInfoModel item3 = new CommonInfoModel();
        item3.setLargeCode("R100");
        item3.setMidiumCode("R100");
        item3.setSmallCode("3");
        item3.setCodeName("스팸");
        items.add(item3);

        // == 4 ==
        CommonInfoModel item4 = new CommonInfoModel();
        item4.setLargeCode("R100");
        item4.setMidiumCode("R100");
        item4.setSmallCode("4");
        item4.setCodeName("게시판 용도 위반/부적절");
        items.add(item4);

        // == 5 ==
        CommonInfoModel item5 = new CommonInfoModel();
        item5.setLargeCode("R100");
        item5.setMidiumCode("R100");
        item5.setSmallCode("5");
        item5.setCodeName("폭력적이거나 금된 내용");
        items.add(item5);

        // == 6 ==
        CommonInfoModel item6 = new CommonInfoModel();
        item6.setLargeCode("R100");
        item6.setMidiumCode("R100");
        item6.setSmallCode("6");
        item6.setCodeName("선정적인 컨텐츠");
        items.add(item6);

        // == 7 ==
        CommonInfoModel item7 = new CommonInfoModel();
        item7.setLargeCode("R100");
        item7.setMidiumCode("R100");
        item7.setSmallCode("7");
        item7.setCodeName("오해의 소지가 있거나 사기");
        items.add(item7);

        return items;

    }

}
