package com.dd.surf.util;

import android.app.Application;
import android.net.wifi.WifiManager;

import com.dd.surf.pojo.User;
import com.dd.surf.service.TCPService;
import com.dd.surf.socket.TCPClient;
import com.dd.surf.socket.UDPClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Server extends Application {

    private static TCPService tcpService;

    public static TCPService getTcpService() {
        return tcpService;
    }

    public static void setTcpService(TCPService tcpService) {
        Server.tcpService = tcpService;
    }

    public static int userId;

    public static HashMap<Integer,User> userHashMap = new HashMap<>();

    public static boolean hasUser(int userId) {
        return userHashMap.containsKey(userId);
    }

    public static void getUserInfo(int userId) {
        tcpService.getUserInfoById(userId);
    }

    public static User getUser(int userId) {
        return userHashMap.get(userId);
    }

    public static void addUser(int userId, User user) {
        userHashMap.put(userId, user);
    }

    public static String host = "192.168.6.165";

    public static String sessionId;

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String sessionId) {
         Server.sessionId = sessionId;
    }

}
