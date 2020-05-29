package com.dream.weblogin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerThread implements Runnable {

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean flag = true;

        System.out.println(socket.getInetAddress().getHostAddress()+":"+socket.getPort()+": 连接到了服务器");

        while (flag) {
            try {
                byte[] bytes = new byte[1024];
                int len = 0;
                if (inputStream != null) {
                    len = inputStream.read(bytes);
                }
                String info = "";
                try {
                    info = new String(bytes, 0, len);
                } catch (Exception e) {
                    info = "用户退出连接";

                }

                System.out.println(socket.getInetAddress().getHostAddress()+
                        ":"+socket.getPort()+": 用户发送过来的信息:" + info);
                String reply="";
                if(info.equals("请求退出")) {
                    flag = false;
                    reply="用户退出";
                } else {
                    if(isLoginOrRegister(info)) {
                        if(verityUser(info)) {
                            reply="登录成功";
                        } else {
                            reply="账户或密码错误，登录失败";
                        }

                    } else {
                        boolean a = isRegister(info.split(",")[0]);
                        if(a) {
                            boolean b = saveUsers(info);
                            if(b){
                                reply="恭喜，注册成功！";
                            } else {
                                reply = "服务器异常，注册失败";
                            }
                        }else{
                            reply="账户已存在，重新注册！";
                        }

                    }

                }

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(reply.getBytes());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static boolean saveUsers(String info) throws Exception {

        Connection conn = null;
        PreparedStatement ps = null;

        try {

            String username = info.split(",")[0];
            String password = info.split(",")[1];
            String age = info.split(",")[2];
            String email = info.split(",")[3];


            conn = DBUtil.getConnection();
            String sql = "insert into users(username, password, age, email) values(?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, Integer.parseInt(age));
            ps.setString(4, email);

            int result = ps.executeUpdate();

            if(result>0) {
                return true;
            }

        } catch (SQLException e) {
        }finally {
            DBUtil.close(conn, ps, null);
        }
        return false;
    }

    public static boolean verityUser(String info) throws SQLException {
        String username = info.split(",")[0];
        String password = info.split(",")[1];

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "select * from users where username=? and password=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();

            if(rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }


        return false;
    }

    public static boolean isLoginOrRegister(String info) {

        return info.split(",").length == 2;

    }

    public static boolean isRegister(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "select username from users where username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            rs = ps.executeQuery();

            if(rs.next()) {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                DBUtil.close(conn, ps, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

}
