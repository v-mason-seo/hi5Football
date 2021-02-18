package com.ddastudio.hifivefootball_android.ui.OrderTest;


import android.app.DatePickerDialog;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.MatchEvent;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchDateModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.ui.presenter.OrderDialogPresenter;
import com.ddastudio.hifivefootball_android.ui.rvadapter.OrderRvAdapter;
import com.ddastudio.hifivefootball_android.ui.utils.GridLineDividerDecoration;
import com.ddastudio.hifivefootball_android.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchListDialogFragment extends BottomSheetDialogFragment implements BaseContract.View {

    public static final String ID_SIZE_SUFFIX = "txt_size";
    public static final String ID_COLOR_SUFFIX = "img_color";
    public static final String ID_DATE_SUFFIX = "txt_date";
    public static final String ID_TIME_SUFFIX = "txt_time";
    private static final String ARG_PRODUCT = "arg_product";

    @BindView(R.id.iv_close) ImageView ivClose;
    @BindView(R.id.tv_fromdate) TextView tvFromDate;
    @BindView(R.id.tv_todate) TextView tvToDate;
    @BindView(R.id.progressBar_cyclic) ProgressBar progressBar;
    @BindView(R.id.rv_competitions) RecyclerView rvList;
    @BindView(R.id.rv_matches) RecyclerView rvMatchList;

    Unbinder unbinder;
    OrderDialogPresenter mPresenter;
    OrderRvAdapter mRvAdapter;
    OrderRvAdapter mRvMatchAdapter;

    @BindView(R.id.switcher) ViewSwitcher switcher;
    @BindView(R.id.main_frameLayout) FrameLayout mFrameLayout;
    @BindView(R.id.btn_go) FrameLayout frameGo;
    @BindView(R.id.main_container) LinearLayout mainContainer;

    private List<View> clonedViews = new ArrayList<>();

    private Transition selectedViewTransition;
    private OrderSelection orderSelection;
    ViewGroup mContainer;


    public static class OrderSelection {
        public int size = 0;
        public int color = 0;
        public String date = "";
        public String time = "";
    }

    public MatchListDialogFragment() {
        // Required empty public constructor
    }

    public static MatchListDialogFragment newInstance(Product product) {
        final Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);

        final MatchListDialogFragment orderFragment = new MatchListDialogFragment();
        orderFragment.setArguments(args);

        return orderFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        mContainer = container;
        mPresenter = new OrderDialogPresenter();
        mPresenter.attachView(this);

        initRecyclerView();
        initMatchRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderSelection = new OrderSelection();

        selectedViewTransition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.transition_selected_view);

        tvFromDate.setText(DateUtils.getNow("yyyy-MM-dd"));
        tvToDate.setText(DateUtils.getNow("yyyy-MM-dd", 3));

        mPresenter.onLoadCompetitions();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        if ( unbinder != null ) {
            unbinder.unbind();
        }
        mPresenter.detachView();
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    /*---------------------------------------------------------------------------------------------*/

    @OnClick(R.id.iv_close)
    public void onClickClose(View view) {
        dismiss();
    }

    /*---------------------------------------------------------------------------------------------*/


    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();

        mRvAdapter = new OrderRvAdapter(itemList);
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);

        final GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        rvList.setLayoutManager(manager);
        //rvList.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider().withDrawOver(true));
        rvList.addItemDecoration(new GridLineDividerDecoration(getContext()));
        rvList.setNestedScrollingEnabled(false);
        rvList.setAdapter(mRvAdapter);

        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.competition_container:
                        CompModel comp = (CompModel)mRvAdapter.getData().get(position);
                        Toast.makeText(getContext(), comp.getCompetitionName(), Toast.LENGTH_SHORT).show();
                        String mstchDateJson
                                = DateUtils.getDatesBetweenJson(tvFromDate.getText().toString(),
                                                                tvToDate.getText().toString(),
                                                                "yyyy-MM-dd",
                                                                "yyyyMMdd");

                        String fromdate = tvFromDate.getText().toString().replaceAll("-", "");
                        String todate = tvToDate.getText().toString().replaceAll("-", "");
                        switcher.setDisplayedChild(1);
                        mPresenter.setSelectedCompId(comp.getCompetitionId());
                        mPresenter.onLoadMatches(comp.getCompetitionId(), fromdate, todate);
                        break;
                }
            }
        });
    }

    private void initMatchRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();

        mRvMatchAdapter = new OrderRvAdapter(itemList);
        mRvMatchAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRvMatchAdapter.isFirstOnly(true);
        mRvMatchAdapter.setNotDoAnimationCount(3);

        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvMatchList.setLayoutManager(manager);
        rvMatchList.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider().withDrawOver(true));
        rvMatchList.setNestedScrollingEnabled(false);

        rvMatchList.setAdapter(mRvMatchAdapter);

        mRvMatchAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                }
            }
        });

        mRvMatchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                MultiItemEntity item = mRvMatchAdapter.getData().get(position);
                if ( item instanceof MatchModel) {
                    // 선택된 매치 정보 넘기기
                    App.getInstance().bus().send(new MatchEvent.ChoiceMatchEvent((MatchModel)item));
                    dismiss();
                    // 종료
                }
                Timber.d("[setOnItemClickListener] view id : %d, position : %d", view.getId(), position);
            }
        });
    }

    public void onLoadFinishedCompetitions(List<CompModel> items) {

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);
        hideProgress();
    }

    public void onLoadFinishedMatches(List<MatchModel> items) {

        mRvMatchAdapter.getData().clear();
        mRvMatchAdapter.notifyDataSetChanged();

        if ( items.size() == 0 ) {
            Toast.makeText(getContext(), "경기일정이 없습니다", Toast.LENGTH_LONG).show();
            hideProgress();
            return;
        }

        String matchDate = "";
        for ( MatchModel match : items) {

            if ( !matchDate.equals(match.getMatchDate())) {
                mRvMatchAdapter.addData(new MatchDateModel(match.getMatchDate()));
                matchDate = match.getMatchDate();
            }
            mRvMatchAdapter.addData(match);
        }

        hideProgress();
    }

    @OnClick(R.id.iv_previous_view)
    public void onClickSwitchView(View view) {
        switcher.setDisplayedChild(0);
        mRvMatchAdapter.getData().clear();
        mRvMatchAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_fromdate)
    public void onClickFromDate(View view) {
        Calendar cal = Calendar.getInstance();
        Date fromDate = DateUtils.toDate(tvFromDate.getText().toString(), "yyyy-MM-dd");
        cal.setTime(fromDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tvFromDate.setText(String.format("%d-%02d-%02d", year, monthOfYear+1, dayOfMonth));
                if ( switcher.getCurrentView().getId() == R.id.layout_step2) {

                    String fromdate = tvFromDate.getText().toString().replaceAll("-", "");
                    String todate = tvToDate.getText().toString().replaceAll("-", "");
                    mPresenter.onLoadMatches(mPresenter.getSelectedCompId(), fromdate, todate);
                }
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.tv_todate)
    public void onClickToDate(View view) {
        Calendar cal = Calendar.getInstance();
        Date toDate = DateUtils.toDate(tvToDate.getText().toString(), "yyyy-MM-dd");
        cal.setTime(toDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tvToDate.setText(String.format("%d-%02d-%02d", year, monthOfYear+1, dayOfMonth));
                if ( switcher.getCurrentView().getId() == R.id.layout_step2) {
                    mPresenter.onLoadMatches(mPresenter.getSelectedCompId(), tvFromDate.getText().toString(), tvToDate.getText().toString());
                }
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.layout_step1)
    public void onClickStep1() {
        Toast.makeText(getContext(), "Setp-1", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_go)
    public void onClickGo(View v) {
        for (View clonedView : clonedViews)
            mainContainer.removeView(clonedView);

        clonedViews.clear();
        showDeliveryForm();
    }

    private void showDeliveryForm() {
        switcher.setDisplayedChild(1);
        //initOrderStepTwoView(binding.layoutStep2);
    }

    private View createSelectionView(View v) {
        final String resourceName = getResources().getResourceEntryName(v.getId());

        return (resourceName.startsWith(ID_COLOR_SUFFIX))
                ? createSelectedColorView((ImageView) v)
                : createSelectedTextView(v);
    }

    private View createSelectedColorView(ImageView selectedView) {

        final ImageView fakeImageView = new CircleImageView(
                getContext(), null, R.attr.colorStyle);

        fakeImageView.setImageDrawable(selectedView.getDrawable());
        fakeImageView.setLayoutParams(SelectedParamsFactory.startColorParams(selectedView));
        return fakeImageView;
    }

    private View createSelectedTextView(View v) {

        final TextView fakeSelectedTextView = new TextView(
                getContext(), null, R.attr.selectedTextStyle);

        final String resourceName = getResources().getResourceEntryName(v.getId());

        if (resourceName.startsWith(ID_DATE_SUFFIX))
            fakeSelectedTextView.setText(orderSelection.date);

        else if (resourceName.startsWith(ID_TIME_SUFFIX))
            fakeSelectedTextView.setText(orderSelection.time);

        else if (resourceName.startsWith(ID_SIZE_SUFFIX))
            fakeSelectedTextView.setText(String.valueOf(orderSelection.size));

        fakeSelectedTextView.setLayoutParams(SelectedParamsFactory.startTextParams(v));
        return fakeSelectedTextView;
    }

    @OnClick(R.id.layout_step2)
    public void onClickStep2() {
        Toast.makeText(getContext(), "Setp-2", Toast.LENGTH_SHORT).show();
    }

    private Product getProduct() {
        return (Product) getArguments().getSerializable(ARG_PRODUCT);
    }

    private Drawable createProductImageDrawable(Product product) {
        final ShapeDrawable background = new ShapeDrawable();
        background.setShape(new OvalShape());
        background.getPaint().setColor(ContextCompat.getColor(getContext(), product.color));

        final BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), product.image));

        final LayerDrawable layerDrawable = new LayerDrawable
                (new Drawable[]{background, bitmapDrawable});

//        final int padding = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
//        layerDrawable.setLayerInset(1, padding, padding, padding, padding);

        return layerDrawable;
    }

    private static class SelectedParamsFactory {
        private static ConstraintLayout.LayoutParams startColorParams(View selectedView) {
            final int colorSize = selectedView.getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.product_color_size);

            final ConstraintLayout.LayoutParams layoutParams =
                    new ConstraintLayout.LayoutParams(colorSize, colorSize);

            setStartState(selectedView, layoutParams);
            return layoutParams;
        }

        private static ConstraintLayout.LayoutParams startTextParams(View selectedView) {
            final ConstraintLayout.LayoutParams layoutParams =
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            setStartState(selectedView, layoutParams);
            return layoutParams;
        }

        private static void setStartState(View selectedView, ConstraintLayout.LayoutParams layoutParams) {
            layoutParams.topToTop = ((ViewGroup) selectedView.getParent().getParent()).getId();
            layoutParams.leftToLeft = ((ViewGroup) selectedView.getParent().getParent()).getId();
            layoutParams.setMargins((int) selectedView.getX(), (int) selectedView.getY(), 0, 0);
        }

        private static FrameLayout.LayoutParams endParams(View v, View targetView) {
            final FrameLayout.LayoutParams layoutParams =
                    (FrameLayout.LayoutParams) v.getLayoutParams();

            final int marginLeft = v.getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.spacing_medium);

            layoutParams.setMargins(marginLeft, 0, 0, 0);
//            layoutParams.topToTop = targetView.getId();
//            layoutParams.startToEnd = targetView.getId();
//            layoutParams.bottomToBottom = targetView.getId();

            return layoutParams;
        }
    }
}
