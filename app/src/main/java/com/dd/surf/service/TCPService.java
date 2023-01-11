package com.dd.surf.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.dd.surf.pojo.Group;
import com.dd.surf.pojo.User;
import com.dd.surf.util.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class TCPService extends Service {

    public Thread tcpThread = null;

    public Socket socket = null;

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
        boolean tcpInitialization = tcpInitialization(Client.host);
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
                        Client.setSessionId(sessionId);
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "connect");
                        intent.putExtra("value",!"".equals(sessionId));
                        sendContent(intent);
                    } else if("login".equals(command)) {
                        boolean login = jsonObject.getBoolean("login");
                        String message = jsonObject.getString("message");
                        Client.userId = jsonObject.getInt("id");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "login");
                        intent.putExtra("login", login);
                        intent.putExtra("message", message);
                        sendContent(intent);
                    } else if ("getUserInfo".equals(command)) {
                        String userName = jsonObject.getString("userName");
                        String name = jsonObject.getString("name");
                        User user = new User();
                        user.setId(Client.userId);
                        user.setUserName(userName);
                        user.setName(name);
                        Client.setUser(Client.userId,user);
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getUserInfo");
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
                    } else if ("getGroupInfo".equals(command)){
                        String groupName = jsonObject.getString("groupName");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getGroupInfo");
                        intent.putExtra("groupName", groupName);
                        sendContent(intent);
                    } else if ("getGroupMessage".equals(command)){
                        JSONArray messageList = jsonObject.getJSONArray("messageList");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getGroupMessage");
                        intent.putExtra("messageList",messageList.toString());
                        sendContent(intent);
                    } else if ("getUserInfoById".equals(command)){
                        int id = jsonObject.getInt("id");
                        String userName = jsonObject.getString("userName");
                        String name = jsonObject.getString("name");
                        String personalProfile = null;
                        if (jsonObject.has("personalProfile")){
                            personalProfile = jsonObject.getString("personalProfile");
                        }
                        User user = new User();
                        user.setId(id);
                        user.setUserName(userName);
                        user.setName(name);
                        user.setPersonalProfile(personalProfile);
                        if (Client.hasUser(id)){
                            User clientUser = Client.getUser(id);
                            if (!clientUser.equals(user)){
                                Client.setUser(id,user);
                            }
                        }else {
                            Client.setUser(id,user);
                        }
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getUserInfoById");
                        intent.putExtra("id",user.getId());
                        intent.putExtra("userName",user.getUserName());
                        intent.putExtra("name",user.getName());
                        intent.putExtra("personalProfile",user.getPersonalProfile());
                        sendContent(intent);
                    } else if ("processMessage".equals(command)){
                        int sender = jsonObject.getInt("sender");
                        String message = jsonObject.getString("message");
                        int contactType = jsonObject.getInt("contactType");
                        int contactId = jsonObject.getInt("contactId");
                        if (contactType == 1){
                            Intent intent=new Intent();
                            intent.setAction("com.dd.surf.service.tcpClient");
                            intent.putExtra("command", "GroupMessage");
                            intent.putExtra("contactId",contactId);
                            intent.putExtra("sender",sender);
                            intent.putExtra("message",message);
                            sendContent(intent);
                        }
                    } else if ("addFriendRequest".equals(command)){
                        int code = jsonObject.getInt("code");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "addFriendRequest");
                        intent.putExtra("code",code);
                        sendContent(intent);
                    } else if ("getFriendRequest".equals(command)){
                        JSONArray relationArray = jsonObject.getJSONArray("relationArray");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getFriendRequest");
                        intent.putExtra("relationArray",relationArray.toString());
                        sendContent(intent);
                    } else if ("agreeFriendRequest".equals(command)){
                        int id = jsonObject.getInt("id");
                        int code = jsonObject.getInt("code");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "agreeFriendRequest");
                        intent.putExtra("id",id);
                        intent.putExtra("code",code);
                        sendContent(intent);
                    } else if ("selectGroup".equals(command)){
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "selectGroup");
                        JSONArray relationArray = jsonObject.getJSONArray("groupList");
                        intent.putExtra("groupList",relationArray.toString());
                        sendContent(intent);
                    } else if ("selectUser".equals(command)){
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "selectUser");
                        JSONArray relationArray = jsonObject.getJSONArray("userList");
                        intent.putExtra("userList",relationArray.toString());
                        sendContent(intent);
                    }else if ("getGroupInfoById".equals(command)) {
                        int id = jsonObject.getInt("id");
                        String groupName = jsonObject.getString("groupName");
                        String groupHead = null;
                        if (jsonObject.has("groupHead")){
                            groupHead = jsonObject.getString("groupHead");
                        }
                        Group group = new Group();
                        group.setId(id);
                        group.setGroupName(groupName);
                        group.setGroupHead(groupHead);
                        if (Client.hasGroupInfo(id)) {
                            Group clientGroup = Client.getGroupInfo(id);
                            if (!clientGroup.equals(group)) {
                                Client.setGroupInfo(id, group);
                            }
                        } else {
                            Client.setGroupInfo(id, group);
                        }
                        Intent intent = new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getGroupInfoById");
                        intent.putExtra("id", group.getId());
                        intent.putExtra("groupName", group.getGroupName());
                        intent.putExtra("groupHead", group.getGroupHead());
                        sendContent(intent);
                    }else if("addGroupRequest".equals(command)){
                        int id = jsonObject.getInt("id");
                        int code = jsonObject.getInt("code");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "addGroupRequest");
                        intent.putExtra("id",id);
                        intent.putExtra("code",code);
                        sendContent(intent);
                    }else if("getGroupRequest".equals(command)){
                        JSONArray relationArray = jsonObject.getJSONArray("relationArray");
                        Intent intent=new Intent();
                        intent.setAction("com.dd.surf.service.tcpClient");
                        intent.putExtra("command", "getGroupRequest");
                        intent.putExtra("relationArray",relationArray.toString());
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getUserInfo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void getUserInfoById(int userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getUserInfoById");
            jsonObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void getGroupInfoById(int groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getGroupInfoById");
            jsonObject.put("groupId", groupId);
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

    public void getGroupMessage(int groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getGroupMessage");
            jsonObject.put("groupId",groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void sendTextMessage(int contactType , int contactId , String message){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","sendTextMessage");
            jsonObject.put("contactType",contactType);
            jsonObject.put("contactId",contactId);
            jsonObject.put("message",message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void addFriendRequest(int userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","addFriendRequest");
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void getFriendRequest(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getFriendRequest");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void getGroupRequest(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getGroupRequest");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void agreeFriendRequest(int userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","agreeFriendRequest");
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void selectGroup(String condition){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","selectGroup");
            jsonObject.put("condition",condition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void selectUser(String condition){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","selectUser");
            jsonObject.put("condition",condition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
    }

    public void addGroupRequest(int groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","addGroupRequest");
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
        try {
            OutputStreamWriter outputStream = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            PrintWriter printWriter = new PrintWriter(outputStream,true);
            printWriter.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
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