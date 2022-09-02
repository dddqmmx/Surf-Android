package com.dd.surf.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UDPClient extends Thread{

    public int port = 11451;
    public String host;

    public void initialization(String host){
        this.host = host;
    }

    @Override
    public void run() {
        DatagramSocket socket= null;
        //接收udp消息的socket

        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        //接收到的udp消息
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        //循环获取收到的消息
        while(true) {
            try {
                //线程阻塞只有接收到消息才运行
                socket.receive(packet);

                //接收到的信息,信息无法在UdpServer内获取原因未知
                String info = new String(packet.getData(), 0, packet.getLength());      //具体消息
                InetAddress inetAddress = packet.getAddress();                                //消息发送者的地址
                int packetPort = packet.getPort();                                                  //对方端口

                //debug用的不用管他
                System.out.println(Arrays.toString(inetAddress.getAddress()));

                //使用多线程进行消息处理
                //作用是处理多个客户端的连接请求
                UDPServerThread udpServerThread = new UDPServerThread(packet,info,inetAddress,packetPort);
                udpServerThread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Object object){
        send(object.toString());
    }

    public void send(String string){
        send(string.getBytes(StandardCharsets.UTF_8));
    }

    public boolean send(byte[] data){
        DatagramSocket socket = null;
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(data, data.length,inetAddress,2077);
            socket = new DatagramSocket();
            socket.send(packet);

            byte[] data2 = new byte[1024];
            DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
            socket.receive(packet2);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                socket.close();
            }
        }
        return false;
    }
}
