package com.dd.surf;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.dd.surf.pojo.User;
import com.dd.surf.service.TCPService;
import com.dd.surf.util.Client;

public class UserInfo extends AppCompatActivity {

    public int id;

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

    private TextView nameTextView;
    private TextView userNameTextView;
    private TextView personalProfileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        LayoutInflater layoutInflater = getLayoutInflater();

        View titleBar = layoutInflater.inflate(R.layout.view_title_chat,new LinearLayout(this),false);
        TextView titleText = titleBar.findViewById(R.id.title);
        titleText.setText(R.string.user_info);

        ImageView imageView = titleBar.findViewById(R.id.back);
        imageView.setOnClickListener((view)->{
            finish();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(titleBar);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.show();
        }

        conn=new MyServiceConn();
        bindService(new Intent(this, TCPService.class), conn, BIND_AUTO_CREATE);

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);

        setContentView(R.layout.activity_user_info);

        nameTextView  = findViewById(R.id.name);
        userNameTextView = findViewById(R.id.user_name);
        personalProfileTextView = findViewById(R.id.personal_profile);

        if (Client.hasUser(id)){
            User user = Client.getUser(id);
            nameTextView.setText(user.getName());
            userNameTextView.setText(user.getUserName());
            personalProfileTextView.setText(user.getPersonalProfile());
        }
        LinearLayout optionsLayout = findViewById(R.id.optionsLayout);
        if(id == Client.userId){
            optionsLayout.removeAllViews();
        }
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        System.out.println(Client.friendsList.contains(id));
        if (Client.friendsList.contains(id)){
            button1.setVisibility(View.GONE);
        }else{
            button1.setOnClickListener(v -> {
                service.addFriendRequest(id);

            });
        }
    }
    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((TCPService.LocalBinder) binder).getService();

            service.getUserInfoById(id);
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
                case "getUserInfoById":
                    int id = intent.getIntExtra("id",0);
                    String name = intent.getStringExtra("name");
                    String userName = intent.getStringExtra("userName");
                    String personalProfile = intent.getStringExtra("personalProfile");
                    nameTextView.setText(name);
                    userNameTextView.setText(userName);
                    personalProfileTextView.setText(personalProfile);
                    break;
                case "addFriendRequest":
                    int code = intent.getIntExtra("code",2);
                    switch (code) {
                        case 0:
                            Toast.makeText(UserInfo.this,"已发送好友请求",Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            Toast.makeText(UserInfo.this,"已经发送过好友申请了,请等待对方同意",Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(UserInfo.this,"服务器内部处理出现错误",Toast.LENGTH_LONG).show();
                            break;
                    }
            }
        }
    }
}