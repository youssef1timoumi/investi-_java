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
    private TextField productNameField, referenceField, amountField, currencyField, shippingField,
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
    private Sale existingSale;

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

    public void setSaleData(Sale s) {
        this.existingSale = s;
        if (s != null) {
            formTitle.setText("Update Sale");
            saveBtn.setText("Save Changes");

            referenceField.setText(s.getReference());
            referenceField.setEditable(false); // Reference shouldn't change
            amountField.setText(String.valueOf(s.getTotalAmount()));
            currencyField.setText(s.getCurrency());
            paymentMethodCombo.setValue(s.getPaymentMethod());
            shippingField.setText(s.getShippingAddress());
            billingField.setText(s.getBillingAddress());
            notesField.setText(s.getNotes());
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
            Sale s = (existingSale != null) ? existingSale : new Sale();
            s.setReference(referenceField.getText());
            s.setCustomerId(1L);
            s.setProductId(
                    product != null ? product.getId() : (existingSale != null ? existingSale.getProductId() : 0));
            s.setTotalAmount(Double.parseDouble(amountField.getText()));
            s.setCurrency(currencyField.getText());
            s.setPaymentMethod(paymentMethodCombo.getValue());
            s.setShippingAddress(shippingField.getText());
            s.setBillingAddress(billingField.getText());
            s.setNotes(notesField.getText());

            if (existingSale != null) {
                saleService.update(s);
            } else {
                s.setStatus("unpaid");
                s.setPaymentStatus("unpaid");
                s.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
                saleService.create(s);
            }

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
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
