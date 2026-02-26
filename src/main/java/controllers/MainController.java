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
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.io.File;
import utils.QRCodeUtils;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML
    private ScrollPane productsPage;
    @FXML
    private FlowPane productGrid;
    @FXML
    private VBox cartPage, cartItemsContainer;
    @FXML
    private HBox mainHeader, filtersContainer, categoryContainer, saleFiltersContainer, saleCategoryContainer,
            historyHeader;
    @FXML
    private Label cartCountLabel, totalProductsLabel, totalRevenueLabel, totalSalesCountLabel;
    @FXML
    private Button btnAddNewProduct;
    @FXML
    private VBox addProductPage;
    @FXML
    private HBox titleBar;

    private final ProductService productService = new ProductService();
    private final SaleService saleService = new SaleService();
    private double xOffset = 0;
    private double yOffset = 0;

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
    private Label detailViews, detailSales, detailCreated;
    @FXML
    private TextFlow detailFullDesc;

    // Order History Page
    @FXML
    private VBox orderHistoryPage, ordersContainer;

    private List<Product> allProducts = new ArrayList<>();
    private List<Sale> allSales = new ArrayList<>();
    private String activeCategory = "All";
    private String activeSaleStatus = "All";
    private String activeTab = "products";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupWindowDragging();
        try {
            loadDatabaseData();
            setupCategories();
            setupSaleFilters();
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
            if (totalSalesCountLabel != null && allSales != null)
                totalSalesCountLabel.setText(String.valueOf(allSales.size()));
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

    private void setupSaleFilters() {
        if (saleCategoryContainer == null)
            return;
        String[] statuses = { "All", "Paid", "Completed", "Pending", "Cancelled" };
        saleCategoryContainer.getChildren().clear();
        for (String status : statuses) {
            Button btn = new Button(status);
            btn.getStyleClass().add("category-btn");
            if (status.equals(activeSaleStatus))
                btn.getStyleClass().add("active");

            btn.setOnAction(e -> {
                activeSaleStatus = status;
                setupSaleFilters();
                renderOrders();
            });
            saleCategoryContainer.getChildren().add(btn);
        }
    }

    private void setupSidebar() {
        navProducts.setOnAction(e -> switchTab("products", navProducts));
        navHistory.setOnAction(e -> switchTab("history", navHistory));
        // navCart is no longer used for shopping, maybe for something else later?
        if (navCart != null) {
            navCart.setVisible(false);
            navCart.setManaged(false);
        }
    }

    @FXML
    private void handleHistoryClick() {
        switchTab("history", navHistory);
    }

    private void handleEditSale(Sale s) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/add-sale.fxml"));
            VBox form = loader.load();

            AddSaleController controller = loader.getController();
            controller.setSaleData(s);
            controller.setOnSaleCreated(v -> {
                loadDatabaseData();
                renderOrders();
                switchTab("history", navHistory);
            });
            controller.setOnCancel(() -> switchTab("history", navHistory));

            addProductPage.getChildren().setAll(form);
            switchTab("add-sale", navHistory);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteSale(Sale s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Sale");
        alert.setHeaderText("Are you sure you want to delete this sale record?");
        alert.setContentText("Reference: " + s.getReference());

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                new services.SaleService().delete((int) s.getId());
                loadDatabaseData();
                renderOrders();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
        } else if (tab.equals("history") && orderHistoryPage != null) {
            orderHistoryPage.setVisible(true);
            orderHistoryPage.setManaged(true);
            renderOrders();
            // Hide product filters
            if (filtersContainer != null) {
                filtersContainer.setVisible(false);
                filtersContainer.setManaged(false);
            }
            // Show sale filters
            if (saleFiltersContainer != null) {
                saleFiltersContainer.setVisible(true);
                saleFiltersContainer.setManaged(true);
            }
            // Switch Headers
            if (mainHeader != null) {
                mainHeader.setVisible(false);
                mainHeader.setManaged(false);
            }
            if (historyHeader != null) {
                historyHeader.setVisible(true);
                historyHeader.setManaged(true);
            }
        } else if (tab.equals("add-product") || tab.equals("add-sale")) {
            addProductPage.setVisible(true);
            addProductPage.setManaged(true);
            if (mainHeader != null) {
                mainHeader.setVisible(false);
                mainHeader.setManaged(false);
            }
            if (historyHeader != null) {
                historyHeader.setVisible(false);
                historyHeader.setManaged(false);
            }
            if (filtersContainer != null) {
                filtersContainer.setVisible(false);
                filtersContainer.setManaged(false);
            }
            if (saleFiltersContainer != null) {
                saleFiltersContainer.setVisible(false);
                saleFiltersContainer.setManaged(false);
            }
        }

        // Restore header/filters if on products page
        if (tab.equals("products")) {
            if (mainHeader != null) {
                mainHeader.setVisible(true);
                mainHeader.setManaged(true);
            }
            if (historyHeader != null) {
                historyHeader.setVisible(false);
                historyHeader.setManaged(false);
            }
            if (filtersContainer != null) {
                filtersContainer.setVisible(true);
                filtersContainer.setManaged(true);
            }
            if (saleFiltersContainer != null) {
                saleFiltersContainer.setVisible(false);
                saleFiltersContainer.setManaged(false);
            }
        }
    }

    @FXML
    private void handleNewProduct() {
        openProductForm(null);
    }

    private void openProductForm(Product existing) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/add-product.fxml"));
            VBox form = loader.load();

            AddProductController controller = loader.getController();
            controller.setProductData(existing);
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

        VBox footer = new VBox(10);
        footer.getStyleClass().add("card-footer");

        HBox priceBox = new HBox();
        priceBox.setAlignment(Pos.CENTER_LEFT);
        Label price = new Label("$" + p.getPrice());
        price.getStyleClass().add("card-price");
        priceBox.getChildren().add(price);

        HBox actionBox = new HBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button buyBtn = new Button("Buy");
        buyBtn.getStyleClass().add("add-btn");
        FontIcon bagIcon = new FontIcon("fth-shopping-bag");
        buyBtn.setGraphic(bagIcon);
        buyBtn.setOnAction(e -> {
            e.consume();
            handleBuyProduct(p);
        });

        Button editBtn = new Button();
        editBtn.getStyleClass().addAll("card-action-btn", "edit");
        editBtn.setGraphic(new FontIcon("fth-edit-2"));
        editBtn.setOnAction(e -> {
            e.consume();
            handleEditProduct(p);
        });

        Button deleteBtn = new Button();
        deleteBtn.getStyleClass().addAll("card-action-btn", "delete");
        deleteBtn.setGraphic(new FontIcon("fth-trash-2"));
        deleteBtn.setOnAction(e -> {
            e.consume();
            handleDeleteProduct(p);
        });

        Button qrBtn = new Button();
        qrBtn.getStyleClass().addAll("card-action-btn", "qr");
        qrBtn.setGraphic(new FontIcon("fth-maximize"));
        qrBtn.setOnAction(e -> {
            e.consume();
            showQRCodeDialog(p);
        });

        actionBox.getChildren().addAll(qrBtn, editBtn, deleteBtn, buyBtn);
        footer.getChildren().addAll(priceBox, actionBox);
        content.getChildren().addAll(title, desc, footer);
        card.getChildren().addAll(imgContainer, content);

        return card;
    }

    private void handleBuyProduct(Product p) {
        openSaleForm(p);
    }

    private void showQRCodeDialog(Product p) {
        String qrData = String.format("ID: %d\nName: %s\nPrice: %.2f %s",
                p.getId(), p.getName(), p.getPrice(), p.getCurrency());

        Image qrImage = QRCodeUtils.generateQRCode(qrData, 250, 250);

        if (qrImage != null) {
            Alert alert = new Alert(Alert.AlertType.NONE); // Use NONE to avoid default icons
            alert.setTitle("QR Code");
            alert.getButtonTypes().add(ButtonType.CLOSE);

            ImageView iv = new ImageView(qrImage);
            iv.setPreserveRatio(true);

            StackPane container = new StackPane(iv);
            container.setPadding(new javafx.geometry.Insets(10));
            container.setAlignment(Pos.CENTER);

            alert.getDialogPane().setContent(container);
            alert.getDialogPane().setHeaderText(null);
            alert.getDialogPane().setGraphic(null);

            alert.showAndWait();
        }
    }

    @FXML
    private void handleQRSearch() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select QR Code Image");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(rootStack.getScene().getWindow());
        if (file != null) {
            String decodedText = QRCodeUtils.decodeQRCode(file);
            if (decodedText != null) {
                System.out.println("Decoded QR: " + decodedText);
                // Simple search logic: look for the ID in the decoded text
                try {
                    String[] lines = decodedText.split("\n");
                    if (lines.length > 0 && lines[0].startsWith("ID: ")) {
                        long id = Long.parseLong(lines[0].replace("ID: ", "").trim());

                        List<Product> results = allProducts.stream()
                                .filter(prod -> prod.getId() == id)
                                .collect(Collectors.toList());

                        if (!results.isEmpty()) {
                            productGrid.getChildren().clear();
                            results.forEach(prod -> productGrid.getChildren().add(createProductCard(prod)));
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Not Found",
                                    "No product matching this QR code was found.");
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Invalid QR",
                                "This QR code does not contain valid product information.");
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to process QR code content.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Decoding Failed", "Could not read QR code from the selected image.");
            }
        }
    }

    private void openSaleForm(Product p) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/add-sale.fxml"));
            VBox form = loader.load();

            AddSaleController controller = loader.getController();
            controller.setProduct(p);
            controller.setOnSaleCreated(v -> {
                loadDatabaseData();
                renderOrders();
                switchTab("history", navHistory);
            });
            controller.setOnCancel(() -> switchTab("products", navProducts));

            addProductPage.getChildren().setAll(form);
            switchTab("add-sale", navProducts); // Using navProducts as active but we are in form
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEditProduct(Product p) {
        openProductForm(p);
    }

    private void handleDeleteProduct(Product p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Product");
        alert.setContentText("Are you sure you want to delete '" + p.getName() + "'?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    productService.delete((int) p.getId());
                    loadDatabaseData();
                    renderProducts();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
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
        System.out.println("DEBUG: Rendering filtered sales...");
        ordersContainer.getChildren().clear();

        List<Sale> filteredSales = allSales.stream()
                .filter(s -> activeSaleStatus.equals("All") || s.getStatus().equalsIgnoreCase(activeSaleStatus))
                .collect(Collectors.toList());

        for (Sale s : filteredSales) {
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
            if (s.getStatus().equalsIgnoreCase("Completed") || s.getStatus().equalsIgnoreCase("Paid"))
                status.getStyleClass().add("status-completed");
            else if (s.getStatus().equalsIgnoreCase("Pending"))
                status.getStyleClass().add("status-pending");
            else
                status.getStyleClass().add("status-cancelled");

            header.getChildren().addAll(refBox, spacer, status);

            // Details Grid
            GridPane grid = new GridPane();
            grid.getStyleClass().add("order-grid");

            ColumnConstraints c1 = new ColumnConstraints();
            c1.setPercentWidth(33.33);
            ColumnConstraints c2 = new ColumnConstraints();
            c2.setPercentWidth(33.33);
            ColumnConstraints c3 = new ColumnConstraints();
            c3.setPercentWidth(33.33);
            grid.getColumnConstraints().addAll(c1, c2, c3);

            int r = 0;
            addDetailItem(grid, 0, r, 1, "fth-credit-card", "Payment Method", s.getPaymentMethod());
            addDetailItem(grid, 1, r, 1, "fth-info", "Payment Status", s.getPaymentStatus());
            addDetailItem(grid, 2, r++, 1, "fth-refresh-cw", "Last Updated",
                    s.getUpdatedAt() != null ? s.getUpdatedAt().toLocalDateTime().toLocalDate().toString() : "N/A");

            addDetailItem(grid, 0, r, 1, "fth-map-pin", "Shipping Address", s.getShippingAddress());
            addDetailItem(grid, 1, r++, 2, "fth-map", "Billing Address", s.getBillingAddress());

            addDetailItem(grid, 0, r++, 3, "fth-file-text", "Additional Notes",
                    (s.getNotes() != null && !s.getNotes().trim().isEmpty()) ? s.getNotes()
                            : "No additional information provided.");

            // Footer
            HBox footer = new HBox();
            footer.getStyleClass().add("order-total-box");

            Button editBtn = new Button();
            editBtn.getStyleClass().addAll("card-action-btn", "edit");
            editBtn.setGraphic(new FontIcon("fth-edit-2"));
            editBtn.setOnAction(e -> handleEditSale(s));

            Button deleteBtn = new Button();
            deleteBtn.getStyleClass().addAll("card-action-btn", "delete");
            deleteBtn.setGraphic(new FontIcon("fth-trash-2"));
            deleteBtn.setOnAction(e -> handleDeleteSale(s));

            Region fSpacer = new Region();
            HBox.setHgrow(fSpacer, Priority.ALWAYS);

            Label totalLabel = new Label("Total Paid: ");
            totalLabel.getStyleClass().add("order-total-label");
            Label totalAmount = new Label((s.getCurrency() != null ? s.getCurrency() : "USD") + " "
                    + String.format("%.2f", s.getTotalAmount()));
            totalAmount.getStyleClass().add("order-total-amount");

            if (!s.getStatus().equalsIgnoreCase("Paid") && !s.getStatus().equalsIgnoreCase("Completed")) {
                Button payBtn = new Button("Pay");
                payBtn.getStyleClass().add("add-btn"); // using the same template style
                payBtn.setGraphic(new FontIcon("fth-credit-card"));
                payBtn.setOnAction(e -> {
                    try {
                        s.setStatus("Paid");
                        s.setPaymentStatus("Paid");
                        saleService.update(s);
                        loadDatabaseData();
                        renderOrders();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
                footer.getChildren().addAll(payBtn, editBtn, deleteBtn, fSpacer, totalLabel, totalAmount);
            } else {
                footer.getChildren().addAll(editBtn, deleteBtn, fSpacer, totalLabel, totalAmount);
            }

            card.getChildren().addAll(header, new Separator(), grid, new Separator(), footer);
            ordersContainer.getChildren().add(card);
        }
    }

    private void addDetailItem(GridPane grid, int col, int row, int colSpan, String iconLiteral, String labelText,
            String valueText) {
        HBox box = new HBox(12);
        box.getStyleClass().add("grid-item-box");

        FontIcon icon = new FontIcon(iconLiteral);
        icon.getStyleClass().add("grid-icon");

        VBox texts = new VBox(2);
        Label label = new Label(labelText);
        label.getStyleClass().add("grid-label");
        Label value = new Label(valueText != null ? valueText : "N/A");
        value.getStyleClass().add("grid-value");
        value.setWrapText(true);
        value.setMaxWidth(Double.MAX_VALUE);

        texts.getChildren().addAll(label, value);
        box.getChildren().addAll(icon, texts);

        grid.add(box, col, row, colSpan, 1);
        GridPane.setHgrow(box, Priority.ALWAYS);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
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

    private void setupWindowDragging() {
        if (titleBar == null)
            return;
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML
    private void handleWindowClose() {
        System.exit(0);
    }

    @FXML
    private void handleWindowMinimize() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleWindowMaximize() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }
}
