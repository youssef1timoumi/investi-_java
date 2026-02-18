package edu.connections3a8.controllers;

import edu.connections3a8.entities.Course;
import edu.connections3a8.services.CouseService;
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
    @FXML private VBox courseListContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Button themeToggleBtn;
    @FXML private Button autoModeBtn;

    private CouseService courseService;
    private Course selectedCourse = null;
    private List<Course> allCourses = new ArrayList<>();
    private boolean isDarkMode = false;
    private boolean isAutoMode = false;
    private javafx.scene.layout.Pane rootPane;

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
        
        // Initialize sort combo
        if (sortCombo != null) {
            sortCombo.setItems(FXCollections.observableArrayList(
                "Title (A-Z)",
                "Title (Z-A)",
                "Points (Low to High)",
                "Points (High to Low)",
                "Duration (Short to Long)",
                "Duration (Long to Short)"
            ));
            sortCombo.setOnAction(e -> applyFiltersAndSort());
        }
        
        // Add listener for search field
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());
        }
        
        statusLabel.setText("");
        
        // Get root pane for theme switching
        if (titleField != null && titleField.getScene() != null) {
            javafx.scene.Parent root = titleField.getScene().getRoot();
            if (root instanceof javafx.scene.layout.Pane) {
                rootPane = (javafx.scene.layout.Pane) root;
            }
        }
        
        // Load courses only if container is initialized
        if (courseListContainer != null) {
            loadCourses();
        } else {
            System.err.println("Warning: courseListContainer not initialized yet");
        }
    }

    @FXML
    private void handleThemeToggle() {
        if (isAutoMode) {
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
        int hour = java.time.LocalTime.now().getHour();
        boolean shouldBeDark = hour >= 18 || hour < 6;
        
        if (isDarkMode != shouldBeDark) {
            isDarkMode = shouldBeDark;
            applyTheme();
            updateThemeButton();
        }
    }
    
    private void applyTheme() {
        if (rootPane == null) {
            if (titleField != null && titleField.getScene() != null) {
                javafx.scene.Parent root = titleField.getScene().getRoot();
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
    private void handleAddCourse() {
        try {
            // Check if fields are initialized
            if (titleField == null || slugField == null || descriptionArea == null || 
                contentUrlField == null || categoryField == null || languageField == null ||
                durationField == null || rewardPointsField == null || thumbnailField == null) {
                showError("Form not properly initialized!");
                return;
            }
            
            // Get text safely
            String title = titleField.getText();
            String slug = slugField.getText();
            String duration = durationField.getText();
            String rewardPoints = rewardPointsField.getText();
            
            // Validate inputs
            if (title == null || title.trim().isEmpty() || 
                slug == null || slug.trim().isEmpty()) {
                showError("Title and Slug are required!");
                return;
            }

            // Validate numeric fields
            if (duration == null || duration.trim().isEmpty() || 
                rewardPoints == null || rewardPoints.trim().isEmpty()) {
                showError("Duration and Reward Points are required!");
                return;
            }

            // Create or update course object
            Course course = selectedCourse != null ? selectedCourse : new Course();
            course.setTitle(title.trim());
            course.setSlug(slug.trim());
            
            String description = descriptionArea.getText();
            course.setDescription(description != null ? description.trim() : "");
            
            String contentUrl = contentUrlField.getText();
            course.setContentUrl(contentUrl != null ? contentUrl.trim() : "");
            
            // Set content type with default
            String contentType = contentTypeCombo.getValue();
            course.setContentType(contentType != null ? contentType : "video");
            
            // Set difficulty with default
            String difficulty = difficultyCombo.getValue();
            course.setDifficultyLevel(difficulty != null ? difficulty : "beginner");
            
            String category = categoryField.getText();
            course.setCategory(category != null ? category.trim() : "");
            
            String language = languageField.getText();
            course.setLanguage(language != null ? language.trim() : "");
            
            // Parse numeric fields
            try {
                int durationValue = Integer.parseInt(duration.trim());
                int rewardPointsValue = Integer.parseInt(rewardPoints.trim());

                if (durationValue < 0 || rewardPointsValue < 0) {
                    showError("Duration and Reward Points must be positive numbers!");
                    return;
                }
                
                course.setEstimatedDuration(durationValue);
                course.setRewardPoints(rewardPointsValue);
            } catch (NumberFormatException e) {
                showError("Duration and Reward Points must be valid numbers!");
                return;
            }
            
            String thumbnail = thumbnailField.getText();
            course.setThumbnailUrl(thumbnail != null ? thumbnail.trim() : "");
            course.setStatus("published");
            course.setVisibility("public");

            // Add or update course in database
            if (selectedCourse != null) {
                courseService.updateCourse(course, selectedCourse.getId());
                showSuccess("Course updated successfully!");
            } else {
                courseService.addCourse(course);
                showSuccess("Course added successfully!");
            }
            
            handleClearForm();
            loadCourses();
            
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
        selectedCourse = null;
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
        statusLabel.setStyle("-fx-text-fill: #28A745; -fx-font-size: 14px; -fx-font-weight: 600;");
        statusLabel.setText("✓ " + message);
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #DC3545; -fx-font-size: 14px; -fx-font-weight: 600;");
        statusLabel.setText("✗ " + message);
    }

    private void loadCourses() {
        try {
            if (courseListContainer == null) {
                System.err.println("courseListContainer is null - FXML not loaded properly");
                return;
            }
            
            allCourses = courseService.getAllCourses();
            applyFiltersAndSort();
            
        } catch (SQLException e) {
            showError("Error loading courses: " + e.getMessage());
        }
    }
    
    private void applyFiltersAndSort() {
        if (courseListContainer == null || allCourses == null) {
            return;
        }
        
        courseListContainer.getChildren().clear();
        
        // Filter courses based on search
        List<Course> filteredCourses = allCourses;
        
        if (searchField != null && searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
            String searchText = searchField.getText().trim().toLowerCase();
            filteredCourses = allCourses.stream()
                .filter(course -> {
                    boolean matchesTitle = course.getTitle().toLowerCase().contains(searchText);
                    boolean matchesCategory = course.getCategory().toLowerCase().contains(searchText);
                    boolean matchesDifficulty = course.getDifficultyLevel().toLowerCase().contains(searchText);
                    return matchesTitle || matchesCategory || matchesDifficulty;
                })
                .collect(Collectors.toList());
        }
        
        // Sort courses
        if (sortCombo != null && sortCombo.getValue() != null) {
            String sortOption = sortCombo.getValue();
            
            switch (sortOption) {
                case "Title (A-Z)":
                    filteredCourses.sort(Comparator.comparing(Course::getTitle));
                    break;
                case "Title (Z-A)":
                    filteredCourses.sort(Comparator.comparing(Course::getTitle).reversed());
                    break;
                case "Points (Low to High)":
                    filteredCourses.sort(Comparator.comparingInt(Course::getRewardPoints));
                    break;
                case "Points (High to Low)":
                    filteredCourses.sort(Comparator.comparingInt(Course::getRewardPoints).reversed());
                    break;
                case "Duration (Short to Long)":
                    filteredCourses.sort(Comparator.comparingInt(Course::getEstimatedDuration));
                    break;
                case "Duration (Long to Short)":
                    filteredCourses.sort(Comparator.comparingInt(Course::getEstimatedDuration).reversed());
                    break;
            }
        }
        
        // Display filtered and sorted courses
        if (filteredCourses.isEmpty()) {
            Label emptyLabel = new Label(searchField != null && !searchField.getText().trim().isEmpty() 
                ? "No courses found matching your search." 
                : "No courses yet. Add your first course!");
            emptyLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic; -fx-font-size: 13px;");
            courseListContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (Course course : filteredCourses) {
            HBox courseItem = createCourseItem(course);
            courseListContainer.getChildren().add(courseItem);
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

    private HBox createCourseItem(Course course) {
        HBox container = new HBox(15);
        container.getStyleClass().add("item-card");
        
        // Course icon
        Label iconLabel = new Label("📚");
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        // Course info
        VBox infoBox = new VBox(5);
        Label titleLabel = new Label(course.getTitle());
        titleLabel.getStyleClass().add("item-card-title");
        
        Label detailsLabel = new Label(course.getDifficultyLevel() + " • " + course.getRewardPoints() + " points • " + course.getEstimatedDuration() + " min");
        detailsLabel.getStyleClass().add("item-card-details");
        
        infoBox.getChildren().addAll(titleLabel, detailsLabel);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().addAll("btn", "btn-secondary");
        editBtn.setStyle("-fx-font-size: 11px; -fx-padding: 6 14 6 14;");
        editBtn.setOnAction(e -> handleEditCourse(course));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8px; -fx-padding: 6 14 6 14; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteCourse(course));
        
        buttonBox.getChildren().addAll(editBtn, deleteBtn);
        HBox.setMargin(buttonBox, new Insets(0, 0, 0, 20));
        
        container.getChildren().addAll(iconLabel, infoBox, buttonBox);
        
        return container;
    }

    private void handleEditCourse(Course course) {
        selectedCourse = course;
        
        titleField.setText(course.getTitle());
        slugField.setText(course.getSlug());
        descriptionArea.setText(course.getDescription());
        contentUrlField.setText(course.getContentUrl());
        contentTypeCombo.setValue(course.getContentType());
        difficultyCombo.setValue(course.getDifficultyLevel());
        categoryField.setText(course.getCategory());
        languageField.setText(course.getLanguage());
        durationField.setText(String.valueOf(course.getEstimatedDuration()));
        rewardPointsField.setText(String.valueOf(course.getRewardPoints()));
        thumbnailField.setText(course.getThumbnailUrl());
        
        statusLabel.setStyle("-fx-text-fill: #456990; -fx-font-size: 14px;");
        statusLabel.setText("✏️ Editing: " + course.getTitle());
    }

    private void handleDeleteCourse(Course course) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Course");
        confirmAlert.setHeaderText("Are you sure?");
        confirmAlert.setContentText("Do you want to delete the course: " + course.getTitle() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    courseService.deleteCourse(course.getId());
                    showSuccess("Course deleted successfully!");
                    loadCourses();
                    
                } catch (SQLException e) {
                    showError("Error deleting course: " + e.getMessage());
                }
            }
        });
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
