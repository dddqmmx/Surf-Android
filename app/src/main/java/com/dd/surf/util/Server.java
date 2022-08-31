package com.dd.surf.util;

import android.app.Application;

import com.dd.surf.socket.TcpClient;
import com.dd.surf.socket.UdpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class Server extends Application {

    private TcpClient tcpClient;
    private UdpClient udpClient;

    private String host = "192.168.117.86";

    public void initialization(){
        tcpClient = new TcpClient();
        udpClient = new UdpClient();
        tcpClient.initialization(host);
    }

    public boolean connect(){
        if (tcpClient.connect()){
            udpClient.start();

        }
        return false;
    }

}
