package edu.connections3a8.tests;

import edu.connections3a8.entities.UserPoints;
import edu.connections3a8.entities.UserQuiz;
import edu.connections3a8.services.GamificationService;

import java.sql.SQLException;
import java.util.List;

public class TestQuizCompletion {
    public static void main(String[] args) {
        
        System.out.println("========== TESTING QUIZ COMPLETION ==========\n");
        
        GamificationService gamificationService = new GamificationService();
        
        try {
            int userId = 1; // Use existing user ID
            
            // Check points before quiz
            System.out.println("--- Before Quiz Completion ---");
            UserPoints pointsBefore = gamificationService.getUserPoints(userId);
            System.out.println("Points: " + pointsBefore.getPoints());
            System.out.println("Level: " + pointsBefore.getLevel());
            
            // Complete a quiz
            System.out.println("\n--- Completing Quiz ---");
            System.out.println("Quiz ID: 2 (SQL Advanced Quiz)");
            System.out.println("Score: 85%");
            System.out.println("Time taken: 450 seconds");
            
            gamificationService.completeQuiz(userId, 2, 85, 450);
            System.out.println("✓ Quiz completed successfully!");
            
            // Check points after quiz
            System.out.println("\n--- After Quiz Completion ---");
            UserPoints pointsAfter = gamificationService.getUserPoints(userId);
            System.out.println("Points: " + pointsAfter.getPoints());
            System.out.println("Level: " + pointsAfter.getLevel());
            System.out.println("Points gained: " + (pointsAfter.getPoints() - pointsBefore.getPoints()));
            
            // Get quiz history
            System.out.println("\n--- User Quiz History ---");
            List<UserQuiz> quizHistory = gamificationService.getUserQuizHistory(userId);
            System.out.println("Total quizzes completed: " + quizHistory.size());
            for (UserQuiz uq : quizHistory) {
                System.out.println("  - Quiz ID: " + uq.getQuizId() + 
                                   ", Score: " + uq.getScore() + 
                                   ", Passed: " + (uq.isPassed() ? "Yes" : "No") +
                                   ", Time: " + uq.getTimeTaken() + "s");
            }
            
            System.out.println("\n========== TEST COMPLETED SUCCESSFULLY ==========");
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
