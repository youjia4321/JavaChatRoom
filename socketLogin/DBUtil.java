package com.dream.weblogin;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBUtil {

    private static String DRIVERNAME = null;
    private static String URL = null;
    private static String USER = null;
    private static String PASSWORD = null;



    static {

        Properties properties = new Properties();
        try {
            properties.load(DBUtil.class.getClassLoader().getResourceAsStream("com/dream/weblogin/dbconfig.properties"));
            DRIVERNAME = properties.getProperty("DRIVERNAME");
            URL = properties.getProperty("URL");
            USER = properties.getProperty("USER");
            PASSWORD = properties.getProperty("PASSWORD");


            Class.forName(DRIVERNAME);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL, USER, PASSWORD);

    }

    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) throws SQLException {

        if(conn!=null) {
            conn.close();
        }

        if(ps!=null) {
            ps.close();
        }

        if(rs!=null) {
            rs.close();
        }
    }
}
