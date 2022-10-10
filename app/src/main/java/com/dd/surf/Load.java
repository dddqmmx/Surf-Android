package com.dd.surf;

import static android.widget.Toast.*;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Person;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.view.Menu;
import android.widget.Toast;

import com.dd.surf.service.TCPService;
import com.dd.surf.util.Server;

public class Load extends AppCompatActivity {

    private ContentReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        setContentView(R.layout.activity_load);

        startService(new Intent(this, TCPService.class));

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);

        //Server server = (Server) getApplication();

/*        try {
\

            if (server.initialization()){
                makeText(this,"服务器连接初始化成功", LENGTH_LONG).show();
            }else{
                makeText(this,"服务器连接初始化失败", LENGTH_LONG).show();
            }
            server.connect();
        }catch (Exception e){
            e.printStackTrace();
        }*/

        /**/

        new Handler((Message msg)->{
            startActivity(new Intent(this,TestActivity.class));
            this.finish();
            return false;
        }).sendEmptyMessageDelayed(0,10000);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mReceiver!=null) {
            unregisterReceiver(mReceiver);
        }
    }

    public static class ContentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name");
            System.out.println(name);
        }
    }

}