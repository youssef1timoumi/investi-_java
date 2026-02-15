package edu.connexion3a8.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {

    private String url="jdbc:mysql://localhost:3306/3a8";
    private String login="root";
    private String pwd="";

    private Connection cnx;

    public MyConnection(){

        try {
          cnx = DriverManager.getConnection(url,login,pwd);
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

    public Connection getCnx() {
        return cnx;
    }
}
