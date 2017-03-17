package com.weibuddy;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.weibuddy.util.ViewUtils;

public class TextPreviewActivity extends AppBaseCompatActivity {

    private Content mContent;

    public static void start(Context context, Content content) {
        Intent intent = new Intent(context, TextPreviewActivity.class);
        intent.putExtra(Intent.EXTRA_REFERRER, content);
        context.startActivity(intent);
    }

    @Override
    protected int layout() {
        return R.layout.activity_text_preview;
    }

    @Override
    protected void setUpArguments() {
        final Intent intent = getIntent();
        mContent = intent.getParcelableExtra(Intent.EXTRA_REFERRER);
    }

    @Override
    protected void setUpViews() {
        setTitle(R.string.title_text_preview);
        TextView text = ViewUtils.findViewById(this, R.id.text);
        text.setText(mContent.getContent());
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

        if (mContent == null) {
            Toast.makeText(this, R.string.data_is_not_ready, Toast.LENGTH_SHORT).show();
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
}
