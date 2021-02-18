package com.ddastudio.hifivefootball_android.ui.utils;

import android.graphics.Bitmap;

public class ImageUtils {

    /*
     Resize a bitmap to the targetSize on its longest side.
     */
    public static Bitmap getScaledBitmapAtLongestSide(Bitmap bitmap, int targetSize) {
        if (bitmap == null || bitmap.getWidth() <= targetSize && bitmap.getHeight() <= targetSize) {
            // Do not resize.
            return bitmap;
        }

        int targetWidth, targetHeight;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            // Resize portrait bitmap
            targetHeight = targetSize;
            float percentage = (float) targetSize / bitmap.getHeight();
            targetWidth = (int)(bitmap.getWidth() * percentage);
        } else {
            // Resize landscape or square image
            targetWidth = targetSize;
            float percentage = (float) targetSize / bitmap.getWidth();
            targetHeight = (int)(bitmap.getHeight() * percentage);
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }
}
