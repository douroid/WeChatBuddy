package com.weibuddy;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.dao.DaoMaster;
import com.weibuddy.dao.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.io.File;

public class WeiBuddyApp extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        setUpVmPolicy();
        setUpDatabase();
        setUpConfig();
        setUpWeChat();
    }

    private void setUpVmPolicy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void setUpDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "WeiBuddy-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    private void setUpConfig() {
        Config.setAvatarFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "avatar.png"));
    }

    private void setUpWeChat() {
        IWXAPI mWXApi = WXAPIFactory.createWXAPI(this, BuildConfig.APP_KEY_WECHAT, false);
        mWXApi.registerApp(BuildConfig.APP_KEY_WECHAT);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
