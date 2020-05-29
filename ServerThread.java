package com.dream.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class ServerThread implements Runnable {

    private HashMap<Integer, String> myDict;
    private List<Socket> mySocket;
    private Socket socket;
    private int connNumber;

    public ServerThread(Socket socket, int connNumber, HashMap<Integer, String> myDict, List<Socket> mySocket) {
        this.socket = socket;
        this.connNumber = connNumber;
        this.myDict = myDict;  // 共享资源 存储<connNumber, nickname>
        this.mySocket = mySocket; // 共享资源 存储客户端对象
    }

    public void tellOthers(int exceptNum, String whatToSay) {
        for(Socket socket: mySocket) {
            if(socket != null) {
                if(socket.getPort() != exceptNum) {
                    OutputStream os;
                    try {
                        os = socket.getOutputStream();

                        os.write(whatToSay.getBytes());

                    } catch (IOException ignored) {}
                }
            }
        }
    }

    @Override
    public void run() {

        InputStream inputStream;
        byte[] bytes = new byte[1024*8];
        int data;
        try {
            socket.getOutputStream().write("【 welcome to the chat room... 】".getBytes());
            inputStream = socket.getInputStream();
            data = inputStream.read(bytes);
            if(data != -1) {
                String nickname = new String(bytes, 0, data);

                myDict.put(connNumber, nickname);

                mySocket.add(socket);

                System.out.println(myDict);

                socket.getOutputStream().write("【 Notice：keys 'bye' to quit the chat room 】".getBytes());

                System.out.println("connection "+ connNumber + " has nickname: " + nickname);

                tellOthers(connNumber, "【系统提示 "+myDict.get(connNumber)+" 进入聊天室】");

                InputStream is = socket.getInputStream();

                while (true) {
                    try {

                        data = is.read(bytes);

                        if(data == -1) {
                            break;
                        }

                        String info = new String(bytes, 0, data);

                        if(info.equals("bye")) {

                            String person = myDict.get(connNumber);

                            myDict.remove(connNumber);

                            System.out.println(person + " exit, " + myDict.size() + " person left");

                            tellOthers(connNumber, "【系统提示 "+ person+ " 离开聊天室】"
                                    + person + " exit, " + myDict.size() + " person left");

                            try {
                                socket.close();
                            } catch (IOException ignored) {}

                            return;
                        }

                        System.out.println(myDict.get(connNumber)+": " + info);

                        tellOthers(connNumber, myDict.get(connNumber) + " :" + info);


                    } catch (Exception ignored) {
                    }
                }
            }

        } catch (IOException ignored) {}
    }
}
