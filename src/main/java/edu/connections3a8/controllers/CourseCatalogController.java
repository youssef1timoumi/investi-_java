package edu.connections3a8.controllers;

import edu.connections3a8.entities.Course;
import edu.connections3a8.entities.CourseInteraction;
import edu.connections3a8.services.CouseService;
import edu.connections3a8.services.GamificationService;
import edu.connections3a8.utils.ThemeManager;
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
    @FXML private Button nightModeToggle;

    private CouseService courseService;
    private GamificationService gamificationService;
    private int currentUserId = 1; // TODO: Get from session/login
    private javafx.scene.layout.Pane rootPane;

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
        
        // Get root pane for theme switching - wait for scene to be ready
        javafx.application.Platform.runLater(() -> {
            if (searchField != null && searchField.getScene() != null) {
                javafx.scene.Parent root = searchField.getScene().getRoot();
                if (root instanceof javafx.scene.layout.Pane) {
                    rootPane = (javafx.scene.layout.Pane) root;
                }
            }
        });
    }
    
    public void setDarkMode(boolean darkMode) {
        System.out.println("Catalog setDarkMode called with: " + darkMode);
        ThemeManager.getInstance().setDarkMode(darkMode);
        // Apply theme after scene is loaded
        javafx.application.Platform.runLater(() -> {
            System.out.println("Catalog applying theme");
            applyTheme();
            updateThemeButton();
            // Reload courses to apply dark mode styling to cards
            loadCourses();
        });
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
                emptyLabel.setStyle("-fx-font-size: 16px; " +
                                   "-fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#8D96A6" : "#6B7280") + "; " +
                                   "-fx-font-style: italic;");
                courseGrid.add(emptyLabel, 0, 0);
            }
            
        } catch (SQLException e) {
            showError("Error loading courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createCourseCard(Course course) {
        // Check if user has enough points to unlock this course
        int userPoints = 0;
        boolean isLocked = false;
        try {
            edu.connections3a8.entities.UserPoints userPointsObj = gamificationService.getUserPoints(currentUserId);
            if (userPointsObj != null) {
                userPoints = userPointsObj.getPoints();
            }
            isLocked = course.getMinimumPointsRequired() > userPoints;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        
        // Apply dark mode styling if active and locked state
        String lockOpacity = isLocked ? "0.6" : "1.0";
        String lockFilter = isLocked ? "grayscale(0.8) brightness(0.7)" : "none";
        
        if (ThemeManager.getInstance().isDarkMode()) {
            String borderColor = isLocked ? "rgba(166,38,57,0.8)" : "rgba(70,70,100,0.6)";
            card.setStyle("-fx-background-color: #161630; -fx-background-radius: 12px; " +
                         "-fx-border-color: " + borderColor + "; -fx-border-width: 2px; -fx-border-radius: 12px; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 14, 0, 0, 5); " +
                         "-fx-opacity: " + lockOpacity + ";");
        } else {
            String borderColor = isLocked ? "#A62639" : "#456990";
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; " +
                         "-fx-border-color: " + borderColor + "; -fx-border-width: 2px; -fx-border-radius: 12px; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
                         "-fx-opacity: " + lockOpacity + ";");
        }
        
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

        // Lock indicator (if course is locked)
        if (isLocked) {
            HBox lockBadge = new HBox(8);
            lockBadge.setAlignment(Pos.CENTER);
            lockBadge.setPadding(new Insets(8, 12, 8, 12));
            lockBadge.setStyle("-fx-background-color: " + (ThemeManager.getInstance().isDarkMode() ? "#A62639" : "#DC3545") + "; " +
                             "-fx-background-radius: 6px;");
            
            Label lockIcon = new Label("🔒");
            lockIcon.setStyle("-fx-font-size: 14px;");
            
            Label lockText = new Label("Requires " + course.getMinimumPointsRequired() + " points");
            lockText.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white;");
            
            lockBadge.getChildren().addAll(lockIcon, lockText);
            card.getChildren().add(lockBadge);
        }

        // Title
        Label titleLabel = new Label(course.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                           "-fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#F0F2FA" : "#000501") + ";");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(50);

        // Description
        Label descLabel = new Label(course.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; " +
                          "-fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#8D96A6" : "#6B7280") + ";");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);

        // Metadata
        HBox metaBox = new HBox(10);
        Label difficultyLabel = new Label("📊 " + course.getDifficultyLevel());
        Label pointsLabel = new Label("⭐ " + course.getRewardPoints() + " pts");
        Label durationLabel = new Label("⏱️ " + course.getEstimatedDuration() + " min");
        
        String metaColor = ThemeManager.getInstance().isDarkMode() ? "#6189B0" : "#456990";
        String pointsColor = ThemeManager.getInstance().isDarkMode() ? "#E4C45E" : "#9B7E46";
        String durationColor = ThemeManager.getInstance().isDarkMode() ? "#8D96A6" : "#6B7280";
        
        difficultyLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + metaColor + ";");
        pointsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + pointsColor + ";");
        durationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + durationColor + ";");
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
            String likeBtnColor = userLiked ? "-fx-background-color: #28A745; -fx-text-fill: white;" : 
                                 (ThemeManager.getInstance().isDarkMode() ? "-fx-background-color: #2A2A3E; -fx-text-fill: #E8E8E8;" : 
                                              "-fx-background-color: #E5E7EB; -fx-text-fill: #000501;");
            likeBtn.setStyle(likeBtnColor + " -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;");
            likeBtn.setOnAction(e -> {
                handleLike(course);
                loadCourses();
            });

            Button dislikeBtn = new Button("👎 " + dislikes);
            String dislikeBtnColor = userDisliked ? "-fx-background-color: #DC3545; -fx-text-fill: white;" : 
                                    (ThemeManager.getInstance().isDarkMode() ? "-fx-background-color: #2A2A3E; -fx-text-fill: #E8E8E8;" : 
                                                 "-fx-background-color: #E5E7EB; -fx-text-fill: #000501;");
            dislikeBtn.setStyle(dislikeBtnColor + " -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;");
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
        
        if (isLocked) {
            // Show locked message button
            Button lockedBtn = new Button("🔒 Locked");
            lockedBtn.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white; -fx-font-weight: 600; " +
                            "-fx-background-radius: 8px; -fx-padding: 10 20; -fx-cursor: not-allowed; -fx-font-size: 13px;");
            lockedBtn.setPrefWidth(270);
            lockedBtn.setDisable(true);
            actionButtons.getChildren().add(lockedBtn);
        } else {
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
        }

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
        root.setStyle("-fx-background-color: " + (ThemeManager.getInstance().isDarkMode() ? "#2A2A3E" : "#F7F0F5") + ";");
        
        // Course title
        Label titleLabel = new Label(course.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; " +
                           "-fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#F0F2FA" : "#000501") + ";");
        titleLabel.setWrapText(true);
        
        // Course description
        Label descLabel = new Label(course.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; " +
                          "-fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#E8E8E8" : "#000501") + ";");
        descLabel.setWrapText(true);
        
        // Course info box
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(15));
        if (ThemeManager.getInstance().isDarkMode()) {
            infoBox.setStyle("-fx-background-color: #161630; -fx-background-radius: 8px; " +
                           "-fx-border-color: rgba(70,70,100,0.6); -fx-border-width: 2px; -fx-border-radius: 8px;");
        } else {
            infoBox.setStyle("-fx-background-color: white; -fx-background-radius: 8px; " +
                           "-fx-border-color: #456990; -fx-border-width: 2px; -fx-border-radius: 8px;");
        }
        
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
        scrollPane.setStyle("-fx-background-color: " + (ThemeManager.getInstance().isDarkMode() ? "#2A2A3E" : "#F7F0F5") + ";");
        
        Scene scene = new Scene(scrollPane, 550, 500);
        dialog.setScene(scene);
        dialog.show();
    }

    private void openCourseContent(Course course) {
        try {
            System.out.println("Catalog openCourseContent: isDarkMode = " + ThemeManager.getInstance().isDarkMode());
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/CourseContentView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            // Pass the course and dark mode state to the controller
            CourseContentController controller = loader.getController();
            controller.setCourse(course);
            controller.setDarkMode(ThemeManager.getInstance().isDarkMode());
            
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
        labelText.setStyle("-fx-font-weight: 600; " +
                          "-fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#6189B0" : "#456990") + "; " +
                          "-fx-min-width: 120px;");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#E8E8E8" : "#000501") + ";");
        
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
    private void toggleNightMode() {
        ThemeManager.getInstance().toggleDarkMode();
        System.out.println("Night mode toggled. isDarkMode = " + ThemeManager.getInstance().isDarkMode());
        applyTheme();
        updateThemeButton();
        
        // Reload courses to update card styling
        loadCourses();
    }
    
    private void applyTheme() {
        // Get the root and find the ScrollPane
        if (searchField != null && searchField.getScene() != null) {
            javafx.scene.Parent root = searchField.getScene().getRoot();
            System.out.println("Root type: " + root.getClass().getName());
            
            // The root could be either BorderPane or ScrollPane depending on navigation
            ScrollPane scrollPane = null;
            VBox vbox = null;
            
            // Case 1: Root is directly a ScrollPane (after navigation back)
            if (root instanceof ScrollPane) {
                scrollPane = (ScrollPane) root;
                System.out.println("Root is ScrollPane directly!");
                if (scrollPane.getContent() instanceof VBox) {
                    vbox = (VBox) scrollPane.getContent();
                    System.out.println("Found VBox in ScrollPane!");
                }
            } 
            // Case 2: Root is BorderPane (initial load)
            else {
                // Try to find the ScrollPane by traversing up from searchField
                javafx.scene.Parent parent = searchField.getParent();
                while (parent != null) {
                    if (parent instanceof VBox && parent.getStyleClass().contains("root-container")) {
                        vbox = (VBox) parent;
                        System.out.println("Found VBox with root-container class!");
                        break;
                    }
                    parent = parent.getParent();
                }
                
                // Now find the ScrollPane that contains this VBox
                if (vbox != null) {
                    parent = vbox.getParent();
                    if (parent instanceof ScrollPane) {
                        scrollPane = (ScrollPane) parent;
                        System.out.println("Found ScrollPane containing VBox!");
                    }
                }
                
                // If we didn't find ScrollPane via parent traversal, try BorderPane's center
                if (scrollPane == null && root instanceof BorderPane) {
                    BorderPane borderPane = (BorderPane) root;
                    if (borderPane.getCenter() instanceof ScrollPane) {
                        scrollPane = (ScrollPane) borderPane.getCenter();
                        System.out.println("Found ScrollPane in BorderPane center!");
                    }
                }
            }
            
            // Apply dark mode to ScrollPane
            if (scrollPane != null) {
                if (ThemeManager.getInstance().isDarkMode()) {
                    scrollPane.setStyle("-fx-background: #0A0A18; -fx-background-color: #0A0A18;");
                    if (!scrollPane.getStyleClass().contains("dark-mode")) {
                        scrollPane.getStyleClass().add("dark-mode");
                    }
                    System.out.println("Applied dark mode to ScrollPane");
                } else {
                    scrollPane.setStyle("-fx-background-color: transparent;");
                    scrollPane.getStyleClass().remove("dark-mode");
                    System.out.println("Applied light mode to ScrollPane");
                }
            } else {
                System.out.println("ScrollPane not found!");
            }
            
            // Apply dark mode to VBox
            if (vbox != null) {
                if (ThemeManager.getInstance().isDarkMode()) {
                    if (!vbox.getStyleClass().contains("dark-mode")) {
                        vbox.getStyleClass().add("dark-mode");
                    }
                    vbox.setStyle("-fx-background: linear-gradient(to bottom right, #12122A, #0A0A18, #100F22);");
                    System.out.println("Applied dark mode to VBox");
                } else {
                    vbox.getStyleClass().remove("dark-mode");
                    vbox.setStyle(""); // Clear inline style to use CSS
                    System.out.println("Applied light mode to VBox");
                }
            }
            
            // Also apply to the BorderPane root if it exists
            if (root instanceof BorderPane) {
                BorderPane borderPane = (BorderPane) root;
                if (ThemeManager.getInstance().isDarkMode()) {
                    if (!borderPane.getStyleClass().contains("dark-mode")) {
                        borderPane.getStyleClass().add("dark-mode");
                    }
                    // Apply dark background to BorderPane
                    borderPane.setStyle("-fx-background-color: #0A0A18;");
                    System.out.println("Applied dark-mode class and background to BorderPane");
                } else {
                    borderPane.getStyleClass().remove("dark-mode");
                    // Reset to original background
                    borderPane.setStyle("");
                    System.out.println("Removed dark-mode class from BorderPane");
                }
            }
        } else {
            System.out.println("searchField or scene is null!");
        }
    }
    
    private void updateThemeButton() {
        if (nightModeToggle != null) {
            if (ThemeManager.getInstance().isDarkMode()) {
                nightModeToggle.setText("☀️ Light Mode");
            } else {
                nightModeToggle.setText("🌙 Night Mode");
            }
        }
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

    @FXML
    private void handleViewHistory() {
        try {
            List<edu.connections3a8.entities.CourseHistory> history = courseService.getUserCourseHistory(currentUserId, 20);
            
            if (history.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Course History");
                alert.setHeaderText("📚 No History Yet");
                alert.setContentText("You haven't visited any courses yet.\nStart exploring courses to build your learning history!");
                styleHistoryDialog(alert);
                alert.showAndWait();
                return;
            }
            
            // Create custom dialog with better design
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Course History");
            dialog.setHeaderText("📚 Your Learning Journey");
            
            // Create content
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: " + (ThemeManager.getInstance().isDarkMode() ? "#0A0A18" : "#F7F0F5") + ";");
            
            // Add header info
            Label headerInfo = new Label("Recently visited courses (" + history.size() + " total)");
            headerInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; " +
                              "-fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#8D96A6" : "#6B7280") + ";");
            content.getChildren().add(headerInfo);
            
            // Create scrollable list of courses
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(400);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            
            VBox courseList = new VBox(12);
            courseList.setPadding(new Insets(10));
            
            for (edu.connections3a8.entities.CourseHistory item : history) {
                Course course = item.getCourse();
                VBox courseCard = createHistoryCard(course, item);
                courseList.getChildren().add(courseCard);
            }
            
            scrollPane.setContent(courseList);
            content.getChildren().add(scrollPane);
            
            // Set content
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().setPrefWidth(700);
            dialog.getDialogPane().setPrefHeight(550);
            
            // Add close button
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            
            // Style the dialog
            styleCustomDialog(dialog);
            
            dialog.showAndWait();
            
        } catch (SQLException e) {
            showError("Error loading history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private VBox createHistoryCard(Course course, edu.connections3a8.entities.CourseHistory historyItem) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        
        if (ThemeManager.getInstance().isDarkMode()) {
            card.setStyle("-fx-background-color: #161630; -fx-background-radius: 10px; " +
                         "-fx-border-color: rgba(70,70,100,0.6); -fx-border-width: 2px; -fx-border-radius: 10px;");
        } else {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                         "-fx-border-color: #456990; -fx-border-width: 2px; -fx-border-radius: 10px; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        }
        
        // Title
        Label titleLabel = new Label(course.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                           "-fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#F0F2FA" : "#000501") + ";");
        titleLabel.setWrapText(true);
        
        // Course info
        HBox infoBox = new HBox(15);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        Label difficultyLabel = new Label("📊 " + course.getDifficultyLevel());
        Label pointsLabel = new Label("⭐ " + course.getRewardPoints() + " pts");
        Label durationLabel = new Label("⏱️ " + course.getEstimatedDuration() + " min");
        
        String textColor = ThemeManager.getInstance().isDarkMode() ? "#8D96A6" : "#6B7280";
        difficultyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + textColor + ";");
        pointsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (ThemeManager.getInstance().isDarkMode() ? "#E4C45E" : "#9B7E46") + ";");
        durationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + textColor + ";");
        
        infoBox.getChildren().addAll(difficultyLabel, pointsLabel, durationLabel);
        
        // Progress bar (if completion > 0)
        VBox progressBox = null;
        if (historyItem.getCompletionPercentage() > 0) {
            progressBox = new VBox(5);
            Label progressLabel = new Label("Progress: " + historyItem.getCompletionPercentage() + "%");
            progressLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + textColor + ";");
            
            ProgressBar progressBar = new ProgressBar(historyItem.getCompletionPercentage() / 100.0);
            progressBar.setPrefWidth(200);
            progressBar.setStyle("-fx-accent: #9B7E46;");
            
            progressBox.getChildren().addAll(progressLabel, progressBar);
        }
        
        // Time info
        java.time.LocalDateTime visitTime = historyItem.getVisitedAt().toLocalDateTime();
        java.time.Duration duration = java.time.Duration.between(visitTime, java.time.LocalDateTime.now());
        
        String timeAgo;
        if (duration.toDays() > 0) {
            timeAgo = duration.toDays() + " day" + (duration.toDays() > 1 ? "s" : "") + " ago";
        } else if (duration.toHours() > 0) {
            timeAgo = duration.toHours() + " hour" + (duration.toHours() > 1 ? "s" : "") + " ago";
        } else if (duration.toMinutes() > 0) {
            timeAgo = duration.toMinutes() + " minute" + (duration.toMinutes() > 1 ? "s" : "") + " ago";
        } else {
            timeAgo = "Just now";
        }
        
        Label timeLabel = new Label("🕒 Last visited: " + timeAgo);
        timeLabel.setStyle("-fx-font-size: 11px; -fx-font-style: italic; -fx-text-fill: " + textColor + ";");
        
        // Action button
        Button continueBtn = new Button("📖 Continue Learning");
        continueBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #E4C45E, #C8A84E, #9B7E46); " +
                           "-fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 6px; " +
                           "-fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 12px;");
        continueBtn.setOnAction(e -> {
            // Close the history dialog
            ((javafx.stage.Stage) continueBtn.getScene().getWindow()).close();
            // Open the course content
            openCourseContent(course);
        });
        
        continueBtn.setOnMouseEntered(e -> {
            continueBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #F0D46E, #D8B85E, #AB8E56); " +
                               "-fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 6px; " +
                               "-fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 12px;");
        });
        
        continueBtn.setOnMouseExited(e -> {
            continueBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #E4C45E, #C8A84E, #9B7E46); " +
                               "-fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 6px; " +
                               "-fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 12px;");
        });
        
        // Add all elements to card
        card.getChildren().addAll(titleLabel, infoBox);
        if (progressBox != null) {
            card.getChildren().add(progressBox);
        }
        card.getChildren().addAll(timeLabel, continueBtn);
        
        return card;
    }
    
    private void styleCustomDialog(Dialog<?> dialog) {
        javafx.scene.control.DialogPane dialogPane = dialog.getDialogPane();
        
        if (ThemeManager.getInstance().isDarkMode()) {
            dialogPane.setStyle(
                "-fx-background-color: #161630; " +
                "-fx-border-color: rgba(70,70,100,0.6); " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 12px; " +
                "-fx-background-radius: 12px;"
            );
            
            // Style header
            javafx.scene.Node header = dialogPane.lookup(".header-panel");
            if (header != null) {
                header.setStyle(
                    "-fx-background-color: #12122A; " +
                    "-fx-background-radius: 12px 12px 0 0; " +
                    "-fx-padding: 15;"
                );
            }
            
            // Style header text
            Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle("-fx-text-fill: #F0F2FA; -fx-font-size: 18px; -fx-font-weight: bold;");
            }
            
            // Style close button
            for (javafx.scene.control.ButtonType buttonType : dialog.getDialogPane().getButtonTypes()) {
                javafx.scene.control.Button button = (javafx.scene.control.Button) dialogPane.lookupButton(buttonType);
                if (button != null) {
                    button.setStyle(
                        "-fx-background-color: #2A2A3E; " +
                        "-fx-text-fill: #E8E8E8; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-padding: 8 20;"
                    );
                }
            }
        } else {
            dialogPane.setStyle(
                "-fx-background-color: #F7F0F5; " +
                "-fx-border-color: #456990; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 12px; " +
                "-fx-background-radius: 12px;"
            );
            
            // Style header
            javafx.scene.Node header = dialogPane.lookup(".header-panel");
            if (header != null) {
                header.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 12px 12px 0 0; " +
                    "-fx-padding: 15; " +
                    "-fx-border-color: transparent transparent #E5E7EB transparent; " +
                    "-fx-border-width: 0 0 2 0;"
                );
            }
            
            // Style close button
            for (javafx.scene.control.ButtonType buttonType : dialog.getDialogPane().getButtonTypes()) {
                javafx.scene.control.Button button = (javafx.scene.control.Button) dialogPane.lookupButton(buttonType);
                if (button != null) {
                    button.setStyle(
                        "-fx-background-color: #E5E7EB; " +
                        "-fx-text-fill: #000501; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-padding: 8 20;"
                    );
                }
            }
        }
    }
    
    private void styleHistoryDialog(Alert alert) {
        if (ThemeManager.getInstance().isDarkMode()) {
            javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle(
                "-fx-background-color: #161630; " +
                "-fx-border-color: rgba(70,70,100,0.6); " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 12px; " +
                "-fx-background-radius: 12px;"
            );
            
            dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: #12122A; " +
                "-fx-background-radius: 12px 12px 0 0;"
            );
            
            for (javafx.scene.Node node : dialogPane.getChildren()) {
                if (node instanceof javafx.scene.control.Label) {
                    node.setStyle("-fx-text-fill: #F0F2FA;");
                }
            }
            
            dialogPane.lookup(".content").setStyle("-fx-background-color: #161630;");
            
            for (javafx.scene.control.ButtonType buttonType : alert.getButtonTypes()) {
                javafx.scene.control.Button button = (javafx.scene.control.Button) dialogPane.lookupButton(buttonType);
                if (button != null) {
                    button.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #E4C45E, #C8A84E, #9B7E46); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-padding: 8 20;"
                    );
                }
            }
        } else {
            javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #456990; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 12px; " +
                "-fx-background-radius: 12px;"
            );
            
            for (javafx.scene.control.ButtonType buttonType : alert.getButtonTypes()) {
                javafx.scene.control.Button button = (javafx.scene.control.Button) dialogPane.lookupButton(buttonType);
                if (button != null) {
                    button.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #E4C45E, #C8A84E, #9B7E46); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-padding: 8 20;"
                    );
                }
            }
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
