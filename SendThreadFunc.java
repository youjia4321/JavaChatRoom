package com.dream.chat;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

class SendThreadFunc implements Runnable{

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
