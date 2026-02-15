package edu.connexion3a8.services;

import edu.connexion3a8.entities.TypeTransport;
import edu.connexion3a8.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeTransportService {
    private Connection connection;

    public TypeTransportService() {
        this.connection = new MyConnection().getCnx();
    }

    public void addTypeTransport(TypeTransport typeTransport) throws SQLException {
        String query = "INSERT INTO TypeTransport (libelle) VALUES (?)";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, typeTransport.getLibelle());
            pst.executeUpdate();
            System.out.println("TypeTransport added successfully!");
        }
    }

    public void updateTypeTransport(int idType, TypeTransport typeTransport) throws SQLException {
        String query = "UPDATE TypeTransport SET libelle=? WHERE idType=?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, typeTransport.getLibelle());
            pst.setInt(2, idType);
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("TypeTransport updated successfully!");
            } else {
                System.out.println("TypeTransport not found!");
            }
        }
    }

    public void deleteTypeTransport(int idType) throws SQLException {
        String query = "DELETE FROM TypeTransport WHERE idType=?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, idType);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("TypeTransport deleted successfully!");
            } else {
                System.out.println("TypeTransport not found!");
            }
        }
    }

    public TypeTransport getTypeTransportById(int idType) throws SQLException {
        String query = "SELECT * FROM TypeTransport WHERE idType=?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, idType);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return extractTypeTransportFromResultSet(rs);
            }
        }
        return null;
    }

    public List<TypeTransport> getAllTypeTransports() throws SQLException {
        List<TypeTransport> typeTransports = new ArrayList<>();
        String query = "SELECT * FROM TypeTransport ORDER BY libelle";
        
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                typeTransports.add(extractTypeTransportFromResultSet(rs));
            }
        }
        return typeTransports;
    }

    private TypeTransport extractTypeTransportFromResultSet(ResultSet rs) throws SQLException {
        TypeTransport typeTransport = new TypeTransport();
        typeTransport.setIdType(rs.getInt("idType"));
        typeTransport.setLibelle(rs.getString("libelle"));
        return typeTransport;
    }
}
