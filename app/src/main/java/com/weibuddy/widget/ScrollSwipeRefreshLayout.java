package com.weibuddy.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

public class ScrollSwipeRefreshLayout extends SwipeRefreshLayout {

    private View mScrollUpChild;

    public ScrollSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ScrollSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollUpChild(View view) {
        mScrollUpChild = view;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if (mScrollUpChild != null) {
            return ViewCompat.canScrollVertically(mScrollUpChild, direction);
        }
        return super.canScrollVertically(direction);
    }
}
