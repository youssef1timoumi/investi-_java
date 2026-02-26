package edu.connections3a8.controllers;

import edu.connections3a8.entities.Course;
import edu.connections3a8.entities.CourseInteraction;
import edu.connections3a8.services.CouseService;
import edu.connections3a8.services.GamificationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


public class CourseCatalogController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> difficultyFilter;
    @FXML private GridPane courseGrid;
    @FXML private Label courseCountLabel;

    private CouseService courseService;
    private GamificationService gamificationService;
    private int currentUserId = 1; // TODO: Get from session/login

    @FXML
    public void initialize() {
        courseService = new CouseService();
        gamificationService = new GamificationService();
        
        loadFilters();
        loadCourses();
        
        // Add listeners for real-time filtering
        searchField.textProperty().addListener((obs, old, newVal) -> loadCourses());
        categoryFilter.setOnAction(e -> loadCourses());
        difficultyFilter.setOnAction(e -> loadCourses());
    }

    private void loadFilters() {
        categoryFilter.setItems(FXCollections.observableArrayList(
            "All Categories", "programming", "database", "web", "design", "business"
        ));
        
        difficultyFilter.setItems(FXCollections.observableArrayList(
            "All Difficulties", "beginner", "intermediate", "advanced", "expert"
        ));
    }

    private void loadCourses() {
        courseGrid.getChildren().clear();
        
        try {
            List<Course> courses = courseService.getAllCourses();
            
            // Apply search filter
            String search = searchField.getText();
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.trim().toLowerCase();
                courses = courses.stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(searchLower) ||
                               c.getDescription().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
            }
            
            // Apply category filter
            String category = categoryFilter.getValue();
            if (category != null && !category.equals("All Categories")) {
                courses = courses.stream()
                    .filter(c -> c.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
            }
            
            // Apply difficulty filter
            String difficulty = difficultyFilter.getValue();
            if (difficulty != null && !difficulty.equals("All Difficulties")) {
                courses = courses.stream()
                    .filter(c -> c.getDifficultyLevel().equalsIgnoreCase(difficulty))
                    .collect(Collectors.toList());
            }
            
            // Update count
            courseCountLabel.setText("Showing " + courses.size() + " course(s)");
            
            // Display courses in grid (3 columns)
            int col = 0, row = 0;
            for (Course course : courses) {
                VBox courseCard = createCourseCard(course);
                courseGrid.add(courseCard, col, row);
                
                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
            
            if (courses.isEmpty()) {
                Label emptyLabel = new Label("No courses found matching your criteria.");
                emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6B7280; -fx-font-style: italic;");
                courseGrid.add(emptyLabel, 0, 0);
            }
            
        } catch (SQLException e) {
            showError("Error loading courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createCourseCard(Course course) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; " +
                     "-fx-border-color: #456990; -fx-border-width: 2px; -fx-border-radius: 12px; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(280);
        card.setMaxWidth(280);
        card.setMinHeight(380);

        // Thumbnail Image
        String thumbnailUrl = course.getThumbnailUrl();
        if (thumbnailUrl != null && !thumbnailUrl.trim().isEmpty()) {
            File thumbnailFile = new File(thumbnailUrl);
            if (thumbnailFile.exists()) {
                try {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(thumbnailFile.toURI().toString());
                    javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(image);
                    imageView.setFitWidth(240);
                    imageView.setFitHeight(140);
                    imageView.setPreserveRatio(false);
                    imageView.setStyle("-fx-background-radius: 8px;");
                    
                    // Clip to rounded rectangle
                    javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(240, 140);
                    clip.setArcWidth(16);
                    clip.setArcHeight(16);
                    imageView.setClip(clip);
                    
                    card.getChildren().add(imageView);
                } catch (Exception e) {
                    // If image fails to load, add placeholder
                    addThumbnailPlaceholder(card);
                }
            } else {
                // File doesn't exist, add placeholder
                addThumbnailPlaceholder(card);
            }
        } else {
            // No thumbnail URL, add placeholder
            addThumbnailPlaceholder(card);
        }

        // Title
        Label titleLabel = new Label(course.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000501;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(50);

        // Description
        Label descLabel = new Label(course.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);

        // Metadata
        HBox metaBox = new HBox(10);
        Label difficultyLabel = new Label("📊 " + course.getDifficultyLevel());
        Label pointsLabel = new Label("⭐ " + course.getRewardPoints() + " pts");
        Label durationLabel = new Label("⏱️ " + course.getEstimatedDuration() + " min");
        difficultyLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #456990;");
        pointsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9B7E46;");
        durationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
        metaBox.getChildren().addAll(difficultyLabel, pointsLabel);

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Interaction buttons
        HBox interactionBox = new HBox(8);
        interactionBox.setAlignment(Pos.CENTER);

        try {
            int likes = courseService.getCourseInteractionCount(course.getId(), "like");
            int dislikes = courseService.getCourseInteractionCount(course.getId(), "dislike");
            boolean userLiked = courseService.hasUserInteracted(currentUserId, course.getId(), "like");
            boolean userDisliked = courseService.hasUserInteracted(currentUserId, course.getId(), "dislike");

            Button likeBtn = new Button("👍 " + likes);
            likeBtn.setStyle((userLiked ? "-fx-background-color: #28A745; -fx-text-fill: white;" : 
                             "-fx-background-color: #E5E7EB; -fx-text-fill: #000501;") +
                            " -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;");
            likeBtn.setOnAction(e -> {
                handleLike(course);
                loadCourses();
            });

            Button dislikeBtn = new Button("👎 " + dislikes);
            dislikeBtn.setStyle((userDisliked ? "-fx-background-color: #DC3545; -fx-text-fill: white;" : 
                                "-fx-background-color: #E5E7EB; -fx-text-fill: #000501;") +
                               " -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;");
            dislikeBtn.setOnAction(e -> {
                handleDislike(course);
                loadCourses();
            });

            Button reportBtn = new Button("🚩");
            reportBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; " +
                             "-fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;");
            reportBtn.setOnAction(e -> handleReport(course));

            interactionBox.getChildren().addAll(likeBtn, dislikeBtn, reportBtn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Action buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setStyle("-fx-background-color: #9B7E46; -fx-text-fill: white; -fx-font-weight: 600; " +
                        "-fx-background-radius: 8px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-size: 13px;");
        viewDetailsBtn.setPrefWidth(130);
        viewDetailsBtn.setOnAction(e -> openCourseDetails(course));
        
        Button courseContentBtn = new Button("Course Content");
        courseContentBtn.setStyle("-fx-background-color: #456990; -fx-text-fill: white; -fx-font-weight: 600; " +
                        "-fx-background-radius: 8px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-size: 13px;");
        courseContentBtn.setPrefWidth(130);
        courseContentBtn.setOnAction(e -> openCourseContent(course));
        
        actionButtons.getChildren().addAll(viewDetailsBtn, courseContentBtn);

        card.getChildren().addAll(titleLabel, descLabel, metaBox, spacer, 
                                  new Separator(), interactionBox, actionButtons);

        return card;
    }

    private void handleLike(Course course) {
        try {
            boolean alreadyLiked = courseService.hasUserInteracted(currentUserId, course.getId(), "like");
            
            if (alreadyLiked) {
                courseService.removeCourseInteraction(currentUserId, course.getId(), "like");
            } else {
                // Remove dislike if exists
                courseService.removeCourseInteraction(currentUserId, course.getId(), "dislike");
                
                CourseInteraction interaction = new CourseInteraction(currentUserId, course.getId(), "like");
                courseService.addCourseInteraction(interaction);
            }
            
        } catch (SQLException e) {
            showError("Error updating like: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDislike(Course course) {
        try {
            boolean alreadyDisliked = courseService.hasUserInteracted(currentUserId, course.getId(), "dislike");
            
            if (alreadyDisliked) {
                courseService.removeCourseInteraction(currentUserId, course.getId(), "dislike");
            } else {
                // Remove like if exists
                courseService.removeCourseInteraction(currentUserId, course.getId(), "like");
                
                CourseInteraction interaction = new CourseInteraction(currentUserId, course.getId(), "dislike");
                courseService.addCourseInteraction(interaction);
            }
            
        } catch (SQLException e) {
            showError("Error updating dislike: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleReport(Course course) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Course");
        dialog.setHeaderText("Report: " + course.getTitle());
        dialog.setContentText("Please provide a reason for reporting this course:");
        
        dialog.showAndWait().ifPresent(reason -> {
            if (reason != null && !reason.trim().isEmpty()) {
                try {
                    CourseInteraction interaction = new CourseInteraction(currentUserId, course.getId(), "report");
                    interaction.setReportReason(reason.trim());
                    courseService.addCourseInteraction(interaction);
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Report Submitted");
                    alert.setHeaderText("Thank you for your feedback");
                    alert.setContentText("Your report has been submitted. We'll review it shortly.");
                    alert.showAndWait();
                    
                } catch (SQLException e) {
                    showError("Error submitting report: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void openCourseDetails(Course course) {
        Stage dialog = new Stage();
        dialog.setTitle(course.getTitle() + " - Details");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #F7F0F5;");
        
        // Course title
        Label titleLabel = new Label(course.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #000501;");
        titleLabel.setWrapText(true);
        
        // Course description
        Label descLabel = new Label(course.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000501;");
        descLabel.setWrapText(true);
        
        // Course info box
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(15));
        infoBox.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #456990; -fx-border-width: 2px; -fx-border-radius: 8px;");
        
        infoBox.getChildren().addAll(
            createInfoRow("📂 Category:", course.getCategory()),
            createInfoRow("📊 Difficulty:", course.getDifficultyLevel()),
            createInfoRow("⏱️ Duration:", course.getEstimatedDuration() + " minutes"),
            createInfoRow("⭐ Reward:", course.getRewardPoints() + " points"),
            createInfoRow("🌐 Language:", course.getLanguage()),
            createInfoRow("📌 Status:", course.getStatus()),
            createInfoRow("🎯 Content Type:", course.getContentType())
        );
        
        // Action buttons
        HBox actionBox = new HBox(12);
        actionBox.setAlignment(Pos.CENTER);
        
        Button viewContentBtn = new Button("📺 View Course Content");
        viewContentBtn.setStyle("-fx-background-color: #456990; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8px; -fx-padding: 12 24; -fx-cursor: hand; -fx-font-size: 14px;");
        viewContentBtn.setOnAction(e -> {
            dialog.close();
            openCourseContent(course);
        });
        
        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8px; -fx-padding: 12 24; -fx-cursor: hand; -fx-font-size: 14px;");
        closeBtn.setOnAction(e -> dialog.close());
        
        actionBox.getChildren().addAll(viewContentBtn, closeBtn);
        
        root.getChildren().addAll(titleLabel, new Separator(), descLabel, new Separator(), 
                                  infoBox, new Separator(), actionBox);
        
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #F7F0F5;");
        
        Scene scene = new Scene(scrollPane, 550, 500);
        dialog.setScene(scene);
        dialog.show();
    }

    private void openCourseContent(Course course) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/CourseContentView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            // Pass the course to the controller
            CourseContentController controller = loader.getController();
            controller.setCourse(course);
            
            javafx.stage.Stage stage = (javafx.stage.Stage) searchField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Course Content - " + course.getTitle());
        } catch (Exception e) {
            showError("Error loading course content: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-weight: 600; -fx-text-fill: #456990; -fx-min-width: 120px;");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: #000501;");
        
        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    private void addThumbnailPlaceholder(VBox card) {
        // Create a placeholder for missing thumbnail
        StackPane placeholder = new StackPane();
        placeholder.setPrefSize(240, 140);
        placeholder.setMaxSize(240, 140);
        placeholder.setStyle("-fx-background-color: linear-gradient(to bottom right, #456990, #9B7E46); " +
                            "-fx-background-radius: 8px;");
        
        Label placeholderLabel = new Label("📚");
        placeholderLabel.setStyle("-fx-font-size: 48px;");
        
        placeholder.getChildren().add(placeholderLabel);
        card.getChildren().add(placeholder);
    }



    @FXML
    private void handleClearFilters() {
        searchField.clear();
        categoryFilter.setValue(null);
        difficultyFilter.setValue(null);
        loadCourses();
    }

    @FXML
    private void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/MainMenu.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.stage.Stage stage = (javafx.stage.Stage) searchField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Gamification System - Main Menu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
