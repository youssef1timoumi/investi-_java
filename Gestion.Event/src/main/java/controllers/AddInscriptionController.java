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

    @FXML private ComboBox<String> cbUtilisateur;
    @FXML private ComboBox<String> cbEvenement;
    @FXML private ComboBox<String> cbStatut;
    @FXML private Button btnAction;
    @FXML private Button btnAnnuler;

    @FXML
    void initialize() {
        System.out.println("✅ Initialisation AddInscriptionController...");


        cbUtilisateur.setItems(FXCollections.observableArrayList(
                "1", "2", "3"
        ));
        cbUtilisateur.setValue("1");


        chargerEvenements();

        cbStatut.setItems(FXCollections.observableArrayList(
                "CONFIRME",
                "EN_ATTENTE",
                "ANNULE"
        ));
        cbStatut.setValue("CONFIRME");

        btnAction.setText("➕ Ajouter");
    }

    private void chargerEvenements() {
        try {
            List<String> evenements = evenementService.getEvenementsForComboBox();

            if (evenements.isEmpty()) {
                cbEvenement.setItems(FXCollections.observableArrayList("1 - Aucun"));
                cbEvenement.setValue("1 - Aucun");
            } else {
                cbEvenement.setItems(FXCollections.observableArrayList(evenements));
                cbEvenement.setValue(evenements.get(0));
            }
        } catch (Exception e) {
            cbEvenement.setItems(FXCollections.observableArrayList("1", "2", "3"));
            cbEvenement.setValue("1");
        }
    }

    // Convertir String → enum
    private statut convertirStringVersEnum(String s) {
        switch (s) {
            case "CONFIRME":
                return statut.CONFIRME;
            case "EN_ATTENTE":
                return statut.EN_ATTENTE;
            case "ANNULE":
                return statut.ANNULE;
            default:
                return statut.CONFIRME;
        }
    }

    public void setInscriptionAModifier(Inscription inscription) {
        this.inscriptionAModifier = inscription;

        cbUtilisateur.setValue(String.valueOf(inscription.getIdUtilisateur()));

        String idEvent = String.valueOf(inscription.getIdEvenement());
        for (String item : cbEvenement.getItems()) {
            if (item.startsWith(idEvent + " - ")) {
                cbEvenement.setValue(item);
                break;
            }
        }

        String statutBDD = inscription.getStatut();
        cbStatut.setValue(statutBDD);
        btnAction.setText("Modifier");
    }

    @FXML
    void ajouterInscription(ActionEvent event) {
        if (!validerChamps()) {
            return;
        }

        try {
            int idUser = Integer.parseInt(cbUtilisateur.getValue());
            int idEvent = Integer.parseInt(cbEvenement.getValue().split(" - ")[0]);

            // String du ComboBox et convertir en enum
            String statutString = cbStatut.getValue();
            statut statutEnum = convertirStringVersEnum(statutString);

            if (inscriptionAModifier == null) {
                // ========== MODE AJOUT ==========
                if (dejaInscrit(idUser, idEvent)) {
                    afficherErreur("Inscription existante",
                            "Cet utilisateur est déjà inscrit à cet événement !");
                    return;
                }

                Inscription inscription = new Inscription(idUser, idEvent, statutEnum);
                inscriptionService.addEntity(inscription);
                afficherSucces("Succès", "Inscription ajoutée avec succès !");

            } else {
                // ========== MODE MODIFICATION ==========
                inscriptionAModifier.setIdUtilisateur(idUser);
                inscriptionAModifier.setIdEvenement(idEvent);
                inscriptionAModifier.setStatut(statutString);

                inscriptionService.updateEntity(inscriptionAModifier);
                afficherSucces("Succès", "Inscription modifiée avec succès !");
            }

            retournerALaListe();

        } catch (Exception e) {
            afficherErreur("Erreur", e.getMessage());
        }
    }

    private boolean dejaInscrit(int idUser, int idEvent) {
        try {
            List<Inscription> inscriptions = inscriptionService.getData();

            if (inscriptionAModifier != null) {
                return inscriptions.stream()
                        .anyMatch(i -> i.getIdUtilisateur() == idUser &&
                                i.getIdEvenement() == idEvent &&
                                i.getIdInscription() != inscriptionAModifier.getIdInscription());
            }

            return inscriptions.stream()
                    .anyMatch(i -> i.getIdUtilisateur() == idUser &&
                            i.getIdEvenement() == idEvent);
        } catch (SQLException e) {
            return false;
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        retournerALaListe();
    }

    private void retournerALaListe() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ShowInscription.fxml"));
            cbUtilisateur.getScene().setRoot(root);
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible de revenir à la liste");
        }
    }

    private boolean validerChamps() {
        if (cbUtilisateur.getValue() == null || cbUtilisateur.getValue().isEmpty()) {
            afficherErreur("Champ vide", "Sélectionnez un utilisateur !");
            return false;
        }
        if (cbEvenement.getValue() == null || cbEvenement.getValue().isEmpty()) {
            afficherErreur("Champ vide", "Sélectionnez un événement !");
            return false;
        }
        if (cbStatut.getValue() == null || cbStatut.getValue().isEmpty()) {
            afficherErreur("Champ vide", "Sélectionnez un statut !");
            return false;
        }
        return true;
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}