package com.dd.surf.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.dd.surf.pojo.User;
import com.dd.surf.socket.TCPClient;
import com.dd.surf.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class TCPService extends Service {

    public Thread tcpThread = null;

    public Socket socket = null;
    public OutputStream os = null;

    private final int port = 2042;

    public TCPService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("create service");
    }

    public final class LocalBinder extends Binder {
        public TCPService getService() {
            return TCPService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return  new LocalBinder();
    }

    public boolean tcpInitialization(String host){
        Executor executor = Executors.newSingleThreadExecutor();
        //任务
        FutureTask<Boolean> future = new FutureTask<>(() -> {
            try {
                socket = new Socket(host, port);
                os = socket.getOutputStream();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
        //执行
        executor.execute(future);

        System.out.println("tcpInitialization");
        try{
            boolean result = future.get(1, TimeUnit.SECONDS);
            System.out.println(result);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void initialization() {
        boolean tcpInitialization = tcpInitialization(Server.host);
        Intent intent=new Intent();
        intent.setAction("com.dd.surf.service.tcpClient");
        intent.putExtra("command", "initialization");
        intent.putExtra("value",tcpInitialization);
        createTcpThread();
        tcpThread.start();
        sendContent(intent);
    }

    public void createTcpThread() {
        tcpThread = new Thread(()-> {
            String line = null;
            try {
                //while是抄的写的非常好
                while ((line = getLine()) != null) {
                    System.out.println(line);
                    JSONObject jsonObject = new JSONObject(line);
                    String command = jsonObject.getString("command");
                    if ("connect".equals(command)) {
                        String sessionId = jsonObject.getString("sessionId");
                        Server.setSessionId(sessionId);
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "connect");
                        intent.putExtra("value",!"".equals(sessionId));
                        sendContent(intent);
                    } else if("login".equals(command)) {
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "login");
                        sendContent(intent);
                    } else if ("getUserInfo".equals(command)) {
                        String userName = jsonObject.getString("userName");
                        String name = jsonObject.getString("name");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getUserInfo");
                        intent.putExtra("userName", userName);
                        intent.putExtra("name",name);
                        sendContent(intent);
                    } else if ("getGroupList".equals(command)) {
                        JSONArray groupList = jsonObject.getJSONArray("groupList");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getGroupList");
                        intent.putExtra("groupList",groupList.toString());
                        sendContent(intent);
                    } else if ("getUserFriendList".equals(command)){
                        JSONArray userList = jsonObject.getJSONArray("userList");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getUserFriendList");
                        intent.putExtra("userList",userList.toString());
                        sendContent(intent);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void connect(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","connect");
            jsonObject.put("system","android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void login(String userName, String password){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","login");
            jsonObject.put("userName",userName);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void getUserInfo(){
        getUserInfo(null);
    }

    public void getUserInfo(String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getUserInfo");
            if (userId != null) {
                jsonObject.put("userId", userId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void getGroupList(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getGroupList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void getUserFriend(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getUserFriendList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void getGroupInfo(int groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getGroupInfo");
            jsonObject.put("groupId",groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    /**
     * 发送广播
     */
    protected void sendContent(Intent intent) {
        // TODO Auto-generated method stub
        sendBroadcast(intent);
    }

    public void send(String json){
        PrintStream ps = new PrintStream(os);
        ps.println(json);
        ps.flush();
    }
    public void send(Object object){
        send(object.toString());
    }

    protected String getLine(){
        //从socket通信管道中得到一个字节输入流
        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //把字节输入流转换成字符输入流
        InputStreamReader isr = new InputStreamReader(is);
        //把字符输入流包装为缓冲字符输入流
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

}