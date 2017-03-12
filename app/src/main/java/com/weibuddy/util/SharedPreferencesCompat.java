package com.weibuddy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class SharedPreferencesCompat {

    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_USER_NAME = "key_user_name";
    private static final String KEY_NICK_NAME = "key_nick_name";
    private static final String KEY_RAND_CODE = "key_rand_code";

    private static SharedPreferencesCompat SINGLETON = null;

    public static SharedPreferencesCompat with(@NonNull Context context) {
        if (SINGLETON == null) {
            synchronized (SharedPreferencesCompat.class) {
                if (SINGLETON == null) {
                    SINGLETON = new SharedPreferencesCompat(context.getApplicationContext());
                }
            }
        }
        return SINGLETON;
    }

    private SharedPreferences mSharedPrefs;

    private SharedPreferencesCompat(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void set(String uid, String username, String nickname, String randCode) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(KEY_USER_ID, uid);
        editor.putString(KEY_USER_NAME, username);
        editor.putString(KEY_NICK_NAME, nickname);
        editor.putString(KEY_RAND_CODE, randCode);
        editor.apply();
    }

    public String getUserId() {
        return mSharedPrefs.getString(KEY_USER_ID, "");
    }

    public String getUserName() {
        return mSharedPrefs.getString(KEY_USER_NAME, "");
    }

    public String getNickName() {
        return mSharedPrefs.getString(KEY_NICK_NAME, "");
    }

    public String getRandCode() {
        return mSharedPrefs.getString(KEY_RAND_CODE, "");
    }

    public boolean isLogined() {
        return !TextUtils.isEmpty(getUserId()) && !TextUtils.isEmpty(getRandCode());
    }

    public void clear() {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_RAND_CODE);
        editor.apply();
    }
}
