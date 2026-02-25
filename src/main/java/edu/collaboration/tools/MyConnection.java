package edu.collaboration.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private String url = "jdbc:mysql://localhost:3306/pi";
    private String login = "root";
    private String pwd = "";
    private Connection cnx;
    public static MyConnection instance;

    private MyConnection() {
        try {
            cnx = DriverManager.getConnection(url, login, pwd);
            System.out.println("Connexion établie!");

            // Auto-patch Database constraints that shouldn't be unique
            try (java.sql.Statement st = cnx.createStatement()) {
                java.sql.ResultSet rs = st.executeQuery(
                        "SHOW INDEX FROM investment WHERE Column_name = 'project_id' AND Non_unique = 0 AND Key_name != 'PRIMARY'");
                java.util.List<String> indexesToDrop = new java.util.ArrayList<>();
                while (rs.next()) {
                    indexesToDrop.add(rs.getString("Key_name"));
                }
                for (String idx : indexesToDrop) {
                    st.executeUpdate("ALTER TABLE investment DROP INDEX " + idx);
                    System.out.println("Patched DB: Dropped unique index " + idx + " on investment table.");
                }

                // Auto-patch missing Idea 5 columns
                String[] newCols = {
                        "ALTER TABLE investment ADD COLUMN progressPercentage int(11) DEFAULT 0",
                        "ALTER TABLE investment ADD COLUMN latestProgressLog varchar(255) DEFAULT NULL",
                        "ALTER TABLE investment ADD COLUMN paymentMonthsCompleted int(11) DEFAULT 0"
                };
                for (String q : newCols) {
                    try {
                        st.executeUpdate(q);
                        System.out.println("Patched DB: Added missing column");
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
                // Ignore silent db patch errors
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getCnx() {
        return cnx;
    }

    public static MyConnection getInstance() {
        if (instance == null)
            instance = new MyConnection();
        return instance;
    }
}
