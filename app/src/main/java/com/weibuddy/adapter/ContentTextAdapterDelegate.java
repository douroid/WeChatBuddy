package com.weibuddy.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.BuildConfig;
import com.weibuddy.CategoryEnum;
import com.weibuddy.Content;
import com.weibuddy.R;
import com.weibuddy.TextPreviewActivity;
import com.weibuddy.util.ViewUtils;

import java.util.List;

class ContentTextAdapterDelegate extends AdapterDelegate<List<Content>> {

    @Override
    protected boolean isForViewType(@NonNull List<Content> items, int position) {
        Content content = items.get(position);
        try {
            CategoryEnum categoryEnum = CategoryEnum.valueOf(content.getCname());
            return categoryEnum == CategoryEnum.wenzi;
        } catch (Exception e) {
            return false;
        }
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_text, parent, false);
        return new ContentTextViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Content> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        if (holder instanceof ContentTextViewHolder) {
            final Content content = items.get(position);
            final ContentTextViewHolder vh = ContentTextViewHolder.class.cast(holder);

            vh.text.setText(content.getContent());
            vh.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextPreviewActivity.start(v.getContext(), content);
                }
            });
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IWXAPI mWXApi = WXAPIFactory.createWXAPI(v.getContext(), BuildConfig.APP_KEY_WECHAT, false);
                    mWXApi.registerApp(BuildConfig.APP_KEY_WECHAT);

                    if (!mWXApi.isWXAppInstalled()) {
                        Toast.makeText(v.getContext(), R.string.wechat_app_not_installed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    WXTextObject textObj = new WXTextObject();
                    textObj.text = content.getContent();

                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = textObj;
                    msg.description = content.getContent();

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                    req.message = msg;
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    mWXApi.sendReq(req);
                }
            });
        }
    }

    private static class ContentTextViewHolder extends BaseViewHolder {

        final TextView text;

        ContentTextViewHolder(@NonNull View itemView) {
            super(itemView);

            text = ViewUtils.findViewById(itemView, R.id.text);
        }
    }
}
