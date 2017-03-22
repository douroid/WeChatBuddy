package com.weibuddy.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.AsyncShareImage;
import com.weibuddy.BuildConfig;
import com.weibuddy.CategoryEnum;
import com.weibuddy.Content;
import com.weibuddy.ImagePreviewActivity;
import com.weibuddy.R;
import com.weibuddy.util.ViewUtils;

import java.util.List;

class ContentImageAdapterDelegate extends AdapterDelegate<List<Content>> {

    @Override
    protected boolean isForViewType(@NonNull List<Content> items, int position) {
        Content content = items.get(position);
        try {
            CategoryEnum categoryEnum = CategoryEnum.valueOf(content.getCname());
            return categoryEnum == CategoryEnum.tupian;
        } catch (Exception e) {
            return false;
        }
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_image, parent, false);
        return new ContentImageViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Content> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        if (holder instanceof ContentImageViewHolder) {
            final Content content = items.get(position);
            final ContentImageViewHolder vh = ContentImageViewHolder.class.cast(holder);

            vh.text.setText(content.getName());
            Glide.with(vh.getContext())
                    .load(content.getContent())
                    .asBitmap()
                    .placeholder(R.drawable.bg_default)
                    .error(R.drawable.bg_default)
                    .into(new BitmapImageViewTarget(vh.image) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            content.setReady(false);
                            vh.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            super.onResourceReady(resource, glideAnimation);
                            content.setReady(true);
                            vh.progressBar.setVisibility(View.GONE);
                        }
                    });

            vh.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePreviewActivity.start(v.getContext(), content);
                }
            });

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final IWXAPI mWXApi = WXAPIFactory.createWXAPI(v.getContext(), BuildConfig.APP_KEY_WECHAT, false);
                    mWXApi.registerApp(BuildConfig.APP_KEY_WECHAT);

                    if (!mWXApi.isWXAppInstalled()) {
                        Toast.makeText(v.getContext(), R.string.wechat_app_not_installed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!content.isReady()) {
                        Toast.makeText(v.getContext(), R.string.data_is_not_ready, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Glide.with(v.getContext())
                            .load(content.getContent())
                            .asBitmap()
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    if (resource == null) {
                                        return;
                                    }

                                    new AsyncShareImage(v.getContext(), resource, content.getName(), mWXApi).execute();
                                }
                            });
                }
            });
        }
    }

    private static class ContentImageViewHolder extends BaseViewHolder {

        final ImageView image;
        final ProgressBar progressBar;
        final TextView text;

        ContentImageViewHolder(@NonNull View itemView) {
            super(itemView);

            image = ViewUtils.findViewById(itemView, R.id.image);
            progressBar = ViewUtils.findViewById(itemView, R.id.progress_bar);
            text = ViewUtils.findViewById(itemView, R.id.text);
        }
    }
}
