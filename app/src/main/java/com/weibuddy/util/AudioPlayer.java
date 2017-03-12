package com.weibuddy.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;

public class AudioPlayer {

    public interface OnPlayListener {
        /**
         * 文件解码完成，准备播放
         */
        public void onPrepared();

        /**
         * 播放完成
         */
        public void onCompletion();

        /**
         * 中断播放
         */
        public void onInterrupt();

        /**
         * 出错
         *
         * @param error 错误原因
         */
        public void onError(String error);

        /**
         * 播放过程
         *
         * @param curPosition 音频当前播放位置
         */
        public void onPlaying(long curPosition);
    }

    private MediaPlayer mPlayer;

    private String mAudioFile;

    private long mIntervalTime = 500L;

    private AudioManager audioManager;

    private OnPlayListener mListener;

    private int audioStreamType = AudioManager.STREAM_VOICE_CALL;

    private static final int WHAT_COUNT_PLAY = 0x000;

    private static final int WHAT_DECODE_SUCCEED = 0x001;

    private static final int WHAT_DECODE_FAILED = 0x002;

    /**
     * 音频播放器构造函数
     *
     * @param context 上下文参数
     */
    public AudioPlayer(Context context) {
        this(context, null, null);
    }

    /**
     * 音频播放器构造函数
     *
     * @param context   上下文参数
     * @param audioFile 待播放音频的文件路径
     * @param listener  播放进度监听者
     */
    public AudioPlayer(Context context, String audioFile, OnPlayListener listener) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioFile = audioFile;
        mListener = listener;
    }

    /**
     * 设置音频来源
     *
     * @param audioFile 待播放音频的文件路径
     */
    public void setDataSource(String audioFile) {
        if (!TextUtils.equals(audioFile, mAudioFile)) {
            mAudioFile = audioFile;
        }
    }

    /**
     * 设置播放监听
     *
     * @param listener
     */
    public void setOnPlayListener(OnPlayListener listener) {
        mListener = listener;
    }

    public OnPlayListener getOnPlayListener() {
        return mListener;
    }

    /**
     * 开始播放
     *
     * @param audioStreamType 设置播放音频流类型, 用于切换听筒/耳机播放 取值见android.media.AudioManager
     */
    public void start(int audioStreamType) {
        this.audioStreamType = audioStreamType;
        startPlay();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mPlayer != null) {
            endPlay();
            if (mListener != null) {
                mListener.onInterrupt();
            }
        }
    }

    /**
     * 查询是否正在播放
     *
     * @return 如果为true，表示正在播放，否则没有播放
     */
    public boolean isPlaying() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取音频持续时间长度
     *
     * @return 持续时间
     */
    public long getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        } else {
            return 0;
        }
    }

    /**
     * 获取当前音频播放进度
     *
     * @return 当前播放进度
     */
    public long getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    /**
     * 让播放器跳转到指定位置继续播放
     *
     * @param msec 指定播放位置，单位为毫秒
     */
    public void seekTo(int msec) {
        mPlayer.seekTo(msec);
    }

    private void startPlay() {
        endPlay();

        startInner();
    }

    private void endPlay() {
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mHandler.removeMessages(WHAT_COUNT_PLAY);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_COUNT_PLAY:
                    if (mListener != null) {
                        mListener.onPlaying(mPlayer.getCurrentPosition());
                    }
                    sendEmptyMessageDelayed(WHAT_COUNT_PLAY, mIntervalTime);
                    break;
                case WHAT_DECODE_FAILED: {
                    //ToastUtil.showToast(mContext, "语音播放失败");
                }
                break;
                case WHAT_DECODE_SUCCEED:
                    startInner();
                    break;

            }
        }
    };


    private void startInner() {
        mPlayer = new MediaPlayer();
        mPlayer.setLooping(false);
        mPlayer.setAudioStreamType(audioStreamType);

        if (audioStreamType == AudioManager.STREAM_MUSIC) {
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setSpeakerphoneOn(false);
        }

        audioManager.requestAudioFocus(
                onAudioFocusChangeListener,
                audioStreamType,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mHandler.sendEmptyMessage(WHAT_COUNT_PLAY);
                if (mListener != null) {
                    mListener.onPrepared();
                }
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                endPlay();
                if (mListener != null) {
                    mListener.onCompletion();
                }
            }
        });

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                endPlay();
                if (mListener != null) {
                    mListener.onError(String.format("OnErrorListener what:%d extra:%d", what, extra));
                }
                return true;
            }
        });

        try {
            if (mAudioFile != null) {
                mPlayer.setDataSource(mAudioFile);
            } else {
                if (mListener != null) {
                    mListener.onError("no datasource");
                }
                return;
            }

            mPlayer.prepare();
            mPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
            endPlay();
            if (mListener != null) {
                mListener.onError("Exception\n" + e.toString());
            }
        }
    }

    private void deleteOnExit() {
        File converted = new File(mAudioFile);
        if (converted.exists()) {
            converted.deleteOnExit();
        }
    }

    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // 获得音频焦点
                    if (isPlaying()) {
                        mPlayer.setVolume(1.0f, 1.0f);
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    // 长久的失去音频焦点，释放MediaPlayer
                    stop();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // 展示失去音频焦点，暂停播放等待重新获得音频焦点
                    stop();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // 失去音频焦点，无需停止播放，降低声音即可
                    if (isPlaying()) {
                        mPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }
    };

}
