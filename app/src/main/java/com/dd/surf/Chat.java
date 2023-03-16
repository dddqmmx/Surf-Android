package com.dd.surf;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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
import com.dd.surf.util.BitMapUtil;
import com.dd.surf.util.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {

    LayoutInflater layoutInflater = null;
    //Control control = null;
    LinearLayout messageList = null;

    ScrollView scrollView = null;

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

        conn=new MyServiceConn();
        bindService(new Intent(Chat.this, TCPService.class), conn, BIND_AUTO_CREATE);

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_chat);


        scrollView = findViewById(R.id.messageScroll);
        //scrollView.setLayoutManager;
        messageList = findViewById(R.id.message_list);


        final View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            decorView.getWindowVisibleDisplayFrame(rect);
            int screenHeight = decorView.getRootView().getHeight();
            int keyboardHeight = screenHeight - rect.bottom;

            // 判断键盘是否弹出
            if (keyboardHeight > 0) {
                // 如果ScrollView已经滚动到底部，则将ScrollView滚动到底部
                if (scrollView.getScrollY() + scrollView.getHeight() >= messageList.getHeight()) {
                    scrollView.post(() -> scrollView.scrollTo(0, messageList.getHeight()));
                }
            }
        });

        EditText messageEditText = findViewById(R.id.messageEditText);
        messageEditText.setOnClickListener((View view)->{
            scrollView.fullScroll(View.FOCUS_DOWN);
        });
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> {
            try {
                String messageText = messageEditText.getText().toString();
                if (!messageText.equals("")) {
                    int userId = Client.userId;
                    addMessageText(userId,messageText);
                    messageEditText.setText("");
                    service.getUserHead(userId);
                    int height = messageList.getHeight();
                    scrollView.post(() -> scrollView.scrollTo(0, height));
                }
            }catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
        });
        ImageView imageView1 = findViewById(R.id.microphone);
        imageView1.setOnClickListener(v -> {
            service.getUserHead(2);
        });
    }

    protected void addMessageText(int id,String text){
        View messageView = null;
        if (id != Client.userId)
            messageView = layoutInflater.inflate(R.layout.view_message_a, null);
        else{
            messageView = layoutInflater.inflate(R.layout.view_message_b,null);
        }
        messageView.setContentDescription(String.valueOf(id));
        TextView nameView = messageView.findViewById(R.id.name);
        if (Client.hasUser(id)){
            User user = Client.getUser(id);
            nameView.setText(user.getName());
        }else{
            if (!getUserIdList.contains(id)){
                Client.getUserInfo(id);
                getUserIdList.add(id);
            }
        }
        LinearLayout linearLayout = messageView.findViewById(R.id.msgList);
        TextView textView = new TextView(this);
        textView.setText(text);
        linearLayout.addView(textView);
        ImageView headImage = messageView.findViewById(R.id.head);
        headImage.setOnClickListener((v)->{
            Intent intent = new Intent(this, UserInfo.class);
            intent.putExtra("id",id);
            this.startActivity(intent);
        });
        messageList.addView(messageView);
    }

    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((TCPService.LocalBinder) binder).getService();
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
                    System.out.println(groupName);
                    titleText.setText(groupName);
                    break;
                case "getGroupMessage":
                    JSONArray groupList = null;
                    try {
                        groupList = new JSONArray(intent.getStringExtra("messageList"));
                        List<Integer> arrayList = new ArrayList<>();
                        for (int i = 0; i < groupList.length(); i++) {
                            JSONObject jsonObject = groupList.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            int senderId = jsonObject.getInt("senderId");
                            if (!arrayList.contains(senderId)) {
                                arrayList.add(senderId);
                            }
                            /*String senderName = jsonObject.getString("senderName");*/
                            String message = jsonObject.getString("message");
                            addMessageText(senderId,message);
                        }
                        for (int i = 0; i <arrayList.size(); i++) {
                            System.out.println(arrayList.get(i));
                            service.getUserHead(arrayList.get(i));
                        }
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
                        System.out.println("chat id"+chat.id);
                        if (contactId == chat.id){
                            int senderId = intent.getIntExtra("sender",0);
                            /*String senderName = jsonObject.getString("senderName");*/
                            String message = intent.getStringExtra("message");
                            addMessageText(senderId,message);
                        }
                    }
                    break;
                case "getUserHead":
                    int userId = intent.getIntExtra("userId", 0);
                    for (int i = 0 ; i < messageList.getChildCount();i++){
                        View child = messageList.getChildAt(i);
                        if (child.getId() == R.id.message){
                            if (child.getContentDescription() == String.valueOf(userId)){
                                ImageView avatarView = child.findViewById(R.id.head);
                                avatarView.setImageBitmap(BitMapUtil.openImage(Chat.this.getExternalFilesDir("image/user/avatar").getAbsolutePath()+"/"+userId+".sf"));
                            }
                        }
                    }
                    break;
            }
        }
    }
}