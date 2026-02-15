package edu.Investi.services;

import edu.Investi.entities.Evenement;
import edu.Investi.interfaces.IService;
import edu.Investi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {
    @Override
    public void addEntity(Evenement evenement) throws SQLException {
        String requete = "INSERT INTO evenement (id_mentor, titre, contenu, lieu, date_debut, date_fin) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, evenement.getIdMentor());
        st.setString(2, evenement.getTitre());
        st.setString(3, evenement.getContenu());
        st.setString(4, evenement.getLieu());
        st.setTimestamp(5, Timestamp.valueOf(evenement.getDateDebut()));
        st.setTimestamp(6, Timestamp.valueOf(evenement.getDateFin()));

        st.executeUpdate();
        System.out.println("Événement ajouté avec succès !");
    }

    @Override
    public void deleteEntity(int id) throws SQLException {
        Evenement existing = getById(id);
        if (existing == null) {
            System.out.println(" NON TROUVÉ: Aucun événement avec ID " + id);
            return;
        }
        String requete = "DELETE FROM evenement WHERE id_evenement = ?";
        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, id);

        st.executeUpdate();
        System.out.println("Événement supprimé avec succès !");
    }

    @Override
    public void updateEntity(Evenement evenement) throws SQLException {
        Evenement existing = getById(evenement.getIdEvenement());
        if (existing == null) {
            System.out.println(" NON TROUVÉ: Aucun événement avec ID " + evenement.getIdEvenement());
            return;
        }
        String requete = "UPDATE evenement SET id_mentor=?, titre=?, contenu=?, lieu=?, " +
                "date_debut=?, date_fin=? WHERE id_evenement=?";

        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, evenement.getIdMentor());
        st.setString(2, evenement.getTitre());
        st.setString(3, evenement.getContenu());
        st.setString(4, evenement.getLieu());
        st.setTimestamp(5, Timestamp.valueOf(evenement.getDateDebut()));
        st.setTimestamp(6, Timestamp.valueOf(evenement.getDateFin()));
        st.setInt(7, evenement.getIdEvenement());

        st.executeUpdate();
        System.out.println("Événement modifié avec succès !");
    }

    @Override
    public List<Evenement> getData() throws SQLException {
        List<Evenement> evenements = new ArrayList<>();
        String requete = "SELECT * FROM evenement";

        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(requete);

        while (rs.next()) {
            Evenement e = new Evenement();
            e.setIdEvenement(rs.getInt("id_evenement"));
            e.setIdMentor(rs.getInt("id_mentor"));
            e.setTitre(rs.getString("titre"));
            e.setContenu(rs.getString("contenu"));
            e.setLieu(rs.getString("lieu"));
            e.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());
            e.setDateFin(rs.getTimestamp("date_fin").toLocalDateTime());

            evenements.add(e);
        }

        return evenements;
    }

    @Override
    public Evenement getById(int id) throws SQLException {
        String requete = "SELECT * FROM evenement WHERE id_evenement = ?";
        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            Evenement e = new Evenement();
            e.setIdEvenement(rs.getInt("id_evenement"));
            e.setIdMentor(rs.getInt("id_mentor"));
            e.setTitre(rs.getString("titre"));
            e.setContenu(rs.getString("contenu"));
            e.setLieu(rs.getString("lieu"));
            e.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());
            e.setDateFin(rs.getTimestamp("date_fin").toLocalDateTime());
            return e;
        }
        System.out.println("INTROUVABLE");
        return null;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String requete = "SELECT 1 FROM evenement WHERE id_evenement = ?";
        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return true;   // Trouvé !
        } else {
            return false;  // Pas trouvé
        }
    }

    public List<Evenement> getEvenementsByMentor(int idMentor) throws SQLException {
        List<Evenement> evenements = new ArrayList<>();
        String requete = "SELECT * FROM evenement WHERE id_mentor = ?";

        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, idMentor);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Evenement e = new Evenement();
            e.setIdEvenement(rs.getInt("id_evenement"));
            e.setIdMentor(rs.getInt("id_mentor"));
            e.setTitre(rs.getString("titre"));
            e.setContenu(rs.getString("contenu"));
            e.setLieu(rs.getString("lieu"));
            e.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());
            e.setDateFin(rs.getTimestamp("date_fin").toLocalDateTime());

            evenements.add(e);
        }

        return evenements;
    }

    public List<String> getEvenementsForComboBox() throws SQLException {
        List<String> evenements = new ArrayList<>();
        String requete = "SELECT id_evenement, titre FROM evenement ORDER BY id_evenement";

        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(requete);

        while (rs.next()) {
            int id = rs.getInt("id_evenement");
            String titre = rs.getString("titre");
            evenements.add(id + " - " + titre);
        }

        return evenements;
    }
}
