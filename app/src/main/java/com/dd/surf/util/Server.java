package com.dd.surf.util;

import android.app.Application;

import com.dd.surf.socket.TCPClient;
import com.dd.surf.socket.UDPClient;

import org.json.JSONException;
import org.json.JSONObject;

public class Server extends Application {

    private TCPClient tcpClient;
    private UDPClient udpClient;

    private String host = "192.168.28.222";

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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("command","setIpPort");
                jsonObject.put("port",udpClient.port);
                udpClient.send(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

}
