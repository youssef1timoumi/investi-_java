package controllers;

import edu.Investi.entities.Inscription;
import edu.Investi.interfaces.statut;
import edu.Investi.services.InscriptionService;
import edu.Investi.services.EvenementService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;

public class AddInscriptionController {

    private InscriptionService inscriptionService = new InscriptionService();
    private EvenementService evenementService = new EvenementService();
    private Inscription inscriptionAModifier = null;
    private final edu.Investi.tools.ThemeManager themeManager = edu.Investi.tools.ThemeManager.getInstance();

    @FXML private ComboBox<String> cbUtilisateur, cbEvenement, cbStatut;
    @FXML private Button btnAction, btnAnnuler;

    @FXML
    void initialize() {
        cbUtilisateur.setItems(FXCollections.observableArrayList(
                "1","2","3","4","5","6","7","8","9"));
        cbUtilisateur.setValue("1");
        chargerEvenements();
        cbStatut.setItems(FXCollections.observableArrayList(
                "CONFIRME", "EN_ATTENTE", "ANNULE"));
        cbStatut.setValue("CONFIRME");
        btnAction.setText("Ajouter");
        
        // Apply current theme when scene is ready
        btnAction.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                themeManager.applyTheme(newScene);
            }
        });
    }

    private void chargerEvenements() {
        try {
            List<String> list = evenementService.getEvenementsForComboBox();
            if (list.isEmpty()) {
                cbEvenement.setItems(FXCollections.observableArrayList("1 - Aucun"));
                cbEvenement.setValue("1 - Aucun");
            } else {
                cbEvenement.setItems(FXCollections.observableArrayList(list));
                cbEvenement.setValue(list.get(0));
            }
        } catch (Exception e) {
            cbEvenement.setItems(FXCollections.observableArrayList("1","2","3"));
            cbEvenement.setValue("1");
        }
    }

    private statut convertirStringVersEnum(String s) {
        switch (s) {
            case "EN_ATTENTE": return statut.EN_ATTENTE;
            case "ANNULE": return statut.ANNULE;
            default: return statut.CONFIRME;
        }
    }

    public void setInscriptionAModifier(Inscription inscription) {
        this.inscriptionAModifier = inscription;
        cbUtilisateur.setValue(String.valueOf(inscription.getIdUtilisateur()));
        String idEvent = String.valueOf(inscription.getIdEvenement());
        for (String item : cbEvenement.getItems()) {
            if (item.startsWith(idEvent + " - ")) {
                cbEvenement.setValue(item); break;
            }
        }
        cbStatut.setValue(inscription.getStatut());
        btnAction.setText("Modifier");
    }

    @FXML
    void ajouterInscription(ActionEvent event) {
        if (!validerChamps()) return;
        try {
            int idUser = Integer.parseInt(cbUtilisateur.getValue());
            int idEvent = Integer.parseInt(cbEvenement.getValue().split(" - ")[0]);
            String statStr = cbStatut.getValue();
            statut statEnum = convertirStringVersEnum(statStr);

            if (inscriptionAModifier == null) {
                if (dejaInscrit(idUser, idEvent)) {
                    afficherErreur("Doublon", "Cet utilisateur est déjà inscrit à cet événement !"); return;
                }
                inscriptionService.addEntity(new Inscription(idUser, idEvent, statEnum));
                afficherSucces("Succès", "Inscription ajoutée !");
            } else {
                inscriptionAModifier.setIdUtilisateur(idUser);
                inscriptionAModifier.setIdEvenement(idEvent);
                inscriptionAModifier.setStatut(statStr);
                inscriptionService.updateEntity(inscriptionAModifier);
                afficherSucces("Succès", "Inscription modifiée !");
            }
            retournerALaListe();
        } catch (Exception e) {
            afficherErreur("Erreur", e.getMessage());
        }
    }

    private boolean dejaInscrit(int idUser, int idEvent) {
        try {
            List<Inscription> list = inscriptionService.getData();
            if (inscriptionAModifier != null) {
                return list.stream().anyMatch(i ->
                        i.getIdUtilisateur() == idUser && i.getIdEvenement() == idEvent &&
                                i.getIdInscription() != inscriptionAModifier.getIdInscription());
            }
            return list.stream().anyMatch(i ->
                    i.getIdUtilisateur() == idUser && i.getIdEvenement() == idEvent);
        } catch (SQLException e) { return false; }
    }

    @FXML
    void annuler(ActionEvent event) { retournerALaListe(); }

    private void retournerALaListe() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
            cbUtilisateur.getScene().setRoot(root);
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible de revenir : " + e.getMessage());
        }
    }

    private boolean validerChamps() {
        if (cbUtilisateur.getValue() == null || cbUtilisateur.getValue().isEmpty()) {
            afficherErreur("Champ vide", "Sélectionnez un utilisateur !"); return false; }
        if (cbEvenement.getValue() == null || cbEvenement.getValue().isEmpty()) {
            afficherErreur("Champ vide", "Sélectionnez un événement !"); return false; }
        if (cbStatut.getValue() == null || cbStatut.getValue().isEmpty()) {
            afficherErreur("Champ vide", "Sélectionnez un statut !"); return false; }
        return true;
    }

    private void afficherErreur(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titre); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private void afficherSucces(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titre); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}