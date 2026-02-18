package edu.connections3a8.controllers;

import edu.connections3a8.entities.Quiz;
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

public class QuizController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField pointsRewardField;
    @FXML private TextField questionCountField;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private TextField categoryField;
    @FXML private TextField timeLimitField;
    @FXML private TextField passingScoreField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label statusLabel;
    @FXML private VBox quizListContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Button themeToggleBtn;
    @FXML private Button autoModeBtn;

    private GamificationService gamificationService;
    private Quiz selectedQuiz = null;
    private List<Quiz> allQuizzes = new ArrayList<>();
    private boolean isDarkMode = false;
    private boolean isAutoMode = false;
    private javafx.scene.layout.Pane rootPane;

    @FXML
    public void initialize() {
        gamificationService = new GamificationService();
        
        // Initialize ComboBoxes
        difficultyCombo.setItems(FXCollections.observableArrayList(
            "beginner", "intermediate", "advanced", "expert"
        ));
        
        statusCombo.setItems(FXCollections.observableArrayList(
            "active", "inactive", "draft"
        ));
        
        // Initialize sort combo
        if (sortCombo != null) {
            sortCombo.setItems(FXCollections.observableArrayList(
                "Title (A-Z)",
                "Title (Z-A)",
                "Points (Low to High)",
                "Points (High to Low)",
                "Questions (Few to Many)",
                "Questions (Many to Few)"
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
        
        // Load quizzes only if container is initialized
        if (quizListContainer != null) {
            loadQuizzes();
        } else {
            System.err.println("Warning: quizListContainer not initialized yet");
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
    private void handleAddQuiz() {
        try {
            // Check if fields are initialized
            if (titleField == null || descriptionArea == null || pointsRewardField == null || 
                questionCountField == null || categoryField == null || timeLimitField == null || 
                passingScoreField == null) {
                showError("Form not properly initialized!");
                return;
            }
            
            // Get text safely
            String title = titleField.getText();
            String pointsReward = pointsRewardField.getText();
            String questionCount = questionCountField.getText();
            
            // Validate inputs
            if (title == null || title.trim().isEmpty()) {
                showError("Title is required!");
                return;
            }

            // Validate numeric fields
            if (pointsReward == null || pointsReward.trim().isEmpty() || 
                questionCount == null || questionCount.trim().isEmpty()) {
                showError("Points Reward and Question Count are required!");
                return;
            }

            // Create or update quiz object
            Quiz quiz = selectedQuiz != null ? selectedQuiz : new Quiz();
            quiz.setTitle(title.trim());
            
            String description = descriptionArea.getText();
            quiz.setDescription(description != null ? description.trim() : "");
            
            // Parse numeric fields
            try {
                int pointsRewardValue = Integer.parseInt(pointsReward.trim());
                int questionCountValue = Integer.parseInt(questionCount.trim());
                
                String timeLimitText = timeLimitField.getText();
                int timeLimit = (timeLimitText == null || timeLimitText.trim().isEmpty()) ? 600 : Integer.parseInt(timeLimitText.trim());
                
                String passingScoreText = passingScoreField.getText();
                int passingScore = (passingScoreText == null || passingScoreText.trim().isEmpty()) ? 70 : Integer.parseInt(passingScoreText.trim());

                if (pointsRewardValue < 0 || questionCountValue < 1 || timeLimit < 0 || passingScore < 0 || passingScore > 100) {
                    showError("Invalid numeric values! Passing score must be between 0-100.");
                    return;
                }
                
                quiz.setPointsReward(pointsRewardValue);
                quiz.setQuestionCount(questionCountValue);
                quiz.setTimeLimit(timeLimit);
                quiz.setPassingScore(passingScore);
            } catch (NumberFormatException e) {
                showError("All numeric fields must be valid numbers!");
                return;
            }
            
            // Set difficulty with default
            String difficulty = difficultyCombo.getValue();
            quiz.setDifficultyLevel(difficulty != null ? difficulty : "beginner");
            
            String category = categoryField.getText();
            quiz.setCategory(category != null ? category.trim() : "");
            
            // Set status with default
            String status = statusCombo.getValue();
            quiz.setStatus(status != null ? status : "active");

            // Add or update quiz in database
            if (selectedQuiz != null) {
                gamificationService.updateQuiz(quiz, selectedQuiz.getId());
                showSuccess("Quiz updated successfully!");
            } else {
                gamificationService.addQuiz(quiz);
                showSuccess("Quiz added successfully!");
            }
            
            handleClearForm();
            loadQuizzes();
            
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
        selectedQuiz = null;
        titleField.clear();
        descriptionArea.clear();
        pointsRewardField.clear();
        questionCountField.clear();
        difficultyCombo.setValue(null);
        categoryField.clear();
        timeLimitField.clear();
        passingScoreField.clear();
        statusCombo.setValue(null);
        statusLabel.setText("");
    }

    @FXML
    private void handleViewAll() {
        try {
            List<Quiz> quizzes = gamificationService.getAllQuizzes();
            
            StringBuilder sb = new StringBuilder();
            sb.append("Total Quizzes: ").append(quizzes.size()).append("\n\n");
            
            for (Quiz quiz : quizzes) {
                sb.append("- ").append(quiz.getTitle())
                  .append(" (").append(quiz.getPointsReward()).append(" points, ")
                  .append(quiz.getQuestionCount()).append(" questions)\n");
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("All Quizzes");
            alert.setHeaderText("Quiz List");
            alert.setContentText(sb.toString());
            alert.showAndWait();
            
        } catch (SQLException e) {
            showError("Error loading quizzes: " + e.getMessage());
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

    private void loadQuizzes() {
        try {
            if (quizListContainer == null) {
                System.err.println("quizListContainer is null - FXML not loaded properly");
                return;
            }
            
            allQuizzes = gamificationService.getAllQuizzes();
            applyFiltersAndSort();
            
        } catch (SQLException e) {
            showError("Error loading quizzes: " + e.getMessage());
        }
    }
    
    private void applyFiltersAndSort() {
        if (quizListContainer == null || allQuizzes == null) {
            return;
        }
        
        quizListContainer.getChildren().clear();
        
        // Filter quizzes based on search
        List<Quiz> filteredQuizzes = allQuizzes;
        
        if (searchField != null && searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
            String searchText = searchField.getText().trim().toLowerCase();
            filteredQuizzes = allQuizzes.stream()
                .filter(quiz -> {
                    boolean matchesTitle = quiz.getTitle().toLowerCase().contains(searchText);
                    boolean matchesCategory = quiz.getCategory().toLowerCase().contains(searchText);
                    boolean matchesDifficulty = quiz.getDifficultyLevel().toLowerCase().contains(searchText);
                    boolean matchesPoints = String.valueOf(quiz.getPointsReward()).contains(searchText);
                    return matchesTitle || matchesCategory || matchesDifficulty || matchesPoints;
                })
                .collect(Collectors.toList());
        }
        
        // Sort quizzes
        if (sortCombo != null && sortCombo.getValue() != null) {
            String sortOption = sortCombo.getValue();
            
            switch (sortOption) {
                case "Title (A-Z)":
                    filteredQuizzes.sort(Comparator.comparing(Quiz::getTitle));
                    break;
                case "Title (Z-A)":
                    filteredQuizzes.sort(Comparator.comparing(Quiz::getTitle).reversed());
                    break;
                case "Points (Low to High)":
                    filteredQuizzes.sort(Comparator.comparingInt(Quiz::getPointsReward));
                    break;
                case "Points (High to Low)":
                    filteredQuizzes.sort(Comparator.comparingInt(Quiz::getPointsReward).reversed());
                    break;
                case "Questions (Few to Many)":
                    filteredQuizzes.sort(Comparator.comparingInt(Quiz::getQuestionCount));
                    break;
                case "Questions (Many to Few)":
                    filteredQuizzes.sort(Comparator.comparingInt(Quiz::getQuestionCount).reversed());
                    break;
            }
        }
        
        // Display filtered and sorted quizzes
        if (filteredQuizzes.isEmpty()) {
            Label emptyLabel = new Label(searchField != null && !searchField.getText().trim().isEmpty() 
                ? "No quizzes found matching your search." 
                : "No quizzes yet. Add your first quiz!");
            emptyLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic; -fx-font-size: 13px;");
            quizListContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (Quiz quiz : filteredQuizzes) {
            HBox quizItem = createQuizItem(quiz);
            quizListContainer.getChildren().add(quizItem);
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

    private HBox createQuizItem(Quiz quiz) {
        HBox container = new HBox(15);
        container.getStyleClass().add("item-card");
        
        // Quiz icon
        Label iconLabel = new Label("📝");
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        // Quiz info
        VBox infoBox = new VBox(5);
        Label titleLabel = new Label(quiz.getTitle());
        titleLabel.getStyleClass().add("item-card-title");
        
        Label detailsLabel = new Label(quiz.getQuestionCount() + " questions • " + quiz.getPointsReward() + " points • " + quiz.getDifficultyLevel());
        detailsLabel.getStyleClass().add("item-card-details");
        
        infoBox.getChildren().addAll(titleLabel, detailsLabel);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().addAll("btn", "btn-secondary");
        editBtn.setStyle("-fx-font-size: 11px; -fx-padding: 6 14 6 14;");
        editBtn.setOnAction(e -> handleEditQuiz(quiz));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8px; -fx-padding: 6 14 6 14; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteQuiz(quiz));
        
        buttonBox.getChildren().addAll(editBtn, deleteBtn);
        HBox.setMargin(buttonBox, new Insets(0, 0, 0, 20));
        
        container.getChildren().addAll(iconLabel, infoBox, buttonBox);
        
        return container;
    }

    private void handleEditQuiz(Quiz quiz) {
        selectedQuiz = quiz;
        
        titleField.setText(quiz.getTitle());
        descriptionArea.setText(quiz.getDescription());
        pointsRewardField.setText(String.valueOf(quiz.getPointsReward()));
        questionCountField.setText(String.valueOf(quiz.getQuestionCount()));
        difficultyCombo.setValue(quiz.getDifficultyLevel());
        categoryField.setText(quiz.getCategory());
        timeLimitField.setText(String.valueOf(quiz.getTimeLimit()));
        passingScoreField.setText(String.valueOf(quiz.getPassingScore()));
        statusCombo.setValue(quiz.getStatus());
        
        statusLabel.setStyle("-fx-text-fill: #456990; -fx-font-size: 14px;");
        statusLabel.setText("✏️ Editing: " + quiz.getTitle());
    }

    private void handleDeleteQuiz(Quiz quiz) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Quiz");
        confirmAlert.setHeaderText("Are you sure?");
        confirmAlert.setContentText("Do you want to delete the quiz: " + quiz.getTitle() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    gamificationService.deleteQuiz(quiz.getId());
                    showSuccess("Quiz deleted successfully!");
                    loadQuizzes();
                    
                } catch (SQLException e) {
                    showError("Error deleting quiz: " + e.getMessage());
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
