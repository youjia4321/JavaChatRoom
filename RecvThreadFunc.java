package com.dream.chat;

import java.io.IOException;
import java.net.Socket;

class RecvThreadFunc implements Runnable{

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
