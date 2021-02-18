package com.ddastudio.hifivefootball_android.ui.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hongmac on 2018. 1. 18..
 */

public class GridDividerDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = { android.R.attr.listDivider };

    private Drawable mDividerCircle;
    private Drawable mDividerLine;
    private Drawable mDividerRect;
    private Drawable mDividerHalfCircle;
    private Drawable mDividerHalfCircle2;

    private Drawable mDividerGreen;
    private Drawable mDividerLightGreen;
    private int mScrollY;
    Map<Integer, Integer> colorMap;

    private Drawable mDivider;
    private int mInsets;
    private Context mContext;

    public GridDividerDecoration(Context context) {
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();

        mContext = context;
        mInsets = context.getResources().getDimensionPixelSize(R.dimen.card_insets);

        mDividerLine = ContextCompat.getDrawable(context, R.drawable.bg_field_line);
        mDividerCircle = ContextCompat.getDrawable(context, R.drawable.bg_field_circle);
        mDividerRect = context.getResources().getDrawable(R.drawable.bg_field_rectangle);
        mDividerHalfCircle = ContextCompat.getDrawable(context, R.drawable.bg_field_half_circle);
        mDividerHalfCircle2 = ContextCompat.getDrawable(context, R.drawable.bg_field_half_circle2);

        mDividerGreen = ContextCompat.getDrawable(context, R.drawable.bg_field_green);
        mDividerLightGreen = ContextCompat.getDrawable(context, R.drawable.bg_field_light_green);

        colorMap = new HashMap<>();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //outRect.set(mInsets, mInsets, mInsets, mInsets);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        //drawHorizontal(c, parent);
        //onDrawVertical(c, parent);
        onDrawFieldBackground(c, parent);
        onDrawField(c, parent);
    }

    /*-------------------------------------------------------------*/

    /**
     * 잔디를 그린다.
     * @param c
     * @param parent
     */
    public void onDrawFieldBackground(Canvas c, RecyclerView parent) {

        final int childCount = parent.getChildCount();

        if (childCount == 0) return;


        /**
         * 1, 0에 따라 배경색을 다르게 한다.
         */
        int bgMode = 1;
        View child = parent.getChildAt(0);
        final RecyclerView.LayoutParams pp =
                (RecyclerView.LayoutParams) child.getLayoutParams();

        /**
         * 기존에 값이 있다면 그 값을 사용한다.
         * 아래 로직이 없으면 화면 스크롤시 색이 변한다.
         */
        if ( colorMap.containsKey(pp.getViewAdapterPosition()) ) {
            int val = colorMap.get(pp.getViewAdapterPosition());
            bgMode = val == 0 ? 1 : 0;
        }

        for ( int i = 0 ; i < childCount; i++) {

            child = parent.getChildAt(i);

            final RecyclerView.LayoutParams p =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            /**
             * 그리드 방식이기 때문에 한 로우에 여러셀이 들어갈 수 있다.
             * 따라서 왼쪽 좌표값이 0인 값을 기준으로 컬럼 값을 변경해준다.
             */
            if ( child.getLeft() == 0) {

                bgMode = bgMode == 0 ? 1 : 0;

                // 컬럼값을 저장해서 다시 그릴때 활용할 수 있도록 한다.
                colorMap.put(p.getViewAdapterPosition(), bgMode);
            }

            if ( bgMode == 0 ) {
                mDividerGreen.setBounds(child.getLeft(), child.getTop(), parent.getMeasuredWidth(), child.getBottom());
                mDividerGreen.draw(c);
            } else {
                mDividerLightGreen.setBounds(child.getLeft(), child.getTop(), parent.getMeasuredWidth(), child.getBottom());
                mDividerLightGreen.draw(c);
            }

        }
    }

    /**
     * 경기장 라인을 그린다.
     * @param c
     * @param parent
     */
    public void onDrawField(Canvas c, RecyclerView parent) {

        final int childCount = parent.getChildCount();

        if (childCount == 0) return;

        for ( int i = 0 ; i < childCount; i++) {

            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            GridLayoutManager.LayoutParams params1 = (GridLayoutManager.LayoutParams) child.getLayoutParams();
            params1.getSpanIndex();
            params1.getLayoutDirection();

//                Timber.d("[onDrawField] index : %d, childCount : %d, AdapterPosition : %d, ViewLayoutPosition : %d, Span index : %d, Span size : %d, viewType : %d",
//                    i,
//                    childCount,
//                        params1.getViewAdapterPosition(),
//                        params1.getViewLayoutPosition(),
//                    params1.getSpanIndex(),
//                    params1.getSpanSize(),
//                        parent.getAdapter().getItemViewType(i));



            //Timber.d("[onDrawField] leftMargin : %d, mInsets : %d", params.leftMargin, mInsets);
            //Timber.d("[onDrawField] left : %d, top : %d, right : %d, bottom : %d", child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
//            Timber.d("[onDrawField2] left : %d, top : %d, right : %d, bottom : %d", left, top, right, bottom);
//            Timber.d("[onDrawField3] whidth : %d, height : %d", parent.getMeasuredWidth(), parent.getMeasuredHeight());

//            if ( child.getLeft() == 36 ) {
//                mDividerLine.setBounds(0, 10, 10, child.getBottom());
//                mDividerLine.draw(c);
//            }
//
//            if ( child.getRight() == 996 ) {
//                mDividerLine.setBounds(right-10, 10, right, child.getBottom());
//                mDividerLine.draw(c);
//            }

            int lineWidth = CommonUtils.dp(6);
            int space = CommonUtils.dp(8);

//            int left = child.getLeft() - params.leftMargin/* - mInsets*/;
//            int right = child.getRight() + params.rightMargin/* + mInsets*/;
//            int top = child.getTop() - params.topMargin/* - mInsets*/;
//            int bottom = child.getBottom() + params.bottomMargin/* + mInsets*/;

            int left = child.getLeft() + space;
            int right = child.getRight() - space;
            int top = child.getTop() + space;
            int bottom = child.getBottom() - space;

            // 왼쪽 라인 그리기
            if ( child.getLeft() == 0 ) {

                if ( params.getViewAdapterPosition() == 0 ) {
                    // 맨위에는 위로 8dp 여백을 준다.
                    mDividerLine.setBounds(left, child.getTop() + space, left + lineWidth, child.getBottom());
                    mDividerLine.draw(c);
                } else if ( params.getViewAdapterPosition() == parent.getAdapter().getItemCount() - 1) {
                    // 맨아래쪽은 아래로 8dp 여백을 준다.
                    mDividerLine.setBounds(left, child.getTop(), left + lineWidth, child.getBottom() - space);
                    mDividerLine.draw(c);
                } else {
                    mDividerLine.setBounds(left, child.getTop(), left + lineWidth, child.getBottom());
                    mDividerLine.draw(c);
                }
            }

            // 오른쪽 선 그리기
            if ( child.getRight() > parent.getMeasuredWidth() - 20 && child.getRight() <= parent.getMeasuredWidth()) {

                if ( params.getViewAdapterPosition() == 0) {
                    //Timber.d("getRight() : %d, getMeasuredWidth() : %d", child.getRight(), parent.getMeasuredWidth());
                    mDividerLine.setBounds(parent.getMeasuredWidth() - space - lineWidth, child.getTop() + space, right, child.getBottom());
                    mDividerLine.draw(c);
                } else if ( params.getViewAdapterPosition() == parent.getAdapter().getItemCount() - 1) {
                    mDividerLine.setBounds(parent.getMeasuredWidth() - space - lineWidth, child.getTop(), parent.getMeasuredWidth() - space, child.getBottom() - space);
                    mDividerLine.draw(c);
                } else {
                    mDividerLine.setBounds(parent.getMeasuredWidth() - space - lineWidth, child.getTop(), parent.getMeasuredWidth() - space, child.getBottom());
                    mDividerLine.draw(c);
                }
            }



            if ( params1.getViewAdapterPosition() == 0 ) {

                // 위쪽 라인 그리기
                mDividerLine.setBounds(left, top, right, top + lineWidth);
                mDividerLine.draw(c);

                int horizontalMargin = CommonUtils.dp(90);
                int verticalMargin = CommonUtils.dp(20);

                // 큰 사각형 그리기
                mDividerRect.setBounds(left + horizontalMargin, top, child.getRight() - horizontalMargin, bottom + verticalMargin);
                mDividerRect.draw(c);

                // 반원 그리기
                int x = ((left + right) / 2);
                int y = (child.getTop() + child.getBottom()) / 2;

                int radius = CommonUtils.dp(30);
                mDividerHalfCircle.setBounds(x-radius, bottom + verticalMargin - lineWidth, x + radius, bottom + verticalMargin + CommonUtils.dp(35));
                mDividerHalfCircle.draw(c);

                // 작은사각형
                horizontalMargin = CommonUtils.dp(140);
                mDividerRect.setBounds(left + horizontalMargin, top, child.getRight() - horizontalMargin, bottom - verticalMargin);
                mDividerRect.draw(c);
            }

            // 홈팀과 어웨이팀 경계선
            int adapterPosition = params1.getViewAdapterPosition();

            if ( parent.getAdapter().getItemViewType(adapterPosition) == ViewType.EMPTY_TYPE ) {

//                Timber.d("[onDrawField] index : %d, childCount : %d, AdapterPosition : %d, ViewLayoutPosition : %d, Span index : %d, Span size : %d, viewType : %d",
//                    i,
//                    childCount,
//                        params1.getViewAdapterPosition(),
//                        params1.getViewLayoutPosition(),
//                    params1.getSpanIndex(),
//                    params1.getSpanSize(),
//                        parent.getAdapter().getItemViewType(i));

                //Timber.d("[onDrawField] left : %d, top : %d, right : %d, bottom : %d", child.getLeft(), child.getTop(), child.getRight(), child.getBottom());

                int x = (child.getLeft() + child.getRight()) / 2;
                int y = (child.getTop() + child.getBottom()) / 2;

                int radious = CommonUtils.dp(60);
                mDividerCircle.setBounds(x-radious, y-radious, x + radious, y + radious);
                mDividerCircle.draw(c);

                mDividerLine.setBounds(left, y, right, y+20);
                mDividerLine.draw(c);
            }
//            if ( params.getViewAdapterPosition() == 11 /*|| params.getViewAdapterPosition() == 12*/ ) {
//
//                int x = (child.getLeft() + child.getRight()) / 2;
//                int y = (child.getTop() + child.getBottom()) / 2;
//
//                int radious = CommonUtils.dp(60);
//                mDividerCircle.setBounds(x-radious, y-radious, x + radious, y + radious);
//                mDividerCircle.draw(c);
//
//                mDividerLine.setBounds(left, y, right, y+20);
//                mDividerLine.draw(c);
//            }

            if ( params.getViewAdapterPosition() == parent.getAdapter().getItemCount() - 1) {

                // 아래쪽 라인 그리기
                mDividerLine.setBounds(left, bottom - lineWidth, right, bottom);
                mDividerLine.draw(c);

                // 큰 사각형
                int horizontalMargin = CommonUtils.dp(90);
                int verticalMargin = CommonUtils.dp(20);
                mDividerRect.setBounds(left + horizontalMargin, top - verticalMargin, child.getRight() - horizontalMargin, bottom);
                mDividerRect.draw(c);

                // 반원 그리기
                int x = (left + right) / 2;
                int y = (child.getTop() + child.getBottom()) / 2;

                int radius = CommonUtils.dp(30);
                mDividerHalfCircle2.setBounds(x-radius, top - verticalMargin - CommonUtils.dp(35) , x + radius, top - verticalMargin + lineWidth);
                mDividerHalfCircle2.draw(c);

                // 작은 사각형
                horizontalMargin = CommonUtils.dp(140);
                mDividerRect.setBounds(left + horizontalMargin, top + verticalMargin, child.getRight() - horizontalMargin, bottom);
                mDividerRect.draw(c);


            }
        }
    }

    /*-------------------------------------------------------------*/

    public void drawHorizontal(Canvas c, RecyclerView parent) {

        final int childCount = parent.getChildCount();

        //Timber.d("[drawHorizontal] childCount : %d", childCount);

        for ( int i = 0 ; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getRight() + params.rightMargin + mInsets;
            final int right = left + mDivider.getIntrinsicWidth();
            final int top = child.getTop() - params.topMargin - mInsets;
            final int bottom = child.getBottom() + params.bottomMargin + mInsets;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }


    public void onDrawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();

        if (childCount == 0) return;

        //Timber.d("[onDrawVertical] childCount : %d", childCount);

        for ( int i = 0 ; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getLeft() - params.leftMargin - mInsets;
            final int right = child.getRight() + params.rightMargin + mInsets;
            final int top = child.getBottom() + params.bottomMargin + mInsets;
            final int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

}
