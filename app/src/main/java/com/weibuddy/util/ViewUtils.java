package com.weibuddy.util;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;

public final class ViewUtils {

    public static <T> T findViewById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    public static <T> T findViewById(View parent, int id) {
        return (T) parent.findViewById(id);
    }

    public static void addOnGlobalLayoutListener(@NonNull final View view, @NonNull final Runnable runnable) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                runnable.run();
            }
        });
    }
}
