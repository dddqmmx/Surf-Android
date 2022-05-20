package com.dd.surf;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

import java.io.IOException;


public class Developers extends AppCompatActivity {
    
    public MediaPlayer mediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_developers);

        mediaPlayer = MediaPlayer.create(this,R.raw.trigger);
        mediaPlayer.start();

    }
}