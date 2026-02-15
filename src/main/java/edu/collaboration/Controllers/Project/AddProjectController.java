package edu.collaboration.controllers.Project;

import edu.collaboration.entities.Project;
import edu.collaboration.services.ProjectService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AddProjectController {

    private final ProjectService ps = new ProjectService();

    @FXML
    private TextField entrepreneurTf;
    @FXML
    private TextField titleTf;
    @FXML
    private TextField descriptionTf;
    @FXML
    private TextField amountTf;
    @FXML
    private TextField equityTf;

    @FXML
    void saveProject(ActionEvent event) {
        if (!validateProjectInputs())
            return;

        try {
            Project p = new Project(
                    Integer.parseInt(entrepreneurTf.getText()),
                    titleTf.getText(),
                    descriptionTf.getText(),
                    Double.parseDouble(amountTf.getText()),
                    Double.parseDouble(equityTf.getText()),
                    "OPEN");

            ps.addEntity(p);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Project added successfully");
            goMain(event); // Navigate back to main/list
        } catch (SQLException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private boolean validateProjectInputs() {
        boolean isValid = true;
        clearErrorStyles();
        StringBuilder errors = new StringBuilder();

        // Check Entrepreneur ID
        if (entrepreneurTf.getText().isEmpty() || !isNumeric(entrepreneurTf.getText())) {
            setErrorStyle(entrepreneurTf);
            errors.append("- Entrepreneur ID must be a valid number.\n");
            isValid = false;
        }

        // Check Title
        if (titleTf.getText().length() < 3) {
            setErrorStyle(titleTf);
            errors.append("- Title must be at least 3 characters.\n");
            isValid = false;
        }

        // Check Description
        if (descriptionTf.getText().length() < 10) {
            setErrorStyle(descriptionTf);
            errors.append("- Description must be at least 10 characters.\n");
            isValid = false;
        }

        // Check Amount
        if (amountTf.getText().isEmpty() || !isNumeric(amountTf.getText())) {
            setErrorStyle(amountTf);
            errors.append("- Target Amount must be a valid number.\n");
            isValid = false;
        } else {
            // Only check range if it's a valid number
            double amount = Double.parseDouble(amountTf.getText());
            if (amount <= 0) {
                setErrorStyle(amountTf);
                errors.append("- Target Amount must be positive.\n");
                isValid = false;
            }
        }

        // Check Equity
        if (equityTf.getText().isEmpty() || !isNumeric(equityTf.getText())) {
            setErrorStyle(equityTf);
            errors.append("- Equity must be a valid number.\n");
            isValid = false;
        } else {
            // Only check range if it's a valid number
            double equity = Double.parseDouble(equityTf.getText());
            if (equity <= 0 || equity > 100) {
                setErrorStyle(equityTf);
                errors.append("- Equity must be between 0 and 100%.\n");
                isValid = false;
            }
        }

        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please correct the following:\n" + errors.toString());
        }
        return isValid;
    }

    private void setErrorStyle(TextField tf) {
        if (!tf.getStyleClass().contains("error")) {
            tf.getStyleClass().add("error");
        }
    }

    private void clearErrorStyles() {
        entrepreneurTf.getStyleClass().remove("error");
        titleTf.getStyleClass().remove("error");
        descriptionTf.getStyleClass().remove("error");
        amountTf.getStyleClass().remove("error");
        equityTf.getStyleClass().remove("error");
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    void goMain(javafx.event.ActionEvent event) {
        navigate(event, "/ShowProject.fxml");
    }

    private void navigate(javafx.event.ActionEvent event, String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene()
                    .getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
