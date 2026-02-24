package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class DashboardController {

    private static DashboardController instance;

    @FXML
    private StackPane contentArea;
    @FXML
    private Label lblHeader;
    @FXML
    private Button btnProducts;
    @FXML
    private Button btnSales;

    public DashboardController() {
        instance = this;
    }

    public static DashboardController getInstance() {
        return instance;
    }

    @FXML
    void initialize() {
        showProducts(); // Load default view
    }

    @FXML
    public void showProducts() {
        loadView("/ShowProducts.fxml", "Product Catalog");
        setActiveButton(btnProducts);
    }

    @FXML
    public void showSales() {
        loadView("/ShowSales.fxml", "Sales Tracking");
        setActiveButton(btnSales);
    }

    public void loadView(String fxml, String title) {
        try {
            java.net.URL url = getClass().getResource(fxml);
            if (url == null) {
                showError("File Not Found", "The view file '" + fxml + "' could not be found.");
                return;
            }
            lblHeader.setText(title);
            Parent view = FXMLLoader.load(url);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Loading Error", "Failed to load view '" + fxml + "'.\nError: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("System Error", "An unexpected error occurred while switching views: " + e.toString());
        }
    }

    private void showError(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setActiveButton(Button activeBtn) {
        btnProducts.getStyleClass().remove("active");
        btnSales.getStyleClass().remove("active");
        activeBtn.getStyleClass().add("active");
    }
}
