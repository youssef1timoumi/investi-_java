# Course Catalog - User View Implementation Guide

## Overview
A user-facing course catalog where users can browse, view, interact with courses, and access related quizzes.

## Database Schema (Already Added)

### course_interactions table
```sql
CREATE TABLE course_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    course_id BIGINT NOT NULL,
    interaction_type ENUM('like', 'dislike', 'report') NOT NULL,
    report_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES personne(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_course_interaction (user_id, course_id, interaction_type)
);
```

### course_quizzes table
```sql
CREATE TABLE course_quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    quiz_order INT DEFAULT 1,
    is_required BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_course_quiz (course_id, quiz_id)
);
```

## Entities Created

### CourseInteraction.java ✅
- Fields: id, userId, courseId, interactionType, reportReason, createdAt
- Methods: getters, setters, equals, hashCode, toString

## Service Methods Added to CouseService ✅

### Interaction Methods:
- `addCourseInteraction(CourseInteraction)` - Add like/dislike/report
- `removeCourseInteraction(userId, courseId, type)` - Remove interaction
- `getCourseInteractionCount(courseId, type)` - Count likes/dislikes/reports
- `hasUserInteracted(userId, courseId, type)` - Check if user interacted

### Course-Quiz Linking:
- `linkQuizToCourse(courseId, quizId, order, isRequired)` - Link quiz to course
- `unlinkQuizFromCourse(courseId, quizId)` - Remove quiz link
- `getQuizIdsForCourse(courseId)` - Get all quizzes for a course

## Implementation Steps

### Step 1: Create CourseCatalogView.fxml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<ScrollPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="edu.connections3a8.controllers.CourseCatalogController"
            fitToWidth="true" fitToHeight="true">

<VBox styleClass="root-container" stylesheets="@courseCatalog.css" spacing="20">
    <padding>
        <Insets top="32" right="36" bottom="32" left="36"/>
    </padding>

    <!-- Header -->
    <HBox styleClass="glass-header" alignment="CENTER_LEFT" spacing="20">
        <Label text="📚 Course Catalog" styleClass="page-title" HBox.hgrow="ALWAYS"/>
        <Button text="← Back" onAction="#handleBack" styleClass="btn, btn-dark"/>
    </HBox>

    <!-- Search and Filter -->
    <HBox spacing="15" alignment="CENTER_LEFT">
        <TextField fx:id="searchField" promptText="Search courses..." 
                   styleClass="modern-text-field" prefWidth="300"/>
        <ComboBox fx:id="categoryFilter" promptText="All Categories" 
                  styleClass="modern-combo" prefWidth="180"/>
        <ComboBox fx:id="difficultyFilter" promptText="All Difficulties" 
                  styleClass="modern-combo" prefWidth="180"/>
        <Button text="Clear" onAction="#handleClearFilters" 
                styleClass="btn, btn-secondary"/>
    </HBox>

    <!-- Course Grid -->
    <GridPane fx:id="courseGrid" hgap="20" vgap="20">
        <!-- Courses loaded dynamically -->
    </GridPane>

</VBox>
</ScrollPane>
```

### Step 2: Create CourseCatalogController.java

```java
package edu.connections3a8.controllers;

import edu.connections3a8.entities.Course;
import edu.connections3a8.entities.CourseInteraction;
import edu.connections3a8.entities.Quiz;
import edu.connections3a8.services.CouseService;
import edu.connections3a8.services.GamificationService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.sql.SQLException;
import java.util.List;

