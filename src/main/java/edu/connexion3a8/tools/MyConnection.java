package edu.connexion3a8.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {

    private static MyConnection instance;
    private String url;
    private String login;
    private String pwd;
    private Connection cnx;

    private MyConnection(){
        this.url = EnvConfig.get("DB_URL", "jdbc:mysql://localhost:3306/3a8");
        this.login = EnvConfig.get("DB_USER", "root");
        this.pwd = EnvConfig.get("DB_PASSWORD", "");

        try {
            cnx = DriverManager.getConnection(url,login,pwd);
            System.out.println("Connexion établie!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get a new database connection
     * Each call returns a fresh connection that should be closed after use
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null) {
            instance = new MyConnection();
        }
        // Always return a NEW connection for thread safety and to avoid closed connection issues
        return DriverManager.getConnection(
            instance.url, 
            instance.login, 
            instance.pwd
        );
    }

    public Connection getCnx() {
        return cnx;
    }
}
