package com.dd.surf;

import static android.widget.Toast.makeText;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AddRequestList extends AppCompatActivity {

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

    public LayoutInflater layoutInflater;

    public LinearLayout  friendRequestList;

    private List<Integer> getUserIdList = new ArrayList<>();

    public int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        layoutInflater = getLayoutInflater();

        View titleBar = layoutInflater.inflate(R.layout.view_title_chat,new LinearLayout(this),false);
        TextView titleText = titleBar.findViewById(R.id.title);
        if (id == 1){
            titleText.setText(R.string.friend_request_list);
        }else {
            titleText.setText(R.string.group_request_list);
        }

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

        setContentView(R.layout.activity_add_friend_request_list);

        friendRequestList = findViewById(R.id.friend_request_list);

    }

    public void addFriendRequest(int id){
        View friendView = layoutInflater.inflate(R.layout.view_friend_request,null);
        friendView.setContentDescription(String.valueOf(id));
        friendView.setOnClickListener((view)->{
            Intent intent = new Intent(this, UserInfo.class);
            intent.putExtra("id",id);
            this.startActivity(intent);
        });
        TextView nameText = friendView.findViewById(R.id.name);
        if (Client.hasUser(id)){
            User user = Client.getUser(id);
            nameText.setText(user.getName());
        }else{
            if (!getUserIdList.contains(id)){
                Client.getUserInfo(id);
                getUserIdList.add(id);
            }
        }
        Button agreeButton = friendView.findViewById(R.id.agree_button);
        agreeButton.setOnClickListener((view)->{
            service.agreeRequest(id);
        });
        friendRequestList.addView(friendView);
    }


    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((TCPService.LocalBinder) binder).getService();
            if (id == 1){
                service.getFriendRequest();
            }else if (id == 2){
                service.getGroupRequest();
            }
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
            int id;
            String name;
            String command = intent.getStringExtra("command");
            System.out.println(command);
            switch (command) {
                case "getFriendRequest":
                    try {
                        JSONArray relationArray = new JSONArray(intent.getStringExtra("relationArray"));
                        for (int i = 0; i < relationArray.length(); i++) {
                            int otherSideId = relationArray.getInt(i);
                            addFriendRequest(otherSideId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "getUserInfoById":
                    id = intent.getIntExtra("id",0);
                    name = intent.getStringExtra("name");
                    for (int i = 0 ; i < friendRequestList.getChildCount();i++){
                        View child = friendRequestList.getChildAt(i);
                        System.out.println(i);
                        if (child.getId() == R.id.friend_request){
                            if (child.getContentDescription() == String.valueOf(id)){
                                TextView nameView = child.findViewById(R.id.name);
                                nameView.setText(name);
                            }
                        }
                    }
                    break;
                case "agreeRequest":
                    int code = intent.getIntExtra("code",1);
                    switch (code) {
                        case 0:
                            Toast.makeText(AddRequestList.this,"已同意好友请求",Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            Toast.makeText(AddRequestList.this,"服务器内部处理出现错误",Toast.LENGTH_LONG).show();
                            break;

                    }
                    id = intent.getIntExtra("id",0);
                    for (int i = 0 ; i < friendRequestList.getChildCount();i++){
                        View child = friendRequestList.getChildAt(i);
                        if (child.getId() == R.id.friend_request){
                            if (child.getContentDescription() == String.valueOf(id)){
                                child.setVisibility(View.GONE);
                            }
                        }
                    }
                    break;
                case "":

            }
        }
    }

}