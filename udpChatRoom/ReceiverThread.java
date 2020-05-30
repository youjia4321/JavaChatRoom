package com.dream.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class ReceiverThread implements Runnable {

    @Override
    public void run() {

        DatagramSocket socket=null;
        try {
            socket=new DatagramSocket(8888);
            byte[] bytes=new byte[1024];
            DatagramPacket packet=new DatagramPacket(bytes, bytes.length);
            while(true){
                socket.receive(packet);
                String ip = packet.getAddress().getHostAddress();
                int port = packet.getPort();
                byte[] buf = packet.getData();
                String message=new String(buf,0, packet.getLength());
                System.out.println(ip+"，" + port +":"+message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert socket != null;
            socket.close();
        }
    }

}
