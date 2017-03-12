package com.weibuddy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.weibuddy.util.ViewUtils;

public class AppBottomDialogFragment extends AppDialogFragment implements View.OnClickListener {

    public interface Callback {
        void onShowPicker();

        void onShowImageCapture();
    }

    private Callback callback;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!getShowsDialog()) {
            return;
        }

        Window window = getDialog().getWindow();
        assert window != null;
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.Animation_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_avatar_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        View album = ViewUtils.findViewById(view, R.id.album);
        View take = ViewUtils.findViewById(view, R.id.take);
        View cancel = ViewUtils.findViewById(view, R.id.cancel);

        album.setOnClickListener(this);
        take.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        callback = null;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.album: {
                if (callback != null) {
                    callback.onShowPicker();
                }
                dismissAllowingStateLoss();
                break;
            }
            case R.id.take: {
                if (callback != null) {
                    callback.onShowImageCapture();
                }
                dismissAllowingStateLoss();
                break;
            }
            case R.id.cancel: {
                dismissAllowingStateLoss();
                break;
            }
        }
    }

    public static void show(FragmentManager manager, Callback callback) {
        AppBottomDialogFragment fragment = new AppBottomDialogFragment();
        fragment.callback = callback;
        fragment.show(manager, "AppBottomDialogFragment");
    }
}
