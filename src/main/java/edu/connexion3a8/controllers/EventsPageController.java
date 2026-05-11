package edu.connexion3a8.controllers;

import edu.connexion3a8.InvestiApp;
import edu.connexion3a8.entities.Evenement;
import edu.connexion3a8.entities.User;
import edu.connexion3a8.services.EvenementService;
import edu.connexion3a8.services.InscriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class EventsPageController implements Initializable {

    @FXML private VBox eventsContainer;
    @FXML private Label userStatusLabel;
    @FXML private TextField searchField;
    @FXML private Button adminManageBtn;

    private User currentUser;
    private EvenementService evenementService = new EvenementService();
    private InscriptionService inscriptionService = new InscriptionService();
    private ObservableList<Evenement> allEvents = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadEvents();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("DEBUG: setCurrentUser called");
        System.out.println("DEBUG: User = " + (user != null ? user.getName() : "NULL"));
        System.out.println("DEBUG: User isActive = " + (user != null ? user.isActive() : "N/A"));
        updateUserStatus();
        // Show the "Manage events" link only for admins / mentors
        if (adminManageBtn != null) {
            boolean canManage = user != null &&
                    ("admin".equalsIgnoreCase(user.getRole()) || "mentor".equalsIgnoreCase(user.getRole()));
            adminManageBtn.setVisible(canManage);
            adminManageBtn.setManaged(canManage);
        }
        loadEvents();
    }

    @FXML
    private void handleOpenManagement() {
        try {
            if (currentUser != null && "mentor".equalsIgnoreCase(currentUser.getRole())) {
                InvestiApp.showMentorDashboard(currentUser);
            } else {
                InvestiApp.showEventManagement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserStatus() {
        if (currentUser != null && !currentUser.isActive()) {
            userStatusLabel.setText("⚠ Complete KYC verification to register for events");
            userStatusLabel.setStyle("-fx-text-fill: #9B7E46; -fx-font-weight: bold;");
        } else {
            userStatusLabel.setText("");
        }
    }

    private void loadEvents() {
        try {
            List<Evenement> events = evenementService.getData();
            allEvents.setAll(events);
            displayEvents(events);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayEvents(List<Evenement> events) {
        eventsContainer.getChildren().clear();

        if (events.isEmpty()) {
            Label emptyLabel = new Label("No events available");
            emptyLabel.getStyleClass().add("muted");
            eventsContainer.getChildren().add(emptyLabel);
            return;
        }

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM");

        for (Evenement event : events) {
            HBox eventCard = new HBox(18);
            eventCard.getStyleClass().add("event-card");
            eventCard.setAlignment(Pos.CENTER_LEFT);

            // Date badge
            VBox dateBadge = new VBox(2);
            dateBadge.getStyleClass().add("event-card-date");
            Label dayLbl = new Label(event.getDateDebut() == null ? "--"
                    : String.valueOf(event.getDateDebut().getDayOfMonth()));
            dayLbl.getStyleClass().add("event-card-date-day");
            Label monthLbl = new Label(event.getDateDebut() == null ? ""
                    : event.getDateDebut().format(monthFmt).toUpperCase());
            monthLbl.getStyleClass().add("event-card-date-month");
            dateBadge.getChildren().addAll(dayLbl, monthLbl);

            // Info column
            VBox info = new VBox(6);
            HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

            Label title = new Label(event.getTitre() == null ? "Untitled event" : event.getTitre());
            title.getStyleClass().add("event-card-title");
            title.setWrapText(true);

            HBox meta = new HBox(16);
            meta.setAlignment(Pos.CENTER_LEFT);
            meta.getStyleClass().add("event-card-meta");
            StringBuilder metaText = new StringBuilder();
            if (event.getDateDebut() != null && event.getDateFin() != null) {
                metaText.append(event.getDateDebut().format(dateFmt))
                        .append("  •  ")
                        .append(event.getDateDebut().format(timeFmt))
                        .append(" – ")
                        .append(event.getDateFin().format(timeFmt));
            }
            if (event.getLieu() != null && !event.getLieu().isBlank()) {
                metaText.append("  •  ").append(event.getLieu());
            }
            if (event.getMentorName() != null && !event.getMentorName().isBlank()) {
                metaText.append("  •  by ").append(event.getMentorName());
            }
            Label metaLbl = new Label(metaText.toString());
            metaLbl.getStyleClass().add("event-card-meta");
            metaLbl.setWrapText(true);

            Label contentPreview = new Label(event.getContenu() == null ? ""
                    : (event.getContenu().length() > 140
                            ? event.getContenu().substring(0, 137) + "…"
                            : event.getContenu()));
            contentPreview.getStyleClass().add("muted");
            contentPreview.setWrapText(true);

            info.getChildren().addAll(title, metaLbl, contentPreview);

            // Actions column
            VBox actions = new VBox(8);
            actions.setAlignment(Pos.TOP_RIGHT);

            Label statusPill = new Label();
            statusPill.getStyleClass().add("event-card-status-pill");
            if (event.getLieu() != null && event.getLieu().toLowerCase().contains("online")) {
                statusPill.setText("Online");
                statusPill.getStyleClass().add("online");
            } else {
                statusPill.setText("In person");
            }

            Button detailsBtn = new Button("View details");
            detailsBtn.getStyleClass().add("btn-secondary");
            detailsBtn.setOnAction(e -> showEventDetails(event));

            Button registerBtn = new Button("Register");
            registerBtn.getStyleClass().add("btn-primary");
            configureRegisterButton(registerBtn, event);

            actions.getChildren().addAll(statusPill, detailsBtn, registerBtn);

            eventCard.getChildren().addAll(dateBadge, info, actions);
            eventsContainer.getChildren().add(eventCard);
        }
    }

    /** Wire the card's register button to the proper state for the current user. */
    private void configureRegisterButton(Button registerBtn, Evenement event) {
        if (currentUser == null) {
            registerBtn.setText("Sign in to register");
            registerBtn.setDisable(true);
            return;
        }
        String role = currentUser.getRole() == null ? "" : currentUser.getRole().toLowerCase();
        if ("admin".equals(role)) {
            registerBtn.setText("Admins can't register");
            registerBtn.setDisable(true);
            return;
        }
        if ("mentor".equals(role) && event.getIdMentor() != null
                && event.getIdMentor().equals(currentUser.getId())) {
            registerBtn.setText("Your event");
            registerBtn.setDisable(true);
            return;
        }
        if (!currentUser.isActive()) {
            registerBtn.setText("KYC required");
            registerBtn.setDisable(true);
            return;
        }
        try {
            if (inscriptionService.isUserRegistered(currentUser.getId(), event.getIdEvenement())) {
                registerBtn.setText("Registered");
                registerBtn.setDisable(true);
                return;
            }
        } catch (SQLException ignored) { }
        registerBtn.setOnAction(e -> registerForEvent(event));
    }

    /** Open a modal with the event details + weather widget + register button. */
    private void showEventDetails(Evenement event) {
        javafx.stage.Stage dialog = new javafx.stage.Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle(event.getTitre() == null ? "Event" : event.getTitre());

        VBox root = new VBox(14);
        root.setStyle("-fx-padding: 26; -fx-background-color: transparent;");
        root.getStyleClass().add("glass-card");

        Label title = new Label(event.getTitre() == null ? "Untitled event" : event.getTitre());
        title.getStyleClass().add("inv-section-title");
        title.setWrapText(true);

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy, HH:mm");
        Label when = new Label(event.getDateDebut() == null ? "" : event.getDateDebut().format(dateFmt));
        when.getStyleClass().add("event-card-meta");

        Label where = new Label(event.getLieu() == null ? "" : event.getLieu());
        where.getStyleClass().add("event-card-meta");

        Label mentor = new Label(event.getMentorName() == null ? "" : "Organizer: " + event.getMentorName());
        mentor.getStyleClass().add("event-card-meta");

        Label content = new Label(event.getContenu() == null ? "" : event.getContenu());
        content.setWrapText(true);
        content.getStyleClass().add("muted");

        // Weather widget (only for in-person events with coords)
        VBox weatherBox = new VBox(6);
        weatherBox.getStyleClass().add("glass-card");
        weatherBox.setVisible(false);
        weatherBox.setManaged(false);
        Label weatherTitle = new Label("Weather forecast");
        weatherTitle.getStyleClass().add("section-title");
        Label weatherValue = new Label("Loading…");
        weatherValue.getStyleClass().add("muted");
        weatherBox.getChildren().addAll(weatherTitle, weatherValue);

        if (event.getLieuLatitude() != null && event.getLieuLongitude() != null
                && event.getDateDebut() != null) {
            weatherBox.setVisible(true);
            weatherBox.setManaged(true);
            fetchWeatherAsync(event.getLieuLatitude(), event.getLieuLongitude(),
                    event.getDateDebut().toLocalDate().toString(), weatherValue);
        }

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("btn-primary");
        configureRegisterButton(registerBtn, event);

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("btn-secondary");
        closeBtn.setOnAction(e -> dialog.close());

        HBox actions = new HBox(10, registerBtn, closeBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(title, when, where, mentor, new javafx.scene.control.Separator(),
                content, weatherBox, actions);

        javafx.scene.Scene scene = new javafx.scene.Scene(root, 620, 540);
        javafx.scene.Scene parentScene = eventsContainer.getScene();
        if (parentScene != null && !parentScene.getStylesheets().isEmpty()) {
            scene.getStylesheets().setAll(parentScene.getStylesheets());
            // Inherit dark theme if active
            if (parentScene.getRoot().getStyleClass().contains("theme-dark")) {
                scene.getRoot().getStyleClass().add("theme-dark");
            }
        }
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /** Hit Open-Meteo for a daily forecast on the event date and render a short summary. */
    private void fetchWeatherAsync(double lat, double lon, String date, Label target) {
        new Thread(() -> {
            try {
                String url = "https://api.open-meteo.com/v1/forecast?latitude=" + lat
                        + "&longitude=" + lon
                        + "&daily=weathercode,temperature_2m_max,temperature_2m_min,precipitation_sum"
                        + "&timezone=auto&start_date=" + date + "&end_date=" + date;
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                try (java.io.BufferedReader r = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) sb.append(line);
                    String json = sb.toString();
                    String tmax = extractFirst(json, "\"temperature_2m_max\":[");
                    String tmin = extractFirst(json, "\"temperature_2m_min\":[");
                    String precipitation = extractFirst(json, "\"precipitation_sum\":[");
                    String summary = (tmin == null || tmax == null)
                            ? "No forecast available for this date."
                            : String.format("High %s°C / Low %s°C  •  %s mm precipitation",
                                    tmax, tmin, precipitation == null ? "0" : precipitation);
                    javafx.application.Platform.runLater(() -> target.setText(summary));
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> target.setText(
                        "Weather unavailable (max 16 days ahead, or no internet)."));
            }
        }, "weather-fetch").start();
    }

    private static String extractFirst(String json, String marker) {
        int idx = json.indexOf(marker);
        if (idx < 0) return null;
        int start = idx + marker.length();
        int end = json.indexOf(',', start);
        int bracket = json.indexOf(']', start);
        if (end < 0 || (bracket >= 0 && bracket < end)) end = bracket;
        if (end < 0 || end <= start) return null;
        return json.substring(start, end).trim();
    }

    private void registerForEvent(Evenement event) {
        try {
            edu.connexion3a8.entities.Inscription inscription = new edu.connexion3a8.entities.Inscription(
                    currentUser.getId(), event.getIdEvenement(), "confirme");
            inscriptionService.addEntity(inscription);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Successfully registered for: " + event.getTitre());
            alert.showAndWait();

            loadEvents();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Registration Failed");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            displayEvents(allEvents);
        } else {
            List<Evenement> filtered = allEvents.filtered(e ->
                    e.getTitre().toLowerCase().contains(query) ||
                    e.getLieu().toLowerCase().contains(query) ||
                    (e.getMentorName() != null && e.getMentorName().toLowerCase().contains(query))
            );
            displayEvents(filtered);
        }
    }

    @FXML
    private void handleBack() {
        try {
            InvestiApp.showHomePage(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
