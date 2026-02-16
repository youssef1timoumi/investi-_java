package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Product;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ProductDetailsController {

    @FXML
    private ImageView productImageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label salesLabel;
    @FXML
    private Label viewsLabel;
    @FXML
    private Label imagePlaceholder;

    public void setProduct(Product p) {
        nameLabel.setText(p.getName());
        priceLabel.setText(String.format("%.2f %s", p.getPrice(), p.getCurrency()));
        descriptionLabel.setText(p.getDescription() == null || p.getDescription().isEmpty()
                ? "Aucune description fournie."
                : p.getDescription());
        statusLabel.setText(p.getStatus().toUpperCase());
        categoryLabel.setText("Catégorie ID: " + p.getCategoryId());
        salesLabel.setText("Ventes: " + p.getSalesCount());
        viewsLabel.setText("Vues: " + p.getViewsCount());

        // Dynamic Status Styling
        String status = p.getStatus().toLowerCase();
        if (status.equals("published"))
            statusLabel.setStyle(
                    "-fx-background-color: #10b981; -fx-padding: 5 15; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-weight: bold;");
        else if (status.equals("draft"))
            statusLabel.setStyle(
                    "-fx-background-color: #f59e0b; -fx-padding: 5 15; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-weight: bold;");
        else if (status.equals("archived"))
            statusLabel.setStyle(
                    "-fx-background-color: #64748b; -fx-padding: 5 15; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-weight: bold;");

        loadImage(p);
    }

    private void loadImage(Product p) {
        if (p.getDownloadUrl() == null || p.getDownloadUrl().isEmpty()) {
            productImageView.setImage(null);
            imagePlaceholder.setVisible(true);
            return;
        }

        String rawUrl = p.getDownloadUrl().trim();
        try {
            if (rawUrl.startsWith("http") || rawUrl.startsWith("https") || rawUrl.startsWith("file:")) {
                productImageView.setImage(new Image(rawUrl, true));
                imagePlaceholder.setVisible(false);
            } else {
                String cleanPath = rawUrl.replace("\\", "/");
                if (cleanPath.startsWith("/"))
                    cleanPath = cleanPath.substring(1);

                String userDir = System.getProperty("user.dir").replace("\\", "/");
                List<String> bases = Arrays.asList(userDir + "/", userDir + "/src/main/resources/",
                        userDir + "/target/classes/", "", "src/main/resources/", "target/classes/", "uploads/");

                boolean loaded = false;
                for (String base : bases) {
                    List<String> subPaths = new ArrayList<>();
                    subPaths.add(cleanPath);
                    if (!cleanPath.startsWith("uploads/"))
                        subPaths.add("uploads/" + cleanPath);

                    for (String sub : subPaths) {
                        try {
                            Path path = Paths.get(base + sub).toAbsolutePath().normalize();
                            if (Files.exists(path) && Files.isRegularFile(path)) {
                                try (FileInputStream fis = new FileInputStream(path.toFile())) {
                                    Image img = new Image(fis);
                                    if (!img.isError()) {
                                        productImageView.setImage(img);
                                        imagePlaceholder.setVisible(false);
                                        loaded = true;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    if (loaded)
                        break;
                }

                if (!loaded) {
                    // Classpath Fallback
                    String resPath = cleanPath.startsWith("/") ? cleanPath : "/" + cleanPath;
                    java.net.URL resource = getClass().getResource(resPath);
                    if (resource != null) {
                        Image img = new Image(resource.toExternalForm());
                        if (!img.isError()) {
                            productImageView.setImage(img);
                            imagePlaceholder.setVisible(false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading details image: " + e.getMessage());
        }
    }

    @FXML
    void closeWindow() {
        ((Stage) nameLabel.getScene().getWindow()).close();
    }
}
