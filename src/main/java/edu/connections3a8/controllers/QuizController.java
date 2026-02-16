package edu.connections3a8.controllers;

import edu.connections3a8.entities.Quiz;
import edu.connections3a8.services.GamificationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

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

    private GamificationService gamificationService;

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
        
        statusLabel.setText("");
    }

    @FXML
    private void handleAddQuiz() {
        try {
            // Validate inputs
            if (titleField.getText().isEmpty()) {
                showError("Title is required!");
                return;
            }

            // Validate numeric fields
            if (pointsRewardField.getText().isEmpty() || questionCountField.getText().isEmpty()) {
                showError("Points Reward and Question Count are required!");
                return;
            }

            // Create quiz object
            Quiz quiz = new Quiz();
            quiz.setTitle(titleField.getText().trim());
            quiz.setDescription(descriptionArea.getText().trim());
            
            // Parse numeric fields
            try {
                int pointsReward = Integer.parseInt(pointsRewardField.getText().trim());
                int questionCount = Integer.parseInt(questionCountField.getText().trim());
                int timeLimit = timeLimitField.getText().isEmpty() ? 600 : Integer.parseInt(timeLimitField.getText().trim());
                int passingScore = passingScoreField.getText().isEmpty() ? 70 : Integer.parseInt(passingScoreField.getText().trim());
                
                if (pointsReward < 0 || questionCount < 1 || timeLimit < 0 || passingScore < 0 || passingScore > 100) {
                    showError("Invalid numeric values! Passing score must be between 0-100.");
                    return;
                }
                
                quiz.setPointsReward(pointsReward);
                quiz.setQuestionCount(questionCount);
                quiz.setTimeLimit(timeLimit);
                quiz.setPassingScore(passingScore);
            } catch (NumberFormatException e) {
                showError("All numeric fields must be valid numbers!");
                return;
            }
            
            // Set difficulty with default
            String difficulty = difficultyCombo.getValue();
            quiz.setDifficultyLevel(difficulty != null ? difficulty : "beginner");
            
            quiz.setCategory(categoryField.getText().trim());
            
            // Set status with default
            String status = statusCombo.getValue();
            quiz.setStatus(status != null ? status : "active");

            // Add quiz to database
            gamificationService.addQuiz(quiz);
            
            showSuccess("Quiz added successfully!");
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
        statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        statusLabel.setText("✓ " + message);
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        statusLabel.setText("✗ " + message);
    }
}
