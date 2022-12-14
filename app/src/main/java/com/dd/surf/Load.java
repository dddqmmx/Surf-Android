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

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        setContentView(R.layout.activity_load);

        conn=new MyServiceConn();
        bindService(new Intent(Load.this, TCPService.class), conn, BIND_AUTO_CREATE);

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);

        System.out.println("is Run");

        //service.initialization();

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

/*        new Handler((Message msg)->{

            return false;
        }).sendEmptyMessageDelayed(0,1000);*/
    }

    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((LocalBinder) binder).getService();
            service.initialization();
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
        unbindService(conn);
        if (mReceiver!=null) {
            unregisterReceiver(mReceiver);
        }
    }

    public class ContentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra("command");
            boolean booleanValue = false;
            switch (command) {
                case "initialization":
                    booleanValue = intent.getBooleanExtra("value", false);
                    if (booleanValue){
                        makeText(context,"初始化成功", LENGTH_LONG).show();
                        service.connect();
                    } else {
                        makeText(context,"初始失败", LENGTH_LONG).show();
                        context.startActivity(new Intent(Load.this,Login.class));
                    }
                    break;
                case "connect":
                    booleanValue = intent.getBooleanExtra("value", false);
                    if (booleanValue) {
                        makeText(context,"服务器连接成功", LENGTH_LONG).show();
                        Load.this.finish();
                    } else {
                        makeText(context,"服务器连接失败", LENGTH_LONG).show();
                    }
                    context.startActivity(new Intent(Load.this,Login.class));
                    break;
            }
        }
    }

}