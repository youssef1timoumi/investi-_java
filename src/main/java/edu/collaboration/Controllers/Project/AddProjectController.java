package edu.collaboration.Controllers.Project;

import edu.collaboration.entities.Project;
import edu.collaboration.services.ProjectService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddProjectController {

    private final ProjectService ps = new ProjectService();

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
            // MOCK ID: 1 (Entrepreneur)
            int entrepreneurId = 1;

            Project p = new Project(
                    entrepreneurId,
                    titleTf.getText(),
                    descriptionTf.getText(),
                    Double.parseDouble(amountTf.getText()),
                    Double.parseDouble(equityTf.getText()),
                    "UNDER_REVIEW"); // Initial status for Admin validation

            ps.addEntity(p);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Project submitted for review.");
            closeStage(event);
        } catch (SQLException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private boolean validateProjectInputs() {
        boolean isValid = true;
        clearErrorStyles();
        StringBuilder errors = new StringBuilder();

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
        closeStage(event);
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
