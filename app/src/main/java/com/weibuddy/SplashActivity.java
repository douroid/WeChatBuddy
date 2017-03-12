package com.weibuddy;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.weibuddy.util.SharedPreferencesCompat;

public class SplashActivity extends AppBaseCompatActivity
        implements Runnable {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(this, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(this);
    }

    @Override
    public void run() {
        if (SharedPreferencesCompat.with(this).isLogined()) {
            MainActivity.start(this);
        } else {
            SignInActivity.start(this);
        }
        finish();
    }
}
