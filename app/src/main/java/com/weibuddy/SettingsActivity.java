package com.weibuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.weibuddy.dao.DaoSession;
import com.weibuddy.util.HexUtil;
import com.weibuddy.util.MD5Util;
import com.weibuddy.util.SharedPreferencesCompat;
import com.weibuddy.util.ViewUtils;
import com.weibuddy.util.http.AsyncCallback;
import com.weibuddy.util.http.AsyncOkHttpClient;
import com.weibuddy.util.http.RequestException;
import com.weibuddy.util.http.SimpleCallback;

import java.io.IOException;

import io.mikael.urlbuilder.UrlBuilder;

public class SettingsActivity extends AppBaseCompatActivity
        implements View.OnClickListener {

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    @Override
    protected int layout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void setUpViews() {
        TextView intro = ViewUtils.findViewById(this, R.id.intro);
        TextView about = ViewUtils.findViewById(this, R.id.about);
        TextView service = ViewUtils.findViewById(this, R.id.service);
        TextView password = ViewUtils.findViewById(this, R.id.password);
        TextView clean = ViewUtils.findViewById(this, R.id.clean);
        TextView signOut = ViewUtils.findViewById(this, R.id.sign_out);

        intro.setOnClickListener(this);
        about.setOnClickListener(this);
        service.setOnClickListener(this);
        password.setOnClickListener(this);
        clean.setOnClickListener(this);
        signOut.setOnClickListener(this);

        final boolean isAuthenticated = SharedPreferencesCompat.with(this).isAuthenticated();
        signOut.setVisibility(isAuthenticated ? View.VISIBLE : View.GONE);
        password.setVisibility(isAuthenticated ? View.VISIBLE : View.GONE);
        clean.setVisibility(isAuthenticated ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.intro: {
                Content content = new Content();
                content.setName(getString(R.string.intro));
                content.setContent(Config.INTRO_URL);
                WebActivity.start(this, content, false);
                break;
            }
            case R.id.about: {
                Content content = new Content();
                content.setName(getString(R.string.about));
                content.setContent(Config.ABOUT_URL);
                WebActivity.start(this, content, false);
                break;
            }
            case R.id.service: {
                Content content = new Content();
                content.setName(getString(R.string.service));
                content.setContent(Config.SERVICE_URL);
                WebActivity.start(this, content, false);
                break;
            }
            case R.id.password: {
                changePassword();
                break;
            }
            case R.id.clean: {
                new MaterialDialog.Builder(this)
                        .content(R.string.action_wipe_cache_message)
                        .contentColor(Color.BLACK)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction dialogAction) {
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        Glide.get(SettingsActivity.this)
                                                .clearDiskCache();
                                        return null;
                                    }
                                }.execute();
                                Glide.get(SettingsActivity.this)
                                        .clearMemory();
                            }
                        })
                        .show();
                break;
            }
            case R.id.sign_out: {
                signOut();
                break;
            }
        }
    }

    private MaterialDialog dialog;
    private TextInputLayout curPasswordWrapper;
    private TextInputLayout newPasswordWrapper;
    private TextInputLayout renewPasswordWrapper;

    private void requestPassword() {
        String curPassword = curPasswordWrapper.getEditText().getText().toString();
        String newPassword = newPasswordWrapper.getEditText().getText().toString();
        String renewPassword = renewPasswordWrapper.getEditText().getText().toString();

        if (TextUtils.isEmpty(curPassword)) {
            curPasswordWrapper.setErrorEnabled(true);
            curPasswordWrapper.setError(getString(R.string.current_password_required));
            return;
        } else {
            curPasswordWrapper.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordWrapper.setErrorEnabled(true);
            newPasswordWrapper.setError(getString(R.string.new_password_required));
            return;
        } else {
            newPasswordWrapper.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(renewPassword)) {
            renewPasswordWrapper.setErrorEnabled(true);
            renewPasswordWrapper.setError(getString(R.string.renew_password_required));
            return;
        } else {
            renewPasswordWrapper.setErrorEnabled(false);
        }

        if (!newPassword.equals(renewPassword)) {
            renewPasswordWrapper.setErrorEnabled(true);
            renewPasswordWrapper.setError(getString(R.string.password_not_match));
            return;
        } else {
            renewPasswordWrapper.setErrorEnabled(false);
        }

        SharedPreferencesCompat sharedPrefs = SharedPreferencesCompat.with(this);
        String sign = HexUtil.encodeHexStr(MD5Util.md5(Config.MD5_KEY.concat(sharedPrefs.getUserId())));

        final String url = UrlBuilder.fromString(Config.API_PASSWORD)
                .addParameter(Config.KEY_USER_ID, sharedPrefs.getUserId())
                .addParameter(Config.KEY_OLD_PWD, curPassword)
                .addParameter(Config.KEY_NEW_PWD, newPassword)
                .addParameter(Config.KEY_SIGN, sign)
                .toString();
        AsyncOkHttpClient.newInstance().get(url, new AsyncCallback<Password>(Password.class) {
            @Override
            public void onSuccess(Password pwd) {
                if (pwd.isSuccessed() && dialog != null) {
                    dialog.dismiss();
                }
                Toast.makeText(SettingsActivity.this, pwd.msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (throwable instanceof RequestException) {
                    Toast.makeText(SettingsActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (throwable instanceof IOException) {
                    Toast.makeText(SettingsActivity.this, R.string.error_io, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePassword() {
        dialog = new MaterialDialog.Builder(this)
                .title(R.string.modify_password)
                .customView(R.layout.layout_password, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .autoDismiss(false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction dialogAction) {
                        requestPassword();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction dialogAction) {
                        dialog.dismiss();
                    }
                })
                .build();
        curPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.cur_password_wrapper);
        newPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.new_password_wrapper);
        renewPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.renew_password_wrapper);
        dialog.show();
    }

    private void resetDatabase() {
        DaoSession daoSession = ((WeiBuddyApp) getApplication()).getDaoSession();
        daoSession.getContentDao().deleteAll();
        daoSession.getCategoryDao().deleteAll();
        daoSession.getFolderDao().deleteAll();
    }

    private void signOut() {
        final SharedPreferencesCompat sharedPrefs = SharedPreferencesCompat.with(this);

        final String url = UrlBuilder.fromString(Config.API)
                .addParameter(Config.KEY_METHOD, Config.VALUE_METHOD_SIGN_OUT)
                .addParameter(Config.KEY_USER_ID, sharedPrefs.getUserId())
                .addParameter(Config.KEY_RAND_CODE, sharedPrefs.getRandCode())
                .toString();

        AsyncOkHttpClient.newInstance().get(url, new SimpleCallback());

        sharedPrefs.clear();
        Config.resetAvatarFile();
        resetDatabase();

        sendBroadcast(new Intent(InternalIntent.ACTION_FINISHED));
        startActivity(new Intent(SettingsActivity.this, SignInActivity.class));
        finish();
    }
}
