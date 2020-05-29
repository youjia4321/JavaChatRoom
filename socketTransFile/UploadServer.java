package com.dream.upload;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class UploadServer {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8080);

        System.out.println("server listening 8080...");

        Object obj = new Object();

        while (true) {

            Socket socket = serverSocket.accept();

            System.out.println(socket.getInetAddress().getHostAddress()+","+socket.getPort()+" 连接服务器...");

            new Thread(new UploadThread(socket, obj)).start();

        }

    }

    static class UploadThread implements Runnable {

        private Socket socket;
        private final Object lock;

        public UploadThread(Socket socket, Object lock) {

            this.socket = socket;
            this.lock = lock;

        }

        @Override
        public void run() {

            synchronized (lock) {

                String  ip = socket.getInetAddress().getHostAddress()+","+socket.getPort();

                InputStream is = null;
                BufferedInputStream bis = null;
                BufferedOutputStream bos = null;
                try {

                    is = socket.getInputStream();

                    bis = new BufferedInputStream(is);

                    byte[] bytes = new byte[1024*8];

                    int data;

                    data = socket.getInputStream().read(bytes);

                    if(data != -1) {

                        String suffix = new String(bytes, 0, data);

                        String filePath = "Files"+ File.separator + "media" + File.separator + suffix;

                        System.out.println("文件存储位置(当前目录下)："+filePath);

                        bos = new BufferedOutputStream(new FileOutputStream(filePath));

                        while ((data = is.read(bytes)) != -1) {

                            bos.write(bytes, 0, data);
                            bos.flush();

                        }

                        System.out.println(ip+" 上传文件成功");
                    } else {
                        System.out.println(ip+" 文件传输失败");
                    }



                } catch (IOException e) {

                    System.out.println(ip+" 文件传输异常");

                } finally {

                    try {
                        if(bos != null) {
                            bos.close();
                        }
                        if(bis != null) {
                            bis.close();
                        }
                        if( is != null) {
                            is.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        }
    }
}

