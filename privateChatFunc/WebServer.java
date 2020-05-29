package com.dream.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class WebServer {

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);

        System.out.println("Server listening 8080......");

        HashMap<Integer, String> mapList = new HashMap<>();

        List<Socket> mySocket = new ArrayList<>();

        while (true) {

            Socket socket = serverSocket.accept();

            System.out.println("Accept a new connection: "+socket.getInetAddress().getHostAddress()+", "+socket.getPort());

            InputStream is = socket.getInputStream();

            byte[] bytes = new byte[1024*8];

            int data = is.read(bytes);

            String code = new String(bytes, 0, data);

            if(code.equals("1")) {

                Thread t = new Thread(new ServerThread(socket, socket.getPort(), mapList, mySocket));

                t.setDaemon(true);

                t.start();
            }
            else {

                OutputStream os = socket.getOutputStream();

                os.write("please go out!".getBytes());

                os.close();

                socket.close();
            }
        }
    }

    public static class ServerThread implements Runnable {

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

        public void tellOnePerson(int exceptNum, String whatToSay) {
            for(Socket socket: mySocket) {
                if(socket != null) {
                    if(socket.getPort() == exceptNum) {
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

                            if(info.charAt(0) == '@') {
                                System.out.println("进入私聊区...");
                                nickname = info.split(":")[0].substring(1);

                                int whatToSayNumber = 0;

                                Set<Map.Entry<Integer, String>> entries = myDict.entrySet();

                                for(Map.Entry<Integer, String > entry: entries) {
                                    if(entry.getValue().equals(nickname)) {
                                        whatToSayNumber = entry.getKey();
                                    }
                                }
                                String message = info.split(":")[1];
                                System.out.println(myDict.get(connNumber)+" @ "+myDict.get(whatToSayNumber)+": " + message);

                                tellOnePerson(whatToSayNumber, myDict.get(connNumber)+"【正在私聊你】: "+ message);

                            } else {
                                System.out.println(myDict.get(connNumber)+": " + info);

                                tellOthers(connNumber, myDict.get(connNumber) + ": " + info);
                            }




                        } catch (Exception ignored) {
                        }
                    }
                }

            } catch (IOException ignored) {}
        }
    }
}
