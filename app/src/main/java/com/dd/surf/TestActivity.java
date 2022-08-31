package com.dd.surf;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.surf.entity.ChatMessage;
import com.dd.surf.util.Control;
import com.dd.surf.util.Server;
import com.dd.surf.view.util.Out;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        setContentView(R.layout.activity_test);
        try {
            Server server = (Server) getApplication();
            server.initialization();
            if (server.connect()) {
                Toast.makeText(this,"服务器连接成功",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"服务器连接失败",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}