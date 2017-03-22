package com.weibuddy;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.util.Util;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.weibuddy.util.BitmapUtil;

import java.io.ByteArrayOutputStream;

public class AsyncShareImage extends AsyncTask<Void, Void, byte[]> {

    private final MaterialDialog dialog;
    private final Bitmap resource;
    private final String title;
    private final IWXAPI mWXApi;

    public AsyncShareImage(Context context, Bitmap resource, String title, IWXAPI wxApi) {
        this.resource = resource;
        this.title = title;
        this.mWXApi = wxApi;

        this.dialog = new MaterialDialog.Builder(context)
                .content(R.string.loading)
                .progress(true, 0)
                .build();
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        final int maxSize = 2048;

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        if (Util.getBitmapByteSize(resource.getWidth(), resource.getHeight(), resource.getConfig()) > Config.IMAGE_LENGTH_LIMIT) {
            Bitmap scaledBitmap = BitmapUtil.createScaledBitmap(resource, maxSize);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 95, output);

            if (scaledBitmap != resource) {
                scaledBitmap.recycle();
            }
        } else {
            resource.compress(Bitmap.CompressFormat.JPEG, 95, output);
        }

        return output.toByteArray();
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = new WXImageObject(bytes);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Bitmap thumb = BitmapUtil.createScaledBitmap(resource, 128);
        thumb.compress(Bitmap.CompressFormat.JPEG, 95, output);
        msg.thumbData = output.toByteArray();

        msg.title = title;

        final SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = SendMessageToWX.Req.WXSceneSession;
        req.message = msg;
        req.transaction = String.valueOf(System.currentTimeMillis());

        mWXApi.sendReq(req);

        dialog.dismiss();
    }
}
