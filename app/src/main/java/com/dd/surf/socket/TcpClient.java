package com.dd.surf.socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class TcpClient {
    Socket socket = null;
    OutputStream os = null;
    public void initialization(String host){
        try {
            socket = new Socket(host, 2042);
            os = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean connect(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command","connect");
            jsonObject.put("system","android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(jsonObject);
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
        //按照行读取消息
        String line;
        try {
            while ((line = br.readLine())!= null){
                JSONObject comeBackJson = new JSONObject(line);
                System.out.println(line);
                System.out.println(Boolean.parseBoolean(comeBackJson.getString("connect")));
                return Boolean.parseBoolean(comeBackJson.getString("connect"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private void send(String json){
        PrintStream ps = new PrintStream(os);
        ps.println(json);
        ps.flush();
    }
    private void send(Object object){
        send(object.toString());
    }
}
