package controllers;

import edu.Investi.entities.Evenement;
import edu.Investi.services.EvenementService;
import edu.Investi.tools.GeocodingService;
import edu.Investi.tools.EventImageGenerator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import edu.Investi.tools.FileOpener;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class AddEvenementController {

    private EvenementService evenementService = new EvenementService();
    private Evenement evenementAModifier = null;
    private final edu.Investi.tools.ThemeManager themeManager = edu.Investi.tools.ThemeManager.getInstance();

    @FXML
    private TextField contenu, lieu, titre;
    @FXML
    private DatePicker date_deb, date_fin;
    @FXML
    private ComboBox<String> id_mentor;
    @FXML
    private Button btnAction, btnAnnuler, btnGenerateImage;
    @FXML
    private Label lblGeoStatus, lblImageStatus;
    @FXML
    private WebView mapWebView;
    @FXML
    private javafx.scene.image.ImageView imagePreview;
    @FXML
    private javafx.scene.layout.VBox imagePreviewContainer;
    
    private WebEngine webEngine;
    private Double selectedLat, selectedLon;
    private String generatedImagePath;

    @FXML
    void initialize() {
        id_mentor.setItems(FXCollections.observableArrayList("1", "2", "3", "5", "6"));
        id_mentor.setValue("1");
        btnAction.setText("Ajouter");

        setupMap();

        // Apply current theme when scene is ready
        btnAction.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                themeManager.applyTheme(newScene);
            }
        });
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

        if (evenement.getLieuLatitude() != null && evenement.getLieuLongitude() != null) {
            this.selectedLat = evenement.getLieuLatitude();
            this.selectedLon = evenement.getLieuLongitude();
            if (lblGeoStatus != null) {
                lblGeoStatus.setText("✓ Géolocalisé");
                lblGeoStatus.setStyle("-fx-text-fill: green;");
            }
            // Update map if it's already loaded
            updateMapLocation(selectedLat, selectedLon, evenement.getLieu());
        }
    }

    private void setupMap() {
        webEngine = mapWebView.getEngine();
        webEngine.load(getClass().getResource("/map.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new MapBridge());

                // If we are modifying, show current location
                if (selectedLat != null && selectedLon != null) {
                    updateMapLocation(selectedLat, selectedLon, lieu.getText());
                }
            }
        });
    }

    private void updateMapLocation(double lat, double lon, String address) {
        if (webEngine != null && webEngine.getLoadWorker().getState() == javafx.concurrent.Worker.State.SUCCEEDED) {
            webEngine.executeScript(
                    String.format("setLocation(%.6f, %.6f, '%s')", lat, lon, address.replace("'", "\\'")));
        }
    }

    public class MapBridge {
        public void onLocationSelected(double lat, double lon, String address) {
            javafx.application.Platform.runLater(() -> {
                selectedLat = lat;
                selectedLon = lon;
                lieu.setText(address);
                if (lblGeoStatus != null) {
                    lblGeoStatus.setText("✓ Sélectionné sur carte");
                    lblGeoStatus.setStyle("-fx-text-fill: green;");
                }
            });
        }
    }

    @FXML
    void add_evenement(ActionEvent event) {
        if (!validerChamps())
            return;
        try {
            int idMentor = Integer.parseInt(id_mentor.getValue());
            LocalDateTime dateDebut = date_deb.getValue().atTime(14, 0);
            LocalDateTime dateFin = date_fin.getValue().atTime(17, 0);

            if (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut)) {
                afficherErreur("Dates invalides", "La date de fin doit être après la date de début !");
                return;
            }

            // Use selected coordinates if available, otherwise fallback to geocoding
            // current text
            Double latitude = selectedLat;
            Double longitude = selectedLon;

            if (latitude == null || longitude == null) {
                GeocodingService.GeoLocation geoLoc = GeocodingService.geocode(lieu.getText().trim());
                if (geoLoc != null) {
                    latitude = geoLoc.latitude;
                    longitude = geoLoc.longitude;
                }
            }

            if (latitude != null) {
                System.out.println("Lieu: " + latitude + ", " + longitude);
            }

            // Generate event image
            String imagePath = "events/event_" + System.currentTimeMillis() + ".png";
            EventImageGenerator.generateEventImage(
                    titre.getText().trim(),
                    lieu.getText().trim(),
                    dateDebut,
                    imagePath);

            if (evenementAModifier == null) {
                Evenement e = new Evenement(idMentor, titre.getText().trim(),
                        contenu.getText().trim(), lieu.getText().trim(),
                        latitude, longitude, imagePath,
                        dateDebut, dateFin);
                evenementService.addEntity(e);
                afficherSucces("Succès", "Événement ajouté avec image et géolocalisation !");
            } else {
                evenementAModifier.setTitre(titre.getText().trim());
                evenementAModifier.setContenu(contenu.getText().trim());
                evenementAModifier.setLieu(lieu.getText().trim());
                evenementAModifier.setLieuLatitude(latitude);
                evenementAModifier.setLieuLongitude(longitude);
                evenementAModifier.setImageUrl(imagePath);
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
    void generateImage(ActionEvent event) {
        // Create dialog for AI prompt
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Générer Image avec IA");
        dialog.setHeaderText("Décrivez l'image que vous voulez générer\n(Nécessite une connexion Internet)");
        dialog.setContentText("Prompt:");

        // Set default prompt based on event details
        if (titre.getText() != null && !titre.getText().isEmpty()) {
            String shortTitre = titre.getText();
            if (shortTitre.length() > 30)
                shortTitre = shortTitre.substring(0, 27) + "...";

            String defaultPrompt = "Event poster for " + shortTitre;

            if (lieu.getText() != null && !lieu.getText().isEmpty()) {
                String shortLieu = lieu.getText();
                // If it's a long address, just take the first part
                if (shortLieu.contains(","))
                    shortLieu = shortLieu.split(",")[0];
                if (shortLieu.length() > 30)
                    shortLieu = shortLieu.substring(0, 27) + "...";
                defaultPrompt += " in " + shortLieu;
            }
            dialog.getEditor().setText(defaultPrompt);
        }

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String prompt = result.get().trim();

            FileChooser fc = new FileChooser();
            fc.setTitle("Enregistrer l'image générée");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
            fc.setInitialFileName("ai_event_" + System.currentTimeMillis() + ".png");

            File file = fc.showSaveDialog(titre.getScene().getWindow());
            if (file != null) {
                // Show loading indicator
                if (lblImageStatus != null) {
                    lblImageStatus.setText("⏳ Génération IA en cours...");
                    lblImageStatus.setStyle("-fx-text-fill: orange;");
                }

                // Generate in background to avoid freezing UI
                new Thread(() -> {
                    boolean success = EventImageGenerator.generateAIImage(prompt, file.getAbsolutePath());

                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            if (lblImageStatus != null) {
                                lblImageStatus.setText("✓ Image IA générée");
                                lblImageStatus.setStyle("-fx-text-fill: green;");
                            }
                            
                            // Show preview
                            showImagePreview(file);
                            generatedImagePath = file.getAbsolutePath();
                            
                            afficherSucces("Succès",
                                    "Image générée avec IA: " + file.getName() +
                                            "\n\nAPI: Stability AI" +
                                            "\nPrompt: " + prompt);
                        } else {
                            if (lblImageStatus != null) {
                                lblImageStatus.setText("✗ Échec génération IA");
                                lblImageStatus.setStyle("-fx-text-fill: red;");
                            }
                            afficherErreur("Erreur de Génération IA",
                                    "Impossible de générer l'image.\n\n" +
                                            "Vérifiez:\n" +
                                            "• Connexion Internet active\n" +
                                            "• Clé API valide\n" +
                                            "• Crédits disponibles\n\n" +
                                            "Consultez la console pour plus de détails.");
                        }
                    });
                }).start();
            }
        }
    }
    
    private void showImagePreview(File imageFile) {
        try {
            javafx.scene.image.Image image = new javafx.scene.image.Image(imageFile.toURI().toString());
            imagePreview.setImage(image);
            imagePreviewContainer.setVisible(true);
            imagePreviewContainer.setManaged(true);
        } catch (Exception e) {
            System.err.println("Error showing preview: " + e.getMessage());
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
            afficherErreur("Champ vide", "Le titre est obligatoire !");
            return false;
        }
        if (contenu.getText() == null || contenu.getText().trim().isEmpty()) {
            afficherErreur("Champ vide", "Le contenu est obligatoire !");
            return false;
        }
        if (lieu.getText() == null || lieu.getText().trim().isEmpty()) {
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

    private void afficherErreur(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void afficherSucces(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}