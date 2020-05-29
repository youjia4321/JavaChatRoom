package com.dream.upload;

import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;

public class UploadClient {

    public static void main(String[] args) throws IOException {



        Scanner scan = new Scanner(System.in);
        while (true) {

            Socket socket = new Socket("localhost", 8080);
            System.out.print("输入上传文件的路径（输入N/n退出）:");

            String filePath = scan.next();

            File file = new File(filePath);

            if(filePath.equalsIgnoreCase("n")) {
                System.out.println("退出");
                System.exit(0);
            }

            if(!file.exists()) {
                System.out.println("输入文件的路径不存在");
                System.exit(0);
            }

            OutputStream os = socket.getOutputStream();

            InputStream is = socket.getInputStream();


            String[] fileSuffix = filePath.split("/");

            // 发给服务器 判断是什么类型的文件
            os.write(fileSuffix[fileSuffix.length - 1].getBytes());

            byte[] bytes = new byte[1024*8];

            long fileLength = file.length();
            System.out.println(fileLength);
            DecimalFormat df = new DecimalFormat("#.00");
            int data;

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            long current = 0;
            while ((data = bis.read(bytes)) != -1) {
                current = current + data;
                double progress = current/(double)fileLength;
                String result = String .format("%.3f", progress);

                System.out.println("传输进度："+ Double.parseDouble(result)*100+ "%");
                os.write(bytes, 0, data);
                os.flush();
            }

            System.out.println("文件传输完成");

            socket.close();
        }

    }

    public static void isError() {

    }

}
