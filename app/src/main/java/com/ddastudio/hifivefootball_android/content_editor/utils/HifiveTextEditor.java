package com.ddastudio.hifivefootball_android.content_editor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.ddastudio.hifivefootball_android.ui.utils.ImageUtils;
import com.ddastudio.hifivefootball_android.content_editor.utils.span.HifiveURLSpan;

public class HifiveTextEditor extends android.support.v7.widget.AppCompatEditText implements TextWatcher {

    PicassoImageGetter imageGetter;

    public HifiveTextEditor(Context context) {
        super(context);
        init();
    }

    public HifiveTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HifiveTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /*------------------------------------------------------------------------------*/

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /*------------------------------------------------------------------------------*/



    int DEFAULT_IMAGE_WIDTH = 800;
    int maxImageWidth = 0;
    int minImageWidth = 0;

    private void init() {

        imageGetter = new PicassoImageGetter(this);
        int minScreenSize = Math.min(getContext().getResources().getDisplayMetrics().widthPixels,
                getContext().getResources().getDisplayMetrics().heightPixels);
        maxImageWidth = Math.min(minScreenSize, DEFAULT_IMAGE_WIDTH);
        minImageWidth = getLineHeight();
    }

    private BitmapDrawable getPlaceholderDrawableFromResId(Context context, @DrawableRes int drawableId, int maxImageWidthForVisualEditor) {

        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap;
        if ( drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            bitmap = ImageUtils.getScaledBitmapAtLongestSide(bitmap, maxImageWidthForVisualEditor);
        } else if ( drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            bitmap = Bitmap.createBitmap(maxImageWidthForVisualEditor, maxImageWidthForVisualEditor, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            throw new IllegalArgumentException("Unsupported Drawable Type");
        }

        bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public void fromHtml(String source) {

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(HifiveParser.fromHtml(source, imageGetter));
        switchToKnifeStyle(builder, 0, builder.length());
        setText(builder);

    }

    public String toHtml() {

        //return Html.toHtml(getText());
        return HifiveParser.toHtml(getEditableText());
    }

    protected void switchToKnifeStyle(Editable editable, int start, int end) {
//        BulletSpan[] bulletSpans = editable.getSpans(start, end, BulletSpan.class);
//        for (BulletSpan span : bulletSpans) {
//            int spanStart = editable.getSpanStart(span);
//            int spanEnd = editable.getSpanEnd(span);
//            spanEnd = 0 < spanEnd && spanEnd < editable.length() && editable.charAt(spanEnd) == '\n' ? spanEnd - 1 : spanEnd;
//            editable.removeSpan(span);
//            editable.setSpan(new KnifeBulletSpan(bulletColor, bulletRadius, bulletGapWidth), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        QuoteSpan[] quoteSpans = editable.getSpans(start, end, QuoteSpan.class);
//        for (QuoteSpan span : quoteSpans) {
//            int spanStart = editable.getSpanStart(span);
//            int spanEnd = editable.getSpanEnd(span);
//            spanEnd = 0 < spanEnd && spanEnd < editable.length() && editable.charAt(spanEnd) == '\n' ? spanEnd - 1 : spanEnd;
//            editable.removeSpan(span);
//            editable.setSpan(new KnifeQuoteSpan(quoteColor, quoteStripeWidth, quoteGapWidth), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }

        URLSpan[] urlSpans = editable.getSpans(start, end, URLSpan.class);
        for (URLSpan span : urlSpans) {
            int spanStart = editable.getSpanStart(span);
            int spanEnd = editable.getSpanEnd(span);
            editable.removeSpan(span);
            //
            // todo linkColor, linkUnderline 값은 스타일로 지정해서 값을 가져오자.
            //
            int linkColor = 0;
            boolean linkUnderline = true;
            editable.setSpan(new HifiveURLSpan(span.getURL(), linkColor, linkUnderline), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

//        ImageSpan[] imageSpans = editable.getSpans(start, end, ImageSpan.class);
//        for (ImageSpan span : imageSpans) {
//            int spanStart = editable.getSpanStart(span);
//            int spanEnd = editable.getSpanEnd(span);
//            editable.removeSpan(span);
//
//            editable.setSpan(new HifiveImageSpan(span.getDrawable(), span.getSource(), 800), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
    }

    /*------------------------------------------------------------------------------*/

    /**
     * Bold
     * @param valid
     */
    public void bold(boolean valid) {
        if ( valid ) {
            styleValid(Typeface.BOLD, getSelectionStart(), getSelectionEnd());
        } else {

        }
    }

    protected void styleValid(int style, int start, int end) {
        switch (style) {
            case Typeface.NORMAL:
            case Typeface.BOLD:
            case Typeface.ITALIC:
            case Typeface.BOLD_ITALIC:
                break;
            default:
                return;
        }

        if ( start >= end ) {
            return;
        }

        getEditableText().setSpan(new StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * 링크
     * @param link
     * @param start
     * @param end
     */
    public void link(String link, int start, int end) {

        if ( link != null && !TextUtils.isEmpty(link.trim())) {
            linkValid(link, start, end);
        } else {
            linkInvalid(start, end);
        }
    }

    protected void linkValid(String link, int start, int end) {
        if ( start >= end) {
            return;
        }

        linkInvalid(start, end);

        //
        // todo linkColor, linkUnderline 값은 스타일로 지정해서 값을 가져오자.
        //
        int linkColor = 0;
        boolean linkUnderline = true;
        getEditableText().setSpan(new HifiveURLSpan(link, linkColor, linkUnderline), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    protected void linkInvalid(int start, int end) {
        if ( start >= end) {
            return;
        }

        URLSpan[] spans = getEditableText().getSpans(start, end, URLSpan.class);
        for(URLSpan span : spans) {
            getEditableText().removeSpan(span);
        }
    }

    /**
     *
     * @param valid
     */
    public void quote(boolean valid) {

        if ( valid) {

        } else {

        }
    }

    protected void quoteValid() {
        String[] linke = TextUtils.split(getEditableText().toString(), "\n");
    }

    /*------------------------------------------------------------------------------*/


}
