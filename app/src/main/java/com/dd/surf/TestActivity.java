package com.dd.surf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.TextView;

import com.dd.surf.socket.UDPClient;

public class TestActivity extends AppCompatActivity {
    public Handler mHandler;

/*    private final Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

                    System.out.println("is Run");
        };
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        setContentView(R.layout.activity_test);
        TextView textView = (TextView) findViewById(R.id.info);
        /*        try {
            Server server = (Server) getApplication();
            server.initialization();
            if (server.connect()) {
                Toast.makeText(this,"服务器连接成功",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"服务器连接失败",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        /*Server server = (Server) getApplication();
        server.initialization(wifiManager);
        if (server.connect()) {
            Toast.makeText(this,"服务器连接成功",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"服务器连接失败",Toast.LENGTH_LONG).show();
        }*/
        UDPClient udpClient = new UDPClient();
        udpClient.initialization("127.0.0.1");

        /*Button button = (Button) findViewById(R.id.msg);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    new Thread(()->{
                        try {Looper.prepareMainLooper();
                        Handler handler = new Handler(Looper.getMainLooper());
                        Message handlerMessage = Message.obtain(); //消息实例

                        Bundle bundle = new Bundle();//bundle实例

                        bundle.putString("msg","oooo");//给bundle添加信息
                        handlerMessage.setData(bundle);// 将bundle存入message
                        handlerMessage.what = 1;    //设置标记（自定义）
                        Toast.makeText(getApplicationContext(), ""+handler.sendMessage(handlerMessage), Toast.LENGTH_SHORT).show();    ;	//发送消息
                        Looper.loop();
                        Looper.myLooper().quit();}catch (Exception e){
                            e.printStackTrace();
                        }
                    }).start();


            }
        });
*/
 /*       new Thread(()->{

        }).start();*/

/*
        new Handler((Message msg)->{
            Toast.makeText(getApplicationContext(), "jb", Toast.LENGTH_SHORT).show();
            //根据信息编码及数据做出相对应的处理
            if (msg.what == 1) {//更新 TextView UI
                String message = (String) msg.getData().get("msg");
                textView.setText(message);
            }
            return true;
        });
*/

        /*mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(getApplicationContext(), "jb", Toast.LENGTH_SHORT).show();
                //根据信息编码及数据做出相对应的处理
                if (msg.what == 1) {//更新 TextView UI
                    String message = (String) msg.getData().get("msg");
                    textView.setText(message);
                }
            }
        };
*/

        /*        UdpHelper udphelper = new UdpHelper(manager);

        //传递WifiManager对象，以便在UDPHelper类里面使用MulticastLock
        Thread tReceived = new Thread(udphelper);
        tReceived.start();*/
    }
}