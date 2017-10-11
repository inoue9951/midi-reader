package com.info.konpe.soundtest;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener
{

    // Statics
    private static final int SAMPLING_RATE = 20000; // サンプリング周波数20kHz
    private static final double REGION = 0.5;       // 振幅の幅 -0.5 ~ 0.5
    private static final String[] PITCH = {
            "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"
    };
    private static final float[] FREQUENCY = {
            // A2 ~ G2#
            110.00f, 116.54f, 123.47f, 130.81f, 138.59f, 146.83f, 155.56f, 164.81f, 174.61f, 185.00f, 196.00f, 207.65f,
            // A3 ~ G3#
            220.00f, 233.08f, 246.94f, 261.63f, 277.18f, 293.66f, 311.13f, 329.63f, 349.23f, 369.99f, 392.00f, 415.30f,
            // A4 ~ G4#
            440.00f, 466.16f, 493.88f, 523.25f, 554.37f, 587.33f, 622.25f, 659.26f, 698.46f, 739.99f, 783.99f, 830.61f
    };

    // View
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XMLのView取得
        ll = (LinearLayout)findViewById(R.id.main_ll);
        for (int i=0; i<FREQUENCY.length; i++) {
            Button btn = new Button(this);
            btn.setText(PITCH[i%PITCH.length] + (i/PITCH.length+2));
            btn.setId(100000 + i);
            btn.setOnClickListener(this);
            ll.addView(btn);
        }
    }

    @Override
    public void onClick(View view) {
        for (int i=0; i<FREQUENCY.length; i++) {
            if (view.getId()==100000+i) {
                new BeepCommand(5, FREQUENCY[i]).start();
            }
        }
    }
}
