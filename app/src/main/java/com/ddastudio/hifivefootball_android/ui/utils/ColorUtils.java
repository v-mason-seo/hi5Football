package com.ddastudio.hifivefootball_android.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

import com.ddastudio.hifivefootball_android.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ColorUtils {

    private ColorUtils() { }

    public static final int IS_LIGHT = 0;
    public static final int IS_DARK = 1;
    public static final int LIGHTNESS_UNKNOWN = 2;


    public static @ColorInt int getSubMinuteTextColor(Context context, String substitution) {

        if ( substitution.equals("off")) {
            return ContextCompat.getColor(context, R.color.md_red_500);
        } else if ( substitution.equals("on")) {
            return ContextCompat.getColor(context, R.color.md_green_400);
        }

        return ContextCompat.getColor(context, R.color.md_grey_800);
    }

    /**
     * 평점별 백그라운드 컬러 가져오기
     * @param context
     * @param rating
     * @return
     */
    public static @ColorInt int getRatingBackgroundColor(Context context, float rating) {

        if ( rating >= 8.0) {
            return ContextCompat.getColor(context, R.color.rating_color_8);
        } else if ( rating >= 7.0 && rating < 8.0) {
            return ContextCompat.getColor(context, R.color.md_light_green_800);
        } else if ( rating >= 6.0 && rating < 7.0) {
            return ContextCompat.getColor(context, R.color.md_lime_600);
        } else if ( rating >= 4.0 && rating < 6.0) {
            return ContextCompat.getColor(context, R.color.md_amber_400);
        } else if ( rating >= 2.0 && rating < 4) {
            return ContextCompat.getColor(context, R.color.md_orange_400);
        } else if ( rating > 0 && rating < 2) {
            return ContextCompat.getColor(context, R.color.md_red_500);
        }

        return ContextCompat.getColor(context, R.color.md_grey_400);
    }

    public static @ColorInt int getRatingTextColor(Context context, float rating) {

        if ( rating >= 8.0) {
            return ContextCompat.getColor(context, R.color.rating_color_8);
        } else if ( rating >= 7.0 && rating < 8.0) {
            return ContextCompat.getColor(context, R.color.md_light_green_800);
        } else if ( rating >= 6.0 && rating < 7.0) {
            return ContextCompat.getColor(context, R.color.md_lime_600);
        } else if ( rating >= 4.0 && rating < 6.0) {
            return ContextCompat.getColor(context, R.color.md_amber_400);
        } else if ( rating >= 2.0 && rating < 4) {
            return ContextCompat.getColor(context, R.color.md_orange_400);
        } else if ( rating > 0 && rating < 2) {
            return ContextCompat.getColor(context, R.color.md_red_500);
        }

        return ContextCompat.getColor(context, R.color.md_grey_800);
    }

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     */
    public static @CheckResult
    @ColorInt
    int modifyAlpha(@ColorInt int color,
                    @IntRange(from = 0, to = 255) int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    /**
     * Checks if the most populous color in the given palette is dark
     * <p/>
     * Annoyingly we have to return this Lightness 'enum' rather than a boolean as palette isn't
     * guaranteed to find the most populous color.
     */
    public static @Lightness int isDark(Palette palette) {
        Palette.Swatch mostPopulous = getMostPopulousSwatch(palette);
        if (mostPopulous == null) return LIGHTNESS_UNKNOWN;
        return isDark(mostPopulous.getHsl()) ? IS_DARK : IS_LIGHT;
    }

    public static @Nullable
    Palette.Swatch getMostPopulousSwatch(Palette palette) {
        Palette.Swatch mostPopulous = null;
        if (palette != null) {
            for (Palette.Swatch swatch : palette.getSwatches()) {
                if (mostPopulous == null || swatch.getPopulation() > mostPopulous.getPopulation()) {
                    mostPopulous = swatch;
                }
            }
        }
        return mostPopulous;
    }

    /**
     * Determines if a given bitmap is dark. This extracts a palette inline so should not be called
     * with a large image!!
     * <p/>
     * Note: If palette fails then check the color of the central pixel
     */
    public static boolean isDark(@NonNull Bitmap bitmap) {
        return isDark(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
    }

    /**
     * Determines if a given bitmap is dark. This extracts a palette inline so should not be called
     * with a large image!! If palette fails then check the color of the specified pixel
     */
    public static boolean isDark(@NonNull Bitmap bitmap, int backupPixelX, int backupPixelY) {
        // first try palette with a small color quant size
        Palette palette = Palette.from(bitmap).maximumColorCount(3).generate();
        if (palette != null && palette.getSwatches().size() > 0) {
            return isDark(palette) == IS_DARK;
        } else {
            // if palette failed, then check the color of the specified pixel
            return isDark(bitmap.getPixel(backupPixelX, backupPixelY));
        }
    }

    /**
     * Check that the lightness value (0–1)
     */
    public static boolean isDark(float[] hsl) { // @Size(3)
        return hsl[2] < 0.5f;
    }

    /**
     * Convert to HSL & check that the lightness value
     */
    public static boolean isDark(@ColorInt int color) {
        float[] hsl = new float[3];
        android.support.v4.graphics.ColorUtils.colorToHSL(color, hsl);
        return isDark(hsl);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IS_LIGHT, IS_DARK, LIGHTNESS_UNKNOWN})
    public @interface Lightness {
    }
}
