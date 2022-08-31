package com.dd.surf.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dd.surf.entity.ChatMessage;
import com.dd.surf.util.Control;

import java.util.List;
import java.util.ResourceBundle;

public class UpdateChatMessageService extends Service {

    public static Thread messageUpdateThread = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Control control = (Control) getApplication();
        messageUpdateThread = new Thread(()->{
            while (true) {
                int serverCount = control.getMessageCount(1,3);
                System.out.println("is Run");
                System.out.println(serverCount);
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
        return super.onStartCommand(intent, flags, startId);
    }

}
