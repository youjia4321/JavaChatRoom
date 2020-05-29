package com.dream.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
}
