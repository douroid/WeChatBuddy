package com.weibuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class AppBaseCompatActivity extends AppCompatActivity {

    public static final String ACTION_FINISHED = "com.weibuddy.intent.action.FINISHED";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver(mFinishedBroadcastReceiver, new IntentFilter(ACTION_FINISHED));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mFinishedBroadcastReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mFinishedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
