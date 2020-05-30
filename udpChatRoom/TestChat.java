package com.dream.udp;

public class TestChat {

    public static void main(String[] args) {

        Thread t1=new Thread(new SenderThread());
        Thread t2=new Thread(new ReceiverThread());
        t1.start();
        t2.start();

    }
}
