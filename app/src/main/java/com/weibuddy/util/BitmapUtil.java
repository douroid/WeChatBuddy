package com.weibuddy.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtil {

    public static Bitmap createScaledBitmap(Bitmap src, int dstSize, boolean filter) {
        Matrix matrix = new Matrix();
        final int width = src.getWidth();
        final int height = src.getHeight();

        float xScale = width * 1f / dstSize;
        float yScale = height * 1f / dstSize;
        float scale = Math.min(xScale, yScale);

        final int dstWidth = (int) (width / scale);
        final int dstHeight = (int) (height / scale);

        matrix.setScale(scale, scale);
        return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, filter);
    }
}
