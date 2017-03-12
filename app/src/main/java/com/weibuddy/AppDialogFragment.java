package com.weibuddy;

import android.support.v7.app.AppCompatDialogFragment;

public abstract class AppDialogFragment extends AppCompatDialogFragment {

    @Override
    public int getTheme() {
        return R.style.AppTheme_Dialog_NoActionBar_Transparent;
    }

}
