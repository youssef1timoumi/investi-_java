package edu.collaboration.controllers.Investment;

import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AddInvestmentController {

    private final InvestmentService is = new InvestmentService();

    @FXML
    private TextField projectTf;
    @FXML
    private TextField investorTf;
    @FXML
    private TextField amountTf;
    @FXML
    private TextField durationTf;
    @FXML
    private TextField equityTf;

    @FXML
    void saveInvestment(ActionEvent event) {
        if (!validateInvestment())
            return;

        double total = Double.parseDouble(amountTf.getText());
        int duration = Integer.parseInt(durationTf.getText());

        Investment i = new Investment(
                Integer.parseInt(projectTf.getText()),
                Integer.parseInt(investorTf.getText()),
                total,
                duration,
                total / duration,
                Double.parseDouble(equityTf.getText()),
                "PENDING");

        try {
            is.addEntity(i);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Investment added successfully");
            goMain(event); // Navigate to list
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private boolean validateInvestment() {
        boolean isValid = true;
        clearErrorStyles();
        StringBuilder errors = new StringBuilder();

        // Check Project ID
        if (projectTf.getText().isEmpty() || !isNumeric(projectTf.getText())) {
            setErrorStyle(projectTf);
            errors.append("- Project ID must be a valid number.\n");
            isValid = false;
        }

        // Check Investor ID
        if (investorTf.getText().isEmpty() || !isNumeric(investorTf.getText())) {
            setErrorStyle(investorTf);
            errors.append("- Investor ID must be a valid number.\n");
            isValid = false;
        }

        // Check Total Amount
        if (amountTf.getText().isEmpty() || !isNumeric(amountTf.getText())) {
            setErrorStyle(amountTf);
            errors.append("- Total Amount must be a valid number.\n");
            isValid = false;
        } else {
            // Only check range if it's a valid number
            double amount = Double.parseDouble(amountTf.getText());
            if (amount <= 0) {
                setErrorStyle(amountTf);
                errors.append("- Total Amount must be positive.\n");
                isValid = false;
            }
        }

        // Check Duration
        if (durationTf.getText().isEmpty() || !isNumeric(durationTf.getText())) {
            setErrorStyle(durationTf);
            errors.append("- Duration must be a valid number.\n");
            isValid = false;
        } else {
            // Only check range if it's a valid number
            int duration = Integer.parseInt(durationTf.getText());
            if (duration <= 0) {
                setErrorStyle(durationTf);
                errors.append("- Duration must be positive.\n");
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
        if (!tf.getStyleClass().contains("error"))
            tf.getStyleClass().add("error");
    }

    private void clearErrorStyles() {
        projectTf.getStyleClass().remove("error");
        investorTf.getStyleClass().remove("error");
        amountTf.getStyleClass().remove("error");
        durationTf.getStyleClass().remove("error");
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
        navigate(event, "/ShowInvestment.fxml");
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
