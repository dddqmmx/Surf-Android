package com.dd.surf.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class TCPCreator {
    public Socket socket = null;
    public OutputStream os = null;

    public TCPCreator(String host,int port){
        try {
            socket = new Socket(host, port);
            os = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLine(){
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
        try {
            if (is != null) {
                is.close();
            }
            isr.close();
            br.close();
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

    public void close(){
        try {
            socket.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
