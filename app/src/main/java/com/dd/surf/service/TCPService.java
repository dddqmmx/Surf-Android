package com.dd.surf.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.dd.surf.pojo.Group;
import com.dd.surf.pojo.User;
import com.dd.surf.util.Client;
import com.dd.surf.util.FileUtil;
import com.dd.surf.util.MessageUtil;
import com.dd.surf.util.SaveParameter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class TCPService extends Service {

    public Thread tcpThread = null;

    public Socket socket = null;
    public BufferedInputStream bufferedInputStream = null;

    private final int port = 2042;

    public static Map<Byte,ByteArrayOutputStream> byteArrayOutputStreamMap = new HashMap<>();

    public static Map<Byte, SaveParameter> byteArraySaveParameterMap = new HashMap<>();


    //被占用的的messageId
    public byte[] occupiedMessageId = new byte[256];
    /*字节的最大上限如果发送消息的时候messageId等于maxMessageId则把message设置为minMessageId
    因为以后可能会改成int,long啥的就把max和min写上了
     */
    public byte maxMessageId = 127;
    //字节的最小上限;
    public byte minMessageId = -128;
    //存储下个message的
    public byte messageId = -128;

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
                bufferedInputStream = new BufferedInputStream(socket.getInputStream());
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
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int length;
                byte[] messageBytes = new byte[0];
                while ((length = inputStream.read(buffer)) != -1) {
                    // 将读取到的字节流拼接到之前的消息字节数组中
                    messageBytes = Arrays.copyOf(messageBytes, messageBytes.length + length);
                    System.arraycopy(buffer, 0, messageBytes, messageBytes.length - length, length);

                    // 如果读取到的字节流中包含完整的消息，则将其分离出来并处理
                    while (messageBytes.length >= 4) {
                        byte[] messageHeader = Arrays.copyOfRange(messageBytes, 0, 4); // 取前 4 个字节作为消息头
                        ByteBuffer buffer1 = ByteBuffer.wrap(messageHeader); // 使用 ByteBuffer 对消息头进行解析
                        int messageLength = buffer1.getInt(); // 解析消息头中的消息长度
                        //byte[] messageData = Arrays.copyOfRange(messageBytes, 4, 4 + messageLength);
                        // 如果读取到的字节流中包含完整的消息，则将其分离出来并处理
                        if (messageBytes.length >= 4 + messageLength)
                        {
                            byte[] message = Arrays.copyOfRange(messageBytes, 4, 4 + messageLength);
                            processMessage(message);
                            messageBytes = Arrays.copyOfRange(messageBytes, 4 + messageLength, messageBytes.length);
                        } else {
                            break;
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
            /*while(true) {
                byte[] by = new byte[1024+3];        //保存包里的字节
                int res = 0;                         //字节长度
                byte messageId;                      //消息id
                ByteArrayOutputStream byteArrayOutputStream;    //把分段发送的字节存到这里,用来读取
                String command;                                 //消息发完消息字符串放在这里
                byte transferCompleteFlag;                      //消息是否发完的标记
                try {


                    String line;
                    while ((line = bufferedInputStream) != null) {
                        System.out.println(line);
                    }

                    *//*StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < by.length; i++)
                    {
                        sb.append("{").append(by[i]).append("},");
                    }*//*
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }*/
        });
    }

    private void processMessage(byte[] bytes) throws IOException, JSONException {
        byte messageId = bytes[0];
        byte transferCompleteFlag = bytes[2];
        if (bytes[1]==1){
            if (byteArrayOutputStreamMap.containsKey(messageId)){
                ByteArrayOutputStream byteArrayOutputStream = byteArrayOutputStreamMap.get(messageId);
                byteArrayOutputStream.write(bytes,3,bytes.length-3);
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
            }else{
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(bytes,3,bytes.length-3);
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
                byteArrayOutputStreamMap.put(messageId,byteArrayOutputStream);
            }
            if (transferCompleteFlag == 1){
                //System.out.println(Arrays.toString(byteArrayOutputStreamMap.get(messageId).toByteArray()));
                String msg = byteArrayOutputStreamMap.get(messageId).toString("UTF-8");
                JSONObject jsonObject = new JSONObject(msg);
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
                    Group group = new Group();
                    group.setId(id);
                    group.setGroupName(groupName);
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
                } else if ("agreeGroupRequest".equals(command)){
                    int groupMemberId = jsonObject.getInt("groupMemberId");
                    boolean bool = jsonObject.getBoolean("bool");
                    Intent intent=new Intent();
                    intent.setAction("com.dd.surf.service.tcpClient");
                    intent.putExtra("command", "agreeGroupRequest");
                    intent.putExtra("groupMemberId",groupMemberId);
                    intent.putExtra("bool",bool);
                    sendContent(intent);
                }else if ("getGroupHead".equals(command)){
                    int groupId = jsonObject.getInt("groupId");
                    byte fileMessageId = (byte) jsonObject.getInt("fileMessageId");
                    SaveParameter saveParameter = new SaveParameter();
                    saveParameter.path = "/image/group";
                    saveParameter.fileName = "/"+groupId+".sf";
                    Intent intent=new Intent();
                    intent.setAction("com.dd.surf.service.tcpClient");
                    intent.putExtra("command", "getGroupHead");
                    intent.putExtra("groupId",groupId);
                    saveParameter.intent = intent;
                    byteArraySaveParameterMap.put(fileMessageId,saveParameter);
                    System.out.println(groupId+" "+fileMessageId);
                }else if ("getUserHead".equals(command)) {
                    int userId = jsonObject.getInt("userId");
                    byte fileMessageId = (byte) jsonObject.getInt("fileMessageId");
                    SaveParameter saveParameter = new SaveParameter();
                    saveParameter.path = "/image/user/avatar";
                    saveParameter.fileName = "/" + userId + ".sf";
                    Intent intent = new Intent();
                    intent.setAction("com.dd.surf.service.tcpClient");
                    intent.putExtra("command", "getUserHead");
                    intent.putExtra("userId", userId);
                    saveParameter.intent = intent;
                    byteArraySaveParameterMap.put(fileMessageId, saveParameter);
                    System.out.println(userId + " " + fileMessageId);
                }else if ("getGroupHeadSurfFile".equals(command)){
                    int groupId = jsonObject.getInt("groupId");
                    long length = jsonObject.getLong("length");
                    String md5 = jsonObject.getString("md5");
                    String groupAvatar = getExternalFilesDir("image/group/").getAbsolutePath() + "/" + groupId + ".sf";
                    File file = new File(groupAvatar);
                    if (file.length() == length){
                        System.out.println("ok");
                        byte[] data = new byte[(int) file.length()];
                        new FileInputStream(file).read(data);
                        if (md5.equals(FileUtil.getMd5(data))){
                            Intent intent=new Intent();
                            intent.setAction("com.dd.surf.service.tcpClient");
                            intent.putExtra("command", "getGroupHead");
                            intent.putExtra("groupId",groupId);
                            sendContent(intent);
                            return;
                        }
                    } else {
                        System.out.println("迷你世界!!!!!!!!!!");
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("command","getGroupHead");
                        jsonObject1.put("groupId",groupId);
                        send(jsonObject1);
                    }
                }else if ("getUserHeadSurfFile".equals(command)){
                    int userId = jsonObject.getInt("userId");
                    long length = jsonObject.getLong("length");
                    String md5 = jsonObject.getString("md5");
                    String groupAvatar = getExternalFilesDir("/image/user/avatar").getAbsolutePath() + "/" + userId + ".sf";
                    File file = new File(groupAvatar);
                    if (file.length() == length){
                        byte[] data = new byte[(int) file.length()];
                        new FileInputStream(file).read(data);
                        if (md5.equals(FileUtil.getMd5(data))){
                            System.out.println("哈姆？哈姆？哈姆的哈贝贝哈姆？");
                            Intent intent=new Intent();
                            intent.setAction("com.dd.surf.service.tcpClient");
                            intent.putExtra("command", "getUserHead");
                            intent.putExtra("userId",userId);
                            sendContent(intent);
                            return;
                        }
                    }else {
                        System.out.println("卧槽冰!!!!!!!!!!");
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("command","getUserHead");
                        jsonObject1.put("userId",userId);
                        send(jsonObject1);
                    }
                }
                System.out.println("message : "+msg);
                byteArrayOutputStreamMap.remove(messageId);
            }
        }else if (bytes[1] == 2){
            if (byteArrayOutputStreamMap.containsKey(messageId)){
                ByteArrayOutputStream byteArrayOutputStream = byteArrayOutputStreamMap.get(messageId);
                byteArrayOutputStream.write(bytes,3,bytes.length-3);
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
            }else{
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(bytes,3,bytes.length-3);
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
                byteArrayOutputStreamMap.put(messageId,byteArrayOutputStream);
            }
            if (transferCompleteFlag == 1){
                System.out.println("我开始了"+messageId);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    FileOutputStream outputStream;
                    try {
                        SaveParameter saveParameter = byteArraySaveParameterMap.get(messageId);
                        outputStream = new FileOutputStream(this.getExternalFilesDir(saveParameter.path).getAbsolutePath()+saveParameter.fileName);
                        outputStream.write(byteArrayOutputStreamMap.get(messageId).toByteArray());
                        outputStream.close();
                        sendContent(saveParameter.intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                byteArrayOutputStreamMap.remove(messageId);
                //System.out.println("我是大傻逼 : "+Arrays.toString(bytes));
            }
        }

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

    public void agreeGroupRequest(int groupMemberId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","agreeGroupRequest");
            jsonObject.put("groupMemberId",groupMemberId);
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

    public void getGroupHead(int groupId){
        String groupAvatar = getExternalFilesDir("image/group/").getAbsolutePath() + "/" + groupId + ".sf";
        File file = new File(groupAvatar);
        JSONObject jsonObject = new JSONObject();
        try {
            if (file.exists()){
                jsonObject.put("command","getGroupHeadSurfFile");
            }else{
                jsonObject.put("command","getGroupHead");
            }
            jsonObject.put("groupId",groupId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        send(jsonObject);
    }

    public void getUserHead(int userId){
        String userAvatar = getExternalFilesDir("image/user/avatar").getAbsolutePath() + "/" + userId + ".sf";
        File file = new File(userAvatar);
        JSONObject jsonObject = new JSONObject();
        try {
            if (file.exists()){
                jsonObject.put("command","getUserHeadSurfFile");
            }else {
                jsonObject.put("command","getUserHead");
            }
            jsonObject.put("userId",userId);
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

   /* public void send(String json){
        try {
            OutputStreamWriter outputStream = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            PrintWriter printWriter = new PrintWriter(outputStream,true);
            printWriter.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    */
    public void send(Object object){
        send(object.toString());
    }

    public void send(String message){
        byte messageId = getMessageId();
        System.out.println("Server send : "+messageId+" : "+message);
        byte[] sb = message.getBytes(); // 转化为字节数组
        ArrayList<byte[]> newByteArr = MessageUtil.reviseArr(sb, messageId);
        for (byte[] bytes : newByteArr) {
            try {
                BufferedOutputStream ps = new BufferedOutputStream(socket.getOutputStream());
                System.out.println(Arrays.toString(bytes));
                //
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                dataOutputStream.writeInt(bytes.length);
                System.out.println(bytes.length);
                dataOutputStream.write(bytes);
                dataOutputStream.flush();
                byte[] messageBytes = byteArrayOutputStream.toByteArray();
                //
                ps.write(messageBytes);   // 写入输出流，将内容发送给客户端的输入流
                ps.flush();
                //ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
        }
    }

    public byte getMessageId() {
        synchronized (this) {
            if (messageId == maxMessageId) {
                messageId = minMessageId;
            }
            return messageId++;
        }
    }
    /*protected String getLine(){
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
    }*/

}