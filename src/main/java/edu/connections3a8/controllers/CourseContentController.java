package edu.connections3a8.controllers;

import edu.connections3a8.entities.Course;
import edu.connections3a8.entities.Quiz;
import edu.connections3a8.services.CouseService;
import edu.connections3a8.services.GamificationService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class CourseContentController {

    @FXML private Label courseTitleLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label pointsLabel;
    @FXML private Label durationLabel;
    @FXML private Label categoryLabel;
    @FXML private VBox mediaContainer;
    @FXML private VBox quizzesContainer;
    @FXML private Label quizCountLabel;

    private CouseService courseService;
    private GamificationService gamificationService;
    private Course currentCourse;
    private MediaPlayer mediaPlayer; // Keep reference to stop when leaving

    public void initialize() {
        courseService = new CouseService();
        gamificationService = new GamificationService();
    }

    public void setCourse(Course course) {
        this.currentCourse = course;
        loadCourseContent();
    }

    private void loadCourseContent() {
        if (currentCourse == null) return;

        // Set course title and info
        courseTitleLabel.setText(currentCourse.getTitle());
        difficultyLabel.setText("📊 " + currentCourse.getDifficultyLevel());
        pointsLabel.setText("⭐ " + currentCourse.getRewardPoints() + " pts");
        durationLabel.setText("⏱️ " + currentCourse.getEstimatedDuration() + " min");
        categoryLabel.setText("📂 " + currentCourse.getCategory());

        // Load media
        loadMedia();

        // Load related quizzes
        loadRelatedQuizzes();
    }

    private void loadMedia() {
        mediaContainer.getChildren().clear();

        String contentUrl = currentCourse.getContentUrl();

        if (contentUrl == null || contentUrl.trim().isEmpty()) {
            Label noMediaLabel = new Label("No media available for this course.");
            noMediaLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic; -fx-font-size: 14px;");
            mediaContainer.getChildren().add(noMediaLabel);
            return;
        }

        File mediaFile = new File(contentUrl);

        if (mediaFile.exists()) {
            String fileExtension = contentUrl.substring(contentUrl.lastIndexOf(".")).toLowerCase();

            if (fileExtension.matches("\\.(mp4|avi|mkv|mov|wmv|flv)")) {
                // Video file
                loadVideoPlayer(mediaFile);
            } else if (fileExtension.equals(".pdf")) {
                // PDF file - embed in page
                loadPDFViewer(mediaFile);
            } else {
                Label unsupportedLabel = new Label("Unsupported media format: " + fileExtension);
                unsupportedLabel.setStyle("-fx-text-fill: #DC3545; -fx-font-size: 13px;");
                mediaContainer.getChildren().add(unsupportedLabel);
            }
        } else {
            // External URL
            Label urlLabel = new Label("External Content:");
            urlLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #456990; -fx-font-size: 14px;");

            Hyperlink link = new Hyperlink(contentUrl);
            link.setStyle("-fx-font-size: 13px;");
            link.setOnAction(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(contentUrl));
                } catch (Exception ex) {
                    showError("Could not open URL: " + ex.getMessage());
                }
            });

            VBox urlBox = new VBox(8, urlLabel, link);
            urlBox.setPadding(new Insets(15));
            urlBox.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #456990; -fx-border-width: 2px; -fx-border-radius: 8px;");
            mediaContainer.getChildren().add(urlBox);
        }
    }

    private void loadVideoPlayer(File videoFile) {
        try {
            Media media = new Media(videoFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            mediaView.setFitWidth(800);
            mediaView.setPreserveRatio(true);

            // Control buttons
            HBox controls = new HBox(12);
            controls.setAlignment(Pos.CENTER);
            controls.setPadding(new Insets(12));
            controls.getStyleClass().add("media-controls");

            Button playBtn = new Button("▶ Play");
            Button pauseBtn = new Button("⏸ Pause");
            Button stopBtn = new Button("⏹ Stop");
            Button rewindBtn = new Button("⏪ Rewind 10s");
            Button forwardBtn = new Button("⏩ Forward 10s");

            playBtn.getStyleClass().addAll("btn", "btn-success");
            pauseBtn.getStyleClass().addAll("btn", "btn-warning");
            stopBtn.getStyleClass().addAll("btn", "btn-danger");
            rewindBtn.getStyleClass().addAll("btn", "btn-secondary");
            forwardBtn.getStyleClass().addAll("btn", "btn-secondary");

            playBtn.setOnAction(e -> mediaPlayer.play());
            pauseBtn.setOnAction(e -> mediaPlayer.pause());
            stopBtn.setOnAction(e -> {
                mediaPlayer.stop();
                mediaPlayer.seek(mediaPlayer.getStartTime());
            });
            rewindBtn.setOnAction(e -> {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(javafx.util.Duration.seconds(10)));
            });
            forwardBtn.setOnAction(e -> {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(10)));
            });

            controls.getChildren().addAll(playBtn, pauseBtn, stopBtn, rewindBtn, forwardBtn);

            VBox videoContainer = new VBox(12, mediaView, controls);
            videoContainer.setAlignment(Pos.CENTER);
            videoContainer.getStyleClass().add("video-container");

            mediaContainer.getChildren().add(videoContainer);

        } catch (Exception e) {
            Label errorLabel = new Label("Error loading video: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #DC3545; -fx-font-size: 13px;");
            mediaContainer.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    private void loadPDFViewer(File pdfFile) {
        VBox pdfContainer = new VBox(20);
        pdfContainer.getStyleClass().add("pdf-container");
        pdfContainer.setAlignment(Pos.CENTER);
        pdfContainer.setPadding(new Insets(40));
        pdfContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12px; " +
                             "-fx-border-color: #DC3545; -fx-border-width: 2px; -fx-border-radius: 12px;");

        // PDF Icon
        Label pdfIcon = new Label("📄");
        pdfIcon.setStyle("-fx-font-size: 72px;");

        // PDF Info
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("PDF Document");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000501;");
        
        Label fileNameLabel = new Label(pdfFile.getName());
        fileNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
        
        // File size
        long fileSizeBytes = pdfFile.length();
        String fileSize = formatFileSize(fileSizeBytes);
        Label fileSizeLabel = new Label("Size: " + fileSize);
        fileSizeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7280;");
        
        infoBox.getChildren().addAll(titleLabel, fileNameLabel, fileSizeLabel);

        // Open PDF Button
        Button openPdfBtn = new Button("📖 Open PDF in Viewer");
        openPdfBtn.getStyleClass().addAll("btn", "btn-danger");
        openPdfBtn.setPrefWidth(220);
        openPdfBtn.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().open(pdfFile);
            } catch (Exception ex) {
                showError("Could not open PDF: " + ex.getMessage());
            }
        });

        // Info message
        Label infoMessage = new Label("Click the button above to open this PDF in your default PDF viewer");
        infoMessage.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280; -fx-font-style: italic;");
        infoMessage.setWrapText(true);
        infoMessage.setMaxWidth(500);
        infoMessage.setAlignment(Pos.CENTER);

        pdfContainer.getChildren().addAll(pdfIcon, infoBox, openPdfBtn, infoMessage);
        mediaContainer.getChildren().add(pdfContainer);
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private void loadRelatedQuizzes() {
        quizzesContainer.getChildren().clear();

        try {
            List<Long> quizIds = courseService.getQuizIdsForCourse(currentCourse.getId());

            if (quizIds.isEmpty()) {
                Label noQuizLabel = new Label("No quizzes available for this course yet.");
                noQuizLabel.getStyleClass().add("empty-state");
                quizzesContainer.getChildren().add(noQuizLabel);
                quizCountLabel.setText("(0)");
            } else {
                quizCountLabel.setText("(" + quizIds.size() + ")");

                for (Long quizId : quizIds) {
                    Quiz quiz = gamificationService.getQuizById(quizId);
                    if (quiz != null) {
                        HBox quizCard = createQuizCard(quiz);
                        quizzesContainer.getChildren().add(quizCard);
                    }
                }
            }

        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading quizzes: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #DC3545; -fx-font-size: 13px;");
            quizzesContainer.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    private HBox createQuizCard(Quiz quiz) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("quiz-card");

        // Quiz icon
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(50, 50);
        iconContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(155,126,70,0.25), rgba(155,126,70,0.1)); -fx-background-radius: 10;");

        Label iconLabel = new Label("📝");
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconContainer.getChildren().add(iconLabel);

        // Quiz info
        VBox quizInfo = new VBox(5);
        HBox.setHgrow(quizInfo, Priority.ALWAYS);

        Label quizTitle = new Label(quiz.getTitle());
        quizTitle.getStyleClass().add("quiz-title");

        Label quizDetails = new Label(quiz.getQuestionCount() + " questions • " + 
                                     quiz.getPointsReward() + " points • " + 
                                     quiz.getDifficultyLevel());
        quizDetails.getStyleClass().add("quiz-details");

        quizInfo.getChildren().addAll(quizTitle, quizDetails);

        // Take quiz button
        Button takeQuizBtn = new Button("Take Quiz");
        takeQuizBtn.getStyleClass().addAll("btn", "btn-primary");
        takeQuizBtn.setOnAction(e -> openQuiz(quiz));

        card.getChildren().addAll(iconContainer, quizInfo, takeQuizBtn);

        return card;
    }

    private void openQuiz(Quiz quiz) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz: " + quiz.getTitle());
        alert.setHeaderText("Quiz Details");
        alert.setContentText("Quiz Title: " + quiz.getTitle() + "\n" +
                           "Questions: " + quiz.getQuestionCount() + "\n" +
                           "Points: " + quiz.getPointsReward() + "\n" +
                           "Difficulty: " + quiz.getDifficultyLevel() + "\n\n" +
                           "Quiz-taking interface coming soon!");
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        // Stop media player if running
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/CourseCatalogView.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) courseTitleLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Course Catalog");
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
