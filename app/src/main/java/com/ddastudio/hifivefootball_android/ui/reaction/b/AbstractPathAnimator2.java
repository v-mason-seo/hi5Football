package com.ddastudio.hifivefootball_android.ui.reaction.b;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hongmac on 2018. 3. 22..
 */

public abstract class AbstractPathAnimator2 {

    private final Random mRandom;
    protected final AbstractPathAnimator2.Config mConfig;


    public AbstractPathAnimator2(AbstractPathAnimator2.Config config) {
        mConfig = config;
        mRandom = new Random();

        //DisplayMetrics displayMetrics = new DisplayMetrics();
        //activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //mScreenWidth = displayMetrics.widthPixels;
    }

    public float randomRotation() {
        return mRandom.nextFloat() * 28.6F - 14.3F;
    }

    public Path createPath(AtomicInteger counter, View view, int factor, boolean isleftToRight) {

        Random r = mRandom;
        int x = r.nextInt(mConfig.xRand);
        int x2 = r.nextInt(mConfig.xRand);
        int y = view.getHeight() - mConfig.initY;
        int y2 = counter.intValue() * 15 + mConfig.animLength * factor + r.nextInt(mConfig.animLengthRand);
        factor = y2 / mConfig.bezierFactor;
        x = mConfig.xPointFactor + x;
        x2 = mConfig.xPointFactor + x2;
        int y3 = y - y2;
        y2 = y - y2 / 2;
        Path p = new Path();
        //p.moveTo(mConfig.initX, y);
        //p.cubicTo(mConfig.initX, y - factor, x, y2 + factor, x, y2);
        //p.moveTo(x, y2);
        //p.cubicTo(x, y2 - factor, x2, y3 + factor, x2, y3);

        if ( isleftToRight ) {
            x = r.nextInt(60);
            x2 = view.getWidth()-155;
            y = r.nextInt(mConfig.xRand) + 30;
            y2 = r.nextInt(mConfig.xRand);

            p.moveTo(x, y);
            p.cubicTo(x2/3, y + 380, x2/2, y+50, x2, y + 10);
        } else {
            x = view.getWidth() - r.nextInt(60);
            x2 = r.nextInt(60);
            y = r.nextInt(mConfig.xRand) + 30;
            y2 = r.nextInt(mConfig.xRand);

            p.moveTo(x, y);
            p.cubicTo(x2/3, y + 380, x2/2, y+50, x2, y + 10);
        }

        return p;
    }

    public abstract void start(View child, ViewGroup parent, boolean isLeftToRight);

    public static class Config {
        public int initX;
        public int initY;
        public int xRand;
        public int animLengthRand;
        public int bezierFactor;
        public int xPointFactor;
        public int animLength;
        public int heartWidth;
        public int heartHeight;
        public int animDuration;

        static AbstractPathAnimator2.Config fromTypeArray(TypedArray typedArray) {
            AbstractPathAnimator2.Config config = new AbstractPathAnimator2.Config();
            Resources res = typedArray.getResources();
            config.initX = (int) typedArray.getDimension(R.styleable.HeartLayout2_initX2,
                    res.getDimensionPixelOffset(R.dimen.heart2_anim_init_x));
            config.initY = (int) typedArray.getDimension(R.styleable.HeartLayout2_initY2,
                    res.getDimensionPixelOffset(R.dimen.heart2_anim_init_y));
            config.xRand = (int) typedArray.getDimension(R.styleable.HeartLayout2_xRand2,
                    res.getDimensionPixelOffset(R.dimen.heart2_anim_bezier_x_rand));
            config.animLength = (int) typedArray.getDimension(R.styleable.HeartLayout2_animLength2,
                    res.getDimensionPixelOffset(R.dimen.heart2_anim_length));
            config.animLengthRand = (int) typedArray.getDimension(R.styleable.HeartLayout_animLengthRand,
                    res.getDimensionPixelOffset(R.dimen.heart2_anim_length_rand));
            config.bezierFactor = typedArray.getInteger(R.styleable.HeartLayout2_bezierFactor2,
                    res.getInteger(R.integer.heart_anim_bezier_factor2));
            config.xPointFactor = (int) typedArray.getDimension(R.styleable.HeartLayout2_xPointFactor2,
                    res.getDimensionPixelOffset(R.dimen.heart2_anim_x_point_factor));
            config.heartWidth = (int) typedArray.getDimension(R.styleable.HeartLayout2_heart_width2,
                    res.getDimensionPixelOffset(R.dimen.heart2_size_width));
            config.heartHeight = (int) typedArray.getDimension(R.styleable.HeartLayout2_heart_height2,
                    res.getDimensionPixelOffset(R.dimen.heart2_size_height));
            config.animDuration = typedArray.getInteger(R.styleable.HeartLayout2_anim_duration2,
                    res.getInteger(R.integer.anim_duration2));
            return config;
        }
    }
}
