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
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class ShowProductsController {
    private final ProductService ps = new ProductService();

    @FXML
    private TilePane productGrid;
    @FXML
    private VBox emptyState;
    @FXML
    private Button btnFilterAll, btnFilterDigital, btnFilterPhysical, btnFilterActive;

    private String currentFilter = "all";

    @FXML
    void initialize() {
        refreshData();
    }

    public void refreshData() {
        try {
            List<Product> products = ps.read();
            List<Product> filteredProducts;

            switch (currentFilter) {
                case "digital":
                    filteredProducts = products.stream().filter(Product::isDigital).toList();
                    break;
                case "physical":
                    filteredProducts = products.stream().filter(p -> !p.isDigital()).toList();
                    break;
                case "active":
                    filteredProducts = products.stream().filter(p -> "active".equalsIgnoreCase(p.getStatus())).toList();
                    break;
                default:
                    filteredProducts = products;
                    break;
            }

            productGrid.getChildren().clear();
            if (filteredProducts.isEmpty()) {
                emptyState.setVisible(true);
            } else {
                emptyState.setVisible(false);
                for (Product p : filteredProducts) {
                    productGrid.getChildren().add(createProductCard(p));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Loading Error", "Failed to refresh product list: " + e.getMessage());
        }
    }

    private VBox createProductCard(Product p) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setPrefWidth(300);
        card.setSpacing(0);

        // Image Area
        VBox imageArea = new VBox();
        imageArea.getStyleClass().add("product-image-area");
        imageArea.setPrefHeight(180);

        String emoji = getEmoji(p.getName());
        Label emojiLbl = new Label(emoji);
        emojiLbl.getStyleClass().add("product-image-emoji");

        // Try to load image if URL exists
        ImageView imageView = new ImageView();
        imageView.setFitHeight(180);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);
        boolean hasImage = false;

        if (p.getDownloadUrl() != null && !p.getDownloadUrl().isEmpty()) {
            // Simplified loading for now to focus on layout
            try {
                String rawUrl = p.getDownloadUrl().trim();
                Image img = null;
                if (rawUrl.startsWith("http")) {
                    img = new Image(rawUrl, true);
                } else {
                    // Fallback to file loading logic if nested in resources
                }
                if (img != null && !img.isError()) {
                    imageView.setImage(img);
                    hasImage = true;
                }
            } catch (Exception e) {
            }
        }

        if (hasImage) {
            imageArea.getChildren().add(imageView);
        } else {
            imageArea.getChildren().add(emojiLbl);
        }
        imageArea.setAlignment(Pos.CENTER);

        // Content Area
        VBox contentArea = new VBox();
        contentArea.getStyleClass().add("product-content-area");
        contentArea.setSpacing(8);

        Label nameLbl = new Label(p.getName());
        nameLbl.getStyleClass().add("product-name-label");

        Label shortDescLbl = new Label(p.getShortDescription() != null ? p.getShortDescription() : "");
        shortDescLbl.getStyleClass().add("product-desc-short");
        shortDescLbl.setWrapText(true);

        // Stats
        HBox statsRow = new HBox(15);
        statsRow.getStyleClass().add("product-stats-row");
        Label viewsLbl = new Label("👁️ " + p.getViewsCount());
        viewsLbl.getStyleClass().add("stat-label");
        Label salesLbl = new Label("💰 " + p.getSalesCount());
        salesLbl.getStyleClass().add("stat-label");
        statsRow.getChildren().addAll(viewsLbl, salesLbl);

        // Price
        HBox priceRow = new HBox(5);
        priceRow.setAlignment(Pos.BASELINE_LEFT);
        Label priceLbl = new Label(String.format("%.2f", p.getPrice()));
        priceLbl.getStyleClass().add("product-price-large");
        Label currencyLbl = new Label(p.getCurrency());
        currencyLbl.getStyleClass().add("product-currency-small");
        priceRow.getChildren().addAll(priceLbl, currencyLbl);

        // Badges
        HBox badgeRow = new HBox(8);
        Label typeBadge = new Label(p.isDigital() ? "⬇️ Digital" : "📦 Physical");
        typeBadge.getStyleClass().addAll("badge", p.isDigital() ? "badge-digital" : "badge-physical");

        Label statusBadge = new Label(p.getStatus().equalsIgnoreCase("active") ? "✓ Active" : "✗ Inactive");
        statusBadge.getStyleClass().addAll("badge", "badge-" + p.getStatus().toLowerCase());
        badgeRow.getChildren().addAll(typeBadge, statusBadge);

        // Actions
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button editBtn = new Button("✏️");
        editBtn.getStyleClass().add("secondary-button");
        editBtn.setOnAction(e -> {
            AddProductController.productToEdit = p;
            DashboardController.getInstance().loadView("/AddProduct.fxml", "Modifier le Produit");
        });

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().add("danger-button");
        deleteBtn.setOnAction(e -> {
            try {
                ps.delete((int) p.getId());
                refreshData();
            } catch (SQLException ex) {
                showAlert("Deletion Error", "Could not delete: " + ex.getMessage());
            }
        });

        actions.getChildren().addAll(editBtn, deleteBtn);

        contentArea.getChildren().addAll(nameLbl, shortDescLbl, statsRow, priceRow, badgeRow, actions);
        card.getChildren().addAll(imageArea, contentArea);

        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
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
            }
        });

        return card;
    }

    private String getEmoji(String type) {
        String name = type.toLowerCase();
        if (name.contains("course"))
            return "📚";
        if (name.contains("livre") || name.contains("book"))
            return "📖";
        if (name.contains("montre") || name.contains("watch"))
            return "⌚";
        if (name.contains("template"))
            return "⚡";
        if (name.contains("portfolio"))
            return "🎨";
        if (name.contains("icons"))
            return "🎨";
        if (name.contains("keyboard") || name.contains("clavier"))
            return "⌨️";
        if (name.contains("guide"))
            return "📘";
        return "📦";
    }

    @FXML
    private void handleFilterAll() {
        updateFilter("all", btnFilterAll);
    }

    @FXML
    private void handleFilterDigital() {
        updateFilter("digital", btnFilterDigital);
    }

    @FXML
    private void handleFilterPhysical() {
        updateFilter("physical", btnFilterPhysical);
    }

    @FXML
    private void handleFilterActive() {
        updateFilter("active", btnFilterActive);
    }

    private void updateFilter(String filter, Button activeBtn) {
        currentFilter = filter;
        btnFilterAll.getStyleClass().remove("active");
        btnFilterDigital.getStyleClass().remove("active");
        btnFilterPhysical.getStyleClass().remove("active");
        btnFilterActive.getStyleClass().remove("active");
        activeBtn.getStyleClass().add("active");
        refreshData();
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
