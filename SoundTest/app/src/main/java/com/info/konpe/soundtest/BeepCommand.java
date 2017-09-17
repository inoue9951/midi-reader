package com.info.konpe.soundtest;

import android.media.AudioFormat;
import android.media.AudioTrack;

/**
 * Created by konpe on 2017/09/16.
 */

public class BeepCommand {

    // Statics
    private static final double REGION = 0.5;         // 振幅の幅 -0.5 ~ 0.5
    private static final int SAMPLING_RATE = 20000;   // サンプリング周波数20kHz

    // Fields
    private byte[] soundData;
    private AudioTrack audioTrack;

    // Constructor
    public BeepCommand(float sec, float hz) {
        // sin波形を生成
        int dsu = (int)((long)(sec*1000.0f)*5000L/250L);
        soundData = new byte[dsu];
        for (int i=0; i<dsu; i++) {
            double d = REGION * Math.sin(2.0 * Math.PI * hz * sec * i / dsu);
            soundData[i] = (byte)(d * 127.0);
        }

        // audioTrack初期化
        if (audioTrack != null) audioTrack.release();
        audioTrack = new AudioTrack(
                AudioTrack.MODE_STREAM,
                SAMPLING_RATE,                               // サンプリング周波数
                AudioFormat.CHANNEL_CONFIGURATION_MONO,      // モノラル
                AudioFormat.ENCODING_PCM_8BIT,               // 量子化ビット8ビット
                soundData.length,                            // データ長
                AudioTrack.MODE_STATIC
        );

        //
        audioTrack.setPlaybackPositionUpdateListener(
                new AudioTrack.OnPlaybackPositionUpdateListener() {
                    @Override
                    public void onMarkerReached(AudioTrack audioTrack) {
                        // 再生完了時のコールバック
                        if (audioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING) {
                            audioTrack.stop();      // 再生完了したので発生停止
                        }
                    }

                    @Override
                    public void onPeriodicNotification(AudioTrack audioTrack) {
                    }
                }
        );

        // stop()後に再び再生する時に必要
        audioTrack.reloadStaticData();
        // 再生するデータをセット
        audioTrack.write(soundData, 0, soundData.length);
        // コールバックタイミング指定
        audioTrack.setNotificationMarkerPosition(soundData.length);
    }

    public void start() {
        audioTrack.play();
    }

    public void stop() {
        if (audioTrack != null) {
            if (audioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.stop();     // 発生中なら停止
            }
            audioTrack.release();
        }
    }
}
