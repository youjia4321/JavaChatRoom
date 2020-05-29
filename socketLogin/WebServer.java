package com.dream.weblogin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class WebServer {
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);

        System.out.println("web服务器启动，端口为:8080......");


        while (true) {

            Socket socket = serverSocket.accept();

            Thread t = new Thread(new ServerThread(socket));

            t.setDaemon(true);

            t.start();

        }
    }

    public static boolean saveUsers(String info) throws SQLException {

        String username = info.split(",")[0];
        String password = info.split(",")[1];
        String age = info.split(",")[2];
        String email = info.split(",")[3];

        Connection conn = null;
        PreparedStatement ps = null;

        try {
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
            e.printStackTrace();
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
