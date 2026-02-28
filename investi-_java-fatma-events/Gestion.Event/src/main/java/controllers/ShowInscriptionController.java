package controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import edu.Investi.entities.Inscription;
import edu.Investi.services.InscriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ShowInscriptionController {

    private final InscriptionService is = new InscriptionService();
    private ObservableList<Inscription> observableList;
    private final edu.Investi.tools.ThemeManager themeManager = edu.Investi.tools.ThemeManager.getInstance();

    @FXML private TableView<Inscription> tabInscription;
    @FXML private TableColumn<Inscription, Integer> colIdUser;
    @FXML private TableColumn<Inscription, Integer> colIdEvenement;
    @FXML private TableColumn<Inscription, String> colStatut;  // ✅ String pour affichage
    @FXML private TableColumn<Inscription, LocalDateTime> colDateInscription;
    @FXML private TableColumn<Inscription, Void> colActions;

    @FXML
    void initialize() {
        colIdUser.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        colIdEvenement.setCellValueFactory(new PropertyValueFactory<>("idEvenement"));
        colDateInscription.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));

        // ✅ Afficher l'enum comme String
        colStatut.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStatut().toString()
                )
        );

        ajouterBoutonsActions();
        refreshData();
        
        // Apply current theme when scene is ready
        tabInscription.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                themeManager.applyTheme(newScene);
            }
        });
    }

    private void ajouterBoutonsActions() {
        Callback<TableColumn<Inscription, Void>, TableCell<Inscription, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<Inscription, Void> call(final TableColumn<Inscription, Void> param) {
                        final TableCell<Inscription, Void> cell = new TableCell<>() {

                            private final Button btnEdit = new Button("✏️ Modifier");
                            private final Button btnDelete = new Button("🗑️ Supprimer");

                            {
                                btnEdit.setStyle("-fx-background-color: #F97316; -fx-text-fill: white; " +
                                        "-fx-cursor: hand; -fx-font-weight: bold;");
                                btnDelete.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; " +
                                        "-fx-cursor: hand; -fx-font-weight: bold;");

                                btnEdit.setOnAction((ActionEvent event) -> {
                                    Inscription inscription = getTableView().getItems().get(getIndex());
                                    modifierInscription(inscription);
                                });

                                btnDelete.setOnAction((ActionEvent event) -> {
                                    Inscription inscription = getTableView().getItems().get(getIndex());
                                    supprimerInscription(inscription);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    HBox hbox = new HBox(10);
                                    hbox.setAlignment(Pos.CENTER);
                                    hbox.getChildren().addAll(btnEdit, btnDelete);
                                    setGraphic(hbox);
                                }
                            }
                        };
                        return cell;
                    }
                };

        colActions.setCellFactory(cellFactory);
    }

    public void refreshData() {
        try {
            List<Inscription> inscriptions = is.getData();
            observableList = FXCollections.observableList(inscriptions);
            tabInscription.setItems(observableList);
        } catch (Exception e) {
            afficherErreur("Erreur", "Erreur de chargement des données", e.getMessage());
        }
    }

    @FXML
    void navAjout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AddInscription.fxml"));
            tabInscription.getScene().setRoot(root);
        } catch (Exception e) {
            afficherErreur("Erreur", "Erreur de navigation !", e.getMessage());
        }
    }

    private void modifierInscription(Inscription inscription) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddInscription.fxml"));
            Parent root = loader.load();

            AddInscriptionController controller = loader.getController();
            controller.setInscriptionAModifier(inscription);

            tabInscription.getScene().setRoot(root);

        } catch (Exception e) {
            afficherErreur("Erreur", "Erreur lors de la modification", e.getMessage());
        }
    }

    private void supprimerInscription(Inscription inscription) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("⚠️ Confirmation de suppression");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer cette inscription ?");
        confirmation.setContentText("Utilisateur ID : " + inscription.getIdUtilisateur() +
                "\nÉvénement ID : " + inscription.getIdEvenement() +
                "\nStatut : " + inscription.getStatut());

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                is.deleteEntity(inscription.getIdInscription());
                refreshData();
                afficherSucces("Succès", "Inscription supprimée avec succès !");

            } catch (Exception e) {
                afficherErreur("Erreur", "Impossible de supprimer l'inscription", e.getMessage());
            }
        } else {
            System.out.println("❌ Suppression annulée");
        }
    }

    private void afficherErreur(String titre, String header, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(header);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private void afficherSucces(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}