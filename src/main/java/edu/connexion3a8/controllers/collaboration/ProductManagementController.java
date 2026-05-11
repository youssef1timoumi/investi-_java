package edu.connexion3a8.controllers.collaboration;

import edu.connexion3a8.InvestiApp;
import edu.connexion3a8.entities.Product;
import edu.connexion3a8.entities.User;
import edu.connexion3a8.services.ProductService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Desktop Product Management — full CRUD + search + navigation.
 *
 * <p>Wires every `onAction` handler referenced by
 * `collaboration/ProductManagement.fxml` so the screen opens cleanly. Uses
 * {@link ProductService} for all DB access (admin-only write operations, per
 * the service's own enforcement).
 */
public class ProductManagementController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Number> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Number> colPrice;
    @FXML private TableColumn<Product, String> colCurrency;
    @FXML private TableColumn<Product, String> colStatus;
    @FXML private TableColumn<Product, Number> colStock;
    @FXML private TableColumn<Product, Number> colViews;
    @FXML private TableColumn<Product, Number> colSales;

    @FXML private TextField searchField;
    @FXML private VBox formContainer;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> currencyCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField stockField;
    @FXML private TextField remiseField;
    @FXML private CheckBox digitalCheckBox;
    @FXML private TextField imageUrlField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final ProductService productService = new ProductService();
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private FilteredList<Product> filteredProducts;
    private Product editing;

    @FXML
    public void initialize() {
        // Column cell-value factories
        if (colId != null) colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        if (colName != null) colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        if (colCategory != null) colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoryName()));
        if (colPrice != null) colPrice.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrice()));
        if (colCurrency != null) colCurrency.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCurrency()));
        if (colStatus != null) colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        if (colStock != null) colStock.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getStock()));
        if (colViews != null) colViews.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getViewsCount()));
        if (colSales != null) colSales.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getSalesCount()));

        // Populate combos
        if (currencyCombo != null) currencyCombo.setItems(FXCollections.observableArrayList("TND", "USD", "EUR", "GBP"));
        if (statusCombo != null) statusCombo.setItems(FXCollections.observableArrayList("draft", "published", "archived"));
        if (categoryCombo != null) categoryCombo.setItems(FXCollections.observableArrayList(
                "Electronics", "Automobile", "Fashion", "Home", "Books", "Services", "Uncategorized"));

        // Filtered list wired to search field
        filteredProducts = new FilteredList<>(allProducts, p -> true);
        if (productTable != null) productTable.setItems(filteredProducts);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                String q = newVal == null ? "" : newVal.toLowerCase().trim();
                filteredProducts.setPredicate(p -> {
                    if (q.isEmpty()) return true;
                    return (p.getName() != null && p.getName().toLowerCase().contains(q))
                        || (p.getCategoryName() != null && p.getCategoryName().toLowerCase().contains(q))
                        || String.valueOf(p.getId()).contains(q);
                });
            });
        }

        if (formContainer != null) {
            formContainer.setVisible(false);
            formContainer.setManaged(false);
        }

        loadProducts();
    }

    private void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            allProducts.setAll(products);
        } catch (SQLException e) {
            showError("Failed to load products", e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Navigation
    // ------------------------------------------------------------------

    @FXML
    private void handleGoHome() {
        try {
            InvestiApp.showHomePage();
        } catch (Exception e) {
            showError("Navigation error", e.getMessage());
        }
    }

    @FXML
    private void handleGoAdminDashboard() {
        try {
            InvestiApp.showAdminDashboard();
        } catch (Exception e) {
            showError("Navigation error", e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // CRUD handlers
    // ------------------------------------------------------------------

    @FXML
    private void handleAddProduct() {
        editing = null;
        clearForm();
        showForm(true);
    }

    @FXML
    private void handleEditProduct() {
        Product selected = productTable == null ? null : productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Select a product", "Pick a row in the table before clicking Edit.");
            return;
        }
        editing = selected;
        nameField.setText(selected.getName());
        descriptionField.setText(selected.getDescription());
        priceField.setText(String.valueOf(selected.getPrice()));
        currencyCombo.setValue(selected.getCurrency());
        categoryCombo.setValue(selected.getCategoryName());
        statusCombo.setValue(selected.getStatus());
        stockField.setText(String.valueOf(selected.getStock()));
        remiseField.setText(String.valueOf(selected.getRemise()));
        digitalCheckBox.setSelected(selected.isDigital());
        imageUrlField.setText(selected.getDownloadUrl());
        showForm(true);
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = productTable == null ? null : productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Select a product", "Pick a row in the table before clicking Delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete product \"" + selected.getName() + "\"? This cannot be undone.",
                ButtonType.YES, ButtonType.CANCEL);
        confirm.setHeaderText("Confirm delete");
        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
            User admin = currentUserOrAlertAdminRequired();
            if (admin == null) return;
            try {
                productService.delete(selected.getId(), admin);
                loadProducts();
            } catch (SQLException e) {
                showError("Delete failed", e.getMessage());
            } catch (SecurityException se) {
                showError("Access denied", se.getMessage());
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadProducts();
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose product image");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"));
        File chosen = fc.showOpenDialog(imageUrlField.getScene().getWindow());
        if (chosen == null) return;
        try {
            // Copy into the canonical upload root and store the relative path (Req 2.5).
            Path canonicalRoot = Paths.get("C:/xampp/htdocs/investi/uploads/products");
            Files.createDirectories(canonicalRoot);
            String ext = "";
            int dot = chosen.getName().lastIndexOf('.');
            if (dot >= 0) ext = chosen.getName().substring(dot);
            String filename = UUID.randomUUID() + ext;
            Path dest = canonicalRoot.resolve(filename);
            Files.copy(chosen.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            imageUrlField.setText("products/" + filename);
        } catch (IOException e) {
            showError("Upload failed", e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        User admin = currentUserOrAlertAdminRequired();
        if (admin == null) return;

        try {
            Product p = editing != null ? editing : new Product();
            p.setName(textOrEmpty(nameField));
            p.setDescription(textOrEmpty(descriptionField));
            p.setPrice(parseDouble(priceField.getText(), 0.0));
            p.setCurrency(comboValueOr(currencyCombo, "TND"));
            p.setCategory(comboValueOr(categoryCombo, "Uncategorized"));
            p.setStatus(comboValueOr(statusCombo, "draft"));
            p.setStock(parseInt(stockField.getText(), 0));
            p.setRemise(parseInt(remiseField.getText(), 0));
            p.setDigital(digitalCheckBox != null && digitalCheckBox.isSelected());
            p.setDownloadUrl(textOrEmpty(imageUrlField));
            if (editing == null && admin.getId() != null) {
                p.setEntrepreneurId(admin.getId());
            }

            if (p.getName() == null || p.getName().isBlank()) {
                showInfo("Name required", "Enter a product name before saving.");
                return;
            }
            if (p.getPrice() < 0) {
                showInfo("Invalid price", "Price must be zero or positive.");
                return;
            }

            if (editing == null) {
                productService.create(p, admin);
            } else {
                productService.update(p, admin);
            }
            showForm(false);
            clearForm();
            editing = null;
            loadProducts();
        } catch (SQLException e) {
            showError("Save failed", e.getMessage());
        } catch (SecurityException se) {
            showError("Access denied", se.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        showForm(false);
        clearForm();
        editing = null;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void showForm(boolean show) {
        if (formContainer != null) {
            formContainer.setVisible(show);
            formContainer.setManaged(show);
        }
    }

    private void clearForm() {
        if (nameField != null) nameField.clear();
        if (descriptionField != null) descriptionField.clear();
        if (priceField != null) priceField.clear();
        if (currencyCombo != null) currencyCombo.setValue("TND");
        if (categoryCombo != null) categoryCombo.setValue("Uncategorized");
        if (statusCombo != null) statusCombo.setValue("draft");
        if (stockField != null) stockField.clear();
        if (remiseField != null) remiseField.clear();
        if (digitalCheckBox != null) digitalCheckBox.setSelected(false);
        if (imageUrlField != null) imageUrlField.clear();
    }

    private User currentUserOrAlertAdminRequired() {
        User u = InvestiApp.getCurrentUser();
        if (u == null || !"admin".equalsIgnoreCase(u.getRole())) {
            showError("Admin access required", "Only administrators can manage products.");
            return null;
        }
        return u;
    }

    private static String textOrEmpty(TextInputControl field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    private static String comboValueOr(ComboBox<String> combo, String fallback) {
        if (combo == null) return fallback;
        Object v = combo.getValue();
        if (v == null) return fallback;
        String s = v.toString().trim();
        return s.isEmpty() ? fallback : s;
    }

    private static double parseDouble(String s, double fallback) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return fallback; }
    }

    private static int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return fallback; }
    }

    private void showError(String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }

    private void showInfo(String header, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}
