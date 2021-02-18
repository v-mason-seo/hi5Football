package com.ddastudio.hifivefootball_android.content_editor.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

import com.ddastudio.hifivefootball_android.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PicassoImageGetter implements Html.ImageGetter {

    private HifiveTextEditor textView = null;

    public PicassoImageGetter() {

    }

    int DEFAULT_IMAGE_WIDTH = 800;
    int maxImageWidth = 0;
    int minImageWidth = 0;

    public PicassoImageGetter(HifiveTextEditor target) {
        textView = target;
        int minScreenSize = Math.min(textView.getContext().getResources().getDisplayMetrics().widthPixels,
                textView.getContext().getResources().getDisplayMetrics().heightPixels);
        maxImageWidth = Math.min(minScreenSize, DEFAULT_IMAGE_WIDTH);
        minImageWidth = textView.getLineHeight();
    }

    @Override
    public Drawable getDrawable(String source) {
        BitmapDrawablePlaceHolder drawable = new BitmapDrawablePlaceHolder();
        Picasso.get()
                .load(source)
                .placeholder(R.drawable.ic_image)
                .into(drawable);
        return drawable;
    }

    private class BitmapDrawablePlaceHolder extends BitmapDrawable implements Target {

        protected Drawable drawable;

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
            int width = drawable.getIntrinsicWidth();
            //int width = maxImageWidth;
            int height = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, width, height);
            setBounds(0, 0, width, height);
            if (textView != null) {
                textView.setText(textView.getText());
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setDrawable(new BitmapDrawable(textView.getContext().getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

    }
}
