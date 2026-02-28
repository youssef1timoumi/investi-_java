package edu.connexion3a8.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {

    private String url;
    private String login;
    private String pwd;

    private Connection cnx;

    public MyConnection(){
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

    public Connection getCnx() {
        return cnx;
    }
}
