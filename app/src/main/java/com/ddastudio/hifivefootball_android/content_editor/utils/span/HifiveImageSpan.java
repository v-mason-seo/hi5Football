package com.ddastudio.hifivefootball_android.content_editor.utils.span;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import java.lang.ref.WeakReference;

public class HifiveImageSpan extends ImageSpan {

    private static final int MIN_SCALE_WIDTH = 240;
    private WeakReference<Drawable> mDrawableRef;
    // TextView's width.
    private int mContainerWidth;

    public HifiveImageSpan(Drawable d, String source, int containerWidth) {
        super(d, source);
        mContainerWidth = containerWidth;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
//        Drawable d = getCachedDrawable();
//        Rect rect = d.getBounds();
//
//        if (fm != null) {
//            fm.ascent = -rect.bottom;
//            fm.descent = 0;
//
//            fm.top = fm.ascent;
//            fm.bottom = 0;
//        }
//
//        return rect.right;

        Drawable d = getCachedDrawable();
        Rect rect = getResizedDrawableBounds(d);

        if (fm != null) {
            fm.ascent = -rect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }
        return rect.right;
    }

    private Rect getResizedDrawableBounds(Drawable d) {
        if (d == null || d.getIntrinsicWidth() == 0) {
            return new Rect(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }
        int scaledHeight;

        if (d.getIntrinsicWidth() < mContainerWidth ) {
            // Image smaller than container's width.
            if (d.getIntrinsicWidth() > MIN_SCALE_WIDTH &&
                    d.getIntrinsicWidth() >= d.getIntrinsicHeight()) {
                // But larger than the minimum scale size, we need to scale the image to fit
                // the width of the container.
                int scaledWidth = mContainerWidth;
                scaledHeight = d.getIntrinsicHeight() * scaledWidth / d.getIntrinsicWidth();
                d.setBounds(0, 0, scaledWidth, scaledHeight);
            } else {
                // Smaller than the minimum scale size, leave it as is.
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            }
        } else {
            // Image is larger than the container's width, scale down to fit the container.
            int scaledWidth = mContainerWidth;
            scaledHeight = d.getIntrinsicHeight() * scaledWidth / d.getIntrinsicWidth();
            d.setBounds(0, 0, scaledWidth, scaledHeight);
        }

        return d.getBounds();
    }

//    @Override
//    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
//        Drawable b = getCachedDrawable();
//        canvas.save();
//
//        int transY = bottom - (b.getBounds().bottom / 2);
//        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
//        transY -= fm.descent - (fm.ascent / 2);
//
//        canvas.translate(x, transY);
//        b.draw(canvas);
//        canvas.restore();
//    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null)
            d = wr.get();

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<>(d);
        }

        return d;
    }
}
