package edu.connections3a8.controllers;

import edu.connections3a8.entities.Badge;
import edu.connections3a8.services.GamificationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BadgeController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField pointsRequiredField;
    @FXML private Label statusLabel;
    @FXML private VBox badgeListContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Button themeToggleBtn;
    @FXML private Button autoModeBtn;

    private GamificationService gamificationService;
    private Badge selectedBadge = null; // For editing
    private List<Badge> allBadges = new ArrayList<>(); // Cache all badges
    private boolean isDarkMode = false;
    private boolean isAutoMode = false;
    private javafx.scene.layout.Pane rootPane;

    @FXML
    public void initialize() {
        gamificationService = new GamificationService();
        statusLabel.setText("");
        
        // Initialize sort combo
        if (sortCombo != null) {
            sortCombo.setItems(FXCollections.observableArrayList(
                "Name (A-Z)",
                "Name (Z-A)",
                "Points (Low to High)",
                "Points (High to Low)"
            ));
            
            // Add listener for sort changes
            sortCombo.setOnAction(e -> applyFiltersAndSort());
        }
        
        // Add listener for search field
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());
        }
        
        // Get root pane for theme switching
        if (nameField != null && nameField.getScene() != null) {
            javafx.scene.Parent root = nameField.getScene().getRoot();
            if (root instanceof javafx.scene.layout.Pane) {
                rootPane = (javafx.scene.layout.Pane) root;
            }
        }
        
        // Load badges only if container is initialized
        if (badgeListContainer != null) {
            loadBadges();
        } else {
            System.err.println("Warning: badgeListContainer not initialized yet");
        }
    }
    
    @FXML
    private void handleThemeToggle() {
        if (isAutoMode) {
            // Disable auto mode when manually toggling
            isAutoMode = false;
            updateAutoModeButton();
        }
        
        isDarkMode = !isDarkMode;
        applyTheme();
        updateThemeButton();
    }
    
    @FXML
    private void handleAutoMode() {
        isAutoMode = !isAutoMode;
        updateAutoModeButton();
        
        if (isAutoMode) {
            applyAutoTheme();
        }
    }
    
    private void applyAutoTheme() {
        // Get current hour (0-23)
        int hour = java.time.LocalTime.now().getHour();
        
        // Dark mode between 6 PM (18:00) and 6 AM (06:00)
        boolean shouldBeDark = hour >= 18 || hour < 6;
        
        if (isDarkMode != shouldBeDark) {
            isDarkMode = shouldBeDark;
            applyTheme();
            updateThemeButton();
        }
    }
    
    private void applyTheme() {
        if (rootPane == null) {
            // Try to get root pane again
            if (nameField != null && nameField.getScene() != null) {
                javafx.scene.Parent root = nameField.getScene().getRoot();
                if (root instanceof javafx.scene.layout.Pane) {
                    rootPane = (javafx.scene.layout.Pane) root;
                }
            }
        }
        
        if (rootPane != null) {
            if (isDarkMode) {
                if (!rootPane.getStyleClass().contains("dark-mode")) {
                    rootPane.getStyleClass().add("dark-mode");
                }
            } else {
                rootPane.getStyleClass().remove("dark-mode");
            }
        }
    }
    
    private void updateThemeButton() {
        if (themeToggleBtn != null) {
            if (isDarkMode) {
                themeToggleBtn.setText("☀️ Light");
            } else {
                themeToggleBtn.setText("🌙 Dark");
            }
        }
    }
    
    private void updateAutoModeButton() {
        if (autoModeBtn != null) {
            if (isAutoMode) {
                autoModeBtn.getStyleClass().add("auto-mode-active");
                autoModeBtn.setText("⏰ Auto ✓");
            } else {
                autoModeBtn.getStyleClass().remove("auto-mode-active");
                autoModeBtn.setText("⏰ Auto");
            }
        }
    }

    @FXML
    private void handleAddBadge() {
        try {
            // Check if fields are initialized
            if (nameField == null || descriptionArea == null || pointsRequiredField == null) {
                showError("Form not properly initialized!");
                return;
            }
            
            // Get text safely
            String name = nameField.getText();
            String description = descriptionArea.getText();
            
            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                showError("Badge name is required!");
                return;
            }

            if (description == null || description.trim().isEmpty()) {
                showError("Description is required!");
                return;
            }

            // Create or update badge object
            Badge badge = selectedBadge != null ? selectedBadge : new Badge();
            badge.setName(name.trim());
            badge.setDescription(description.trim());
            
            // Parse points required
            try {
                String pointsText = pointsRequiredField.getText();
                int pointsRequired = (pointsText == null || pointsText.trim().isEmpty()) ? 0 : 
                                    Integer.parseInt(pointsText.trim());
                
                if (pointsRequired < 0) {
                    showError("Points required must be 0 or positive!");
                    return;
                }
                
                badge.setPointsRequired(pointsRequired);
            } catch (NumberFormatException e) {
                showError("Points required must be a valid number!");
                return;
            }

            // Add or update badge in database
            if (selectedBadge != null) {
                updateBadgeInDatabase(badge);
                showSuccess("Badge updated successfully!");
            } else {
                addBadgeToDatabase(badge);
                showSuccess("Badge added successfully!");
            }
            
            handleClearForm();
            loadBadges(); // Refresh the list
            
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addBadgeToDatabase(Badge badge) throws SQLException {
        // Using direct SQL since we don't have addBadge in service
        String query = "INSERT INTO badges (name, description, points_required) VALUES (?, ?, ?)";
        java.sql.PreparedStatement pst = gamificationService.getCnx().prepareStatement(query);
        pst.setString(1, badge.getName());
        pst.setString(2, badge.getDescription());
        pst.setInt(3, badge.getPointsRequired());
        pst.executeUpdate();
    }

    private void updateBadgeInDatabase(Badge badge) throws SQLException {
        String query = "UPDATE badges SET name = ?, description = ?, points_required = ? WHERE id = ?";
        java.sql.PreparedStatement pst = gamificationService.getCnx().prepareStatement(query);
        pst.setString(1, badge.getName());
        pst.setString(2, badge.getDescription());
        pst.setInt(3, badge.getPointsRequired());
        pst.setLong(4, badge.getId());
        pst.executeUpdate();
    }

    @FXML
    private void handleClearForm() {
        selectedBadge = null;
        nameField.clear();
        descriptionArea.clear();
        pointsRequiredField.clear();
        statusLabel.setText("");
    }

    @FXML
    private void handleViewAll() {
        try {
            List<Badge> badges = gamificationService.getAllBadges();
            
            StringBuilder sb = new StringBuilder();
            sb.append("Total Badges: ").append(badges.size()).append("\n\n");
            
            for (Badge badge : badges) {
                sb.append("🏅 ").append(badge.getName())
                  .append("\n   ").append(badge.getDescription())
                  .append("\n   Points Required: ").append(badge.getPointsRequired())
                  .append("\n\n");
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("All Badges");
            alert.setHeaderText("Badge List");
            alert.setContentText(sb.toString());
            alert.showAndWait();
            
        } catch (SQLException e) {
            showError("Error loading badges: " + e.getMessage());
        }
    }

    private void loadBadges() {
        try {
            if (badgeListContainer == null) {
                System.err.println("badgeListContainer is null - FXML not loaded properly");
                return;
            }
            
            allBadges = gamificationService.getAllBadges();
            applyFiltersAndSort();
            
        } catch (SQLException e) {
            showError("Error loading badges: " + e.getMessage());
        }
    }
    
    private void applyFiltersAndSort() {
        if (badgeListContainer == null || allBadges == null) {
            return;
        }
        
        badgeListContainer.getChildren().clear();
        
        // Filter badges based on search
        List<Badge> filteredBadges = allBadges;
        
        if (searchField != null && searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
            String searchText = searchField.getText().trim().toLowerCase();
            filteredBadges = allBadges.stream()
                .filter(badge -> {
                    // Search by name
                    boolean matchesName = badge.getName().toLowerCase().contains(searchText);
                    // Search by points
                    boolean matchesPoints = String.valueOf(badge.getPointsRequired()).contains(searchText);
                    return matchesName || matchesPoints;
                })
                .collect(Collectors.toList());
        }
        
        // Sort badges
        if (sortCombo != null && sortCombo.getValue() != null) {
            String sortOption = sortCombo.getValue();
            
            switch (sortOption) {
                case "Name (A-Z)":
                    filteredBadges.sort(Comparator.comparing(Badge::getName));
                    break;
                case "Name (Z-A)":
                    filteredBadges.sort(Comparator.comparing(Badge::getName).reversed());
                    break;
                case "Points (Low to High)":
                    filteredBadges.sort(Comparator.comparingInt(Badge::getPointsRequired));
                    break;
                case "Points (High to Low)":
                    filteredBadges.sort(Comparator.comparingInt(Badge::getPointsRequired).reversed());
                    break;
            }
        }
        
        // Display filtered and sorted badges
        if (filteredBadges.isEmpty()) {
            Label emptyLabel = new Label(searchField != null && !searchField.getText().trim().isEmpty() 
                ? "No badges found matching your search." 
                : "No badges yet. Add your first badge!");
            emptyLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic; -fx-font-size: 13px;");
            badgeListContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (Badge badge : filteredBadges) {
            HBox badgeItem = createBadgeItem(badge);
            badgeListContainer.getChildren().add(badgeItem);
        }
    }
    
    @FXML
    private void handleClearSearch() {
        if (searchField != null) {
            searchField.clear();
        }
        if (sortCombo != null) {
            sortCombo.setValue(null);
        }
        applyFiltersAndSort();
    }

    private HBox createBadgeItem(Badge badge) {
        HBox container = new HBox(15);
        container.getStyleClass().add("badge-card");
        
        // Badge icon
        Label iconLabel = new Label("🏅");
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        // Badge info
        VBox infoBox = new VBox(5);
        Label nameLabel = new Label(badge.getName());
        nameLabel.getStyleClass().add("badge-card-title");
        
        Label descLabel = new Label(badge.getDescription());
        descLabel.getStyleClass().add("badge-card-description");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);
        
        Label pointsLabel = new Label("Points Required: " + badge.getPointsRequired());
        pointsLabel.getStyleClass().add("badge-card-points");
        
        infoBox.getChildren().addAll(nameLabel, descLabel, pointsLabel);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().addAll("btn", "btn-secondary");
        editBtn.setStyle("-fx-font-size: 11px; -fx-padding: 6 14 6 14;");
        editBtn.setOnAction(e -> handleEditBadge(badge));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8px; -fx-padding: 6 14 6 14; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteBadge(badge));
        
        buttonBox.getChildren().addAll(editBtn, deleteBtn);
        HBox.setMargin(buttonBox, new Insets(0, 0, 0, 20));
        
        container.getChildren().addAll(iconLabel, infoBox, buttonBox);
        
        return container;
    }

    private void handleEditBadge(Badge badge) {
        selectedBadge = badge;
        
        nameField.setText(badge.getName());
        descriptionArea.setText(badge.getDescription());
        pointsRequiredField.setText(String.valueOf(badge.getPointsRequired()));
        
        statusLabel.setStyle("-fx-text-fill: #456990; -fx-font-size: 14px;");
        statusLabel.setText("✏️ Editing: " + badge.getName());
    }

    private void handleDeleteBadge(Badge badge) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Badge");
        confirmAlert.setHeaderText("Are you sure?");
        confirmAlert.setContentText("Do you want to delete the badge: " + badge.getName() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String query = "DELETE FROM badges WHERE id = ?";
                    java.sql.PreparedStatement pst = gamificationService.getCnx().prepareStatement(query);
                    pst.setLong(1, badge.getId());
                    pst.executeUpdate();
                    
                    showSuccess("Badge deleted successfully!");
                    loadBadges(); // Refresh the list
                    
                } catch (SQLException e) {
                    showError("Error deleting badge: " + e.getMessage());
                }
            }
        });
    }

    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: #28A745; -fx-font-size: 14px; -fx-font-weight: 600;");
        statusLabel.setText("✓ " + message);
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #DC3545; -fx-font-size: 14px; -fx-font-weight: 600;");
        statusLabel.setText("✗ " + message);
    }

    @FXML
    private void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/MainMenu.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.stage.Stage stage = (javafx.stage.Stage) statusLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Gamification System - Main Menu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
