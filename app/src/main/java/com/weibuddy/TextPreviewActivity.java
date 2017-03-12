package com.weibuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weibuddy.util.ViewUtils;

public class TextPreviewActivity extends AppBaseCompatActivity {

    private IWXAPI mWXApi;
    private Content mContent;

    public static void start(Context context, Content content) {
        Intent intent = new Intent(context, TextPreviewActivity.class);
        intent.putExtra(Intent.EXTRA_REFERRER, content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_preview);

        setUpArguments();
        setUpWeChat();
        setUpViews();
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
        ImageButton send = ViewUtils.findViewById(this, R.id.send);
        TextView text = ViewUtils.findViewById(this, R.id.text);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

                WXTextObject textObj = new WXTextObject();
                textObj.text = mContent.getContent();

                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = textObj;
                msg.description = mContent.getContent();

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.scene = SendMessageToWX.Req.WXSceneSession;
                req.message = msg;
                req.transaction = String.valueOf(System.currentTimeMillis());
                mWXApi.sendReq(req);
            }
        });

        text.setText(mContent.getContent());
    }
}
