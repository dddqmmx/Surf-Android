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

    public static String host = "192.168.5.6";

    public static String sessionId;

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String sessionId) {
         Server.sessionId = sessionId;
    }
/*
    public boolean initialization(){
        udpClient = new UDPClient();
        boolean tcpInitialization = tcpClient.initialization(host);
        udpClient.initialization(host);
        tcpClient.start();

        return tcpInitialization;
    }*/


}
