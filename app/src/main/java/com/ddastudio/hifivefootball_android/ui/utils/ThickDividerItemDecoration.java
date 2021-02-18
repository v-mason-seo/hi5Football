package com.ddastudio.hifivefootball_android.ui.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;

public class ThickDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mPaddingStart;

    public ThickDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.thick_line_divider);
        mPaddingStart = 0;
    }

    public ThickDividerItemDecoration(Context context, int paddingStart) {
        mDivider = context.getResources().getDrawable(R.drawable.thick_line_divider);
        mPaddingStart = paddingStart;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft()+ CommonUtils.dpToPx(parent.getContext(), mPaddingStart);
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}

