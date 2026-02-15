package edu.Investi.services;

import edu.Investi.entities.Evenement;
import edu.Investi.entities.Inscription;
import edu.Investi.interfaces.IService;
import edu.Investi.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InscriptionService implements IService<Inscription> {
    @Override
    public void addEntity(Inscription inscription) throws SQLException {
        String requete = "INSERT INTO inscription (id_user, id_evenement, statut) " +
                "VALUES (?, ?, ?)";

        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, inscription.getIdUtilisateur());
        st.setInt(2, inscription.getIdEvenement());
        st.setString(3, inscription.getStatut());

        st.executeUpdate();
        System.out.println("Inscription ajoutée avec succès !");
    }

    @Override
    public void deleteEntity(int id) throws SQLException {
        Inscription existing= getById(id);
        if (existing == null) {
            System.out.println(" NON TROUVÉ: Aucun événement avec ID " + id);
            return;
        }
        String requete = "DELETE FROM inscription WHERE id_inscription = ?";
        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, id);

        st.executeUpdate();
        System.out.println("Inscription supprimée avec succès !");
    }

    @Override
    public void updateEntity(Inscription inscription) throws SQLException {
        Inscription existing= getById(inscription.getIdInscription());
        if (existing == null) {
            System.out.println(" NON TROUVÉ: Aucun événement avec ID " + inscription.getIdInscription());
            return;
        }
        String requete = "UPDATE inscription SET statut=? WHERE id_inscription=?";

        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setString(1, inscription.getStatut());
        st.setInt(2, inscription.getIdInscription());

        st.executeUpdate();
        System.out.println("Inscription modifiée avec succès !");
    }

    @Override
    public List<Inscription> getData() throws SQLException {
        List<Inscription> inscriptions = new ArrayList<>();
        String requete = "SELECT * FROM inscription";

        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(requete);

        while (rs.next()) {
            Inscription i = new Inscription();
            i.setIdInscription(rs.getInt("id_inscription"));
            i.setIdUtilisateur(rs.getInt("id_user"));
            i.setIdEvenement(rs.getInt("id_evenement"));
            i.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
            i.setStatut(rs.getString("statut"));

            inscriptions.add(i);
        }

        return inscriptions;
    }

    @Override
    public Inscription getById(int id) throws SQLException {
        String requete = "SELECT * FROM inscription WHERE id_inscription = ?";
        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            Inscription i = new Inscription();
            i.setIdInscription(rs.getInt("id_inscription"));
            i.setIdUtilisateur(rs.getInt("id_user"));
            i.setIdEvenement(rs.getInt("id_evenement"));
            i.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
            i.setStatut(rs.getString("statut"));
            return i;
        }

        return null;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String requete = "SELECT 1 FROM inscription WHERE id_inscription = ?";
        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public List<Inscription> getInscriptionsByEvenement(int idEvenement) throws SQLException {
        List<Inscription> inscriptions = new ArrayList<>();
        String requete = "SELECT * FROM inscription WHERE id_evenement = ?";

        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, idEvenement);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Inscription i = new Inscription();
            i.setIdInscription(rs.getInt("id_inscription"));
            i.setIdUtilisateur(rs.getInt("id_user"));
            i.setIdEvenement(rs.getInt("id_evenement"));
            i.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
            i.setStatut(rs.getString("statut"));

            inscriptions.add(i);
        }

        return inscriptions;
    }

    public List<Inscription> getInscriptionsByUtilisateur(int idUtilisateur) throws SQLException {
        List<Inscription> inscriptions = new ArrayList<>();
        String requete = "SELECT * FROM inscription WHERE id_utilisateur = ?";

        PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(requete);
        st.setInt(1, idUtilisateur);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Inscription i = new Inscription();
            i.setIdInscription(rs.getInt("id_inscription"));
            i.setIdUtilisateur(rs.getInt("id_user"));
            i.setIdEvenement(rs.getInt("id_evenement"));
            i.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
            i.setStatut(rs.getString("statut"));

            inscriptions.add(i);
        }

        return inscriptions;
    }
}
