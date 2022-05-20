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

    private String userName = null;
    private String password = null;

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

    public String getName(String userName){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","getName");
            jsonObject.put("userName",userName);
            String reply = send(jsonObject.toString().getBytes());
            JSONObject replyJson = new JSONObject(reply);
            return replyJson.getString("name");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    public List<Message> getMessageList(String userName,String userPass){
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

}
