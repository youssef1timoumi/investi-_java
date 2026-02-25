package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Pos;
import models.Product;
import models.Sale;
import services.ProductService;
import services.SaleService;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML
    private ScrollPane productsPage;
    @FXML
    private FlowPane productGrid;
    @FXML
    private VBox cartPage, cartItemsContainer;
    @FXML
    private HBox mainHeader, filtersContainer, categoryContainer;
    @FXML
    private Label cartCountLabel, totalProductsLabel, totalRevenueLabel;
    @FXML
    private Button btnAddNewProduct;
    @FXML
    private VBox addProductPage;

    private final ProductService productService = new ProductService();
    private final SaleService saleService = new SaleService();

    // Sidebar items
    @FXML
    private Button navProducts, navHistory, navCart, navSettings;

    // Details Overlay
    @FXML
    private StackPane rootStack;
    @FXML
    private VBox detailOverlay;
    @FXML
    private ImageView detailImage;
    @FXML
    private Label detailTitle, detailShortDesc, detailStatus, detailPrice, detailCurrency;
    @FXML
    private Label detailProjectId, detailEntrepreneurId, detailViews, detailSales, detailCreated;
    @FXML
    private TextFlow detailFullDesc;
    @FXML
    private Button buyButton;

    // Order History Page
    @FXML
    private VBox orderHistoryPage, ordersContainer;

    private List<Product> allProducts = new ArrayList<>();
    private List<Sale> allSales = new ArrayList<>();
    private String activeCategory = "All";
    private String activeTab = "products";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadDatabaseData();
            setupCategories();
            setupSidebar();
            renderProducts();

            // Ensure overlay is hidden initially
            if (detailOverlay != null)
                detailOverlay.setVisible(false);
        } catch (Throwable t) {
            System.err.println("FATAL ERROR during initialization:");
            t.printStackTrace();
        }
    }

    private void loadDatabaseData() {
        try {
            if (productService == null) {
                System.err.println("DEBUG: productService is NULL!");
                return;
            }
            allProducts = productService.read();

            if (saleService == null) {
                System.err.println("DEBUG: saleService is NULL!");
            } else {
                allSales = saleService.read();
            }

            System.out.println("DEBUG: Loaded " + (allProducts != null ? allProducts.size() : 0) + " products.");
            System.out.println("DEBUG: Loaded " + (allSales != null ? allSales.size() : 0) + " sales.");

            // Update stats
            if (totalProductsLabel != null && allProducts != null)
                totalProductsLabel.setText(String.valueOf(allProducts.size()));
            if (totalRevenueLabel != null && allSales != null) {
                double total = allSales.stream().mapToDouble(Sale::getTotalAmount).sum();
                totalRevenueLabel.setText(String.format("%.2f", total));
            }
        } catch (SQLException e) {
            System.err.println("DEBUG: SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("DEBUG: Unexpected Error in loadDatabaseData:");
            t.printStackTrace();
        }
    }

    private void setupCategories() {
        String[] cats = { "All", "Software", "Analytics", "Web", "Design", "Mobile" };
        categoryContainer.getChildren().clear();
        for (String cat : cats) {
            Button btn = new Button(cat);
            btn.getStyleClass().add("category-btn");
            if (cat.equals(activeCategory))
                btn.getStyleClass().add("active");

            btn.setOnAction(e -> {
                activeCategory = cat;
                setupCategories();
                renderProducts();
            });
            categoryContainer.getChildren().add(btn);
        }
    }

    private void setupSidebar() {
        navProducts.setOnAction(e -> switchTab("products", navProducts));
        navHistory.setOnAction(e -> switchTab("history", navHistory));
        navCart.setOnAction(e -> switchTab("cart", navCart));
    }

    @FXML
    private void handleHistoryClick() {
        switchTab("history", navHistory);
    }

    private void switchTab(String tab, Button activeBtn) {
        activeTab = tab;

        // Reset nav styles
        navProducts.getStyleClass().remove("active");
        navCart.getStyleClass().remove("active");
        if (navHistory != null)
            navHistory.getStyleClass().remove("active");
        if (navSettings != null)
            navSettings.getStyleClass().remove("active");

        activeBtn.getStyleClass().add("active");

        // Hide all pages
        if (productsPage != null) {
            productsPage.setVisible(false);
            productsPage.setManaged(false);
        }
        if (cartPage != null) {
            cartPage.setVisible(false);
            cartPage.setManaged(false);
        }
        if (orderHistoryPage != null) {
            orderHistoryPage.setVisible(false);
            orderHistoryPage.setManaged(false);
        }
        if (addProductPage != null) {
            addProductPage.setVisible(false);
            addProductPage.setManaged(false);
        }

        // Show selected
        if (tab.equals("products") && productsPage != null) {
            productsPage.setVisible(true);
            productsPage.setManaged(true);
        } else if (tab.equals("cart") && cartPage != null) {
            cartPage.setVisible(true);
            cartPage.setManaged(true);
        } else if (tab.equals("history") && orderHistoryPage != null) {
            orderHistoryPage.setVisible(true);
            orderHistoryPage.setManaged(true);
            renderOrders();
        } else if (tab.equals("add-product") && addProductPage != null) {
            addProductPage.setVisible(true);
            addProductPage.setManaged(true);
            if (mainHeader != null) {
                mainHeader.setVisible(false);
                mainHeader.setManaged(false);
            }
            if (filtersContainer != null) {
                filtersContainer.setVisible(false);
                filtersContainer.setManaged(false);
            }
        }

        // Restore header/filters if not in add-product
        if (!tab.equals("add-product")) {
            if (mainHeader != null) {
                mainHeader.setVisible(true);
                mainHeader.setManaged(true);
            }
            if (filtersContainer != null) {
                filtersContainer.setVisible(true);
                filtersContainer.setManaged(true);
            }
        }
    }

    @FXML
    private void handleNewProduct() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/add-product.fxml"));
            VBox form = loader.load();

            AddProductController controller = loader.getController();
            controller.setOnProductAdded(v -> {
                loadDatabaseData();
                renderProducts();
                switchTab("products", navProducts);
            });
            controller.setOnCancel(() -> switchTab("products", navProducts));

            addProductPage.getChildren().setAll(form);
            switchTab("add-product", btnAddNewProduct);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void renderProducts() {
        productGrid.getChildren().clear();
        List<Product> filtered = allProducts.stream()
                .filter(p -> activeCategory.equals("All")
                        || (p.getCategoryName() != null && p.getCategoryName().equals(activeCategory)))
                .collect(Collectors.toList());

        System.out.println("DEBUG: Rendering " + filtered.size() + " products for category: " + activeCategory);
        filtered.forEach(p -> productGrid.getChildren().add(createProductCard(p)));
    }

    private VBox createProductCard(Product p) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> showProductDetails(p));

        StackPane imgContainer = new StackPane();
        imgContainer.getStyleClass().add("card-img-container");
        imgContainer.setStyle("-fx-background-color: " + mapTailwindGradient(p.getGradient()) + ";");

        String imageUrl = p.getImage();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "https://via.placeholder.com/180"; // Fallback URL
        }

        ImageView iv = new ImageView();
        try {
            iv.setImage(new Image(imageUrl, true));
        } catch (Exception e) {
            System.err.println("DEBUG: Failed to load image: " + imageUrl);
            iv.setImage(new Image("https://via.placeholder.com/180", true));
        }

        iv.setFitWidth(180);
        iv.setFitHeight(180);
        iv.setPreserveRatio(true);
        iv.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.2)));

        Label badge = new Label(p.getCategoryName());
        badge.getStyleClass().add("category-badge");
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);

        imgContainer.getChildren().addAll(iv, badge);

        VBox content = new VBox(15);
        content.getStyleClass().add("card-content");

        Label title = new Label(p.getTitle());
        title.getStyleClass().add("card-title");

        Label desc = new Label(p.getShortDescription());
        desc.getStyleClass().add("card-desc");
        desc.setWrapText(true);

        HBox footer = new HBox();
        footer.getStyleClass().add("card-footer");
        footer.setAlignment(Pos.CENTER_LEFT);

        Label price = new Label("$" + p.getPrice());
        price.getStyleClass().add("card-price");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("add-btn");
        FontIcon cartIcon = new FontIcon("fth-shopping-cart");
        addBtn.setGraphic(cartIcon);
        addBtn.setOnAction(e -> {
            e.consume();
            addToCart(p);
        });

        footer.getChildren().addAll(price, spacer, addBtn);
        content.getChildren().addAll(title, desc, footer);
        card.getChildren().addAll(imgContainer, content);

        return card;
    }

    private void showProductDetails(Product p) {
        if (detailOverlay == null)
            return;

        detailImage.setImage(new Image(p.getImage(), true));
        detailTitle.setText(p.getName());
        detailShortDesc.setText(p.getShortDescription());
        detailStatus.setText(p.getStatus());
        detailPrice.setText(String.valueOf(p.getPrice()));
        detailCurrency.setText(p.getCurrency());
        detailProjectId.setText("#" + p.getProjectId());
        detailEntrepreneurId.setText("#" + p.getEntrepreneurId());
        detailViews.setText(String.valueOf(p.getViewsCount()));
        detailSales.setText(String.valueOf(p.getSalesCount()));

        if (p.getCreatedAt() != null) {
            detailCreated.setText(p.getCreatedAt().toLocalDateTime().toLocalDate().toString());
        }

        detailFullDesc.getChildren().clear();
        Label fullDescLabel = new Label(p.getDescription());
        fullDescLabel.getStyleClass().add("detail-description");
        fullDescLabel.setWrapText(true);
        detailFullDesc.getChildren().add(fullDescLabel);

        detailOverlay.setVisible(true);
        detailOverlay.toFront();
    }

    @FXML
    private void hideProductDetails() {
        if (detailOverlay != null)
            detailOverlay.setVisible(false);
    }

    private void renderOrders() {
        if (ordersContainer == null) {
            System.err.println("DEBUG: ordersContainer is NULL!");
            return;
        }
        System.out.println("DEBUG: Rendering " + allSales.size() + " sales...");
        ordersContainer.getChildren().clear();

        for (Sale s : allSales) {
            VBox card = new VBox(20);
            card.getStyleClass().add("order-card");

            // Header
            HBox header = new HBox();
            header.getStyleClass().add("order-header");

            VBox refBox = new VBox(5);
            Label ref = new Label(s.getReference());
            ref.getStyleClass().add("order-ref");
            Label date = new Label("Ordered on "
                    + (s.getCreatedAt() != null ? s.getCreatedAt().toLocalDateTime().toLocalDate() : "N/A"));
            date.getStyleClass().add("order-date");
            refBox.getChildren().addAll(ref, date);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label status = new Label(s.getStatus().toUpperCase());
            status.getStyleClass().add("status-chip");
            if (s.getStatus().equalsIgnoreCase("Completed"))
                status.getStyleClass().add("status-completed");
            else if (s.getStatus().equalsIgnoreCase("Pending"))
                status.getStyleClass().add("status-pending");
            else
                status.getStyleClass().add("status-cancelled");

            header.getChildren().addAll(refBox, spacer, status);

            // Details Grid
            GridPane grid = new GridPane();
            grid.getStyleClass().add("order-grid");

            addGridRow(grid, 0, "Payment Method", s.getPaymentMethod());
            addGridRow(grid, 1, "Transaction ID", s.getTransactionId());
            addGridRow(grid, 2, "Shipping Address", s.getShippingAddress());
            addGridRow(grid, 3, "Payment Status", s.getPaymentStatus());

            // Footer
            HBox footer = new HBox();
            footer.getStyleClass().add("order-total-box");
            Label totalLabel = new Label("Total Amount: ");
            totalLabel.getStyleClass().add("order-total-label");
            Label totalAmount = new Label(s.getCurrency() + " " + String.format("%.2f", s.getTotalAmount()));
            totalAmount.getStyleClass().add("order-total-amount");
            footer.getChildren().addAll(totalLabel, totalAmount);

            card.getChildren().addAll(header, new Separator(), grid, new Separator(), footer);
            ordersContainer.getChildren().add(card);
        }
    }

    private void addGridRow(GridPane grid, int row, String labelText, String valueText) {
        Label label = new Label(labelText);
        label.getStyleClass().add("grid-label");
        Label value = new Label(valueText);
        value.getStyleClass().add("grid-value");
        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private String mapTailwindGradient(String tw) {
        if (tw == null)
            return "linear-gradient(to bottom right, #ffffff, #f7f0f5)";
        if (tw.contains("blue-600") || tw.contains("purple-600"))
            return "linear-gradient(to bottom right, #f0f4f8, #d9e2ec)"; // Slate variations
        if (tw.contains("orange-600") || tw.contains("yellow-600"))
            return "linear-gradient(to bottom right, #fdfaf3, #f5ead3)"; // Gold variations
        if (tw.contains("pink-600") || tw.contains("red-600"))
            return "linear-gradient(to bottom right, #faf5f6, #f2e3e5)"; // Rose variations
        return "linear-gradient(to bottom right, #ffffff, #f7f0f5)";
    }

    private void addToCart(Product p) {
        int currentCount = Integer.parseInt(cartCountLabel.getText());
        cartCountLabel.setText(String.valueOf(currentCount + 1));
    }

    @FXML
    private void handleCartClick() {
        switchTab("cart", navCart);
    }
}
