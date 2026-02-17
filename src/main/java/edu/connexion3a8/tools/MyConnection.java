package edu.connexion3a8.tools;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MyConnection {

    private Connection cnx;

    public MyConnection() {
        Properties config = loadConfig();
        String url = config.getProperty("db.url", "jdbc:mysql://localhost:3306/3a8");
        String login = config.getProperty("db.user", "root");
        String pwd = config.getProperty("db.password", "");

        try {
            cnx = DriverManager.getConnection(url, login, pwd);
            System.out.println("Connexion établie!");
        } catch (SQLException e) {
            System.err.println("ERREUR DE CONNEXION À LA BASE DE DONNÉES:");
            System.err.println("URL: " + url);
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nVérifiez que:");
            System.err.println("1. MySQL est démarré");
            System.err.println("2. La base de données '3a8' existe");
            System.err.println("3. Les identifiants sont corrects (user: " + login + ")");
            e.printStackTrace();
        }
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load config.properties, using defaults.");
        }
        return props;
    }

    public Connection getCnx() {
        return cnx;
    }
}
