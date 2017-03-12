package com.weibuddy.adapter;

import android.content.Context;

import com.weibuddy.Content;
import com.weibuddy.util.AudioPlayerManager;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends ListRefreshableAdapter<Content, List<Content>> {

    private AudioPlayerManager audioPlayerManager;

    public ContentAdapter(Context context) {
        audioPlayerManager = new AudioPlayerManager(context);

        addDelegate(new ContentTextAdapterDelegate());
        addDelegate(new ContentImageAdapterDelegate());
        addDelegate(new ContentAudioAdapterDelegate(this, audioPlayerManager));
        addDelegate(new ContentVideoAdapterDelegate());
        addDelegate(new ContentDocumentAdapterDelegate());

        setItems(new ArrayList<Content>());
    }

    public void onDestroy() {
        audioPlayerManager.onDestroy();
    }
}
