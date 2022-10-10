package com.dd.surf.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TCPService extends Service {
    public TCPService() {
    }


    private int number = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        asyncSendPerson("dinner");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public final class LocalBinder extends Binder {
        public TCPService getService() {
            return TCPService.this;
        }
    }

    public void asyncSendPerson(final String name) {
        // 休息5秒，模拟异步任务
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(5000);
                        //可以在子线程中直接发送广播
                        sendContentBroadcast((number++)+name);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送广播
     * @param name
     */
    protected void sendContentBroadcast(String name) {
        // TODO Auto-generated method stub
        Intent intent=new Intent();
        intent.setAction("com.dd.surf.service.tcpClient");
        intent.putExtra("name", name);
        sendBroadcast(intent);
    }


}