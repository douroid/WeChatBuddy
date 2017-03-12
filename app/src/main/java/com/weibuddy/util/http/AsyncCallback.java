package com.weibuddy.util.http;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.weibuddy.util.GsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AsyncCallback<T> extends SimpleCallback implements Handler.Callback {

    private static final int START = 1;
    private static final int SUCCESS = START << 1;
    private static final int FAIL = START << 2;
    private static final int FINISH = START << 3;

    private Handler mHandler;
    private Class<T> mClazz;

    public AsyncCallback(Class<T> clazz) {
        this.mClazz = clazz;
        this.mHandler = new Handler(Looper.getMainLooper(), this);

        sendStartMessage();
    }

    public void onStart() {
    }

    public void onSuccess(T obj) {
    }

    public void onFailure(Throwable throwable) {
    }

    public void onFinish() {
    }

    @Override
    public final boolean handleMessage(Message message) {
        switch (message.what) {
            case START:
                onStart();
                return true;
            case FINISH:
                onFinish();
                return true;
            case SUCCESS:
                T obj = (T) message.obj;
                onSuccess(obj);
                return true;
            case FAIL:
                Throwable throwable = (Throwable) message.obj;
                onFailure(throwable);
                return true;
        }
        return false;
    }

    @Override
    public final void onFailure(Call call, IOException e) {
        sendFailMessage(e);
        sendEndMessage();
    }

    @Override
    public final void onResponse(Call call, Response response) throws IOException {
        try {
            if (response.isSuccessful()) {
                T obj = GsonUtil.fromJson(response.body().charStream(), mClazz);
                sendSuccessMessage(obj);
            } else {
                int responseCode = response.code();
                String responseBody = response.body().string();
                sendFailMessage(new RequestException(responseCode, responseBody));
            }
        } catch (Exception e) {
            sendFailMessage(e);
        } finally {
            sendEndMessage();
        }
    }

    private Message obtainMessage(int responseMessage, Object response) {
        Message message;
        if (mHandler != null) {
            message = mHandler.obtainMessage(responseMessage, response);
        } else {
            message = Message.obtain();
            message.what = responseMessage;
            message.obj = response;
        }
        return message;
    }

    private void sendMessage(Message message) {
        if (mHandler != null) {
            mHandler.sendMessage(message);
        } else {
            handleMessage(message);
        }
    }

    private void sendSuccessMessage(T obj) {
        sendMessage(obtainMessage(SUCCESS, obj));
    }

    private void sendFailMessage(Throwable error) {
        sendMessage(obtainMessage(FAIL, error));
    }

    private void sendStartMessage() {
        sendMessage(obtainMessage(START, null));
    }

    private void sendEndMessage() {
        sendMessage(obtainMessage(FINISH, null));
    }
}
