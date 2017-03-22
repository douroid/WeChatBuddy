package com.weibuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.weibuddy.util.ViewUtils;

import uk.co.senab.photoview.PhotoView;

public class ImagePreviewActivity extends AppBaseCompatActivity {

    private PhotoView mPhotoView;
    private ProgressBar mProgressBar;
    private Content mContent;

    private Bitmap mResource;

    public static void start(Context context, Content content) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(Intent.EXTRA_REFERRER, content);
        context.startActivity(intent);
    }

    @Override
    protected int layout() {
        return R.layout.activity_image_preview;
    }

    @Override
    protected void setUpArguments() {
        final Intent intent = getIntent();
        mContent = intent.getParcelableExtra(Intent.EXTRA_REFERRER);
    }

    @Override
    protected void setUpViews() {
        setTitle(mContent.getName());

        mPhotoView = ViewUtils.findViewById(this, R.id.photo_view);
        mProgressBar = ViewUtils.findViewById(this, R.id.progress_bar);

        ViewUtils.addOnGlobalLayoutListener(mPhotoView, new Runnable() {
            @Override
            public void run() {
                render();
            }
        });
    }

    @Override
    protected boolean shareEnabled() {
        return true;
    }

    @Override
    protected void onShare() {
        if (!mWXApi.isWXAppInstalled()) {
            Toast.makeText(this, R.string.wechat_app_not_installed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mContent == null || mResource == null) {
            Toast.makeText(this, R.string.data_is_not_ready, Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncShareImage(this, mResource, mContent.getName(), mWXApi)
                .execute();
    }

    private void render() {
        final String url = mContent.getContent();
        Glide.with(this)
                .load(url)
                .asBitmap()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565)
                .into(new BitmapImageViewTarget(mPhotoView) {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        super.onLoadStarted(placeholder);
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mResource = resource;
                        super.onResourceReady(resource, glideAnimation);
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(ImagePreviewActivity.this, R.string.loading_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