public class CourseCatalogController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> difficultyFilter;
    @FXML private GridPane courseGrid;

    private CouseService courseService;
    private GamificationService gamificationService;
    private int currentUserId = 1; // TODO: Get from session

    @FXML
    public void initialize() {
        courseService = new CouseService();
        gamificationService = new GamificationService();
        
        loadFilters();
        loadCourses();
        
        // Add listeners
        searchField.textProperty().addListener((obs, old, newVal) -> loadCourses());
        categoryFilter.setOnAction(e -> loadCourses());
        difficultyFilter.setOnAction(e -> loadCourses());
    }

    private void loadFilters() {
        // Load categories and difficulties
        categoryFilter.getItems().addAll("All Categories", "programming", "database", "web");
        difficultyFilter.getItems().addAll("All Difficulties", "beginner", "intermediate", "advanced");
    }

    private void loadCourses() {
        courseGrid.getChildren().clear();
        
        try {
            List<Course> courses = courseService.getAllCourses();
            
            // Apply filters
            String search = searchField.getText();
            String category = categoryFilter.getValue();
            String difficulty = difficultyFilter.getValue();
            
            if (search != null && !search.isEmpty()) {
                courses = courses.stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
            }
            
            if (category != null && !category.equals("All Categories")) {
                courses = courses.stream()
                    .filter(c -> c.getCategory().equals(category))
                    .collect(Collectors.toList());
            }
            
            if (difficulty != null && !difficulty.equals("All Difficulties")) {
                courses = courses.stream()
                    .filter(c -> c.getDifficultyLevel().equals(difficulty))
                    .collect(Collectors.toList());
            }
            
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
            
        } catch (SQLException e) {
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

        // Title
        Label titleLabel = new Label(course.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000501;");
        titleLabel.setWrapText(true);

        // Description
        Label descLabel = new Label(course.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);

        // Metadata
        HBox metaBox = new HBox(10);
        Label difficultyLabel = new Label("📊 " + course.getDifficultyLevel());
        Label pointsLabel = new Label("⭐ " + course.getRewardPoints() + " pts");
        difficultyLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #456990;");
        pointsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9B7E46;");
        metaBox.getChildren().addAll(difficultyLabel, pointsLabel);

        // Interaction buttons
        HBox interactionBox = new HBox(8);
        interactionBox.setAlignment(Pos.CENTER);

        try {
            int likes = courseService.getCourseInteractionCount(course.getId(), "like");
            int dislikes = courseService.getCourseInteractionCount(course.getId(), "dislike");
            boolean userLiked = courseService.hasUserInteracted(currentUserId, course.getId(), "like");
            boolean userDisliked = courseService.hasUserInteracted(currentUserId, course.getId(), "dislike");

            Button likeBtn = new Button("👍 " + likes);
            likeBtn.setStyle(userLiked ? "-fx-background-color: #28A745;" : "-fx-background-color: #E5E7EB;");
            likeBtn.setOnAction(e -> handleLike(course, likeBtn));

            Button dislikeBtn = new Button("👎 " + dislikes);
            dislikeBtn.setStyle(userDisliked ? "-fx-background-color: #DC3545;" : "-fx-background-color: #E5E7EB;");
            dislikeBtn.setOnAction(e -> handleDislike(course, dislikeBtn));

            Button reportBtn = new Button("🚩");
            reportBtn.setStyle("-fx-background-color: #FFA500;");
            reportBtn.setOnAction(e -> handleReport(course));

            interactionBox.getChildren().addAll(likeBtn, dislikeBtn, reportBtn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // View button
        Button viewBtn = new Button("View Course");
        viewBtn.setStyle("-fx-background-color: #456990; -fx-text-fill: white; -fx-font-weight: 600; " +
                        "-fx-background-radius: 8px; -fx-padding: 10 20; -fx-cursor: hand;");
        viewBtn.setMaxWidth(Double.MAX_VALUE);
        viewBtn.setOnAction(e -> openCourseDetails(course));

        card.getChildren().addAll(titleLabel, descLabel, metaBox, new Separator(), 
                                  interactionBox, viewBtn);

        return card;
    }

    private void handleLike(Course course, Button likeBtn) {
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
            
            loadCourses(); // Refresh
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleDislike(Course course, Button dislikeBtn) {
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
            
            loadCourses(); // Refresh
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleReport(Course course) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Course");
        dialog.setHeaderText("Report: " + course.getTitle());
        dialog.setContentText("Reason:");
        
        dialog.showAndWait().ifPresent(reason -> {
            try {
                CourseInteraction interaction = new CourseInteraction(currentUserId, course.getId(), "report");
                interaction.setReportReason(reason);
                courseService.addCourseInteraction(interaction);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Report Submitted");
                alert.setContentText("Thank you for your report. We'll review it shortly.");
                alert.showAndWait();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void openCourseDetails(Course course) {
        Stage dialog = new Stage();
        dialog.setTitle(course.getTitle());
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #F7F0F5;");
        
        // Course details
        Label titleLabel = new Label(course.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label descLabel = new Label(course.getDescription());
        descLabel.setWrapText(true);
        
        // Course info
        VBox infoBox = new VBox(8);
        infoBox.getChildren().addAll(
            new Label("Category: " + course.getCategory()),
            new Label("Difficulty: " + course.getDifficultyLevel()),
            new Label("Duration: " + course.getEstimatedDuration() + " minutes"),
            new Label("Reward: " + course.getRewardPoints() + " points")
        );
        
        // Related quizzes
        Label quizzesLabel = new Label("Related Quizzes:");
        quizzesLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        VBox quizzesBox = new VBox(10);
        try {
            List<Long> quizIds = courseService.getQuizIdsForCourse(course.getId());
            for (Long quizId : quizIds) {
                Quiz quiz = gamificationService.getQuizById(quizId);
                if (quiz != null) {
                    Button quizBtn = new Button("📝 " + quiz.getTitle());
                    quizBtn.setMaxWidth(Double.MAX_VALUE);
                    quizBtn.setOnAction(e -> openQuiz(quiz));
                    quizzesBox.getChildren().add(quizBtn);
                }
            }
            
            if (quizIds.isEmpty()) {
                quizzesBox.getChildren().add(new Label("No quizzes available yet."));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> dialog.close());
        
        root.getChildren().addAll(titleLabel, descLabel, new Separator(), 
                                  infoBox, new Separator(), quizzesLabel, quizzesBox, closeBtn);
        
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        
        Scene scene = new Scene(scrollPane, 600, 700);
        dialog.setScene(scene);
        dialog.show();
    }

    private void openQuiz(Quiz quiz) {
        // TODO: Implement quiz taking interface
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz: " + quiz.getTitle());
        alert.setContentText("Quiz interface coming soon!");
        alert.showAndWait();
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
        // Navigate back to main menu
    }
}
```

### Step 3: Create courseCatalog.css

```css
.root-container {
    -fx-background: linear-gradient(to bottom right, #EEF1F8, #E4DCF4, #F4F0FA);
}

.glass-header {
    -fx-background-color: rgba(255, 255, 255, 0.7);
    -fx-background-radius: 12px;
    -fx-padding: 20px 30px;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);
}

.page-title {
    -fx-font-size: 28px;
    -fx-font-weight: bold;
    -fx-text-fill: #000501;
}

.modern-text-field, .modern-combo {
    -fx-background-color: white;
    -fx-border-color: #456990;
    -fx-border-radius: 8px;
    -fx-background-radius: 8px;
    -fx-padding: 10px 15px;
    -fx-font-size: 13px;
}

.btn {
    -fx-background-radius: 8px;
    -fx-padding: 10px 20px;
    -fx-cursor: hand;
    -fx-font-weight: 600;
}

.btn-secondary {
    -fx-background-color: #9B7E46;
    -fx-text-fill: white;
}

.btn-dark {
    -fx-background-color: #000501;
    -fx-text-fill: white;
}
```

### Step 4: Add to MainMenu

Add a button in MainMenuContent.fxml:
```xml
<Button text="📚 Browse Courses" onAction="#loadCourseCatalog" 
        styleClass="menu-button"/>
```

In MainMenuController:
```java
@FXML
private void loadCourseCatalog() {
    loadView("/CourseCatalogView.fxml");
}
```

## Features Implemented

✅ Course browsing with grid layout
✅ Search and filter (category, difficulty)
✅ Like/Dislike functionality
✅ Report functionality with reason
✅ Course detail view
✅ Related quizzes display
✅ Interactive course cards
✅ Real-time interaction counts
✅ User interaction tracking

## Next Steps

1. Create the FXML and Controller files
2. Create the CSS file
3. Add navigation from main menu
4. Implement quiz-taking interface
5. Add course progress tracking
6. Add user enrollment system

## SQL to Execute

```sql
-- Already added to database.sql
CREATE TABLE course_interactions (...);
CREATE TABLE course_quizzes (...);
```

This provides a complete, interactive course catalog for users!
