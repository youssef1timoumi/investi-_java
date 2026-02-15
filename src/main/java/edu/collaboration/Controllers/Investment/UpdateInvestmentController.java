package edu.collaboration.controllers.Investment;

import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class UpdateInvestmentController {

    private final InvestmentService is = new InvestmentService();

    @FXML
    private TextField idTf;
    @FXML
    private TextField amountTf;
    @FXML
    private TextField statusTf;

    public void initData(Investment i) {
        idTf.setText(String.valueOf(i.getInvestmentId()));
        idTf.setDisable(true);
        amountTf.setText(String.valueOf(i.getTotalAmount()));
        statusTf.setText(i.getStatus());
    }

    @FXML
    void updateInvestment(ActionEvent event) {
        if (!validateUpdate())
            return;

        try {
            int id = Integer.parseInt(idTf.getText());
            double amount = Double.parseDouble(amountTf.getText());
            String status = statusTf.getText().toUpperCase();

            Investment i = new Investment();
            i.setTotalAmount(amount);
            i.setStatus(status);

            boolean success = is.update(id, i);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Investment updated successfully");
                goMain(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Investment update failed.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid input");
        }
    }

    private boolean validateUpdate() {
        boolean isValid = true;
        clearErrorStyles();
        StringBuilder errors = new StringBuilder();

        String status = statusTf.getText().toUpperCase();
        if (status.isEmpty()
                || (!status.equals("PENDING") && !status.equals("ACCEPTED") && !status.equals("REFUSED"))) {
            setErrorStyle(statusTf);
            errors.append("- Status must be PENDING, ACCEPTED, or REFUSED.\n");
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
        amountTf.getStyleClass().remove("error");
        statusTf.getStyleClass().remove("error");
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
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

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
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
