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
import com.dd.surf.util.Control;
import com.dd.surf.view.util.Out;

import java.util.List;

public class Chat extends AppCompatActivity {

    LayoutInflater layoutInflater = null;
    Control control = null;
    LinearLayout messageList = null;

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


        Out.print(this,String.valueOf(control.getMessageCount(type,id)));
        List<ChatMessage> chatMessageList = control.getMessageList(type,id,0,20);

        for (ChatMessage chatMessage : chatMessageList){
            addMessageText(chatMessage.getSenderId(),chatMessage.getMessage());
        }

        /*
        addMessageText(2,"我也是傻逼");
        addMessageText(3,"👀👀👀");
        addMessageText(4,"扣1送原神6480");*/

    }

    public void addMessageText(int id,String text){
        View messageView = null;
        if (id != control.getId()){
            messageView = layoutInflater.inflate(R.layout.view_message_a,null);
        }else{
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