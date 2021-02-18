package com.ddastudio.hifivefootball_android.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hongmac on 2017. 11. 6..
 * RecyclerView 스크롤 후 항목 클릭시 지연현상이 있어 커스텀 recyclerview를 사용함.
 * 구글 버전업시 위 현상이 수정되면 커스텀 리사이클러뷰 클래스는 필요없음
 */

public class CustomRecyclerView extends RecyclerView {
    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean requestCancelDisallowInterceptTouchEvent = getScrollState() == SCROLL_STATE_SETTLING;
        boolean consumed = super.onInterceptTouchEvent(event);
        final int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if( requestCancelDisallowInterceptTouchEvent ){
                    getParent().requestDisallowInterceptTouchEvent(false);
                    // stop scroll to enable child view get the touch event
                    stopScroll();
                    // not consume the event
                    return false;
                }
                break;
        }

        return consumed;
    }
}
