package edu.connexion3a8.controllers;

import edu.connexion3a8.entities.Product;
import edu.connexion3a8.services.ProductService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * User-facing Marketplace catalog — shows published products as cards.
 *
 * <p>Mirrors the web {@code /products} experience: cover image, name, category
 * pill, price, stock indicator, and a "View" button that opens the product
 * detail dialog.
 */
public class ProductsCatalogController implements Initializable {

    @FXML private FlowPane cardsGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> sortCombo;

    private final ProductService productService = new ProductService();
    private List<Product> allPublished = java.util.Collections.emptyList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sortCombo != null) {
            sortCombo.setItems(FXCollections.observableArrayList(
                    "Newest", "Price: low to high", "Price: high to low", "Most viewed"));
            sortCombo.setValue("Newest");
            sortCombo.valueProperty().addListener((obs, o, n) -> applyFilters());
        }
        if (categoryCombo != null) {
            categoryCombo.valueProperty().addListener((obs, o, n) -> applyFilters());
        }
        if (searchField != null) {
            searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        }
        loadProducts();
    }

    @FXML
    private void handleRefresh() {
        loadProducts();
    }

    private void loadProducts() {
        try {
            allPublished = productService.getPublishedProducts();

            if (categoryCombo != null) {
                var cats = allPublished.stream()
                        .map(Product::getCategoryName)
                        .filter(s -> s != null && !s.isBlank())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
                cats.add(0, "All categories");
                categoryCombo.setItems(FXCollections.observableArrayList(cats));
                if (categoryCombo.getValue() == null) categoryCombo.setValue("All categories");
            }

            applyFilters();
        } catch (SQLException e) {
            showError("Could not load products", e.getMessage());
        }
    }

    private void applyFilters() {
        if (cardsGrid == null) return;
        String query = searchField == null || searchField.getText() == null
                ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String cat = categoryCombo == null ? null : categoryCombo.getValue();
        String sort = sortCombo == null ? "Newest" : sortCombo.getValue();

        List<Product> filtered = allPublished.stream()
                .filter(p -> query.isEmpty()
                        || (p.getName() != null && p.getName().toLowerCase(Locale.ROOT).contains(query))
                        || (p.getDescription() != null && p.getDescription().toLowerCase(Locale.ROOT).contains(query))
                        || (p.getCategoryName() != null && p.getCategoryName().toLowerCase(Locale.ROOT).contains(query)))
                .filter(p -> cat == null || cat.equals("All categories") || cat.equals(p.getCategoryName()))
                .collect(Collectors.toList());

        Comparator<Product> cmp;
        switch (sort == null ? "Newest" : sort) {
            case "Price: low to high":
                cmp = Comparator.comparingDouble(Product::getPrice);
                break;
            case "Price: high to low":
                cmp = Comparator.comparingDouble(Product::getPrice).reversed();
                break;
            case "Most viewed":
                cmp = Comparator.comparingInt(Product::getViewsCount).reversed();
                break;
            default:
                cmp = Comparator.comparing(
                        (Product p) -> p.getCreatedAt() == null ? 0L : p.getCreatedAt().getTime())
                        .reversed();
        }
        filtered.sort(cmp);

        cardsGrid.getChildren().setAll(filtered.stream().map(this::buildCard).collect(Collectors.toList()));
        if (filtered.isEmpty()) {
            Label empty = new Label("No products match your filters.");
            empty.getStyleClass().add("muted");
            cardsGrid.getChildren().add(empty);
        }
    }

    private VBox buildCard(Product p) {
        VBox card = new VBox(12);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(260);
        card.setMaxWidth(260);
        card.setMinWidth(240);

        // Cover
        StackPane cover = new StackPane();
        cover.setPrefHeight(150);
        cover.setMinHeight(150);
        cover.setMaxHeight(150);
        cover.getStyleClass().add("product-card-cover");

        Image coverImage = resolveCover(p);
        if (coverImage != null) {
            ImageView iv = new ImageView(coverImage);
            iv.setFitHeight(150);
            iv.setFitWidth(260);
            iv.setPreserveRatio(false);
            iv.setSmooth(true);
            Rectangle clip = new Rectangle(260, 150);
            clip.setArcWidth(18);
            clip.setArcHeight(18);
            iv.setClip(clip);
            cover.getChildren().add(iv);
        } else {
            Label placeholder = new Label(initials(p.getName()));
            placeholder.getStyleClass().add("product-card-placeholder");
            cover.getChildren().add(placeholder);
        }

        if (p.getRemise() > 0) {
            Label badge = new Label("-" + p.getRemise() + "%");
            badge.getStyleClass().add("product-card-discount");
            StackPane.setAlignment(badge, Pos.TOP_RIGHT);
            StackPane.setMargin(badge, new Insets(10, 10, 0, 0));
            cover.getChildren().add(badge);
        }

        // Category pill
        Label category = new Label(p.getCategoryName());
        category.getStyleClass().add("product-card-category");

        // Name
        Label name = new Label(p.getName() == null ? "Untitled product" : p.getName());
        name.getStyleClass().add("product-card-title");
        name.setWrapText(true);
        name.setMaxHeight(46);

        // Description
        Label desc = new Label(truncate(p.getDescription(), 90));
        desc.getStyleClass().add("product-card-desc");
        desc.setWrapText(true);
        desc.setMaxHeight(40);

        // Price row
        HBox priceRow = new HBox(10);
        priceRow.setAlignment(Pos.CENTER_LEFT);

        Label priceLabel = new Label(formatPrice(p));
        priceLabel.getStyleClass().add("product-card-price");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label stockLabel = new Label(p.getStock() > 0
                ? p.getStock() + " in stock"
                : (p.isDigital() ? "Digital" : "Out of stock"));
        stockLabel.getStyleClass().add(p.getStock() > 0 || p.isDigital()
                ? "product-card-stock" : "product-card-stock-out");
        priceRow.getChildren().addAll(priceLabel, spacer, stockLabel);

        Button view = new Button("View details");
        view.getStyleClass().add("btn-primary");
        view.setMaxWidth(Double.MAX_VALUE);
        view.setOnAction(e -> {
            try { productService.incrementViewsCount(p.getId()); } catch (SQLException ignored) {}
            showDetails(p);
        });

        card.getChildren().addAll(cover, category, name, desc, priceRow, view);
        return card;
    }

    // ------------------------------------------------------------------

    private void showDetails(Product p) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(p.getName() == null ? "Product" : p.getName());
        a.setHeaderText(p.getName());
        StringBuilder sb = new StringBuilder();
        sb.append("Category: ").append(p.getCategoryName()).append('\n');
        sb.append("Price: ").append(formatPrice(p)).append('\n');
        if (p.getRemise() > 0) sb.append("Discount: ").append(p.getRemise()).append("%\n");
        sb.append("Stock: ").append(p.isDigital() ? "Digital product" : String.valueOf(p.getStock())).append('\n');
        sb.append("Views: ").append(p.getViewsCount()).append('\n');
        sb.append('\n');
        sb.append(p.getDescription() == null ? "No description provided." : p.getDescription());
        a.setContentText(sb.toString());
        a.getDialogPane().setPrefWidth(520);
        a.showAndWait();
    }

    private Image resolveCover(Product p) {
        String raw = p.getDownloadUrl();
        if (raw == null || raw.isBlank()) return null;
        try {
            if (raw.startsWith("http://") || raw.startsWith("https://")) {
                return new Image(raw, true);
            }
            // Resolve against the canonical uploads root.
            Path canonical = Paths.get("C:/xampp/htdocs/investi/uploads").resolve(raw);
            if (Files.exists(canonical)) {
                return new Image(canonical.toUri().toString(), true);
            }
            File direct = new File(raw);
            if (direct.exists()) {
                return new Image(direct.toURI().toString(), true);
            }
        } catch (Exception ignored) { }
        return null;
    }

    private static String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        return sb.toString();
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        s = s.trim();
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String formatPrice(Product p) {
        double effective = p.getPrice();
        if (p.getRemise() > 0) {
            effective = effective * (100.0 - p.getRemise()) / 100.0;
        }
        String ccy = p.getCurrency() == null ? "TND" : p.getCurrency();
        return String.format(Locale.ROOT, "%.2f %s", effective, ccy);
    }

    private void showError(String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}
