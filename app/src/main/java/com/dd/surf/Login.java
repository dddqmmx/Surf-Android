package com.dd.surf;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dd.surf.service.TCPService;
import com.dd.surf.util.Control;
import com.dd.surf.util.Server;
import com.dd.surf.view.util.Out;

public class Login extends AppCompatActivity {

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Server server = (Server) getApplication();

        conn=new MyServiceConn();
        bindService(new Intent(Login.this, TCPService.class), conn, BIND_AUTO_CREATE);

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);

        EditText userName = findViewById(R.id.user_name);
        EditText password = findViewById(R.id.password);

        ImageButton imageButton = findViewById(R.id.login_button);
        imageButton.setOnClickListener((view)->{
            service.login(userName.getText().toString(), password.getText().toString());
        });

        //这是启动开发者选项的事件
        imageButton.setOnLongClickListener((view)-> {
            startActivity(new Intent(this,DeveloperOption.class));
            finish();
            return true;
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
        unbindService(conn);
        if (mReceiver!=null) {
            unregisterReceiver(mReceiver);
        }
    }

    public class ContentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra("command");
            switch (command) {
            }
        }
    }
}