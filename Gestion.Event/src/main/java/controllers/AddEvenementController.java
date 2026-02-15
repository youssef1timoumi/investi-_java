package controllers;

import edu.Investi.entities.Evenement;
import edu.Investi.services.EvenementService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddEvenementController {
    EvenementService evenementService = new EvenementService();
    private Evenement evenementAModifier = null;

    @FXML private TextField contenu;
    @FXML private DatePicker date_deb;
    @FXML private DatePicker date_fin;
    @FXML private ComboBox<String> id_mentor;
    @FXML private TextField lieu;
    @FXML private TextField titre;
    @FXML private Button btnAction;
    @FXML private Button btnAnnuler;


    @FXML
    void initialize() {
        // Peupler le ComboBox
        id_mentor.setItems(FXCollections.observableArrayList(
                "1", "2", "3"  // IDs des mentors
        ));
        id_mentor.setValue("1");
        btnAction.setText("Ajouter");
    }

    public void setEvenementAModifier(Evenement evenement) {
        this.evenementAModifier = evenement;

        titre.setText(evenement.getTitre());
        contenu.setText(evenement.getContenu());
        lieu.setText(evenement.getLieu());
        date_deb.setValue(evenement.getDateDebut().toLocalDate());
        date_fin.setValue(evenement.getDateFin().toLocalDate());
        id_mentor.setValue(String.valueOf(evenement.getIdMentor()));

        btnAction.setText("Modifier");
    }

    @FXML
    void add_evenement(ActionEvent event) {
        if (!validerChamps()) {return;}
        try {
            int idMentor = Integer.parseInt(id_mentor.getValue());
            LocalDateTime dateDebut = date_deb.getValue().atTime(14, 0);
            LocalDateTime dateFin = date_fin.getValue().atTime(17, 0);

            if (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut)) {
                afficherErreur("Dates invalides", "La date de fin doit être après la date de début !");
                return;
            }

            if (evenementAModifier == null) {
                // ========== MODE AJOUT ==========
                Evenement evenement = new Evenement(
                        idMentor,
                        titre.getText(),
                        contenu.getText(),
                        lieu.getText(),
                        dateDebut,
                        dateFin
                );
                evenementService.addEntity(evenement);
                afficherSucces("Succès", "Événement ajouté avec succès !");

            } else {
                // ========== MODE MODIFICATION ==========
                evenementAModifier.setTitre(titre.getText().trim());
                evenementAModifier.setContenu(contenu.getText().trim());
                evenementAModifier.setLieu(lieu.getText().trim());
                evenementAModifier.setIdMentor(idMentor);
                evenementAModifier.setDateDebut(dateDebut);
                evenementAModifier.setDateFin(dateFin);

                evenementService.updateEntity(evenementAModifier);
                afficherSucces("Succès", "Événement modifié avec succès !");
            }
            retournerALaListe();
        } catch (Exception e) {
            afficherErreur("Erreur", e.getMessage());
        }
    }

    // ========== ANNULER ==========
    @FXML
    void annuler(ActionEvent event) {
        retournerALaListe();
    }

    // ========== RETOURNER À LA LISTE ==========
    private void retournerALaListe() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ShowEvenement.fxml"));
            titre.getScene().setRoot(root);
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible de revenir à la liste : " + e.getMessage());
        }
    }

    private boolean validerChamps() {
        if (titre.getText() == null || titre.getText().isEmpty()) {
            afficherErreur("Champ vide", "Le titre est obligatoire !");
            return false;
        }
        if (contenu.getText() == null || contenu.getText().isEmpty()) {
            afficherErreur("Champ vide", "Le contenu est obligatoire !");
            return false;
        }
        if (lieu.getText() == null || lieu.getText().isEmpty()) {
            afficherErreur("Champ vide", "Le lieu est obligatoire !");
            return false;
        }
        if (date_deb.getValue() == null) {
            afficherErreur("Champ vide", "La date de début est obligatoire !");
            return false;
        }
        if (date_fin.getValue() == null) {
            afficherErreur("Champ vide", "La date de fin est obligatoire !");
            return false;
        }
        if (date_deb.getValue().isBefore(LocalDate.now())) {
            afficherErreur("Date invalide", "La date ne peut pas être dans le passé !");
            return false;
        }
        return true;
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

