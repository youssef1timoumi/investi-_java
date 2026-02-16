package edu.connections3a8.controllers;

import edu.connections3a8.entities.Badge;
import edu.connections3a8.services.GamificationService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class BadgeController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField pointsRequiredField;
    @FXML private Label statusLabel;
    @FXML private VBox badgeListContainer;

    private GamificationService gamificationService;

    @FXML
    public void initialize() {
        gamificationService = new GamificationService();
        statusLabel.setText("");
        loadBadges();
    }

    @FXML
    private void handleAddBadge() {
        try {
            // Validate inputs
            if (nameField.getText().isEmpty()) {
                showError("Badge name is required!");
                return;
            }

            if (descriptionArea.getText().isEmpty()) {
                showError("Description is required!");
                return;
            }

            // Create badge object
            Badge badge = new Badge();
            badge.setName(nameField.getText().trim());
            badge.setDescription(descriptionArea.getText().trim());
            
            // Parse points required
            try {
                int pointsRequired = pointsRequiredField.getText().isEmpty() ? 0 : 
                                    Integer.parseInt(pointsRequiredField.getText().trim());
                
                if (pointsRequired < 0) {
                    showError("Points required must be 0 or positive!");
                    return;
                }
                
                badge.setPointsRequired(pointsRequired);
            } catch (NumberFormatException e) {
                showError("Points required must be a valid number!");
                return;
            }

            // Add badge to database
            addBadgeToDatabase(badge);
            
            showSuccess("Badge added successfully!");
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

    @FXML
    private void handleClearForm() {
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
            badgeListContainer.getChildren().clear();
            List<Badge> badges = gamificationService.getAllBadges();
            
            if (badges.isEmpty()) {
                Label emptyLabel = new Label("No badges yet. Add your first badge!");
                emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
                badgeListContainer.getChildren().add(emptyLabel);
                return;
            }
            
            for (Badge badge : badges) {
                HBox badgeItem = createBadgeItem(badge);
                badgeListContainer.getChildren().add(badgeItem);
            }
            
        } catch (SQLException e) {
            showError("Error loading badges: " + e.getMessage());
        }
    }

    private HBox createBadgeItem(Badge badge) {
        HBox container = new HBox(15);
        container.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        // Badge icon
        Label iconLabel = new Label("🏅");
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        // Badge info
        VBox infoBox = new VBox(5);
        Label nameLabel = new Label(badge.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label descLabel = new Label(badge.getDescription());
        descLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);
        
        Label pointsLabel = new Label("Points Required: " + badge.getPointsRequired());
        pointsLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 11px; -fx-font-weight: bold;");
        
        infoBox.getChildren().addAll(nameLabel, descLabel, pointsLabel);
        
        // Delete button
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 11px;");
        deleteBtn.setOnAction(e -> handleDeleteBadge(badge));
        
        HBox.setMargin(deleteBtn, new Insets(0, 0, 0, 20));
        
        container.getChildren().addAll(iconLabel, infoBox, deleteBtn);
        
        return container;
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
        statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        statusLabel.setText("✓ " + message);
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        statusLabel.setText("✗ " + message);
    }
}
