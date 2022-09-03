package com.dd.surf.util;

import android.app.Application;

import com.dd.surf.socket.TCPClient;
import com.dd.surf.socket.UDPClient;

import org.json.JSONException;
import org.json.JSONObject;

public class Server extends Application {

    private TCPClient tcpClient;
    private UDPClient udpClient;

    private String host = "192.168.117.118";

    public void initialization(){
        tcpClient = new TCPClient();
        udpClient = new UDPClient();
        tcpClient.initialization(host);
        udpClient.initialization(host);
    }

    public boolean connect(){
        try {
            if (tcpClient.connect()){
                udpClient.start();
                udpClient.setIpPort();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
