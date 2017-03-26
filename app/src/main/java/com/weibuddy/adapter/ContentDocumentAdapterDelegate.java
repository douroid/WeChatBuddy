package com.weibuddy.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.BuildConfig;
import com.weibuddy.CategoryEnum;
import com.weibuddy.Content;
import com.weibuddy.R;
import com.weibuddy.util.ViewUtils;

import java.util.List;

class ContentDocumentAdapterDelegate extends AdapterDelegate<List<Content>> {

    @Override
    protected boolean isForViewType(@NonNull List<Content> items, int position) {
        Content content = items.get(position);
        try {
            CategoryEnum categoryEnum = CategoryEnum.valueOf(content.getCname());
            return categoryEnum == CategoryEnum.files;
        } catch (Exception e) {
            return false;
        }
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_document, parent, false);
        return new ContentDocumentViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Content> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        if (holder instanceof ContentDocumentViewHolder) {
            final Content content = items.get(position);
            final ContentDocumentViewHolder vh = ContentDocumentViewHolder.class.cast(holder);

            vh.text.setText(content.getName());
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final IWXAPI mWXApi = WXAPIFactory.createWXAPI(v.getContext(), BuildConfig.APP_KEY_WECHAT, false);
                    mWXApi.registerApp(BuildConfig.APP_KEY_WECHAT);

                    if (!mWXApi.isWXAppInstalled()) {
                        Toast.makeText(v.getContext(), R.string.wechat_app_not_installed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Glide.with(v.getContext())
                            .load(R.mipmap.ic_launcher)
                            .asBitmap()
                            .override(100, 100)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    WXWebpageObject webPageObj = new WXWebpageObject();
                                    webPageObj.webpageUrl = content.getContent();

                                    WXMediaMessage msg = new WXMediaMessage();
                                    msg.mediaObject = webPageObj;
                                    msg.title = content.getName();
                                    msg.setThumbImage(resource);

                                    SendMessageToWX.Req req = new SendMessageToWX.Req();
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

    private static class ContentDocumentViewHolder extends BaseViewHolder {

        final TextView text;

        ContentDocumentViewHolder(@NonNull View itemView) {
            super(itemView);

            text = ViewUtils.findViewById(itemView, R.id.text);
        }
    }
}
