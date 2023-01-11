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

import com.dd.surf.pojo.Group;
import com.dd.surf.service.TCPService;
import com.dd.surf.util.Client;

public class GroupInfo extends AppCompatActivity {

    LayoutInflater layoutInflater = null;

    public int id;

    public static TextView nameTextView;
    public static TextView idTextView;

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        layoutInflater = getLayoutInflater();
        View titleBar = layoutInflater.inflate(R.layout.view_title_chat,new LinearLayout(this),false);
        TextView titleText = titleBar.findViewById(R.id.title);
        titleText.setText(R.string.group_info);

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
        bindService(new Intent(GroupInfo.this, TCPService.class), conn, BIND_AUTO_CREATE);

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);

        setContentView(R.layout.activity_group_info);

        nameTextView = findViewById(R.id.name);
        idTextView = findViewById(R.id.user_name);

        if (Client.hasGroupInfo(id)){
            Group groupInfo = Client.getGroupInfo(id);
            nameTextView.setText(groupInfo.getGroupName());
            idTextView.setText(String.valueOf(groupInfo.getId()));
        } else {
            Client.getGroupInfoByServer(id);
        }

        LinearLayout optionsLayout = findViewById(R.id.optionsLayout);
        if (Client.groupList.contains(id)){
            Button button1 = findViewById(R.id.button1);
            button1.setVisibility(View.GONE);
            Button button2 = findViewById(R.id.button2);
            button2.setOnClickListener((view)->{
                Intent chatInteger = new Intent(GroupInfo.this, Chat.class);
                chatInteger.putExtra("type",1);
                chatInteger.putExtra("id",id);
                GroupInfo.this.startActivity(chatInteger);
            });
        }else{
            Button button1 = findViewById(R.id.button1);
            button1.setOnClickListener((view)->{
                service.addGroupRequest(id);
            });
            Button button2 = findViewById(R.id.button2);
            button2.setVisibility(View.GONE);
        }

        /*if(id == Client.userId){
            optionsLayout.removeAllViews();
        }else{
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
        }*/
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
            int id = 0;
            String name = null;
            switch (command) {
                case "getGroupInfoById":
                    id = intent.getIntExtra("id",0);
                    name= intent.getStringExtra("groupName");
                    nameTextView.setText(name);
                    idTextView.setText(String.valueOf(id));
                    break;
                case "addGroupRequest":
                    int code = intent.getIntExtra("code",2);
                    switch (code) {
                        case 0:
                            Toast.makeText(GroupInfo.this,"已发送加群请求",Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            Toast.makeText(GroupInfo.this,"已经发送过加群申请了,请等待群管理员同意",Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(GroupInfo.this,"服务器内部处理出现错误",Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }
        }
    }

}