package com.dd.surf.socket;

import android.os.Handler;
import android.os.Looper;

import com.dd.surf.util.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TCPClient extends Thread{

    public Socket socket = null;
    public OutputStream os = null;

    private final int port = 2042;

    public boolean initialization(String host){
        Executor executor = Executors.newSingleThreadExecutor();
        //任务
        FutureTask<Boolean> future = new FutureTask<>(() -> {
            try {
                try {
                    socket = new Socket(host, port);
                    os = socket.getOutputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        //执行
        executor.execute(future);
        try{
            boolean result = future.get(1, TimeUnit.SECONDS);
            System.out.println(result);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        String line = null;
        try {
            //while是抄的写的非常好
            while ((line = getLine()) != null) {
                System.out.println(line);
                JSONObject jsonObject = new JSONObject(line);
                String command = jsonObject.getString("command");
                if (command.equals("connect")) {
                    System.out.println("Connect 成功");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
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

    private String getLine(){
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

    public void send(String json){
        PrintStream ps = new PrintStream(os);
        ps.println(json);
        ps.flush();
    }
    public void send(Object object){
        send(object.toString());
    }

}
