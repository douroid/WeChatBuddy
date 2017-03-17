package com.weibuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.util.ViewUtils;

public abstract class AppBaseCompatActivity extends AppCompatActivity {

    private TextView mTitle;

    protected IWXAPI mWXApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());
        setUpArguments();
        setUpWeChatSDK();
        setUpDaoSession();
        setUpToolbar();
        setUpViews();
        setUpReceiver();
    }

    protected abstract int layout();

    protected void setUpArguments() {

    }

    protected void setUpWeChatSDK() {
        mWXApi = WXAPIFactory.createWXAPI(this, BuildConfig.APP_KEY_WECHAT, false);
        mWXApi.registerApp(BuildConfig.APP_KEY_WECHAT);
    }

    protected void setUpDaoSession() {

    }

    protected void setUpToolbar() {
        Toolbar toolbar = ViewUtils.findViewById(this, R.id.toolbar);
        mTitle = ViewUtils.findViewById(this, R.id.title);

        if (navigationEnabled()) {
            toolbar.setNavigationIcon(R.drawable.ic_nav_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        toolbar.inflateMenu(R.menu.menu_basic);
        Menu menu = toolbar.getMenu();
        final int size = menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            switch (item.getItemId()) {
                case R.id.menu_settings: {
                    item.setVisible(settingsEnabled());
                    break;
                }
                case R.id.menu_share: {
                    item.setVisible(shareEnabled());
                    break;
                }
            }
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_share: {
                        onShare();
                        break;
                    }
                    case R.id.menu_settings: {
                        onSettings();
                        break;
                    }
                }
                return true;
            }
        });
    }

    protected void setUpViews() {

    }

    protected void setUpReceiver() {
        registerReceiver(mFinishedBroadcastReceiver, new IntentFilter(InternalIntent.ACTION_FINISHED));
    }

    protected boolean navigationEnabled() {
        return true;
    }

    protected boolean shareEnabled() {
        return false;
    }

    protected boolean settingsEnabled() {
        return false;
    }

    protected void onShare() {
    }

    protected void onSettings() {
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
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
