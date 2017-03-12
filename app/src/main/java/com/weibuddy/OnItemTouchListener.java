package com.weibuddy;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnItemTouchListener extends GestureDetector.SimpleOnGestureListener implements RecyclerView.OnItemTouchListener {

    public interface OnItemClickListener {
        void onItemClick(RecyclerView recyclerView, int position);
    }

    private RecyclerView mRecyclerView;
    private GestureDetectorCompat mGestureDetector;

    private OnItemClickListener mOnItemClickListener;

    public OnItemTouchListener setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        return this;
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        if (mRecyclerView != null) {
            mRecyclerView.removeOnItemTouchListener(this);
        }

        mRecyclerView = recyclerView;

        if (mRecyclerView != null) {
            mRecyclerView.addOnItemTouchListener(this);

            if (mGestureDetector == null) {
                mGestureDetector = new GestureDetectorCompat(mRecyclerView.getContext(), this);
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        try {
            View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mOnItemClickListener != null) {
                int position = mRecyclerView.getChildAdapterPosition(childView);

                mOnItemClickListener.onItemClick(mRecyclerView, position);
            }
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }
}
