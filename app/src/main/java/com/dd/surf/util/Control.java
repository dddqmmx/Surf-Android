package com.dd.surf.util;

import android.app.Application;

import com.dd.surf.entity.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Control extends Application {

    private int id = 0;

    private String userName = null;
    private String password = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String serverAddress;
    public int port = 2077;
    InetAddress inetAddress = null;

    public boolean initialize(){
        try {
            inetAddress = InetAddress.getByName("192.168.5.4");
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String send(byte[] data){
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length,inetAddress,port);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            byte[] data2 = new byte[1024];

            DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
            socket.receive(packet2);
            String reply = new String(data2, 0, packet2.getLength());
            socket.close();
            return reply;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean login(String userName,String userPass){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","login");
            jsonObject.put("userName",userName);
            jsonObject.put("userPass",userPass);
            String reply = send(jsonObject.toString().getBytes());
            JSONObject replyJson = new JSONObject(reply);
            return replyJson.getBoolean("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getUserId(String userName,String userPass){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getUserId");
            jsonObject.put("userName",userName);
            jsonObject.put("userPass",userPass);
            String reply = send(jsonObject.toString().getBytes());
            JSONObject replyJson = new JSONObject(reply);
            return replyJson.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getNameByUserName(String userName){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getNameByUserName");
            jsonObject.put("userName",userName);
            String reply = send(jsonObject.toString().getBytes());
            JSONObject replyJson = new JSONObject(reply);
            return replyJson.getString("name");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    public String getNameById(int id){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getNameByUserId");
            jsonObject.put("id",id);
            String reply = send(jsonObject.toString().getBytes());
            JSONObject replyJson = new JSONObject(reply);
            return replyJson.getString("name");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    public List<Message> getGroupList(String userName,String userPass){
        List<Message> messageList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getGroupList");
            jsonObject.put("userName",userName);
            jsonObject.put("userPass",userPass);
            String reply = send(jsonObject.toString().getBytes());
            JSONObject repostJson = new JSONObject(reply);
            Iterator<?> keys = repostJson.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONArray jsonArray = repostJson.getJSONArray(key);
                Message message = new Message();
                message.setType(1);
                message.setId(Integer.parseInt(key));
                message.setName(jsonArray.getString(0));
                message.setHead(jsonArray.getString(1));
                messageList.add(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return messageList;
    }

    public JSONArray getFriendList(String userName,String userPass){
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getFriendList");
            jsonObject.put("userName",userName);
            jsonObject.put("userPass",userPass);
            String reply = send(jsonObject.toString().getBytes());
            jsonArray = new JSONArray(reply);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONArray getFriendRequestList(String userName,String userPass){
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getFriendRequestList");
            jsonObject.put("userName",userName);
            jsonObject.put("userPass",userPass);
            String reply = send(jsonObject.toString().getBytes());
            jsonArray = new JSONArray(reply);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }

    public String getGroupNameById(int id){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getGroupNameById");
            jsonObject.put("id",id);
            String reply = send(jsonObject.toString().getBytes());
            JSONObject replyJson = new JSONObject(reply);
            return replyJson.getString("name");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

}
