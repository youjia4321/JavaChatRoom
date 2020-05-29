package com.dream.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class WebClient {

    public static void main(String[] args) throws Exception {

        Socket socket= new Socket("10.7.173.54",8080);

        Scanner scan = new Scanner(System.in);

        InputStream inputStream = null;

        OutputStream outputStream = null;

        byte[] bytes = new byte[1024*8];

        inputStream = socket.getInputStream();

        outputStream = socket.getOutputStream();

        outputStream.write("1".getBytes());

        int data = inputStream.read(bytes);

        System.out.println(new String(bytes, 0, data));

        System.out.print("your name: ");

        outputStream.write(scan.nextLine().getBytes());

        Thread t1 = new Thread(new SendThreadFunc(socket));
        Thread t2 = new Thread(new RecvThreadFunc(socket));

        t1.start();
        t2.start();

        t1.join();

    }

    static class SendThreadFunc implements Runnable{

        private Socket socket;

        public SendThreadFunc(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true) {
                Scanner scan = new Scanner(System.in);
                String myWord = null;
                if(scan.hasNext()) {
                    myWord = scan.nextLine().trim();

                    if(myWord.length() == 0) {
                        while (true) {
                            String isNull = scan.nextLine().trim();
                            if(isNull.length() != 0) {
                                myWord = isNull;
                                break;
                            }
                        }
                    }

                    try {
                        socket.getOutputStream().write(myWord.getBytes());
                        if(myWord.equals("bye")) {
                            break;
                        }
                    } catch (IOException e) {
                        System.out.println("Server is closed!");
                    }

                }

            }
        }
    }

     static class RecvThreadFunc implements Runnable{

        private Socket socket;

        public RecvThreadFunc(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true) {
                String otherWord = "";
                byte[] bytes = new byte[1024*8];
                try {
                    int data = socket.getInputStream().read(bytes);
                    if( data == -1) {
                        break;
                    }
                    otherWord = new String(bytes, 0, data);
                    System.out.println(otherWord);
                } catch (IOException ignored) {
                    System.out.println("Server is closed!");
                }
            }
        }
    }
}

