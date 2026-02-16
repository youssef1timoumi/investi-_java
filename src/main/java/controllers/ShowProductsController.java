package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import models.Product;
import services.ProductService;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ShowProductsController {
    private final ProductService ps = new ProductService();

    @FXML
    private TilePane productGrid;

    @FXML
    void initialize() {
        refreshData();
    }

    public void refreshData() {
        try {
            List<Product> products = ps.read();
            productGrid.getChildren().clear();
            for (Product p : products) {
                productGrid.getChildren().add(createProductCard(p));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Loading Error", "Failed to refresh product list: " + e.getMessage());
        }
    }

    private VBox createProductCard(Product p) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setPrefWidth(220);
        card.setSpacing(0);

        // Image Container
        StackPane imgStack = new StackPane();
        imgStack.getStyleClass().add("product-card-image-container");
        imgStack.setPrefHeight(180);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(160);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Placeholder for when image fails
        StackPane placeholder = new StackPane();
        placeholder.getStyleClass().add("product-card-image-container");
        Text placeholderText = new Text("No Image");
        placeholderText.setStyle("-fx-fill: #ADB5BD; -fx-font-weight: bold;");
        placeholder.getChildren().add(placeholderText);

        // Load image from path or fallback
        boolean loaded = false;
        if (p.getDownloadUrl() != null && !p.getDownloadUrl().isEmpty()) {
            String rawUrl = p.getDownloadUrl().trim();

            try {
                // 1. Web or URL-prefixed paths
                if (rawUrl.startsWith("http") || rawUrl.startsWith("https") || rawUrl.startsWith("file:")) {
                    Image img = new Image(rawUrl, true);
                    imageView.setImage(img);
                    loaded = true;
                } else {
                    // 2. Local File Scanning with Hyper-Verbose Diagnostics
                    String cleanPath = rawUrl.replace("\\", "/");
                    if (cleanPath.startsWith("/"))
                        cleanPath = cleanPath.substring(1);

                    String userDir = System.getProperty("user.dir").replace("\\", "/");
                    java.util.List<String> bases = java.util.Arrays.asList(
                            userDir + "/",
                            userDir + "/src/main/resources/",
                            userDir + "/target/classes/",
                            "",
                            "src/main/resources/",
                            "target/classes/",
                            "uploads/");

                    for (String base : bases) {
                        // Try both the cleanPath as is, and with "uploads/" prefix if missing
                        java.util.List<String> subPaths = new java.util.ArrayList<>();
                        subPaths.add(cleanPath);
                        if (!cleanPath.startsWith("uploads/")) {
                            subPaths.add("uploads/" + cleanPath);
                        }

                        for (String sub : subPaths) {
                            try {
                                java.nio.file.Path path = java.nio.file.Paths.get(base + sub).toAbsolutePath()
                                        .normalize();
                                if (java.nio.file.Files.exists(path) && java.nio.file.Files.isRegularFile(path)) {
                                    // Found a file! Try to load it via FileInputStream (more resilient)
                                    try (java.io.FileInputStream fis = new java.io.FileInputStream(path.toFile())) {
                                        Image img = new Image(fis);
                                        if (!img.isError()) {
                                            imageView.setImage(img);
                                            loaded = true;
                                            break;
                                        } else {
                                            System.err.println("❌ DECODE ERROR for [" + p.getName() + "] at " + path);
                                            if (img.getException() != null)
                                                img.getException().printStackTrace();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // Skip invalid paths
                            }
                        }
                        if (loaded)
                            break;
                    }

                    // 3. Classpath Fallback (Final attempt)
                    if (!loaded) {
                        String resPath = cleanPath.startsWith("/") ? cleanPath : "/" + cleanPath;
                        java.net.URL resource = getClass().getResource(resPath);
                        if (resource == null && !resPath.startsWith("/uploads/")) {
                            resource = getClass().getResource("/uploads" + resPath);
                        }
                        if (resource != null) {
                            Image img = new Image(resource.toExternalForm());
                            if (!img.isError()) {
                                imageView.setImage(img);
                                loaded = true;
                            }
                        }
                    }

                    // 4. Final Diagnostic Summary on Failure
                    if (!loaded) {
                        System.err.println("❌ TOTAL FAILURE: Image not found for [" + p.getName() + "]");
                        System.err.println("   DB Value: " + rawUrl);
                        System.err.println("   Working Dir: " + userDir);
                        System.err.println("   Normalized: " + cleanPath);
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error during loading [" + rawUrl + "]: " + e.getMessage());
            }
        }

        if (loaded) {
            imgStack.getChildren().add(imageView);
        } else {
            imgStack.getChildren().add(placeholder);
        }

        imgStack.setAlignment(Pos.CENTER);

        // Details Container
        VBox details = new VBox();
        details.getStyleClass().add("product-card-details");

        Label nameLbl = new Label(p.getName());
        nameLbl.getStyleClass().add("product-card-name");

        Label priceLbl = new Label(String.format("%.2f %s", p.getPrice(), p.getCurrency()));
        priceLbl.getStyleClass().add("product-card-price");

        Label statusBadge = new Label(p.getStatus().toUpperCase());
        statusBadge.getStyleClass().addAll("product-card-status", "status-" + p.getStatus().toLowerCase());

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button editBtn = new Button("✏️");
        editBtn.getStyleClass().add("secondary-button");
        editBtn.setStyle("-fx-font-size: 11; -fx-padding: 5 10;");
        editBtn.setOnAction(e -> {
            AddProductController.productToEdit = p;
            DashboardController.getInstance().loadView("/AddProduct.fxml", "Modifier le Produit");
        });

        Button buyBtn = new Button("Buy");
        buyBtn.getStyleClass().add("primary-button");
        buyBtn.setStyle("-fx-font-size: 11; -fx-padding: 5 15;");
        buyBtn.setOnAction(e -> {
            AddSaleController.preSelectedProduct = p;
            DashboardController.getInstance().loadView("/AddSale.fxml", "Create New Sale");
        });

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().add("danger-button");
        deleteBtn.setStyle("-fx-font-size: 11; -fx-padding: 5 10;");
        deleteBtn.setOnAction(e -> {
            try {
                ps.delete((int) p.getId());
                refreshData();
            } catch (SQLException ex) {
                showAlert("Deletion Error", "Could not delete: " + ex.getMessage());
            }
        });

        actions.getChildren().addAll(editBtn, buyBtn, deleteBtn);

        card.setStyle("-fx-cursor: hand;");
        card.setOnMouseClicked(e -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/ProductDetails.fxml"));
                javafx.scene.Parent root = loader.load();

                ProductDetailsController controller = loader.getController();
                controller.setProduct(p);

                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Détails: " + p.getName());
                stage.setScene(new javafx.scene.Scene(root));
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.show();
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        });

        details.getChildren().addAll(nameLbl, priceLbl, statusBadge, actions);
        VBox.setVgrow(details, Priority.ALWAYS);

        card.getChildren().addAll(imgStack, details);
        return card;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void navigateAddProduct() {
        AddProductController.productToEdit = null;
        DashboardController.getInstance().loadView("/AddProduct.fxml", "Add New Product");
    }

    @FXML
    void navigateShowSales() {
        DashboardController.getInstance().showSales();
    }
}
