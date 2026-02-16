package edu.connections3a8.controllers;

import edu.connections3a8.entities.Course;
import edu.connections3a8.services.CouseService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class CourseController {

    @FXML private TextField titleField;
    @FXML private TextField slugField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField contentUrlField;
    @FXML private ComboBox<String> contentTypeCombo;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private TextField categoryField;
    @FXML private TextField languageField;
    @FXML private TextField durationField;
    @FXML private TextField rewardPointsField;
    @FXML private TextField thumbnailField;
    @FXML private Label statusLabel;

    private CouseService courseService;

    @FXML
    public void initialize() {
        courseService = new CouseService();
        
        // Initialize ComboBoxes
        contentTypeCombo.setItems(FXCollections.observableArrayList(
            "video", "article", "interactive", "pdf", "quiz"
        ));
        
        difficultyCombo.setItems(FXCollections.observableArrayList(
            "beginner", "intermediate", "advanced", "expert"
        ));
        
        statusLabel.setText("");
    }

    @FXML
    private void handleAddCourse() {
        try {
            // Validate inputs
            if (titleField.getText().isEmpty() || slugField.getText().isEmpty()) {
                showError("Title and Slug are required!");
                return;
            }

            // Validate numeric fields
            if (durationField.getText().isEmpty() || rewardPointsField.getText().isEmpty()) {
                showError("Duration and Reward Points are required!");
                return;
            }

            // Create course object
            Course course = new Course();
            course.setTitle(titleField.getText().trim());
            course.setSlug(slugField.getText().trim());
            course.setDescription(descriptionArea.getText().trim());
            course.setContentUrl(contentUrlField.getText().trim());
            
            // Set content type with default
            String contentType = contentTypeCombo.getValue();
            course.setContentType(contentType != null ? contentType : "video");
            
            // Set difficulty with default
            String difficulty = difficultyCombo.getValue();
            course.setDifficultyLevel(difficulty != null ? difficulty : "beginner");
            
            course.setCategory(categoryField.getText().trim());
            course.setLanguage(languageField.getText().trim());
            
            // Parse numeric fields
            try {
                int duration = Integer.parseInt(durationField.getText().trim());
                int rewardPoints = Integer.parseInt(rewardPointsField.getText().trim());
                
                if (duration < 0 || rewardPoints < 0) {
                    showError("Duration and Reward Points must be positive numbers!");
                    return;
                }
                
                course.setEstimatedDuration(duration);
                course.setRewardPoints(rewardPoints);
            } catch (NumberFormatException e) {
                showError("Duration and Reward Points must be valid numbers!");
                return;
            }
            
            course.setThumbnailUrl(thumbnailField.getText().trim());
            course.setStatus("published");
            course.setVisibility("public");

            // Add course to database
            courseService.addCourse(course);
            
            showSuccess("Course added successfully!");
            handleClearForm();
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearForm() {
        titleField.clear();
        slugField.clear();
        descriptionArea.clear();
        contentUrlField.clear();
        contentTypeCombo.setValue(null);
        difficultyCombo.setValue(null);
        categoryField.clear();
        languageField.clear();
        durationField.clear();
        rewardPointsField.clear();
        thumbnailField.clear();
        statusLabel.setText("");
    }

    @FXML
    private void handleViewAll() {
        try {
            List<Course> courses = courseService.getAllCourses();
            
            StringBuilder sb = new StringBuilder();
            sb.append("Total Courses: ").append(courses.size()).append("\n\n");
            
            for (Course course : courses) {
                sb.append("- ").append(course.getTitle())
                  .append(" (").append(course.getRewardPoints()).append(" points)\n");
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("All Courses");
            alert.setHeaderText("Course List");
            alert.setContentText(sb.toString());
            alert.showAndWait();
            
        } catch (SQLException e) {
            showError("Error loading courses: " + e.getMessage());
        }
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
