package com.dd.surf;

import static android.widget.Toast.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;

import com.dd.surf.service.TCPService;
import com.dd.surf.service.TCPService.LocalBinder;

public class Load extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        /*读取配置文件或加载插件
        禁止进行联网操作*/

        String filePath = this.getExternalFilesDir(null).getAbsolutePath();


        setContentView(R.layout.activity_load);

        startActivity(new Intent(this,Login.class));
    }

}