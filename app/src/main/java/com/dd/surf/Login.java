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
import com.dd.surf.util.Client;

public class Login extends AppCompatActivity {

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

    EditText userName;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Client client = (Client) getApplication();

        conn=new MyServiceConn();
        bindService(new Intent(Login.this, TCPService.class), conn, BIND_AUTO_CREATE);

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);

        userName = findViewById(R.id.user_name);
        password = findViewById(R.id.password);

        ImageButton imageButton = findViewById(R.id.login_button);
        imageButton.setOnClickListener((view)->{
            service.initialization();
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
            boolean booleanValue = false;
            switch (command) {
                case "initialization":
                    booleanValue = intent.getBooleanExtra("value", false);
                    if (booleanValue){
                        service.connect();
                    } else {
                        makeText(context,"初始失败", LENGTH_LONG).show();
                    }
                    break;
                case "connect":
                    booleanValue = intent.getBooleanExtra("value", false);
                    if (booleanValue) {
                        service.login(userName.getText().toString(), password.getText().toString());
                    } else {
                        makeText(context,"服务器连接失败", LENGTH_LONG).show();
                    }
                    break;
                case "login":
                    boolean login = intent.getBooleanExtra("login",false);
                    String message = intent.getStringExtra("message");
                    if (login) {
                        makeText(context,message, LENGTH_LONG).show();
                        Intent chatInteger = new Intent(context, AddRequestList.class);
                        chatInteger.putExtra("id",2);
                        context.startActivity(chatInteger);
                        /*context.startActivity(new Intent(Login.this,,Main.class));
                        */
                        Client.setTcpService(service);
                        Login.this.finish();
                    }else {
                        makeText(context,"登录失败"+message, LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }
}