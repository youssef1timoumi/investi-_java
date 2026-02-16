package edu.connections3a8.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    private void openCourseForm() {
        openWindow("/CourseForm.fxml", "Course Management", 600, 700);
    }

    @FXML
    private void openQuizForm() {
        openWindow("/QuizForm.fxml", "Quiz Management", 600, 650);
    }

    @FXML
    private void openBadgeForm() {
        openWindow("/BadgeForm.fxml", "Badge Management", 600, 700);
    }

    @FXML
    private void openUserView() {
        // TODO: Create User view
        showComingSoon("User Management");
    }

    private void openWindow(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open window");
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
