package com.dd.surf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import com.dd.surf.service.TCPService;
import com.dd.surf.socket.UDPClient;

public class TestActivity extends AppCompatActivity {
    //public Handler mHandler;

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

/*    private final Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

                    System.out.println("is Run");
        };
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        conn=new MyServiceConn();
        bindService(new Intent(TestActivity.this, TCPService.class), conn, BIND_AUTO_CREATE);


        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);


        setContentView(R.layout.activity_test);

        View viewById = findViewById(R.id.msg);
        viewById.setOnClickListener(v -> {
            service.getGroupHead(2);
        });
    }

    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((TCPService.LocalBinder) binder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            service = null;
        }
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

        }
    }

}