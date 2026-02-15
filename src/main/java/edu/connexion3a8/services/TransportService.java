package edu.connexion3a8.services;

import edu.connexion3a8.entities.Transport;
import edu.connexion3a8.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransportService {
    private Connection connection;

    public TransportService() {
        this.connection = new MyConnection().getCnx();
    }

    public void addTransport(Transport transport) throws SQLException {
        String query = "INSERT INTO Transport (idType) VALUES (?)";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, transport.getIdType());
            pst.executeUpdate();
            System.out.println("Transport added successfully!");
        }
    }

    public void updateTransport(int idTransport, Transport transport) throws SQLException {
        String query = "UPDATE Transport SET idType=? WHERE idTransport=?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, transport.getIdType());
            pst.setInt(2, idTransport);
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transport updated successfully!");
            } else {
                System.out.println("Transport not found!");
            }
        }
    }

    public void deleteTransport(int idTransport) throws SQLException {
        String query = "DELETE FROM Transport WHERE idTransport=?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, idTransport);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transport deleted successfully!");
            } else {
                System.out.println("Transport not found!");
            }
        }
    }

    public Transport getTransportById(int idTransport) throws SQLException {
        String query = "SELECT t.*, tt.libelle as typeLibelle " +
                      "FROM Transport t " +
                      "LEFT JOIN TypeTransport tt ON t.idType = tt.idType " +
                      "WHERE t.idTransport=?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, idTransport);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return extractTransportFromResultSet(rs);
            }
        }
        return null;
    }

    public List<Transport> getAllTransports() throws SQLException {
        List<Transport> transports = new ArrayList<>();
        String query = "SELECT t.*, tt.libelle as typeLibelle " +
                      "FROM Transport t " +
                      "LEFT JOIN TypeTransport tt ON t.idType = tt.idType " +
                      "ORDER BY t.idTransport";
        
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                transports.add(extractTransportFromResultSet(rs));
            }
        }
        return transports;
    }

    public List<Transport> getTransportsByType(int idType) throws SQLException {
        List<Transport> transports = new ArrayList<>();
        String query = "SELECT t.*, tt.libelle as typeLibelle " +
                      "FROM Transport t " +
                      "LEFT JOIN TypeTransport tt ON t.idType = tt.idType " +
                      "WHERE t.idType=? " +
                      "ORDER BY t.idTransport";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, idType);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                transports.add(extractTransportFromResultSet(rs));
            }
        }
        return transports;
    }

    private Transport extractTransportFromResultSet(ResultSet rs) throws SQLException {
        Transport transport = new Transport();
        transport.setIdTransport(rs.getInt("idTransport"));
        transport.setIdType(rs.getInt("idType"));
        transport.setTypeLibelle(rs.getString("typeLibelle"));
        return transport;
    }
}
