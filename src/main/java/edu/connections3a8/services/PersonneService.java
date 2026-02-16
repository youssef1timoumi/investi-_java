package edu.connections3a8.services;

import edu.connections3a8.interfaces.Iservice;
import edu.connections3a8.tools.MyConnection;
import edu.connections3a8.entities.Personne;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonneService implements Iservice<Personne> {

    private final Connection cnx;

    public PersonneService() {
        this.cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void addEntity(Personne entity) throws SQLException {
        String sql = "INSERT INTO personne (nom, prenom) VALUES (?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, entity.getNom());
            pst.setString(2, entity.getPrenom());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                        System.out.println("Personne added with ID: " + entity.getId());
                    }
                }
            }
        }
        // Note: connection is NOT closed here → managed by singleton
    }

    // Variant with full control (sometimes used for transactions or special cases)
    @Override
    public void addEntity2(Personne entity) throws SQLException {
        String sql = "INSERT INTO personne (nom, prenom) VALUES (?, ?)";

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, entity.getNom());
            pst.setString(2, entity.getPrenom());

            pst.executeUpdate();

            rs = pst.getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getInt(1));
            }
        } finally {
            // We close only ResultSet & PreparedStatement — NOT the connection!
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (pst != null) try { pst.close(); } catch (SQLException ignored) {}
        }
    }

    @Override
    public void deleteEntity(Personne entity) {
        if (entity.getId() <= 0) {
            System.out.println("Cannot delete: invalid ID");
            return;
        }

        String sql = "DELETE FROM personne WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, entity.getId());
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Personne with ID " + entity.getId() + " deleted.");
            } else {
                System.out.println("No personne found with ID " + entity.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error deleting personne: " + e.getMessage());
        }
    }

    // Most common version: update by known ID
    @Override
    public void updateEntity(Personne entity, int id) {
        String sql = "UPDATE personne SET nom = ?, prenom = ? WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, entity.getNom());
            pst.setString(2, entity.getPrenom());
            pst.setInt(3, id);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                // Optional: update the object ID if someone passed wrong one
                entity.setId(id);
                System.out.println("Personne with ID " + id + " updated.");
            } else {
                System.out.println("No personne found with ID " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error updating personne: " + e.getMessage());
        }
    }

    @Override
    public List<Personne> getData() {
        List<Personne> personnes = new ArrayList<>();

        String sql = "SELECT id, nom, prenom FROM personne ORDER BY id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Personne p = new Personne();
                p.setId(rs.getInt("id"));
                p.setNom(rs.getString("nom"));
                p.setPrenom(rs.getString("prenom"));
                personnes.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving personnes: " + e.getMessage());
        }

        return personnes;
    }

    // ─────────────────────────────────────────────
    // Bonus: very useful method not in interface
    // ─────────────────────────────────────────────
    public Personne getById(int id) {
        String sql = "SELECT id, nom, prenom FROM personne WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Personne p = new Personne();
                    p.setId(rs.getInt("id"));
                    p.setNom(rs.getString("nom"));
                    p.setPrenom(rs.getString("prenom"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching personne by ID: " + e.getMessage());
        }
        return null;
    }
}