package com.weibuddy.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.util.Util;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.BuildConfig;
import com.weibuddy.CategoryEnum;
import com.weibuddy.Config;
import com.weibuddy.Content;
import com.weibuddy.ImagePreviewActivity;
import com.weibuddy.R;
import com.weibuddy.util.BitmapUtil;
import com.weibuddy.util.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

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
            String url = content.getContent();
            if (url.toLowerCase(Locale.getDefault()).endsWith(Config.SUFFIX_GIF)) {
                Glide.with(vh.getContext())
                        .load(url)
                        .asGif()
                        .placeholder(R.drawable.bg_default_loading)
                        .error(R.drawable.bg_default_error)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(vh.image);
            } else {
                Glide.with(vh.getContext())
                        .load(content.getContent())
                        .asBitmap()
                        .placeholder(R.drawable.bg_default)
                        .error(R.drawable.bg_default)
                        .into(vh.image);
            }

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

                    Glide.with(v.getContext())
                            .load(content.getContent())
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
                                    msg.title = content.getName();

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
    }

    private static class ContentImageViewHolder extends BaseViewHolder {

        final ImageView image;
        final TextView text;

        ContentImageViewHolder(@NonNull View itemView) {
            super(itemView);

            image = ViewUtils.findViewById(itemView, R.id.image);
            text = ViewUtils.findViewById(itemView, R.id.text);
        }
    }
}
