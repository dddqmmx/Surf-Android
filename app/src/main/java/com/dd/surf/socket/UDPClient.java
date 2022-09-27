package com.dd.surf.socket;

import android.net.wifi.WifiManager;
import android.net.wifi.aware.DiscoverySession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UDPClient{

    public String host;

    public void initialization(String host){
        this.host = host;
    }

    public void send(Object object){
        send(object.toString());
    }

    public void send(String string){
        send(string.getBytes(StandardCharsets.UTF_8));
    }

    public void send(byte[] data){
        DatagramSocket socket = null;
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(data, data.length,inetAddress,2077);
            socket = new DatagramSocket();
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
