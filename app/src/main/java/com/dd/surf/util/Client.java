package com.dd.surf.util;

import android.app.Application;

import com.dd.surf.pojo.Group;
import com.dd.surf.pojo.User;
import com.dd.surf.service.TCPService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client extends Application {

    private static TCPService tcpService;

    public static TCPService getTcpService() {
        return tcpService;
    }

    public static void setTcpService(TCPService tcpService) {
        Client.tcpService = tcpService;
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

    public static void setUser(int userId, User user) {
        userHashMap.put(userId, user);
    }

    public static HashMap<Integer, Group> groupInfoHashMap = new HashMap<>();

    public static boolean hasGroupInfo(int groupId) {
        return groupInfoHashMap.containsKey(groupId);
    }

    public static void getGroupInfoByServer(int groupId) {
        tcpService.getGroupInfoById(groupId);
    }

    public static Group getGroupInfo(int groupId) {
        return groupInfoHashMap.get(groupId);
    }

    public static void setGroupInfo(int groupId, Group group) {
        groupInfoHashMap.put(groupId, group);
    }

    public static List<Integer> friendsList = new ArrayList<>();

    public static List<Integer> groupList = new ArrayList<>();

    public static String host = "192.168.100.145";

    public static String sessionId;

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String sessionId) {
         Client.sessionId = sessionId;
    }

}
