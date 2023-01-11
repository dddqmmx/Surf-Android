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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.surf.pojo.Group;
import com.dd.surf.pojo.User;
import com.dd.surf.service.TCPService;
import com.dd.surf.util.Client;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AddFriendAndGroup extends AppCompatActivity {

    LayoutInflater layoutInflater = null;
    public TextView titleText;

    private MyServiceConn conn;
    private TCPService service;
    private ContentReceiver mReceiver;

    LinearLayout list;
    private final List<Integer> getUserIdList = new ArrayList<>();
    private final List<Integer> getGroupIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutInflater = getLayoutInflater();
        View titleBar = layoutInflater.inflate(R.layout.view_title_chat,new LinearLayout(this),false);
        titleText = titleBar.findViewById(R.id.title);

        ImageView imageView = titleBar.findViewById(R.id.back);
        imageView.setOnClickListener((view)->{
            finish();
        });

        titleText.setText(R.string.add_friend_or_add_group);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(titleBar);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.show();
        }

        conn=new MyServiceConn();
        bindService(new Intent(AddFriendAndGroup.this, TCPService.class), conn, BIND_AUTO_CREATE);

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);


        setContentView(R.layout.activity_add_friend_and_group);

        list = findViewById(R.id.list);

        EditText editText = findViewById(R.id.condition);

        Button selectGroupButton = findViewById(R.id.select_group);
        selectGroupButton.setOnClickListener((view)->{
            service.selectGroup(String.valueOf(editText.getText()));
        });

        Button selectUserButton = findViewById(R.id.select_user);
        selectUserButton.setOnClickListener((view)->{
            service.selectUser(String.valueOf(editText.getText()));
        });

    }

    public void addGroup(int id){
        View messageView = layoutInflater.inflate(R.layout.view_message,null);
        messageView.setContentDescription(String.valueOf(id));
        messageView.setOnClickListener((view)->{
            Intent chatInteger = new Intent(AddFriendAndGroup.this, GroupInfo.class);
            chatInteger.putExtra("id",id);
            AddFriendAndGroup.this.startActivity(chatInteger);
        });
        TextView nameText = messageView.findViewById(R.id.name);
        if (Client.hasGroupInfo(id)){
            Group group = Client.getGroupInfo(id);
            nameText.setText(group.getGroupName());
        }else{
            if (!getGroupIdList.contains(id)){
                Client.getGroupInfoByServer(id);
                getGroupIdList.add(id);
            }
        }
        list.addView(messageView);
    }

    public void addUser(int id){
        View friendView = layoutInflater.inflate(R.layout.view_message,null);
        friendView.setContentDescription(String.valueOf(id));
        friendView.setOnClickListener((view)->{
            Intent intent = new Intent(AddFriendAndGroup.this, UserInfo.class);
            intent.putExtra("id",id);
            AddFriendAndGroup.this.startActivity(intent);
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
        list.addView(friendView);
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
            int id;
            String name;
            String command = intent.getStringExtra("command");
            boolean booleanValue = false;
            JSONArray jsonArrayValue = null;
            switch (command) {
                case "selectGroup":
                    try {
                        list.removeAllViews();
                        if (intent.hasExtra("groupList")){
                            jsonArrayValue = new JSONArray(intent.getStringExtra("groupList"));
                            for (int i = 0; i < jsonArrayValue.length(); i++) {
                                id = jsonArrayValue.getInt(i);
                                addGroup(id);
                                //makeText(context,id, LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "selectUser":
                    list.removeAllViews();
                    try {
                        if (intent.hasExtra("userList")) {
                            jsonArrayValue = new JSONArray(intent.getStringExtra("userList"));
                            for (int i = 0; i < jsonArrayValue.length(); i++) {
                                id = jsonArrayValue.getInt(i);
                                addUser(id);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "getUserInfoById":
                    id = intent.getIntExtra("id",0);
                    name = intent.getStringExtra("name");
                    for (int i = 0 ; i < list.getChildCount();i++){
                        View child = list.getChildAt(i);
                        if (child.getId() == R.id.message_root){
                            if (child.getContentDescription() == String.valueOf(id)){
                                TextView nameView = child.findViewById(R.id.name);
                                nameView.setText(name);
                            }
                        }
                    }
                    break;
                case "getGroupInfoById":
                    id = intent.getIntExtra("id",0);
                    name = intent.getStringExtra("groupName");
                    for (int i = 0 ; i < list.getChildCount();i++){
                        View child = list.getChildAt(i);
                        if (child.getId() == R.id.message_root){
                            if (child.getContentDescription() == String.valueOf(id)){
                                TextView nameView = child.findViewById(R.id.name);
                                nameView.setText(name);
                            }
                        }
                    }
                    break;
            }
        }
    }
}