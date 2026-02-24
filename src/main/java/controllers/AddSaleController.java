package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Product;
import models.Sale;
import services.ProductService;
import services.SaleService;
import javafx.application.Platform;
import javafx.util.StringConverter;
import java.sql.SQLException;
import java.util.List;

public class AddSaleController {

    @FXML
    private TextField refTf;
    @FXML
    private TextField customerTf;
    @FXML
    private TextField amountTf;
    @FXML
    private TextField currencyTf;
    @FXML
    private ComboBox<String> statusCb;
    @FXML
    private ComboBox<String> paymentStatusCb;
    @FXML
    private TextArea addressTa;
    @FXML
    private ComboBox<Product> productCb;
    @FXML
    private Label titleLabel;
    @FXML
    private Button submitBtn;

    public static Sale saleToEdit = null;

    private final SaleService ss = new SaleService();
    private final ProductService ps = new ProductService();

    // Static field for deep-linking (pre-selection)
    public static Product preSelectedProduct;

    @FXML
    void initialize() {
        // Init Statuses
        statusCb.getItems().addAll("pending", "paid", "shipped", "delivered", "cancelled");
        statusCb.setValue("pending");

        paymentStatusCb.getItems().addAll("unpaid", "paid", "refunded");
        paymentStatusCb.setValue("unpaid");

        // Load Products
        loadProducts();

        // Auto-update price when product changes
        productCb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && saleToEdit == null) { // Only auto-fill if we're not in edit mode (or let user
                                                        // override)
                amountTf.setText(String.valueOf(newVal.getPrice()));
                currencyTf.setText(newVal.getCurrency());
                if (refTf.getText().isEmpty()) {
                    refTf.setText("REF-" + System.currentTimeMillis() % 1000000);
                }
            }
        });

        // Handle pre-selection if any
        if (preSelectedProduct != null) {
            final Product toSelect = preSelectedProduct;
            preSelectedProduct = null; // Clear it immediately

            Platform.runLater(() -> {
                Product found = null;
                for (Product p : productCb.getItems()) {
                    if (p.getId() == toSelect.getId()) {
                        found = p;
                        break;
                    }
                }

                if (found != null) {
                    productCb.setValue(found);
                    productCb.getSelectionModel().select(found);

                    // Force update fields immediately to ensure success
                    amountTf.setText(String.valueOf(found.getPrice()));
                    currencyTf.setText(found.getCurrency());
                    if (refTf.getText().isEmpty() || refTf.getText().startsWith("REF-")) {
                        refTf.setText("REF-" + (System.currentTimeMillis() % 1000000));
                    }
                }
            });
        }

        // Handle sale update pre-filling
        if (saleToEdit != null) {
            titleLabel.setText("📝 Modifier une Vente");
            submitBtn.setText("✓ Mettre à jour la Vente");

            refTf.setText(saleToEdit.getReference());
            customerTf.setText(String.valueOf(saleToEdit.getCustomerId()));
            amountTf.setText(String.valueOf(saleToEdit.getTotalAmount()));
            currencyTf.setText(saleToEdit.getCurrency());
            statusCb.setValue(saleToEdit.getStatus());
            paymentStatusCb.setValue(saleToEdit.getPaymentStatus());
            addressTa.setText(saleToEdit.getShippingAddress());

            // Select product in dropdown (search by name in description or ID if possible)
            // For now, let's just wait for products to load
            Platform.runLater(() -> {
                for (Product p : productCb.getItems()) {
                    if (saleToEdit.getNotes() != null && saleToEdit.getNotes().contains(p.getName())) {
                        productCb.getSelectionModel().select(p);
                        break;
                    }
                }
            });
        }
    }

    private void loadProducts() {
        try {
            List<Product> products = ps.read();
            productCb.getItems().setAll(products);

            productCb.setConverter(new StringConverter<Product>() {
                @Override
                public String toString(Product product) {
                    return product == null ? ""
                            : product.getName() + " (" + product.getPrice() + " " + product.getCurrency() + ")";
                }

                @Override
                public Product fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not load products: " + e.getMessage());
        }
    }

    @FXML
    void saveSale() {
        try {
            // Validation
            Product selectedProduct = productCb.getValue();
            if (selectedProduct == null) {
                showAlert("Missing Product", "Please select a product to purchase.");
                return;
            }
            if (customerTf.getText().isEmpty()) {
                showAlert("Missing Customer", "Please enter a Customer ID.");
                return;
            }
            if (refTf.getText().isEmpty()) {
                showAlert("Missing Reference", "Please enter an Order Reference.");
                return;
            }

            Sale s = (saleToEdit != null) ? saleToEdit : new Sale();
            s.setReference(refTf.getText());
            s.setCustomerId(Long.parseLong(customerTf.getText().trim()));
            s.setTotalAmount(Double.parseDouble(amountTf.getText().trim()));
            s.setCurrency(currencyTf.getText());
            s.setStatus(statusCb.getValue());
            s.setPaymentMethod("Direct");
            s.setPaymentStatus(paymentStatusCb.getValue());
            s.setShippingAddress(addressTa.getText());
            s.setBillingAddress(addressTa.getText());
            s.setNotes("Product: " + selectedProduct.getName());

            if (saleToEdit != null) {
                ss.update(s);
            } else {
                ss.create(s);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(saleToEdit != null ? "Sale updated successfully!" : "Sale recorded successfully!");
            alert.showAndWait();

            saleToEdit = null;

            navigateShowSales();
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please ensure Customer ID and Amount are valid numbers.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save sale: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void navigateShowSales() {
        DashboardController.getInstance().showSales();
    }
}
