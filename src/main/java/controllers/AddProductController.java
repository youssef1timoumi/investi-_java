package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Product;
import services.ProductService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.UUID;

public class AddProductController {

    @FXML
    private TextField nameTf;
    @FXML
    private TextField priceTf;
    @FXML
    private TextArea descriptionTa;
    @FXML
    private ComboBox<String> statusCb;
    @FXML
    private TextField categoryTf;
    @FXML
    private ImageView imagePreview;
    @FXML
    private TextField imageUrlTf;
    @FXML
    private Label placeholderLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Button submitBtn;

    public static Product productToEdit = null;

    private final ProductService ps = new ProductService();
    private File selectedImageFile;

    @FXML
    void initialize() {
        statusCb.getItems().addAll("draft", "published", "archived");
        statusCb.setValue("draft");

        // Ensure preview looks good
        imagePreview.setPreserveRatio(true);

        if (productToEdit != null) {
            titleLabel.setText("📝 Modifier le Produit");
            submitBtn.setText("✓ Mettre à jour le Produit");

            nameTf.setText(productToEdit.getName());
            priceTf.setText(String.valueOf(productToEdit.getPrice()));
            categoryTf.setText(String.valueOf(productToEdit.getCategoryId()));
            descriptionTa.setText(productToEdit.getDescription());
            statusCb.setValue(productToEdit.getStatus());

            if (productToEdit.getDownloadUrl() != null && !productToEdit.getDownloadUrl().isEmpty()) {
                imageUrlTf.setText(productToEdit.getDownloadUrl());
            }
        }
    }

    @FXML
    void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(nameTf.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imageUrlTf.setText(file.getAbsolutePath());
            imagePreview.setImage(new Image(file.toURI().toString()));
            placeholderLabel.setVisible(false);
        }
    }

    @FXML
    void saveProduct() {
        try {
            // Basic validation
            if (nameTf.getText().isEmpty() || priceTf.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in the product name and price.");
                return;
            }

            String imagePath = "";
            if (selectedImageFile != null) {
                try {
                    String fileName = UUID.randomUUID().toString() + "_" + selectedImageFile.getName();
                    File destDir = new File("src/main/resources/uploads");
                    if (!destDir.exists())
                        destDir.mkdirs();

                    File destFile = new File(destDir, fileName);
                    Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    imagePath = "/uploads/" + fileName;
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to save image: " + e.getMessage());
                }
            }

            Product p = (productToEdit != null) ? productToEdit : new Product();
            p.setName(nameTf.getText());
            p.setDescription(descriptionTa.getText());
            p.setShortDescription("");
            p.setPrice(Double.parseDouble(priceTf.getText().trim()));
            p.setCurrency("TND");
            p.setDigital(false);
            if (!imagePath.isEmpty()) {
                p.setDownloadUrl(imagePath);
            }
            p.setProjectId(1L);
            p.setEntrepreneurId(1L);
            p.setCategoryId(categoryTf.getText().isEmpty() ? 1L : Long.parseLong(categoryTf.getText().trim()));
            p.setStatus(statusCb.getValue());

            if (productToEdit != null) {
                ps.update(p);
            } else {
                ps.create(p);
            }

            // Success feedback
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle(productToEdit != null ? "Product Updated" : "Product Saved");
            success.setHeaderText(null);
            success.setContentText("Product '" + p.getName() + "' has been successfully "
                    + (productToEdit != null ? "updated." : "added."));
            success.showAndWait();

            productToEdit = null;

            resetForm();
            navigateShowProducts();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error",
                    "Please ensure the price and category ID are valid numbers.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save product: " + e.getMessage());
        }
    }

    @FXML
    void resetForm() {
        productToEdit = null;
        nameTf.clear();
        priceTf.clear();
        categoryTf.clear();
        descriptionTa.clear();
        imageUrlTf.clear();
        statusCb.setValue("draft");
        selectedImageFile = null;
        imagePreview.setImage(null);
        placeholderLabel.setVisible(true);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void navigateShowProducts() {
        DashboardController.getInstance().showProducts();
    }
}
