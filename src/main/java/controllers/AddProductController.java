package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Product;
import services.ProductService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.Consumer;

import org.json.JSONObject;

public class AddProductController implements Initializable {

    @FXML
    private TextField nameField, titleField, shortDescField, imageField, priceField, stockField;
    @FXML
    private TextArea fullDescField;
    @FXML
    private ComboBox<String> currencyCombo, categoryCombo, statusCombo, gradientCombo;
    @FXML
    private CheckBox digitalCheck;
    @FXML
    private Label nameError, priceError;

    private final ProductService productService = new ProductService();
    private final services.NavyApiService navyApiService = new services.NavyApiService();
    private Consumer<Void> onProductAdded;
    private Runnable onCancel;
    private File selectedFile;
    private Product existingProduct;

    @FXML
    private Button saveBtn;
    @FXML
    private Label formTitle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize combos
        currencyCombo.getItems().addAll("USD", "TND", "EUR", "GBP");
        currencyCombo.setValue("USD");

        categoryCombo.getItems().addAll("Software", "Analytics", "Web", "Design", "Mobile");
        categoryCombo.setValue("Software");

        statusCombo.getItems().addAll("published", "draft", "retired");
        statusCombo.setValue("published");

        gradientCombo.getItems().addAll("blue-600", "orange-600", "pink-600", "purple-600", "slate-600");
        gradientCombo.setValue("blue-600");
    }

    public void setProductData(Product p) {
        this.existingProduct = p;
        if (p != null) {
            nameField.setText(p.getName());
            if (titleField != null)
                titleField.setText(p.getName()); // titleField might be used for something else now?
            shortDescField.setText(p.getShortDescription());
            fullDescField.setText(p.getDescription());
            priceField.setText(String.valueOf(p.getPrice()));
            if (stockField != null)
                stockField.setText(String.valueOf(p.getStock()));
            currencyCombo.setValue(p.getCurrency());
            digitalCheck.setSelected(p.isDigital());
            statusCombo.setValue(p.getStatus());
            gradientCombo.setValue(p.getGradient());
            imageField.setText(p.getImage() != null ? "Existing Image" : "");

            categoryCombo.setValue(mapIdToCategory(p.getCategoryId()));

            if (saveBtn != null)
                saveBtn.setText("Update Product");
            if (formTitle != null)
                formTitle.setText("Edit Product");
        }
    }

    private String mapIdToCategory(long id) {
        return switch ((int) id) {
            case 1 -> "Software";
            case 2 -> "Analytics";
            case 3 -> "Web";
            case 4 -> "Design";
            case 5 -> "Mobile";
            default -> "Software";
        };
    }

    public void setOnProductAdded(Consumer<Void> onProductAdded) {
        this.onProductAdded = onProductAdded;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        Stage stage = (Stage) nameField.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            imageField.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleGenerateTitle() {
        if (selectedFile == null) {
            showAlert(Alert.AlertType.WARNING, "No Image", "Veuillez d'abord sélectionner une image (Browse) !");
            return;
        }
        String prompt = "Generate a short, catchy, and professional product title (max 5 words) based on this image. Return ONLY the title, no quotes or explanations.";
        nameField.setPromptText("Génération en cours...");
        nameField.setDisable(true);

        navyApiService.generateContentFromImage(selectedFile, prompt)
                .thenAccept(title -> {
                    javafx.application.Platform.runLater(() -> {
                        nameField.setText(title.replace("\"", ""));
                        nameField.setDisable(false);
                    });
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() -> {
                        nameField.setDisable(false);
                        showAlert(Alert.AlertType.ERROR, "Erreur IA", "Échec : " + ex.getMessage());
                    });
                    return null;
                });
    }

    @FXML
    private void handleGenerateDescription() {
        if (selectedFile == null) {
            showAlert(Alert.AlertType.WARNING, "No Image", "Veuillez d'abord sélectionner une image (Browse) !");
            return;
        }
        String prompt = "Write a comprehensive, engaging product description based on this image. Highlight potential features and benefits in French. Use professional marketing language. Return ONLY the description text.";
        fullDescField.setPromptText("Génération de la description en cours...");
        fullDescField.setDisable(true);

        navyApiService.generateContentFromImage(selectedFile, prompt)
                .thenAccept(desc -> {
                    javafx.application.Platform.runLater(() -> {
                        fullDescField.setText(desc);
                        fullDescField.setDisable(false);
                    });
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() -> {
                        fullDescField.setDisable(false);
                        showAlert(Alert.AlertType.ERROR, "Erreur IA", "Échec : " + ex.getMessage());
                    });
                    return null;
                });
    }

    @FXML
    private void handleSave() {
        if (!validateForm())
            return;

        double price = Double.parseDouble(priceField.getText());
        String name = nameField.getText();
        String description = fullDescField.getText();
        String currency = currencyCombo.getValue();

        saveBtn.setText("Validating (AI)...");
        saveBtn.setDisable(true);

        navyApiService.validateProductContext(selectedFile, name, description, price, currency)
                .thenAccept(jsonResult -> {
                    javafx.application.Platform.runLater(() -> {
                        boolean isValid = jsonResult.optBoolean("valid", false);
                        String reason = jsonResult.optString("reason", "Le produit n'est pas cohérent.");

                        if (isValid) {
                            proceedWithSave(price);
                        } else {
                            saveBtn.setDisable(false);
                            saveBtn.setText(existingProduct == null ? "Create Product" : "Update Product");
                            showAlert(Alert.AlertType.WARNING, "Validation Rejetée par l'IA", reason);
                        }
                    });
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() -> {
                        saveBtn.setDisable(false);
                        saveBtn.setText(existingProduct == null ? "Create Product" : "Update Product");
                        showAlert(Alert.AlertType.ERROR, "Erreur IA",
                                "Impossible de valider le produit : " + ex.getMessage());
                    });
                    return null;
                });
    }

    private void proceedWithSave(double price) {
        try {
            String imagePath = existingProduct != null ? existingProduct.getImage() : "https://via.placeholder.com/180";

            if (selectedFile != null) {
                // Ensure uploads directory exists
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // Generative unique filename to avoid overwrites
                String fileName = UUID.randomUUID().toString() + "_" + selectedFile.getName();
                Path targetPath = uploadDir.resolve(fileName);

                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = targetPath.toAbsolutePath().toUri().toString();
            }

            Product p = existingProduct != null ? existingProduct : new Product();
            p.setName(nameField.getText());
            p.setDescription(fullDescField.getText());
            p.setShortDescription(shortDescField.getText());
            p.setPrice(Double.parseDouble(priceField.getText()));
            try {
                if (stockField != null && !stockField.getText().isEmpty()) {
                    p.setStock(Integer.parseInt(stockField.getText()));
                } else {
                    p.setStock(0);
                }
            } catch (NumberFormatException ignored) {
                p.setStock(0);
            }
            p.setCurrency(currencyCombo.getValue());
            p.setDigital(digitalCheck.isSelected());
            p.setStatus(statusCombo.getValue());
            p.setImage(imagePath);
            p.setGradient(gradientCombo.getValue());

            p.setCategoryId(mapCategoryToId(categoryCombo.getValue()));
            p.setProjectId(1);
            p.setEntrepreneurId(1);

            if (existingProduct == null) {
                productService.create(p);
            } else {
                productService.update(p);
            }

            if (onProductAdded != null) {
                onProductAdded.accept(null);
            }
        } catch (SQLException | NumberFormatException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save product: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        if (onCancel != null)
            onCancel.run();
    }

    @FXML
    private void handleBack() {
        handleCancel();
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (nameField.getText().isEmpty()) {
            nameError.setText("Product name is required");
            nameError.setVisible(true);
            nameError.setManaged(true);
            isValid = false;
        } else {
            nameError.setVisible(false);
            nameError.setManaged(false);
        }

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price < 0) {
                priceError.setText("Price cannot be negative");
                priceError.setVisible(true);
                priceError.setManaged(true);
                isValid = false;
            } else {
                priceError.setVisible(false);
                priceError.setManaged(false);
            }
        } catch (NumberFormatException e) {
            priceError.setText("Invalid price format");
            priceError.setVisible(true);
            priceError.setManaged(true);
            isValid = false;
        }

        return isValid;
    }

    private long mapCategoryToId(String cat) {
        return switch (cat) {
            case "Software" -> 1;
            case "Analytics" -> 2;
            case "Web" -> 3;
            case "Design" -> 4;
            case "Mobile" -> 5;
            default -> 1;
        };
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);

        Label label = new Label(content);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);

        // Ensure sufficient width for readability
        alert.getDialogPane().setPrefWidth(500);
        alert.getDialogPane().setContent(label);

        alert.show();
    }
}
