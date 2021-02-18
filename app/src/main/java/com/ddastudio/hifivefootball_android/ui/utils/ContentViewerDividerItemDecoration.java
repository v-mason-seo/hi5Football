package com.ddastudio.hifivefootball_android.ui.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;

/**
 * Created by hongmac on 2017. 11. 7..
 */

public class ContentViewerDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mPaddingStart;

    public ContentViewerDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        mPaddingStart = 0;
    }

    public ContentViewerDividerItemDecoration(Context context, int paddingStart) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        mPaddingStart = paddingStart;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft()+ CommonUtils.dpToPx(parent.getContext(), mPaddingStart);
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            int viewtype = parent.getAdapter().getItemViewType(position);

            if ( !(viewtype == MultipleItem.CONTENT_TITLE
                    || viewtype == MultipleItem.CONTENT_BODY
                    //|| viewtype == MultipleItem.CONTENT_COMMENT_INFO
                    || viewtype == MultipleItem.CONTENT_RELATION)) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
