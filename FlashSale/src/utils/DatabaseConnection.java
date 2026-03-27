package utils;

import java.sql.*;

public class DatabaseConnection {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/FlashSale";
    private static final String USER = "root";
    private static final String PASS = "12345678";



    public static Connection getConnection() throws SQLException {
        Connection conn=null;
        try {
            Class.forName(DRIVER);
             conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return  conn;

    }
    public static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}