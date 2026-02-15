package edu.collaboration.controllers.Investment;

import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class UpdateInvestmentController {

    private final InvestmentService is = new InvestmentService();

    @FXML private TextField idTf;
    @FXML private TextField projectTf;
    @FXML private TextField investorTf;
    @FXML private TextField amountTf;
    @FXML private TextField durationTf;
    @FXML private TextField equityTf;
    @FXML private TextField statusTf;

    public void initData(Investment i) {

        idTf.setText(String.valueOf(i.getInvestmentId()));
        idTf.setDisable(true);

        projectTf.setText(String.valueOf(i.getProjectId()));
        projectTf.setDisable(true); // LOCKED

        investorTf.setText(String.valueOf(i.getInvestorId()));
        investorTf.setDisable(true); // LOCKED

        amountTf.setText(String.valueOf(i.getTotalAmount()));
        durationTf.setText(String.valueOf(i.getDurationMonths()));
        equityTf.setText(String.valueOf(i.getEquityRequested()));
        statusTf.setText(i.getStatus());
    }

    @FXML
    void updateInvestment(ActionEvent event) {

        if (!validateInvestment())
            return;

        try {

            int id = Integer.parseInt(idTf.getText());
            int projectId = Integer.parseInt(projectTf.getText());
            int investorId = Integer.parseInt(investorTf.getText());
            double total = Double.parseDouble(amountTf.getText());
            int duration = Integer.parseInt(durationTf.getText());
            double equity = Double.parseDouble(equityTf.getText());
            String status = statusTf.getText().toUpperCase();

            Investment i = new Investment(
                    projectId,
                    investorId,
                    total,
                    duration,
                    total / duration,
                    equity,
                    status
            );

            boolean success = is.update(id, i);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION,
                        "Success",
                        "Investment updated successfully");
                goMain(event);
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Error",
                        "Investment update failed.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Invalid input values.");
        }
    }

    private boolean validateInvestment() {

        boolean isValid = true;
        clearErrorStyles();
        StringBuilder errors = new StringBuilder();

        if (amountTf.getText().isEmpty()
                || !isNumeric(amountTf.getText())
                || Double.parseDouble(amountTf.getText()) <= 0) {
            setErrorStyle(amountTf);
            errors.append("- Total Amount must be positive.\n");
            isValid = false;
        }

        if (durationTf.getText().isEmpty()
                || !isNumeric(durationTf.getText())
                || Integer.parseInt(durationTf.getText()) <= 0) {
            setErrorStyle(durationTf);
            errors.append("- Duration must be positive.\n");
            isValid = false;
        }

        if (equityTf.getText().isEmpty()
                || !isNumeric(equityTf.getText())) {
            setErrorStyle(equityTf);
            errors.append("- Equity must be a valid number.\n");
            isValid = false;
        } else {
            double equity = Double.parseDouble(equityTf.getText());
            if (equity <= 0 || equity > 100) {
                setErrorStyle(equityTf);
                errors.append("- Equity must be between 0 and 100.\n");
                isValid = false;
            }
        }

        String status = statusTf.getText().toUpperCase();
        if (status.isEmpty()
                || (!status.equals("PENDING")
                && !status.equals("ACCEPTED")
                && !status.equals("REFUSED"))) {
            setErrorStyle(statusTf);
            errors.append("- Status must be PENDING, ACCEPTED, or REFUSED.\n");
            isValid = false;
        }

        if (!isValid) {
            showAlert(Alert.AlertType.ERROR,
                    "Validation Error",
                    "Please correct the following:\n" + errors);
        }

        return isValid;
    }

    private void setErrorStyle(TextField tf) {
        if (!tf.getStyleClass().contains("error"))
            tf.getStyleClass().add("error");
    }

    private void clearErrorStyles() {
        amountTf.getStyleClass().remove("error");
        durationTf.getStyleClass().remove("error");
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
    void goMain(ActionEvent event) {
        navigate(event, "/ShowInvestment.fxml");
    }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage =
                    (javafx.stage.Stage) ((javafx.scene.Node) event.getSource())
                            .getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
