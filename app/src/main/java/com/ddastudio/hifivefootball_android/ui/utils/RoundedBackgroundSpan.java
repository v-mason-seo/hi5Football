package com.ddastudio.hifivefootball_android.ui.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;

import com.ddastudio.hifivefootball_android.R;

public class RoundedBackgroundSpan extends ReplacementSpan {

    private static int CORNER_RADIUS = 8;
    private int backgroundColor = 0;
    private int textColor = 0;

    public RoundedBackgroundSpan(Context context) {
        super();
        backgroundColor = context.getResources().getColor(R.color.gray);
        textColor = context.getResources().getColor(R.color.md_grey_500);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        RectF rect = new RectF(x, top, x + measureText(paint, text, start, end) + 24, bottom);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(backgroundColor);
        //paint.setStrokeWidth(1.5f);
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(textColor);
        //paint.setStrokeWidth(1.0f);
        canvas.drawText(text, start, end, x+12, y, paint);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end));
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }

//    private int radius = 0;
//    private int backgroundColor;
//    private int textColor;
//
//    public RoundedBackgroundSpan(@ColorInt int textColor,
//                          @ColorInt int backgroundColor,
//                          int radius) {
//        super();
//        this.backgroundColor = backgroundColor;
//        this.textColor = textColor;
//        this.radius = radius;
//    }
//
//    public RoundedBackgroundSpan(@ColorInt int backgroundColor,
//                          int radius) {
//        super();
//        this.backgroundColor = backgroundColor;
//        this.radius = radius;
//    }
//
//    @Override
//    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
//        return (int) paint.measureText(text.subSequence(start, end).toString());
//    }
//
//    @Override
//    public void draw(@NonNull Canvas canvas, CharSequence text,
//                     int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
//        boolean isLeftEdge = x == 0.0f;
//
//        if (backgroundColor != 0) {
//            int extra1Dp = (int) (((TextPaint) paint).density * 1 + 0.5f);
//            float width = paint.measureText(text.subSequence(start, end).toString());
//            int newTop = (int) (bottom - paint.getFontSpacing() - paint.descent()) + 2 * extra1Dp;
//            int newBottom = bottom - extra1Dp;
//            int newLeft = (int) (isLeftEdge ? x : x - radius);
//            int newRight = (int) (isLeftEdge ? x + width + 2 * radius : x + width + radius);
//            RectF rect = new RectF(newLeft, newTop, newRight, newBottom);
//            paint.setColor(backgroundColor);
//            canvas.drawRoundRect(rect, radius, radius, paint);
//        }
//
//        if (textColor != 0) {
//            float textX = isLeftEdge ? x + radius : x;
//            paint.setColor(textColor);
//            canvas.drawText(text, start, end, textX, y, paint);
//        }
//    }

//    private Drawable mDrawable;
//    private int mPadding;
//    private static int CORNER_RADIUS = 8;
//    private int backgroundColor = 0;
//    private int textColor = 0;
//
//    public RoundedBackgroundSpan(Context context) {
//        super();
//        mDrawable = context.getDrawable(R.drawable.bg_rounded_rectangle_border_grey);
//        mPadding = 8;
//    }
//
//    @Override
//    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
//        RectF rect = new RectF(x - mPadding, top - mPadding, x + measureText(paint, text, start, end) + mPadding, bottom + mPadding);
//
//        mDrawable.setBounds((int) rect.left, (int)rect.top, (int)rect.right, (int)rect.bottom);
//
//        canvas.drawText(text, start, end, x, y, paint);
//        mDrawable.draw(canvas);
//    }
//
//    @Override
//    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
//        return Math.round(paint.measureText(text, start, end));
//    }
//
//    private float measureText(Paint paint, CharSequence text, int start, int end) {
//        return paint.measureText(text, start, end);
//    }

//    private static final int CORNER_RADIUS = 12;
//
////    private static final float PADDING_X = GeneralUtils.convertDpToPx(12);
////    private static final float PADDING_Y = GeneralUtils.convertDpToPx(2);
////    private static final float MAGIC_NUMBER = GeneralUtils.convertDpToPx(2);
//
//    private static final float PADDING_X = 12;
//    private static final float PADDING_Y = 2;
//    private static final float MAGIC_NUMBER =2;
//
//    private int mBackgroundColor;
//    private int mTextColor;
//    private float mTextSize;
//
//    /**
//     * @param backgroundColor color value, not res id
//     * @param textSize        in pixels
//     */
//    public RoundedBackgroundSpan(int backgroundColor, int textColor, float textSize) {
//        mBackgroundColor = backgroundColor;
//        mTextColor = textColor;
//        mTextSize = textSize;
//    }
//
//    @Override
//    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
//        paint = new Paint(paint); // make a copy for not editing the referenced paint
//
//        paint.setTextSize(mTextSize);
//
//        // Draw the rounded background
//        paint.setColor(mBackgroundColor);
//        //float textHeightWrapping = GeneralUtils.convertDpToPx(4);
//        float textHeightWrapping = 8;
//        float tagBottom = top + textHeightWrapping + PADDING_Y + mTextSize + PADDING_Y + textHeightWrapping;
//        float tagRight = x + getTagWidth(text, start, end, paint);
//        RectF rect = new RectF(x, top, tagRight, tagBottom);
//        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
//
//        // Draw the text
//        paint.setColor(mTextColor);
//        canvas.drawText(text, start, end, x + PADDING_X, tagBottom - PADDING_Y - textHeightWrapping - MAGIC_NUMBER, paint);
//    }
//
//    private int getTagWidth(CharSequence text, int start, int end, Paint paint) {
//        return Math.round(PADDING_X + paint.measureText(text.subSequence(start, end).toString()) + PADDING_X);
//    }
//
//    @Override
//    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
//        paint = new Paint(paint); // make a copy for not editing the referenced paint
//        paint.setTextSize(mTextSize);
//        return getTagWidth(text, start, end, paint);
//    }
}