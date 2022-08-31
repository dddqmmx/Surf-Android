package com.dd.surf;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.surf.entity.ChatMessage;
import com.dd.surf.service.UpdateChatMessageService;
import com.dd.surf.util.Control;
import com.dd.surf.view.util.Out;

import java.util.List;

public class Chat extends AppCompatActivity {

    LayoutInflater layoutInflater = null;
    Control control = null;
    LinearLayout messageList = null;

    int rowCount = 0;

    public static Thread messageUpdateThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutInflater = getLayoutInflater();

        Intent intent = getIntent();
        int type = intent.getIntExtra("type",0);
        int id = intent.getIntExtra("id",0);

        control = (Control) getApplication();

        LayoutInflater layoutInflater = getLayoutInflater();

        View titleBar = layoutInflater.inflate(R.layout.view_title_chat,new LinearLayout(this),false);
        TextView titleText = titleBar.findViewById(R.id.title);
        String name = null;
        if (type == 1){
            name = control.getGroupNameById(id);
        }
        titleText.setText(name);
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

        messageList = findViewById(R.id.message_list);

        rowCount = control.getMessageCount(type,id);
        if (rowCount <= 20){
            rowCount = 0;
            List<ChatMessage> chatMessageList = control.getMessageList(type,id,rowCount,20);
            for (ChatMessage chatMessage : chatMessageList){
                addMessageText(chatMessage.getSenderId(),chatMessage.getMessage());
            }
        }

        /*final Intent intent1 = new Intent(this, UpdateChatMessageService.class);
        startService(intent1);*/
        messageUpdateThread = new Thread(()->{
            while (true) {
                try {
                    System.out.println("is Run");
                    int serverCount = control.getMessageCount(1,3);
                    System.out.println(serverCount);
                }catch (Exception e){
                    e.printStackTrace();
                }
                /*if (serverCount > rowCount){
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
                }*/
            }
        });
        messageUpdateThread.start();

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
        /*
        addMessageText(2,"我也是傻逼");
        addMessageText(3,"👀👀👀");
        addMessageText(4,"扣1送原神6480");*/

    }

    public void addMessageText(int id,String text){
        View messageView = null;
        if (id != control.getId())
            messageView = layoutInflater.inflate(R.layout.view_message_a, null);
        else{
            messageView = layoutInflater.inflate(R.layout.view_message_b,null);
        }
        TextView nameView = messageView.findViewById(R.id.name);
        nameView.setText(control.getNameById(id));
        LinearLayout linearLayout = messageView.findViewById(R.id.msgList);
        TextView textView = new TextView(this);
        textView.setText(text);
        linearLayout.addView(textView);
        messageList.addView(messageView);
    }
}