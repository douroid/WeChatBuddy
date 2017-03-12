package com.weibuddy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.weibuddy.util.KeyboardUtil;
import com.weibuddy.util.SharedPreferencesCompat;
import com.weibuddy.util.ValueUtil;
import com.weibuddy.util.ViewUtils;
import com.weibuddy.util.http.AsyncCallback;
import com.weibuddy.util.http.AsyncOkHttpClient;
import com.weibuddy.util.http.RequestException;

import java.io.IOException;

import io.mikael.urlbuilder.UrlBuilder;

public class SignInActivity extends AppBaseCompatActivity {

    private TextInputLayout mUsernameWrapper;
    private TextInputLayout mPasswordWrapper;
    private TextView mSignInButton;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SignInActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setUpViews();
    }

    private void setUpViews() {
    	ImageButton settings = ViewUtils.findViewById(this, R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.start(v.getContext());
            }
        });

        mUsernameWrapper = ViewUtils.findViewById(this, R.id.username_wrapper);
        mPasswordWrapper = ViewUtils.findViewById(this, R.id.password_wrapper);
        mSignInButton = ViewUtils.findViewById(this, R.id.sign_in);
        TextView visit = ViewUtils.findViewById(this, R.id.visit);

        mSignInButton.setOnClickListener(mSignInClick);
        visit.setText(getString(R.string.visit, getString(R.string.app_name)));
        LinkBuilder.on(visit)
                .addLink(
                        new Link("访问官网")
                                .setTextColor(0xFF2C67C8)
                                .setUnderlined(false)
                                .setHighlightAlpha(0f)
                                .setOnClickListener(new Link.OnClickListener() {
                                    @Override
                                    public void onClick(String clickedText) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(Config.SITE));
                                        startActivity(intent);
                                    }
                                })
                                .setBold(false)
                )
                .build();

        EditText username = mUsernameWrapper.getEditText();
        if (username != null) {
            String name = ValueUtil.value(SharedPreferencesCompat.with(this).getUserName(), "");
            username.setText(name);
            Selection.setSelection(username.getText(), name.length());
        }
    }

    private View.OnClickListener mSignInClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = "";
            if (mUsernameWrapper.getEditText() != null) {
                username = mUsernameWrapper.getEditText().getText().toString();
            }
            String password = "";
            if (mPasswordWrapper.getEditText() != null) {
                password = mPasswordWrapper.getEditText().getText().toString();
            }

            if (!validateTextLength(username)) {
                mUsernameWrapper.setErrorEnabled(true);
                mUsernameWrapper.setError(getString(R.string.username_required));
                mUsernameWrapper.requestFocus();
                return;
            } else {
                mUsernameWrapper.setErrorEnabled(false);
            }

            if (!validateTextLength(password)) {
                mPasswordWrapper.setErrorEnabled(true);
                mPasswordWrapper.setError(getString(R.string.password_required));
                mPasswordWrapper.requestFocus();
                return;
            } else {
                mPasswordWrapper.setErrorEnabled(false);
            }

            v.setEnabled(false);
            KeyboardUtil.hideKeyboard(v);
            signIn(username, password);
        }
    };

    private void signIn(final String username, String password) {
        final String url = UrlBuilder.fromString(Config.API)
                .addParameter(Config.KEY_METHOD, Config.VALUE_METHOD_SIGN_IN)
                .addParameter(Config.KEY_USER_NAME, username)
                .addParameter(Config.KEY_USER_PWD, password)
                .toString();

        AsyncOkHttpClient.newInstance().get(url, new AsyncCallback<User>(User.class) {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(User user) {
                if (user.isSuccessed()) {
                    SharedPreferencesCompat.with(SignInActivity.this).set(user.id, username, user.name, user.randCode);

                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignInActivity.this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (throwable instanceof RequestException) {
                    Toast.makeText(SignInActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (throwable instanceof IOException) {
                    Toast.makeText(SignInActivity.this, R.string.error_io, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignInActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mSignInButton.setEnabled(true);
            }
        });
    }

    private boolean validateTextLength(String text) {
        return !TextUtils.isEmpty(text);
    }

}
