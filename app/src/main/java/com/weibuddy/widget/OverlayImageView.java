package com.weibuddy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

public class OverlayImageView extends android.support.v7.widget.AppCompatImageView {

    static int[] ATTRS = {
            android.R.attr.colorForeground
    };

    int overlayColor = Color.TRANSPARENT;

    public OverlayImageView(Context context) {
        this(context, null);
    }

    public OverlayImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
            overlayColor = a.getColor(0, Color.TRANSPARENT);
            a.recycle();
        }

        setColorFilter(overlayColor, PorterDuff.Mode.SRC_OVER);
    }

    public void setColorForeground(int color) {
        setColorFilter(color, PorterDuff.Mode.SRC_OVER);
    }
}
