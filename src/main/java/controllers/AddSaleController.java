package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Product;
import models.Sale;
import services.SaleService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.Consumer;

public class AddSaleController implements Initializable {

    @FXML
    private TextField productNameField, referenceField, amountField, currencyField, customerIdField, shippingField,
            billingField;
    @FXML
    private ComboBox<String> paymentMethodCombo;
    @FXML
    private TextArea notesField;
    @FXML
    private Button saveBtn;
    @FXML
    private Label formTitle;

    private final SaleService saleService = new SaleService();
    private Consumer<Void> onSaleCreated;
    private Runnable onCancel;
    private Product product;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentMethodCombo.getItems().addAll("Credit Card", "PayPal", "Bank Transfer", "Cash");
        paymentMethodCombo.setValue("Credit Card");

        // Auto-generate reference
        referenceField.setText("SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    public void setProduct(Product p) {
        this.product = p;
        if (p != null) {
            productNameField.setText(p.getName());
            amountField.setText(String.valueOf(p.getPrice()));
            currencyField.setText(p.getCurrency() != null ? p.getCurrency() : "USD");
        }
    }

    public void setOnSaleCreated(Consumer<Void> onSaleCreated) {
        this.onSaleCreated = onSaleCreated;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    @FXML
    private void handleSave() {
        if (!validateForm())
            return;

        try {
            Sale s = new Sale();
            s.setReference(referenceField.getText());
            s.setCustomerId(Long.parseLong(customerIdField.getText()));
            s.setProductId(product != null ? product.getId() : 0);
            s.setTotalAmount(Double.parseDouble(amountField.getText()));
            s.setCurrency(currencyField.getText());
            s.setStatus("paid");
            s.setPaymentMethod(paymentMethodCombo.getValue());
            s.setPaymentStatus("paid");
            s.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
            s.setShippingAddress(shippingField.getText());
            s.setBillingAddress(billingField.getText());
            s.setNotes(notesField.getText());

            saleService.create(s);

            if (onSaleCreated != null) {
                onSaleCreated.accept(null);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not complete sale: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        if (onCancel != null)
            onCancel.run();
    }

    private boolean validateForm() {
        if (customerIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Customer ID is required");
            return false;
        }
        try {
            Long.parseLong(customerIdField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Customer ID must be a number");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
