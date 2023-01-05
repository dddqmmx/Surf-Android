package com.dd.surf;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;


public class Developers extends AppCompatActivity {
    
    public MediaPlayer mediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater layoutInflater = getLayoutInflater();
        View titleBar = layoutInflater.inflate(R.layout.view_title_chat,new LinearLayout(this),false);
        TextView titleText = titleBar.findViewById(R.id.title);
        titleText.setText(R.string.developers);

        ImageView imageView = titleBar.findViewById(R.id.back);
        imageView.setOnClickListener((view)->{
            finish();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(titleBar);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.show();
        }

        setContentView(R.layout.activity_developers);

        /*mediaPlayer = MediaPlayer.create(this,R.raw.kurenai);*/
        /*mediaPlayer.start();*/

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
        }
    }
}