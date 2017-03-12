package com.weibuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.util.Util;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.util.BitmapUtil;
import com.weibuddy.util.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import uk.co.senab.photoview.PhotoView;

public class ImagePreviewActivity extends AppBaseCompatActivity {

    private IWXAPI mWXApi;
    private PhotoView mPhotoView;
    private Content mContent;

    public static void start(Context context, Content content) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(Intent.EXTRA_REFERRER, content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        setContentView(R.layout.activity_image_preview);

        setUpArguments();
        setUpWeChat();
        setUpViews();
        render();
    }

    private void setUpArguments() {
        final Intent intent = getIntent();
        mContent = intent.getParcelableExtra(Intent.EXTRA_REFERRER);
    }

    private void setUpWeChat() {
        mWXApi = WXAPIFactory.createWXAPI(this, BuildConfig.APP_KEY_WECHAT, false);
        mWXApi.registerApp(BuildConfig.APP_KEY_WECHAT);
    }

    private void setUpViews() {
        Toolbar toolbar = ViewUtils.findViewById(this, R.id.toolbar);
        TextView title = ViewUtils.findViewById(this, R.id.title);
        ImageButton send = ViewUtils.findViewById(this, R.id.send);
        mPhotoView = ViewUtils.findViewById(this, R.id.photo_view);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText(mContent.getName());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWXApi.isWXAppInstalled()) {
                    Toast.makeText(v.getContext(), R.string.wechat_app_not_installed, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mContent == null) {
                    Toast.makeText(v.getContext(), R.string.data_is_not_ready, Toast.LENGTH_SHORT).show();
                    return;
                }

                Glide.with(ImagePreviewActivity.this)
                        .load(mContent.getContent())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource == null) {
                                    return;
                                }

                                int quality = 90;
                                int realLength = Util.getBitmapByteSize(resource.getWidth(), resource.getHeight(), Bitmap.Config.ARGB_8888);
                                if (realLength > Config.IMAGE_LENGTH_LIMIT) {
                                    quality = (int) (Config.IMAGE_LENGTH_LIMIT * 1f / realLength * 100);
                                }
                                if (quality < 75) {
                                    quality = 75;
                                }
                                ByteArrayOutputStream output = new ByteArrayOutputStream();
                                resource.compress(Bitmap.CompressFormat.JPEG, quality, output);
                                WXImageObject imageObj = new WXImageObject(output.toByteArray());

                                WXMediaMessage msg = new WXMediaMessage();
                                msg.mediaObject = imageObj;
                                msg.title = mContent.getName();

                                Bitmap thumb = BitmapUtil.createScaledBitmap(resource, 100, true);
                                output.reset();
                                thumb.compress(Bitmap.CompressFormat.JPEG, 85, output);
                                msg.thumbData = output.toByteArray();

                                final SendMessageToWX.Req req = new SendMessageToWX.Req();
                                req.scene = SendMessageToWX.Req.WXSceneSession;
                                req.message = msg;
                                req.transaction = String.valueOf(System.currentTimeMillis());

                                mWXApi.sendReq(req);
                            }
                        });
            }
        });
    }

    private void render() {
        final String url = mContent.getContent();
        if (url.toLowerCase(Locale.getDefault()).endsWith(Config.SUFFIX_GIF)) {
            Glide.with(this)
                    .load(url)
                    .asGif()
                    .placeholder(R.drawable.bg_default_loading)
                    .error(R.drawable.bg_default_error)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mPhotoView);
        } else {
            Glide.with(this)
                    .load(url)
                    .asBitmap()
                    .placeholder(R.drawable.bg_default_loading)
                    .error(R.drawable.bg_default_error)
                    .into(new BitmapImageViewTarget(mPhotoView));
        }
    }
}
