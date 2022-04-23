package com.dd.surf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class Load extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        new Handler((Message msg)->{
            startActivity(new Intent(this,Login.class));
            this.finish();
            return false;
        }).sendEmptyMessageDelayed(0,3000);
    }
}