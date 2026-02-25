package edu.collaboration;

import edu.collaboration.tools.MyConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PrintSchema {
    public static void main(String[] args) {
        try {
            Connection cnx = MyConnection.getInstance().getCnx();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery("SHOW CREATE TABLE investment");
            if (rs.next()) {
                System.out.println(rs.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
