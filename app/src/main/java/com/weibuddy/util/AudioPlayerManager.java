package com.weibuddy.util;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;

public final class AudioPlayerManager {

    private AudioPlayer mAudioPlayer;
    private String mLastAudioFile;

    public AudioPlayerManager(@NonNull Context context) {
        mAudioPlayer = new AudioPlayer(context.getApplicationContext());
    }

    public void setOnPlayListener(AudioPlayer.OnPlayListener listener) {
        mAudioPlayer.setOnPlayListener(listener);
    }

    public void startPlay(String audioFile) {
        mAudioPlayer.setDataSource(audioFile);
        mAudioPlayer.start(AudioManager.STREAM_VOICE_CALL);
        mLastAudioFile = audioFile;
    }

    public void seekTo(int msec) {
        mAudioPlayer.seekTo(msec);
    }

    public void stopPlay() {
        mAudioPlayer.stop();
    }

    public boolean isPlaying() {
        return mAudioPlayer.isPlaying();
    }

    public long getCurrentPosition() {
        return mAudioPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return mAudioPlayer.getDuration();
    }

    /**
     * 当播放的时候，才能取得播放地址
     */
    public String getLastAudioFile() {
        return isPlaying() ? mLastAudioFile : null;
    }

    public void onDestroy() {
        if (mAudioPlayer != null) {
            mAudioPlayer.setOnPlayListener(null);
            mAudioPlayer.stop();
            mAudioPlayer = null;
        }
    }

}
