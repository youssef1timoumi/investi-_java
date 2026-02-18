package edu.connections3a8.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    private BorderPane mainContainer;

    @FXML
    public void initialize() {
        // Initialization if needed
    }

    @FXML
    private void openCourseForm() {
        loadView("/CourseForm.fxml", "Course Management");
    }

    @FXML
    private void openQuizForm() {
        loadView("/QuizForm.fxml", "Quiz Management");
    }

    @FXML
    private void openBadgeForm() {
        loadView("/BadgeForm.fxml", "Badge Management");
    }

    @FXML
    private void openUserView() {
        // TODO: Create User view
        showComingSoon("User Management");
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Set the loaded view as the center of the BorderPane
            mainContainer.setCenter(view);
            
            // Update window title
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setTitle("Gamification System - " + title);
            
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load view");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainMenuContent.fxml"));
            Parent view = loader.load();
            
            // Wire up the buttons programmatically
            javafx.scene.control.Button coursesBtn = (javafx.scene.control.Button) view.lookup("#coursesBtn");
            javafx.scene.control.Button quizzesBtn = (javafx.scene.control.Button) view.lookup("#quizzesBtn");
            javafx.scene.control.Button badgesBtn = (javafx.scene.control.Button) view.lookup("#badgesBtn");
            javafx.scene.control.Button usersBtn = (javafx.scene.control.Button) view.lookup("#usersBtn");
            
            if (coursesBtn != null) coursesBtn.setOnAction(e -> openCourseForm());
            if (quizzesBtn != null) quizzesBtn.setOnAction(e -> openQuizForm());
            if (badgesBtn != null) badgesBtn.setOnAction(e -> openBadgeForm());
            if (usersBtn != null) usersBtn.setOnAction(e -> openUserView());
            
            mainContainer.setCenter(view);
            
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setTitle("Gamification System - Main Menu");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load main menu");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void showComingSoon(String feature) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle("Coming Soon");
        alert.setHeaderText(feature);
        alert.setContentText("This feature is coming soon!");
        alert.showAndWait();
    }
}
