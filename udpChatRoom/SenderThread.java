package com.dream.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @BelongsProject: Day20200529-Server
 * @BelongsPackage: com.bruceliu.demo3
 * @Author: bruceliu
 * @QQ:1241488705
 * @CreateTime: 2020-05-29 15:30
 * @Description: 发送端
 */
public class SenderThread implements Runnable {

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        DatagramPacket datagramPacket =null;
        DatagramSocket socket=null;
        try {
            socket=new DatagramSocket();
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            //只要控台能读取到数据
            while ((line=bufferedReader.readLine())!=null) {
                //接收控制台输入
                datagramPacket = new DatagramPacket(line.getBytes(), line.getBytes().length,
                        InetAddress.getByName("10.7.173.255"), 8888);  // 广播地址 局域网内
                socket.send(datagramPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
