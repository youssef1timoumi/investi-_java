package edu.collaboration.Controllers.Project;

import edu.collaboration.entities.Project;
import edu.collaboration.services.ProjectService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    private String originalStatus;

    public void initData(Project p) {
        this.originalStatus = p.getStatus();
        idTf.setText(String.valueOf(p.getProjectId()));
        idTf.setDisable(true);
        titleTf.setText(p.getTitle());
        descTf.setText(p.getDescription());
        amountTf.setText(String.valueOf(p.getAmountRequested()));
        equityTf.setText(String.valueOf(p.getEquityOffered()));
        statusTf.setText(p.getStatus());
        statusTf.setDisable(true); // Status is read-only for Entrepreneur
    }

    @FXML
    void updateProject(ActionEvent event) {
        // LOCKING LOGIC
        if ("FUNDED".equalsIgnoreCase(originalStatus) || "CLOSED".equalsIgnoreCase(originalStatus)) {
            showAlert(Alert.AlertType.ERROR, "Modification Denied",
                    "You cannot edit a project that is FUNDED or CLOSED.");
            return;
        }

        if (!validateUpdate())
            return;

        try {
            Project p = new Project();
            p.setProjectId(Integer.parseInt(idTf.getText()));
            p.setTitle(titleTf.getText());
            p.setDescription(descTf.getText());
            p.setAmountRequested(Double.parseDouble(amountTf.getText()));
            p.setEquityOffered(Double.parseDouble(equityTf.getText()));

            // RESET LOGIC: Any update requires re-validation
            p.setStatus("UNDER_REVIEW");

            System.out.println("DEBUG: Controller attempting update for ID " + p.getProjectId());
            System.out.println("DEBUG: Values - Title: " + p.getTitle() + ", Status: " + p.getStatus());

            boolean success = ps.update(p.getProjectId(), p);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project updated successfully");
                closeStage(event);
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
        closeStage(event);
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
