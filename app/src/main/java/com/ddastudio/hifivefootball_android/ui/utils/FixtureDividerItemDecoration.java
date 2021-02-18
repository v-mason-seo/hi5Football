package com.ddastudio.hifivefootball_android.ui.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;

import timber.log.Timber;

/**
 * Created by hongmac on 2017. 11. 5..
 */

public class FixtureDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mPaddingStart;
    private int mPaddingEnd;

    public FixtureDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        mPaddingStart = 0;
        mPaddingEnd = 0;
    }

    public FixtureDividerItemDecoration(Context context, int paddingStart, int paddingEnd) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        mPaddingStart = paddingStart;
        mPaddingEnd = paddingEnd;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() + CommonUtils.dpToPx(parent.getContext(), mPaddingStart);
        int right = parent.getWidth() - ( parent.getPaddingRight()+ CommonUtils.dpToPx(parent.getContext(), mPaddingEnd) );

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            int position = parent.getChildAdapterPosition(child);
            int viewtype = parent.getAdapter().getItemViewType(position);


            if ( viewtype == MultipleItem.ARENA_LAST_SCHEDULE ) {

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
