package edu.connections3a8.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MyConnection {

    private static MyConnection instance;
    private Connection cnx;

    private final String url = "jdbc:mysql://localhost:3306/3a8?useSSL=false&serverTimezone=UTC";
    private final String login = "root";
    private final String password = "";

    // Private constructor (Singleton)
    private MyConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            cnx = DriverManager.getConnection(url, login, password);
            System.out.println("✓ Connected to database successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    // Singleton access method
    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            // Check if connection is still valid
            if (cnx == null || cnx.isClosed()) {
                System.out.println("Reconnecting to database...");
                cnx = DriverManager.getConnection(url, login, password);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking/reconnecting to database: " + e.getMessage());
            e.printStackTrace();
        }
        return cnx;
    }
}
