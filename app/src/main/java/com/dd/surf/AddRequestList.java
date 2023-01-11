package com.dd.surf;

import static android.widget.Toast.makeText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.dd.surf.pojo.User;
import com.dd.surf.service.TCPService;
import com.dd.surf.util.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddRequestList extends AppCompatActivity {

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

    public LayoutInflater layoutInflater;

    public LinearLayout  friendRequestList;

    private final List<Integer> getUserIdList = new ArrayList<>();
    private final List<Integer> getGroupIdList = new ArrayList<>();

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

    @SuppressLint("SetTextI18n")
    public void addGroupRequest(int userId, int groupId){
        View friendView = layoutInflater.inflate(R.layout.view_friend_request,null);
        friendView.setContentDescription("userId:"+userId+"groupId:"+groupId);
        friendView.setOnClickListener((view)->{
            Intent intent = new Intent(this, UserInfo.class);
            intent.putExtra("id",userId);
            this.startActivity(intent);
        });
        TextView nameText = friendView.findViewById(R.id.name);
        if (Client.hasUser(userId)){
            User user = Client.getUser(userId);
            nameText.setText(user.getName());
        }else{
            if (!getUserIdList.contains(userId)){
                Client.getUserInfo(userId);
                getUserIdList.add(userId);
            }
        }
        TextView messageText = friendView.findViewById(R.id.message);
        if (Client.hasGroupInfo(groupId)){
            Group group = Client.getGroupInfo(groupId);
            messageText.setText(this.getString(R.string.applications_for_membership)+group.getGroupName());
        }else{
            if (!getGroupIdList.contains(groupId)){
                Client.getGroupInfoByServer(groupId);
                getGroupIdList.add(groupId);
            }
        }
        Button agreeButton = friendView.findViewById(R.id.agree_button);
        agreeButton.setOnClickListener((view)->{
            service.agreeFriendRequest(groupId);
        });
        friendRequestList.addView(friendView);
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
            service.agreeFriendRequest(id);
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
            String regId = "userId:([0-9]+)groupId:([0-9]+)";
            Matcher matcher;
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
                            String contentDescription = (String) child.getContentDescription();
                            matcher = Pattern.compile(regId).matcher(contentDescription);
                            if (contentDescription.equals(String.valueOf(id))){
                                TextView nameView = child.findViewById(R.id.name);
                                nameView.setText(name);
                            }else if (matcher.find() && String.valueOf(id).equals(matcher.group(1))){
                                TextView nameView = child.findViewById(R.id.name);
                                nameView.setText(name);
                            }
                        }
                    }
                    break;
                case "agreeFriendRequest":
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
                case "getGroupRequest":
                    try {
                        JSONArray relationArray = new JSONArray(intent.getStringExtra("relationArray"));
                        for (int i = 0; i < relationArray.length(); i++) {
                            JSONObject jsonObject = relationArray.getJSONObject(i);
                            int groupId = jsonObject.getInt("groupId");
                            int userId = jsonObject.getInt("userId");
                            addGroupRequest(userId, groupId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "getGroupInfoById":
                    id = intent.getIntExtra("id",0);
                    name = intent.getStringExtra("groupName");
                    for (int i = 0 ; i < friendRequestList.getChildCount();i++){
                        View child = friendRequestList.getChildAt(i);
                        if (child.getId() == R.id.friend_request){
                            String contentDescription = (String) child.getContentDescription();
                            matcher = Pattern.compile(regId).matcher(contentDescription);
                            if (contentDescription.equals(String.valueOf(id))){
                                TextView nameView = child.findViewById(R.id.message);
                                nameView.setText(name);
                            }else if (matcher.find() && String.valueOf(id).equals(matcher.group(2))){
                                TextView nameView = child.findViewById(R.id.message);
                                nameView.setText(name);
                            }
                        }
                    }
                    break;
            }
        }
    }

}