package com.weibuddy.adapter;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.BuildConfig;
import com.weibuddy.CategoryEnum;
import com.weibuddy.Content;
import com.weibuddy.R;
import com.weibuddy.util.AudioPlayerManager;
import com.weibuddy.util.ViewUtils;

import java.util.List;

class ContentAudioAdapterDelegate extends AdapterDelegate<List<Content>> {

    private ContentAdapter adapter;
    private AudioPlayerManager audioPlayerManager;

    ContentAudioAdapterDelegate(ContentAdapter adapter, AudioPlayerManager audioPlayerManager) {
        this.adapter = adapter;
        this.audioPlayerManager = audioPlayerManager;
    }

    @Override
    protected boolean isForViewType(@NonNull List<Content> items, int position) {
        Content content = items.get(position);
        try {
            CategoryEnum categoryEnum = CategoryEnum.valueOf(content.getCname());
            return categoryEnum == CategoryEnum.yuyin;
        } catch (Exception e) {
            return false;
        }
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_audio, parent, false);
        return new ContentAudioViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Content> items, final int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        if (holder instanceof ContentAudioViewHolder) {
            final Content content = items.get(position);
            final ContentAudioViewHolder vh = ContentAudioViewHolder.class.cast(holder);

            vh.text.setText(content.getName());
            final String audioFile = audioPlayerManager.getLastAudioFile();
            vh.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(audioFile) && audioFile.equals(content.getContent())) {
                        audioPlayerManager.stopPlay();
                    } else {
                        audioPlayerManager.startPlay(content.getContent());
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            vh.play.setSelected(!TextUtils.isEmpty(audioFile) && audioFile.equalsIgnoreCase(content.getContent()));

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IWXAPI mWXApi = WXAPIFactory.createWXAPI(v.getContext(), BuildConfig.APP_KEY_WECHAT, false);
                    mWXApi.registerApp(BuildConfig.APP_KEY_WECHAT);

                    if (!mWXApi.isWXAppInstalled()) {
                        Toast.makeText(v.getContext(), R.string.wechat_app_not_installed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    WXMusicObject musicObj = new WXMusicObject();
                    musicObj.musicUrl = content.getContent();

                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = musicObj;
                    msg.title = content.getName();
                    msg.setThumbImage(BitmapFactory.decodeResource(v.getResources(), R.mipmap.ic_launcher));

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                    req.message = msg;
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    mWXApi.sendReq(req);
                }
            });
        }
    }

    private static class ContentAudioViewHolder extends BaseViewHolder {

        final LinearLayout play;
        final TextView text;

        ContentAudioViewHolder(@NonNull View itemView) {
            super(itemView);

            play = ViewUtils.findViewById(itemView, R.id.play);
            text = ViewUtils.findViewById(itemView, R.id.text);
        }
    }
}
