package com.weibuddy.util;

import android.graphics.Bitmap;

public class BitmapUtil {

    public static Bitmap createScaledBitmap(Bitmap src, int dstSize, boolean filter) {
        final int width = src.getWidth();
        final int height = src.getHeight();

        float xScale = width * 1f / dstSize;
        float yScale = height * 1f / dstSize;
        float scale = Math.min(xScale, yScale);

        final int dstWidth = (int) (width / scale);
        final int dstHeight = (int) (height / scale);

        return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, filter);
    }

    public static Bitmap createScaledBitmap(Bitmap resource, int dstSize) {
        final int width = resource.getWidth();
        final int height = resource.getHeight();

        float scale = dstSize * 1f / Math.max(width, height);

        final int dstWidth = (int) (width * scale);
        final int dstHeight = (int) (height * scale);

        return Bitmap.createScaledBitmap(resource, dstWidth, dstHeight, true);
    }
}
