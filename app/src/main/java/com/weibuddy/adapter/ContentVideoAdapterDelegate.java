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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.BuildConfig;
import com.weibuddy.CategoryEnum;
import com.weibuddy.Content;
import com.weibuddy.R;
import com.weibuddy.WebActivity;
import com.weibuddy.util.ViewUtils;

import java.util.List;

class ContentVideoAdapterDelegate extends AdapterDelegate<List<Content>> {

    @Override
    protected boolean isForViewType(@NonNull List<Content> items, int position) {
        Content content = items.get(position);
        try {
            CategoryEnum categoryEnum = CategoryEnum.valueOf(content.getCname());
            return categoryEnum == CategoryEnum.shipin;
        } catch (Exception e) {
            return false;
        }
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_video, parent, false);
        return new ContentVideoViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Content> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        if (holder instanceof ContentVideoViewHolder) {
            final Content content = items.get(position);
            final ContentVideoViewHolder vh = ContentVideoViewHolder.class.cast(holder);

            vh.text.setText(content.getName());
            Glide.with(vh.getContext())
                    .load(content.getVideoPic())
                    .placeholder(R.drawable.bg_default)
                    .error(R.drawable.bg_default)
                    .fitCenter()
                    .into(vh.image);

            vh.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.start(v.getContext(), content, true);
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
                            .load(content.getVideoPic())
                            .asBitmap()
                            .override(100, 100)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    WXVideoObject videoObj = new WXVideoObject();
                                    videoObj.videoUrl = content.getContent();

                                    WXMediaMessage msg = new WXMediaMessage();
                                    msg.mediaObject = videoObj;
                                    msg.title = content.getName();
                                    msg.setThumbImage(resource);

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

    private static class ContentVideoViewHolder extends BaseViewHolder {

        final ImageView image;
        final TextView text;

        ContentVideoViewHolder(@NonNull View itemView) {
            super(itemView);

            image = ViewUtils.findViewById(itemView, R.id.image);
            text = ViewUtils.findViewById(itemView, R.id.text);
        }
    }
}
