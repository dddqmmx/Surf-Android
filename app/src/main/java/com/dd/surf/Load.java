package com.dd.surf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;

import com.dd.surf.util.Control;
import com.dd.surf.util.Server;

public class Load extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        setContentView(R.layout.activity_load);
        try {
            Server server = (Server) getApplication();
            server.initialization();
            if (server.connect()){
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        new Handler((Message msg)->{
            startActivity(new Intent(this,Login.class));
            this.finish();
            return false;
        }).sendEmptyMessageDelayed(0,3000);
    }
}