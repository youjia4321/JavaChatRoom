package com.dream.weblogin;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class WebClient {
    public static void main(String[] args) throws Exception {

        Scanner input=new Scanner(System.in);


        // 127.0.0.1 表示的当前的主机  本机
        Socket socket=new Socket("127.0.0.1",8080);
        //字节输出流
        OutputStream outputStream = socket.getOutputStream();
        //字节输入流
        InputStream inputStream = socket.getInputStream();
        //2.向服务器发送要注册的信息,封装数据

        boolean flag = true;

        while(flag) {
            System.out.println("1.注册 2.登录 3.退出");
            String content = null;
            switch (input.next()) {
                case "1":
                    content = register(input);
                    outputStream.write(content.getBytes());
                    break;
                case "2":
                    content = login(input);
                    outputStream.write(content.getBytes());
                    break;
                case "3":
                    outputStream.write("请求退出".getBytes());
                    flag = false;
                    break;
            }

            byte[] bytes=new byte[1024];
            int data = inputStream.read(bytes);
            System.out.println("服务器："+new String(bytes,0, data));
        }




        inputStream.close();
        outputStream.close();
        socket.close();

    }

    public static String register(Scanner input) {

        System.out.print("请输入您的账号:");
        String name=input.next();
        System.out.print("请输入您的密码:");
        String pass=input.next();
        System.out.print("请输入您的年龄:");
        int age=input.nextInt();
        System.out.print("请输入您的邮箱:");
        String email=input.next();

        return name+","+pass+","+age+","+email;
    }

    public static String login(Scanner input) {

        System.out.print("请输入您的账号:");
        String name=input.next();
        System.out.print("请输入您的密码:");
        String pass=input.next();

        return name+","+pass;
    }
}
