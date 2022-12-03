package com.dd.surf;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.dd.surf.pojo.User;
import com.dd.surf.service.TCPService;
import com.dd.surf.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {

    LayoutInflater layoutInflater = null;
    //Control control = null;
    LinearLayout messageList = null;

    public TextView titleText;

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

    public int type;
    public int id;

    private List<Integer> getUserIdList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutInflater = getLayoutInflater();

        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);
        id = intent.getIntExtra("id",0);

        /*control = (Control) getApplication();*/

        View titleBar = layoutInflater.inflate(R.layout.view_title_chat,new LinearLayout(this),false);
        titleText = titleBar.findViewById(R.id.title);

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

        setContentView(R.layout.activity_chat);

        conn=new MyServiceConn();
        bindService(new Intent(Chat.this, TCPService.class), conn, BIND_AUTO_CREATE);

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);

        ScrollView scrollView = findViewById(R.id.messageScroll);
        messageList = findViewById(R.id.message_list);


        EditText messageEditText = findViewById(R.id.messageEditText);
        messageEditText.setOnClickListener(v -> {

        });
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> {
            try {
                String messageText = messageEditText.getText().toString();
                if (!messageText.equals("")) {
                    int userId = Server.userId;
                    addMessageText(userId,messageText);
                    service.sendTextMessage(type,id,messageText);
                    messageEditText.setText("");
                }
            }catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
        });

/*        rowCount = control.getMessageCount(type,id);
        if (rowCount <= 20){
            rowCount = 0;
            List<ChatMessage> chatMessageList = control.getMessageList(type,id,rowCount,20);
            for (ChatMessage chatMessage : chatMessageList){
                addMessageText(chatMessage.getSenderId(),chatMessage.getMessage());
            }
        }*/

        /*final Intent intent1 = new Intent(this, UpdateChatMessageService.class);
        startService(intent1);*/
        /*messageUpdateThread = new Thread(()->{
            while (true) {
                try {
                    System.out.println("is Run");
                    int serverCount = control.getMessageCount(1,3);
                    System.out.println(serverCount);
                }catch (Exception e){
                    e.printStackTrace();
                }
                *//*if (serverCount > rowCount){
                    rowCount = serverCount;
                    List<ChatMessage> chatMessageList = control.getMessageList(type,id,rowCount,20);
                    for (ChatMessage chatMessage : chatMessageList){
                        addMessageText(chatMessage.getSenderId(),chatMessage.getMessage());
                    }
                }
                try {
                    Thread.sleep(0);
                }catch (Exception e){
                    e.printStackTrace();
                }*//*
            }
        });
        messageUpdateThread.start();*/

        //Out.print(this,String.valueOf());
/*        messageUpdateThread = new Thread(()->{
            while (true) {
                int serverCount = control.getMessageCount(type,id);
                System.out.println("is Run");
                if (serverCount > rowCount){
                    rowCount = serverCount;
                    List<ChatMessage> chatMessageList = control.getMessageList(type,id,rowCount,20);
                    for (ChatMessage chatMessage : chatMessageList){
                        addMessageText(chatMessage.getSenderId(),chatMessage.getMessage());
                    }
                }
                try {
                    Thread.sleep(0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        messageUpdateThread.start();*/

/*        addMessageText(1,"我是傻逼");
        addMessageText(2,"我也是傻逼");
        a1ddMessageText(3,"👀👀👀");
        addMessageText(4,"扣1送原神6480");*/

    }

    protected void addMessageText(int id,String text){
        View messageView = null;
        System.out.println(id);
        System.out.println(Server.userId);
        System.out.println(id != Server.userId);
        if (id != Server.userId)
            messageView = layoutInflater.inflate(R.layout.view_message_a, null);
        else{
            messageView = layoutInflater.inflate(R.layout.view_message_b,null);
        }
        messageView.setContentDescription(String.valueOf(id));
        TextView nameView = messageView.findViewById(R.id.name);
        if (Server.hasUser(id)){
            User user = Server.getUser(id);
            nameView.setText(user.getName());
        }else{
            if (!getUserIdList.contains(id)){
                Server.getUserInfo(id);
                getUserIdList.add(id);
            }
        }
        LinearLayout linearLayout = messageView.findViewById(R.id.msgList);
        TextView textView = new TextView(this);
        textView.setText(text);
        linearLayout.addView(textView);
        messageList.addView(messageView);
    }

    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((TCPService.LocalBinder) binder).getService();
            /*service.initialization();*/
            if (type == 1){
                service.getGroupInfo(id);
                service.getGroupMessage(id);
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
        System.out.println("is Run");
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
                case "getGroupInfo":
                    String groupName = intent.getStringExtra("groupName");
                    titleText.setText(groupName);
                    break;
                case "getGroupMessage":
                    JSONArray groupList = null;
                    try {
                        groupList = new JSONArray(intent.getStringExtra("messageList"));
                        for (int i = 0; i < groupList.length(); i++) {
                            JSONObject jsonObject = groupList.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            int senderId = jsonObject.getInt("senderId");
                            /*String senderName = jsonObject.getString("senderName");*/
                            String message = jsonObject.getString("message");
                            addMessageText(senderId,message);
                        }
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "getUserInfoById":
                    int id = intent.getIntExtra("id",0);
                    String name = intent.getStringExtra("name");
                    for (int i = 0 ; i < messageList.getChildCount();i++){
                        View child = messageList.getChildAt(i);
                        if (child.getId() == R.id.message){
                            if (child.getContentDescription() == String.valueOf(id)){
                                TextView nameView = child.findViewById(R.id.name);
                                nameView.setText(name);
                            }
                        }
                    }
                    break;
                case "GroupMessage":
                    if (type == 1){
                        int contactId = intent.getIntExtra("contactId",0);
                        Chat chat = (Chat) context;
                        System.out.println(""+chat.id);
                        if (contactId == chat.id){
                            int senderId = intent.getIntExtra("sender",0);
                            /*String senderName = jsonObject.getString("senderName");*/
                            String message = intent.getStringExtra("message");
                            addMessageText(senderId,message);
                        }
                    }
            }
        }
    }
}