package com.dd.surf;

import static android.widget.Toast.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Toast;

import com.dd.surf.util.Server;

public class Load extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        setContentView(R.layout.activity_load);

        try {
            Server server = (Server) getApplication();
            if (server.initialization()){
                makeText(this,"服务器连接初始化成功", LENGTH_LONG).show();
                if (server.connect()) {
                    makeText(this,"服务器连接成功", LENGTH_LONG).show();
                } else {
                    makeText(this,"服务器连接失败", LENGTH_LONG).show();
                }
            }else{
                makeText(this,"服务器连接初始化失败", LENGTH_LONG).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        /*new Handler((Message msg)->{
            startActivity(new Intent(this,Login.class));
            this.finish();
            return false;
        }).sendEmptyMessageDelayed(0,3000);*/
    }

}