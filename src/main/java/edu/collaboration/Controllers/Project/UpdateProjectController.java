package edu.collaboration.controllers.Project;

import edu.collaboration.entities.Project;
import edu.collaboration.services.ProjectService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class UpdateProjectController {

    private final ProjectService ps = new ProjectService();

    @FXML
    private TextField idTf;
    @FXML
    private TextField titleTf;
    @FXML
    private TextField descTf;
    @FXML
    private TextField amountTf;
    @FXML
    private TextField equityTf;
    @FXML
    private TextField statusTf;

    public void initData(Project p) {
        idTf.setText(String.valueOf(p.getProjectId()));
        idTf.setDisable(true);
        titleTf.setText(p.getTitle());
        descTf.setText(p.getDescription());
        amountTf.setText(String.valueOf(p.getAmountRequested()));
        equityTf.setText(String.valueOf(p.getEquityOffered()));
        statusTf.setText(p.getStatus());
    }

    @FXML
    void updateProject(ActionEvent event) {
        if (!validateUpdate())
            return;

        try {
            Project p = new Project();
            p.setProjectId(Integer.parseInt(idTf.getText()));
            p.setTitle(titleTf.getText());
            p.setDescription(descTf.getText());
            p.setAmountRequested(Double.parseDouble(amountTf.getText()));
            p.setEquityOffered(Double.parseDouble(equityTf.getText()));
            p.setStatus(statusTf.getText().toUpperCase());

            boolean success = ps.update(p.getProjectId(), p);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project updated successfully");
                goMain(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Project update failed.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid number format in Amount or Equity fields.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateUpdate() {
        boolean isValid = true;
        clearErrorStyles();
        StringBuilder errors = new StringBuilder();

        if (titleTf.getText().isEmpty()) {
            setErrorStyle(titleTf);
            errors.append("- Title cannot be empty.\n");
            isValid = false;
        }
        if (descTf.getText().isEmpty()) {
            setErrorStyle(descTf);
            errors.append("- Description cannot be empty.\n");
            isValid = false;
        }

        // Validate Amount
        if (amountTf.getText().isEmpty() || !isNumeric(amountTf.getText())) {
            setErrorStyle(amountTf);
            errors.append("- Amount must be a valid number.\n");
            isValid = false;
        } else {
            double amount = Double.parseDouble(amountTf.getText());
            if (amount <= 0) {
                setErrorStyle(amountTf);
                errors.append("- Amount must be positive.\n");
                isValid = false;
            }
        }

        // Validate Equity
        if (equityTf.getText().isEmpty() || !isNumeric(equityTf.getText())) {
            setErrorStyle(equityTf);
            errors.append("- Equity must be a valid number.\n");
            isValid = false;
        } else {
            double equity = Double.parseDouble(equityTf.getText());
            if (equity <= 0 || equity > 100) {
                setErrorStyle(equityTf);
                errors.append("- Equity must be between 0 and 100%.\n");
                isValid = false;
            }
        }

        String status = statusTf.getText().toUpperCase();
        if (!status.equals("OPEN") && !status.equals("FUNDED") && !status.equals("CLOSED")) {
            setErrorStyle(statusTf);
            errors.append("- Status must be OPEN, FUNDED, or CLOSED.\n");
            isValid = false;
        }

        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please correct the following:\n" + errors.toString());
        }
        return isValid;
    }

    private void setErrorStyle(TextField tf) {
        if (!tf.getStyleClass().contains("error"))
            tf.getStyleClass().add("error");
    }

    private void clearErrorStyles() {
        idTf.getStyleClass().remove("error");
        titleTf.getStyleClass().remove("error");
        descTf.getStyleClass().remove("error");
        amountTf.getStyleClass().remove("error");
        equityTf.getStyleClass().remove("error");
        statusTf.getStyleClass().remove("error");
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
