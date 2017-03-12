package com.weibuddy.util.http;

import android.util.Log;

import com.weibuddy.BuildConfig;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class AsyncOkHttpClient {

    private static final String TAG = AsyncOkHttpClient.class.getSimpleName();

    private static class Holder {
        static final AsyncOkHttpClient INSTANCE = new AsyncOkHttpClient();
    }

    private final OkHttpClient mOkHttpClient;

    private AsyncOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.v(TAG, message);
            }
        });
        logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);
        builder.addNetworkInterceptor(logging);

        mOkHttpClient = builder.build();
    }

    public static AsyncOkHttpClient newInstance() {
        return Holder.INSTANCE;
    }

    public void get(String url, Callback callback) {
        mOkHttpClient.newCall(
                new Request.Builder()
                        .get()
                        .url(url)
                        .build())
                .enqueue(callback);
    }
}
