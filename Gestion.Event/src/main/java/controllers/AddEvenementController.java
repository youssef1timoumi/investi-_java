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

    private EvenementService evenementService = new EvenementService();
    private Evenement evenementAModifier = null;

    @FXML private TextField contenu, lieu, titre;
    @FXML private DatePicker date_deb, date_fin;
    @FXML private ComboBox<String> id_mentor;
    @FXML private Button btnAction, btnAnnuler;

    @FXML
    void initialize() {
        id_mentor.setItems(FXCollections.observableArrayList("1", "2", "3"));
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
        if (!validerChamps()) return;
        try {
            int idMentor = Integer.parseInt(id_mentor.getValue());
            LocalDateTime dateDebut = date_deb.getValue().atTime(14, 0);
            LocalDateTime dateFin = date_fin.getValue().atTime(17, 0);

            if (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut)) {
                afficherErreur("Dates invalides", "La date de fin doit être après la date de début !");
                return;
            }

            if (evenementAModifier == null) {
                Evenement e = new Evenement(idMentor, titre.getText().trim(),
                        contenu.getText().trim(), lieu.getText().trim(), dateDebut, dateFin);
                evenementService.addEntity(e);
                afficherSucces("Succès", "Événement ajouté !");
            } else {
                evenementAModifier.setTitre(titre.getText().trim());
                evenementAModifier.setContenu(contenu.getText().trim());
                evenementAModifier.setLieu(lieu.getText().trim());
                evenementAModifier.setIdMentor(idMentor);
                evenementAModifier.setDateDebut(dateDebut);
                evenementAModifier.setDateFin(dateFin);
                evenementService.updateEntity(evenementAModifier);
                afficherSucces("Succès", "Événement modifié !");
            }
            retournerALaListe();
        } catch (Exception e) {
            afficherErreur("Erreur", e.getMessage());
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        retournerALaListe();
    }

    private void retournerALaListe() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
            titre.getScene().setRoot(root);
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible de revenir : " + e.getMessage());
        }
    }

    private boolean validerChamps() {
        if (titre.getText() == null || titre.getText().trim().isEmpty()) {
            afficherErreur("Champ vide", "Le titre est obligatoire !"); return false; }
        if (contenu.getText() == null || contenu.getText().trim().isEmpty()) {
            afficherErreur("Champ vide", "Le contenu est obligatoire !"); return false; }
        if (lieu.getText() == null || lieu.getText().trim().isEmpty()) {
            afficherErreur("Champ vide", "Le lieu est obligatoire !"); return false; }
        if (date_deb.getValue() == null) {
            afficherErreur("Champ vide", "La date de début est obligatoire !"); return false; }
        if (date_fin.getValue() == null) {
            afficherErreur("Champ vide", "La date de fin est obligatoire !"); return false; }
        if (date_deb.getValue().isBefore(LocalDate.now())) {
            afficherErreur("Date invalide", "La date ne peut pas être dans le passé !"); return false; }
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