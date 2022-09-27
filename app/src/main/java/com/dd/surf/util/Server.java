package com.dd.surf.util;

import android.app.Application;
import android.net.wifi.WifiManager;

import com.dd.surf.socket.TCPClient;
import com.dd.surf.socket.UDPClient;

import org.json.JSONException;
import org.json.JSONObject;

public class Server extends Application {

    private TCPClient tcpClient;
    private UDPClient udpClient;

    private String host = "192.168.5.3";

    public boolean initialization(){
        tcpClient = new TCPClient();
        udpClient = new UDPClient();
        boolean tcpInitialization = tcpClient.initialization(host);
        udpClient.initialization(host);
        return tcpInitialization;
    }

    public boolean connect(){
        try {
            if (tcpClient.connect()){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
