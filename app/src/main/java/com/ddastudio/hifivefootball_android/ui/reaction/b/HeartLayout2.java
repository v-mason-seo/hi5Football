package com.ddastudio.hifivefootball_android.ui.reaction.b;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ddastudio.hifivefootball_android.R;

/**
 * Created by hongmac on 2018. 3. 22..
 */

public class HeartLayout2 extends RelativeLayout  {

    private AbstractPathAnimator2 mAnimator;

    public HeartLayout2(Context context) {
        super(context);
        init(null, 0);
    }

    public HeartLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HeartLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.HeartLayout2, defStyleAttr, 0);

        mAnimator = new PathAnimator2(AbstractPathAnimator2.Config.fromTypeArray(a));

        a.recycle();
    }

    public AbstractPathAnimator2 getAnimator() {
        return mAnimator;
    }

    public void setAnimator(AbstractPathAnimator2 animator) {
        clearAnimation();
        mAnimator = animator;
    }

    public void clearAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        removeAllViews();
    }

    public void addHeart(int color) {
        HeartView heartView = new HeartView(getContext());
        heartView.setColor(color);
        mAnimator.start(heartView, this, true);
    }

    public void addHeart(int heartResId, boolean isLeftToRight) {
        HeartView heartView = new HeartView(getContext());
        heartView.setColorAndDrawables(heartResId);
        mAnimator.start(heartView, this, isLeftToRight);
    }
}
